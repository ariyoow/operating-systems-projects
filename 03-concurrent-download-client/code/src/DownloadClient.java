import java.util.*;
import java.util.concurrent.*;

public class DownloadClient {

    private final FileServer fileServer;
    private boolean verbose;
    private static final int THREAD_COUNT = 8;

    private int sharedChecksum;
    private final TicketLock ticketLock;

    private final BlockingQueue<LogEvent> logsQueue;
    private final BlockingQueue<Integer> missingBlocksQueue;

    public DownloadClient(FileServer fileServer) {
        this.fileServer = fileServer;
        this.verbose = true;
        this.sharedChecksum = 0;
        this.ticketLock = new TicketLock();
        this.logsQueue = new LinkedBlockingQueue<>();
        this.missingBlocksQueue = new LinkedBlockingQueue<>();
    }

    public long download(String fileName) throws InterruptedException {

        FileServer.FileInfo info = findFile(fileName);
        if (info == null) {
            System.out.println("File not found.");
            return 0L;
        }

        int totalBlocks = info.getBlockCount();
        int fileChecksum = info.getFileChecksum();

        if (verbose)
            System.out.println("Downloading " + fileName + " with " + THREAD_COUNT + " threads...\n");

        long start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(totalBlocks);

        int perThread = totalBlocks / THREAD_COUNT;
        int rem = totalBlocks % THREAD_COUNT;

        int index = 0;
        for (int i = 0; i < THREAD_COUNT; i++) {

            int share = perThread + (i < rem ? 1 : 0);
            int startIndex = index;
            int endIndex = index + share;
            index = endIndex;

            if (share == 0) continue;

            DownloadWorker worker = new DownloadWorker(
                    fileServer,
                    this,
                    fileName,
                    startIndex,
                    endIndex,
                    verbose,
                    latch
            );

            executor.execute(worker);
        }

        latch.await();
        executor.shutdownNow();

        long duration = System.currentTimeMillis() - start;

        if (verbose) {
            System.out.println("\nDownload finished in " + duration + " ms");
            System.out.println(sharedChecksum == fileChecksum ? "File checksum: Valid" : "File checksum: Invalid");
        }

        return duration;
    }

    private FileServer.FileInfo findFile(String name) {
        for (FileServer.FileInfo f : fileServer.list()) {
            if (f.getFileName().equals(name)) return f;
        }
        return null;
    }


    public void setVerbose(boolean v) {
        this.verbose = v;
    }


    // ---------- Logging Helpers ----------
    private void logInternal(String workerId, int blockIndex, LogEventType type, long duration, int ticket) {
        logsQueue.add(new LogEvent(workerId, blockIndex, System.currentTimeMillis(), type, duration, ticket));
    }

    public void logEnterWait(String w, int b, int ticket) {
        logInternal(w, b, LogEventType.ENTER_WAIT, 0, ticket);
    }

    public void logLockAcquired(String w, int b, int ticket) {
        logInternal(w, b, LogEventType.LOCK_ACQUIRED, 0, ticket);
    }

    public void logRetry(String w, int b, int ticket) {
        logInternal(w, b, LogEventType.RETRY_WAIT, 0, ticket);
    }

    public void logAbort(String w, int b, int ticket) {
        logInternal(w, b, LogEventType.FINAL_ABORT, 0, ticket);
    }

    public void logAbortInterrupted(String w, int b, int ticket) {
        logInternal(w, b, LogEventType.FINAL_ABORT_INTERRUPTED, 0, ticket);
    }

    public void logLockReleased(String w, int b, long d, int ticket) {
        logInternal(w, b, LogEventType.LOCK_RELEASED, d, ticket);
    }

    // ---------- Ticket-based Checksum Logic ----------

    public boolean updateChecksumWithLock(int blockChecksum, String workerName, int blockIndex) {
        final int MAX_TRIES = 3;
        final long LOCK_TIMEOUT_MS = 2000L;

        for (int attempt = 1; attempt <= MAX_TRIES; attempt++) {

            int myTicket = ticketLock.getTicket();
            long enterQueueTime = System.currentTimeMillis();
            logEnterWait(workerName, blockIndex, myTicket);
            // الان این acquired time خودش یه ناحیه بحرانی محسوب نمیشه؟
            long acquiredTime = 0L;

            try {
                boolean gotTurn = ticketLock.waitTurn(myTicket, LOCK_TIMEOUT_MS);
                if (gotTurn) {
                    acquiredTime = System.currentTimeMillis();
                    logLockAcquired(workerName, blockIndex, myTicket);

                    // Critical section
                    updateSharedChecksum(blockChecksum);

                    long releaseTime = System.currentTimeMillis();
                    long duration = releaseTime - acquiredTime;
                    ticketLock.release();

                    logLockReleased(workerName, blockIndex, duration, myTicket);

                    return true;
                } else {
                    if (verbose) {
                        System.out.println(workerName + ": Timeout waiting for ticket " + myTicket
                                + " for block " + blockIndex + " (attempt " + attempt + "/" + MAX_TRIES + ")");
                    }

                    if (attempt < MAX_TRIES) {
                        logRetry(workerName, blockIndex, myTicket);
                        // backoff before trying again (new ticket next loop)
                        int backoff = ThreadLocalRandom.current().nextInt(100, 301);
                        try {
                            Thread.sleep(backoff);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            logAbortInterrupted(workerName, blockIndex, myTicket);
                            return false;
                        }
                        continue; // next attempt will take a new ticket
                    } else {
                        // final abort
                        logAbort(workerName, blockIndex, myTicket);
                        return false;
                    }
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logAbortInterrupted(workerName, blockIndex, myTicket);
                return false;
            } finally {
                // Note: if we got the turn and executed, we already released above.
                // If we somehow acquired but didn't release (shouldn't happen), ensure release.
                // We only call release on successful gotTurn branch above.
            }
        }

        // fallback
        logAbort(workerName, blockIndex, -1);
        return false;
    }

    private synchronized void updateSharedChecksum(int blockChecksum) {
        this.sharedChecksum ^= blockChecksum;
    }

    // getters (unchanged)
    public BlockingQueue<String> getOldLogsQueueAsStrings() {
        return null;
    } // placeholder if existed

    public BlockingQueue<LogEvent> getLogsQueue() {
        return logsQueue;
    }

    public BlockingQueue<Integer> getMissingBlocksQueue() {
        return missingBlocksQueue;
    }

    public int getSharedChecksum() {
        return sharedChecksum;
    }

    public void reportMissingBlock(int blockIndex) {
        missingBlocksQueue.add(blockIndex);
    }

}

