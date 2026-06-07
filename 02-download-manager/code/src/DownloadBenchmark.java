public class DownloadBenchmark {

    public static void run(FileServer server, String fileName) {

        FileServer.FileInfo fileInfo = server.list().stream()
                .filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .orElse(null);

        System.out.println("-------------------------------------------");
        System.out.println("Benchmarking: " + fileName + " | Blocks: " + fileInfo.getBlockCount());
        System.out.println("-------------------------------------------");
        System.out.printf("| %-7s | %-10s | %-7s |\n", "Threads", "Time (ms)", "Speedup");
        System.out.println("-------------------------------------------");

        int[] threadCounts = {1, 2, 4, 8, 16, 24, 32, 40};
        long baselineTime = 0;

        for (int n : threadCounts) {

            DownloadClient client = new DownloadClient(server);
            client.setVerbose(false);

            long duration = 0;

            try {
                duration = client.download(fileName, n);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (n == 1) baselineTime = duration;
            double speedup = (double) baselineTime / duration;

            System.out.printf("| %-7d | %-10d | %-7.2fx |\n", n, duration, speedup);
        }
        System.out.println("-------------------------------------------");
    }
}
