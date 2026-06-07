import java.util.*;
import java.util.concurrent.*;

public class DownloadClient {

    private final FileServer fileServer;
    private boolean verbose;

    public DownloadClient(FileServer fileServer) {
        this.fileServer = fileServer;
        this.verbose = true;
    }

    public long download(String fileName, int threadCount) throws InterruptedException {

        FileServer.FileInfo info = findFile(fileName);
        if (info == null) {
            System.out.println("File not found.");
            return 0L;
        }

        int totalBlocks = info.getBlockCount();
        int fileChecksum = info.getFileChecksum();

        if (verbose)
            System.out.println("Downloading " + fileName + " with " + threadCount + " threads...\n");

        long startTime = System.currentTimeMillis();

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        List<Block> sharedMemory = new ArrayList<>(Collections.nCopies(totalBlocks, null));

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(totalBlocks);

        int perThread = totalBlocks / threadCount;
        int rem = totalBlocks % threadCount;

        int index = 0;

        for (int i = 0; i < threadCount; i++) {
            int share = perThread + (i < rem ? 1 : 0);

            int start = index;
            int end = start + share;
            index = end;

            if (share == 0) continue;

            DownloadWorker w = new DownloadWorker(
                    fileServer,
                    fileName,
                    start,
                    end,
                    queue,
                    sharedMemory,
                    verbose,
                    latch
            );

            executor.execute(w);
        }

        latch.await();
        executor.shutdownNow();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        List<Integer> all = new ArrayList<>();
        queue.drainTo(all);

        if (all.size() != totalBlocks) {
            if (verbose)
                System.out.println("VALIDATION FAILED");
            return duration;
        }

        int agg = 0;
        for (int c : all) agg ^= c;

        if (verbose) {
            System.out.println("\nDownload completed in " + duration + " ms");
            System.out.println(agg == fileChecksum ? "File checksum: Valid" : "File checksum: Invalid");
        }

        return duration;
    }

    private FileServer.FileInfo findFile(String name) {
        for (FileServer.FileInfo f : fileServer.list()) {
            if (f.getFileName().equals(name)) return f;
        }
        return null;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
