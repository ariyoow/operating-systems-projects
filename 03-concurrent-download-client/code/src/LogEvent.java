public class LogEvent {
    public final String workerId;
    public final int blockIndex;
    public final long timestamp;
    public final LogEventType eventType;
    public final long duration;
    public final int ticket; // -1 if not applicable / unknown

    public LogEvent(String workerId, int blockIndex, long timestamp, LogEventType eventType, long duration, int ticket) {
        this.workerId = workerId;
        this.blockIndex = blockIndex;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.duration = duration;
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "[" + workerId
                + ", " + blockIndex
                + ", " + timestamp
                + ", " + eventType
                + ", " + duration
                + ", " + ticket
                + "]";
    }
}
