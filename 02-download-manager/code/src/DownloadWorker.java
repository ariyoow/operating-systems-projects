import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class DownloadWorker implements Runnable {
    private final FileServer fileServer;
    private final String fileName;
    private final int startIndex;
    private final int endIndex;
    private final BlockingQueue<Integer> queue;
    private final List<Block> sharedMemory;
    private final boolean verbose;

    private final List<Block> memory = new ArrayList<>();
    private final CountDownLatch latch;

    public DownloadWorker(FileServer fileServer,
                          String fileName,
                          int startIndex,
                          int endIndex,
                          BlockingQueue<Integer> queue,
                          List<Block> sharedMemory,
                          boolean verbose,
                          CountDownLatch latch) {

        this.fileServer = fileServer;
        this.fileName = fileName;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.queue = queue;
        this.sharedMemory = sharedMemory;
        this.verbose = verbose;
        this.latch = latch;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
            download(i);
        }
    }

    private void download(int index) {
        String threadName = Thread.currentThread().getName();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (verbose)
                    System.out.println(threadName + ": Processing block " + index + "...");

                Block b = fileServer.get(fileName, index);
                int c = checksum(b.data());

                if (c != b.checksum()) {
                    if (verbose)
                        System.out.println(threadName + ": CORRUPTION DETECTED in block " + index + ". Retrying...");
                    continue;
                }

                memory.add(b);

                try {
                    sharedMemory.set(index, b);
                } catch (Exception ignored) {}

                queue.put(b.checksum());

                if (verbose)
                    System.out.println(threadName + ": Successfully verified and stored block " + index + ".");

                break;

            } catch (Exception e) {
                try { Thread.sleep(5); }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        latch.countDown();
    }

    private int checksum(byte[] data) {
        // Calculate checksum using XOR across all bytes
        // Each byte is masked with 0xFF to convert to unsigned 0-255 value
        // Final result is one byte (0-255) representing data checksum
        int cs = 0;
        for (byte b : data) cs ^= (b & 0xFF);
        return cs;
    }

    public List<Block> getMemory() {
        return memory;
    }
}
