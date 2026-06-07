import java.util.Random;

public class ParticleSimSOA {

    Particle particle;
    Random rand;

    public ParticleSimSOA(int n) {
        particle = new Particle(n);
        rand = new Random();
        setParticle(n);
    }

    private void setParticle(int n) {
        for (int i = 0; i < n; i++) {
            particle.x[i]  = rand.nextFloat() * n;
            particle.y[i]  = rand.nextFloat() * n;
            particle.z[i]  = rand.nextFloat() * n;

            particle.vx[i] = rand.nextFloat();
            particle.vy[i] = rand.nextFloat();
            particle.vz[i] = rand.nextFloat();
        }
    }

//     کدام یکی بهتره این یا بالایی؟
//    private void setParticle(int n){
//        for (int i = 0; i < n; i++) {
//            particle.x[i] = rand.nextInt(n - 1) + 1;
//        }
//        for (int i = 0; i < n; i++) {
//            particle.y[i] = rand.nextInt(n - 1) + 1;
//        }
//        for (int i = 0; i < n; i++) {
//            particle.z[i] = rand.nextInt(n - 1) + 1;
//        }
//        for (int i = 0; i < n; i++) {
//            particle.vx[i] = rand.nextInt(n - 1) + 1;
//        }
//        for (int i = 0; i < n; i++) {
//            particle.vy[i] = rand.nextInt(n - 1) + 1;
//        }
//        for (int i = 0; i < n; i++) {
//            particle.vz[i] = rand.nextInt(n - 1) + 1;
//        }
//    }


    public void updatePosition(float dt) {
        int n = particle.x.length;
        for (int i = 0; i < n; i++) {
            particle.x[i] += particle.vx[i] * dt;
            particle.y[i] += particle.vy[i] * dt;
            particle.z[i] += particle.vz[i] * dt;
        }
    }

//    public void updatePosition(float t){
//        for (int i = 0; i < particle.x.length; i++) {
//            particle.x[i] += particle.vx[i] * t;
//        }
//        for (int i = 0; i < particle.y.length; i++) {
//            particle.y[i] += particle.vy[i] * t;
//        }
//        for (int i = 0; i < particle.z.length; i++) {
//            particle.z[i] += particle.vz[i] * t;
//        }
//    }

    static class Particle {
        float[] x, y, z;
        float[] vx, vy, vz;

        public Particle(int n) {
            x  = new float[n];
            y  = new float[n];
            z  = new float[n];
            vx = new float[n];
            vy = new float[n];
            vz = new float[n];
        }
    }
}
