public class Main {
    public static void main(String[] args) {

        long studentId = 40241963L;

        FileServer fs = new FileServer(studentId);
        DownloadClient client = new DownloadClient(fs);

        boolean verbose = true;
        client.setVerbose(verbose);

        try {
            long duration = client.download("small.txt");
            DownloadReportGenerator.printReport(client, duration, verbose);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
