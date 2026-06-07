import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Thread> lockOwner = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Thread, String> waitingFor = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Thread, Long> waitTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Thread, Set<String>> ownedLocks = new ConcurrentHashMap<>();

    public LockManager() {
        System.out.println("============================================================");
        System.out.println("LockManager Initialized");
        System.out.println("============================================================");

        Thread monitor = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1500);
                    DeadlockDetection();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        monitor.setDaemon(true);
        monitor.start();
    }

    public void createLock(String name) {
        locks.putIfAbsent(name, new ReentrantLock());
        System.out.println("[LockManager] Lock '" + name + "' created.");
    }

    public void acquireLock(String name) throws InterruptedException {
        ReentrantLock lock = locks.get(name);
        Thread current = Thread.currentThread();

        System.out.println("[" + current.getName() + "] Trying to acquire lock '" + name + "'...");

        waitingFor.put(current, name);
        waitTime.putIfAbsent(current, System.nanoTime());

        try {
            lock.lockInterruptibly();

            waitingFor.remove(current);
            lockOwner.put(name, current);
            ownedLocks.computeIfAbsent(current, k -> ConcurrentHashMap.newKeySet()).add(name);
            System.out.println("[" + current.getName() + "] Acquired lock '" + name + "'.");
        } catch (InterruptedException e) {
            waitingFor.remove(current);
            throw e;
        }
    }

    public void releaseLock(String name) {
        ReentrantLock lock = locks.get(name);
        Thread t = Thread.currentThread();

        if (lock.isHeldByCurrentThread()) {
            ownedLocks.getOrDefault(t, Collections.emptySet()).remove(name);
            lockOwner.remove(name);
            lock.unlock();
            System.out.println("[" + t.getName() + "] Released lock '" + name + "'.");
        }
    }

    public void DeadlockDetection() {
        for (Thread t : waitingFor.keySet()) {
            if (hasCycle(t, new HashSet<>())) {
                System.out.println("============================================================");
                System.out.println("DEADLOCK DETECTED!");
                System.out.println("============================================================");
                System.out.println("Threads involved:");
                for (Thread victim : waitingFor.keySet()) {
                    System.out.println("- " + victim.getName() + " | Holding: " +
                            ownedLocks.getOrDefault(victim, Set.of()) +
                            " | Waiting for: " + waitingFor.get(victim));
                }
                DeadlockCorrection();
                break;
            }
        }
    }

    private boolean hasCycle(Thread current, Set<Thread> visited) {
        if (!visited.add(current)) return true;
        String lockName = waitingFor.get(current);
        if (lockName == null) return false;
        Thread owner = lockOwner.get(lockName);
        if (owner == null) return false;
        return hasCycle(owner, visited);
    }

    public void DeadlockCorrection() {
        System.out.println("============================================================");
        System.out.println("Resolving deadlock...");

        List<Thread> waiters = new ArrayList<>(waitingFor.keySet());
        waiters.sort(Comparator.comparingLong(waitTime::get));

        System.out.println("Order of resolution (last to sleep first):");
        for (int i = waiters.size() - 1; i >= 0; i--) {
            System.out.println((waiters.size() - i) + ". " + waiters.get(i).getName());
        }

        for (int i = waiters.size() - 1; i >= 0; i--) {
            Thread victim = waiters.get(i);
            String neededLock = waitingFor.get(victim);
            Thread owner = lockOwner.get(neededLock);

            if (owner != null) {
                System.out.println("Transferring lock '" + neededLock + "' from " + owner.getName() + " to " + victim.getName());
            }

            victim.interrupt();

            try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public void shutdown() {
        locks.clear();
    }
}