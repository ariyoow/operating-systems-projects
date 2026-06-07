public class Main {
    static final int N = 10_000_000;
    static final int STEPS = 100;

    public static void main(String[] args) {
//        simpleRun();
//        benchmarkMaxN();
        benchmarkMaxNSeparate();
    }
    public static void simpleRun(){
        ParticleSimSOA soa = new ParticleSimSOA(N);
        ParticleSimAOS aos = new ParticleSimAOS(N);

        // ---- Warm-up ----
        for (int i = 0; i < 5; i++) {
            soa.updatePosition(0.01f);
            aos.updatePosition(0.01f);
        }

        // ---- AOS benchmark ----
        long start = System.nanoTime();
        for (int i = 0; i < STEPS; i++) {
            aos.updatePosition(0.01f);
        }
        long end = System.nanoTime();
        double aosTime = (end - start) / 1e9;

        // ---- SOA benchmark ----
        start = System.nanoTime();
        for (int i = 0; i < STEPS; i++) {
            soa.updatePosition(0.01f);
        }
        end = System.nanoTime();
        double soaTime = (end - start) / 1e9;

        System.out.println("AOS time: " + aosTime + " s");
        System.out.println("SOA time: " + soaTime + " s");
        System.out.println("Time Difference: "+ (aosTime - soaTime) + "s");

        //Prevent of Dead Code Elimination (DCE)
        System.out.println("Sum of x's value in both soa and aos" + (soa.particle.x[0] + aos.particles[0].x));

    }
    public static void benchmarkMaxN() {
        final int STEP_N = 1_000_000;
        final double MAX_TIME_SEC = 5;
        int n = STEP_N;

        while (true) {
            try {
                System.out.println("Testing N = " + n);

                ParticleSimSOA soa = new ParticleSimSOA(n);
                ParticleSimAOS aos = new ParticleSimAOS(n);

                // Warm-up
                for (int i = 0; i < 5; i++) {
                    soa.updatePosition(0.01f);
                    aos.updatePosition(0.01f);
                }

                long start, end;
                double aosTime, soaTime;

                // AOS timing
                start = System.nanoTime();
                for (int i = 0; i < STEPS; i++) {
                    aos.updatePosition(0.01f);
                }
                end = System.nanoTime();
                aosTime = (end - start) / 1e9;

                // SOA timing
                start = System.nanoTime();
                for (int i = 0; i < STEPS; i++) {
                    soa.updatePosition(0.01f);
                }
                end = System.nanoTime();
                soaTime = (end - start) / 1e9;

                // Prevent Dead Code Elimination
                System.out.println("Sum check: " + (soa.particle.x[0] + aos.particles[0].x));
                System.out.println("AOS time: " + aosTime + " s, SOA time: " + soaTime + " s");
                System.out.println("-------------------------------");

                // اگر زمان طولانی شد، به معنی practical limit (cache/memory) است
                if (aosTime > MAX_TIME_SEC || soaTime > MAX_TIME_SEC) {
                    System.out.println("Reached practical limit at N = " + n);
                    break;
                }

                n += STEP_N;

            } catch (OutOfMemoryError e) {
                System.out.println("OutOfMemoryError at N = " + n);
                break;
            }
        }
    }
    public static void benchmarkMaxNSeparate() {
        final int STEP_N = 1_000_000;
        final double MAX_TIME_SEC = 5; // practical limit per method

        System.out.println("=== Finding max N for AoS ===");
        int n = STEP_N;
        while (true) {
            try {
                ParticleSimAOS aos = new ParticleSimAOS(n);

                // Warm-up
                for (int i = 0; i < 5; i++) {
                    aos.updatePosition(0.01f);
                }

                // Timing
                long start = System.nanoTime();
                for (int i = 0; i < STEPS; i++) {
                    aos.updatePosition(0.01f);
                }
                long end = System.nanoTime();
                double time = (end - start) / 1e9;

                System.out.println("N = " + n + ", Time = " + time + " s, Sum check = " + aos.particles[0].x);

                if (time > MAX_TIME_SEC) {
                    System.out.println("Practical limit reached for AoS at N = " + n);
                    break;
                }

                n += STEP_N;

            } catch (OutOfMemoryError e) {
                System.out.println("OutOfMemoryError for AoS at N = " + n);
                break;
            }
        }

        System.out.println("\n=== Finding max N for SoA ===");
        n = STEP_N * 5;
        while (true) {
            try {
                ParticleSimSOA soa = new ParticleSimSOA(n);

                // Warm-up
                for (int i = 0; i < 5; i++) {
                    soa.updatePosition(0.01f);
                }

                // Timing
                long start = System.nanoTime();
                for (int i = 0; i < STEPS; i++) {
                    soa.updatePosition(0.01f);
                }
                long end = System.nanoTime();
                double time = (end - start) / 1e9;

                System.out.println("N = " + n + ", Time = " + time + " s, Sum check = " + soa.particle.x[0]);

                if (time > MAX_TIME_SEC) {
                    System.out.println("Practical limit reached for SoA at N = " + n);
                    break;
                }

                n += STEP_N * 5;

            } catch (OutOfMemoryError e) {
                System.out.println("OutOfMemoryError for SoA at N = " + n);
                break;
            }
        }
    }

}
