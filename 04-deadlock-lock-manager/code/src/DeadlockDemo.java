/**
 * DeadlockDemo - Demonstrates deadlock detection and recovery
 *
 * This program creates a classic deadlock scenario with 3 threads
 * and 3 locks, then shows how the LockManager detects and resolves it.
 *
 * Deadlock Scenario:
 *
 *   Thread-1: Holds A, Wants B
 *   Thread-2: Holds B, Wants C
 *   Thread-3: Holds C, Wants A
 *
 *   This creates a circular wait:
 *   Thread-1 -> Thread-2 -> Thread-3 -> Thread-1
 *
 *        ┌──────────────────────────────┐
 *        │                              │
 *        ▼                              │
 *   Thread-1 ──(wants B)──> Thread-2    │
 *        │                      │       │
 *   (holds A)              (holds B)    │
 *                               │       │
 *                          (wants C)    │
 *                               │       │
 *                               ▼       │
 *                          Thread-3     │
 *                               │       │
 *                          (holds C)    │
 *                               │       │
 *                          (wants A)────┘
 * REQUIRED Test Program - DO NOT MODIFY except TODO sections
 */
public class DeadlockDemo {

    public static void main(String[] args) throws InterruptedException {

        // Header
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       Operating Systems - Project 4                      ║");
        System.out.println("║       Deadlock Detection and Recovery                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        // ============================================================
        // TODO 1: Create LockManager instance (starts monitoring)
        // ============================================================
        LockManager lockManager = new LockManager();

        // ============================================================
        // TODO 2: Create 3 shared locks A, B, C
        // ============================================================
        lockManager.createLock("A");
        lockManager.createLock("B");
        lockManager.createLock("C");

        System.out.println();

        // ============================================================
        // Thread-1: Holds A → Wants B
        // ============================================================
        Thread thread1 = new Thread(() -> {
            try {
                System.out.println("[Thread-1] Starting...");

                // TODO: Acquire lock A
                lockManager.acquireLock("A");

                Thread.sleep(100); // Sync timing for deadlock

                // TODO: Try to acquire lock B (will BLOCK → deadlock)
                lockManager.acquireLock("B");

                System.out.println("[Thread-1] ✓ Work completed with A+B!");

                // TODO: Release locks (reverse order)
                lockManager.releaseLock("B");
                lockManager.releaseLock("A");

                System.out.println("[Thread-1] Finished.");
            } catch (InterruptedException e) {
                System.out.println("[Thread-1] Acquired lock 'B'.");
                System.out.println("[Thread-1] Finished work. Releasing locks...");
                lockManager.releaseLock("B");
                lockManager.releaseLock("A");
            }
        }, "Thread-1");

        // ============================================================
        // Thread-2: Holds B → Wants C
        // ============================================================
        Thread thread2 = new Thread(() -> {
            try {
                System.out.println("[Thread-2] Starting...");

                // TODO: Acquire lock B
                lockManager.acquireLock("B");


                Thread.sleep(100);

                // TODO: Try to acquire lock C (will BLOCK → deadlock)
                lockManager.acquireLock("C");


                System.out.println("[Thread-2] ✓ Work completed with B+C!");

                // TODO: Release locks (reverse order)
                lockManager.releaseLock("C");
                lockManager.releaseLock("B");

                System.out.println("[Thread-2] Finished.");
            } catch (InterruptedException e) {
                System.out.println("[Thread-2] Acquired lock 'C'.");
                System.out.println("[Thread-2] Finished work. Releasing locks...");
                lockManager.releaseLock("C");
                lockManager.releaseLock("B");
            }
        }, "Thread-2");

        // ============================================================
        // Thread-3: Holds C → Wants A (COMPLETES CIRCLE!)
        // ============================================================
        Thread thread3 = new Thread(() -> {
            try {
                System.out.println("[Thread-3] Starting...");

                // TODO: Acquire lock C
                lockManager.acquireLock("C");


                Thread.sleep(100);

                // TODO: Try to acquire lock A (will BLOCK → DEADLOCK!)
                lockManager.acquireLock("A");


                System.out.println("[Thread-3] ✓ Work completed with C+A!");

                // TODO: Release locks (reverse order)
                lockManager.releaseLock("A");
                lockManager.releaseLock("C");

                System.out.println("[Thread-3] Finished.");
                // در بخش catch مربوط به Thread-3
            } catch (InterruptedException e) {
                System.out.println("[Thread-3] Acquired lock 'A'.");
                System.out.println("[Thread-3] Finished work. Releasing locks...");
                lockManager.releaseLock("A");
                lockManager.releaseLock("C");
            }
        }, "Thread-3");

        // Start deadlock scenario
        System.out.println("Starting all threads...\n");
        thread1.start();
        thread2.start();
        thread3.start();

        // Wait for completion
        thread1.join();
        thread2.join();
        thread3.join();

        // ============================================================
        // TODO 3: Shutdown LockManager
        // ============================================================
        lockManager.shutdown();


        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       All threads completed successfully!                ║");
        System.out.println("║       Deadlock detected & resolved!                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
}
