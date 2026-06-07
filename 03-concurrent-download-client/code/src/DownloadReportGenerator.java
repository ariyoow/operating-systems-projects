import java.util.*;

public class DownloadReportGenerator {

    public static void printReport(DownloadClient client, long duration, boolean verbose) {


        System.out.println("\n==================== REPORT ====================");

        // 1) Final checksum
        System.out.println("Final checksum: " + client.getSharedChecksum());

        // 2) Missing blocks
        List<Integer> missing = new ArrayList<>();
        client.getMissingBlocksQueue().drainTo(missing);
        Collections.sort(missing);
        System.out.println("Missing blocks: " + missing);

        // 3) Parse logs
        List<LogEvent> logs = new ArrayList<>();
        client.getLogsQueue().drainTo(logs);

        long totalWait = 0;
        int count = 0;
        int aborts = 0;

        for (LogEvent l : logs) {
            if (l.eventType == LogEventType.LOCK_RELEASED) {
                totalWait += l.duration;
                count++;
            }
            if (l.eventType == LogEventType.FINAL_ABORT) {
                aborts++;
            }
        }

        double avg = (count == 0) ? 0 : (double) totalWait / count;
        System.out.println("Average lock wait time: " + avg + " ms");
        System.out.println("Aborted blocks: " + aborts);

        if (verbose) {
            System.out.println("\nExecution Trace:");
            System.out.println("[worker, block, ts, event, duration, ticket]");
            for (LogEvent l : logs)
                System.out.println(l);
        }


        System.out.println("================================================\n");
    }
}
