import java.util.concurrent.atomic.AtomicInteger;


public class TicketLock {
    private final AtomicInteger nextTicket = new AtomicInteger(0);
    private final AtomicInteger serving = new AtomicInteger(0);

    public int getTicket() {
        return nextTicket.getAndIncrement();
    }


    public boolean waitTurn(int ticket, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        if (serving.get() == ticket) return true;

        synchronized (this) {
            while (true) {
                if (serving.get() == ticket) return true;
                long now = System.currentTimeMillis();
                long remaining = deadline - now;
                if (remaining <= 0) return false;
                this.wait(Math.min(remaining, 200));
            }
        }
    }


    public void release() {
        serving.incrementAndGet();
        synchronized (this) {
            this.notifyAll();
        }
    }

    public int getServing() {
        return serving.get();
    }


    public int getNextTicket() {
        return nextTicket.get();
    }
}
