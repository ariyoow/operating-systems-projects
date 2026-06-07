import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class DownloadWorker implements Runnable {

    private final FileServer fileServer;
    private final DownloadClient client;
    private final String fileName;
    private final int startIndex;
    private final int endIndex;
    private final boolean verbose;
    private final CountDownLatch latch;

    public DownloadWorker(FileServer fileServer,
                          DownloadClient client,
                          String fileName,
                          int startIndex,
                          int endIndex,
                          boolean verbose,
                          CountDownLatch latch) {

        this.fileServer = fileServer;
        this.client = client;
        this.fileName = fileName;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.verbose = verbose;
        this.latch = latch;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
            downloadBlock(i);
        }
    }

    private void downloadBlock(int index) {
        String threadName = Thread.currentThread().getName();

        while (!Thread.currentThread().isInterrupted()) {

            try {
                if (verbose)
                    System.out.println(threadName + ": Processing block " + index);

                Block block = fileServer.get(fileName, index);

                if (block == null) {
                    client.reportMissingBlock(index);
                    client.getLogsQueue().add(
                            new LogEvent(threadName, index, System.currentTimeMillis(), LogEventType.MISSING_BLOCK, 0, -1)
                    );
                }

                int cs = checksum(block.data());
                if (cs != block.checksum()) {
                    if (verbose)
                        System.out.println(threadName + ": Corruption detected in block " + index + ", retrying...");
                    continue;
                }

                boolean ok = client.updateChecksumWithLock(cs, threadName, index);
                if (!ok) {
                    client.getLogsQueue().add(
                            new LogEvent(threadName, index, System.currentTimeMillis(), LogEventType.WORKER_SEEN_ABORT, 0, -1)
                    );
                }

                break;

            } catch (Exception e) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        latch.countDown();
    }

    private int checksum(byte[] data) {
        int c = 0;
        for (byte b : data) c ^= (b & 0xFF);
        return c;
    }
}
