public class Main {
    public static void main(String[] args) {
        long studentId = 40241963L;

        FileServer fileServer = new FileServer(studentId);
//        DownloadClient downloadClient = new DownloadClient(fileServer);
//
//        try {
//            downloadClient.download("large.txt", 32);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println("\n[2] Running Benchmarks...");
        DownloadBenchmark benchmark = new DownloadBenchmark();

        DownloadBenchmark.run(fileServer, "small.txt");
        DownloadBenchmark.run(fileServer, "medium.txt");
        DownloadBenchmark.run(fileServer, "large.txt");
        DownloadBenchmark.run(fileServer, "big.txt");
    }
}
