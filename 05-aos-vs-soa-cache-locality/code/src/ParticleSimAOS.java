import java.util.Random;

public class ParticleSimAOS {

    Particle[] particles;
    Random rand;

    public ParticleSimAOS(int n) {
        particles = new Particle[n];
        rand = new Random();
        setParticles(n);
    }

    private void setParticles(int n) {
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(
                    rand.nextFloat() * n,
                    rand.nextFloat() * n,
                    rand.nextFloat() * n,
                    rand.nextFloat(),
                    rand.nextFloat(),
                    rand.nextFloat()
            );
        }
    }

    public void updatePosition(float dt) {
        for (Particle p : particles) {
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.z += p.vz * dt;
        }
    }

    static class Particle {
        float x, y, z;
        float vx, vy, vz;

        public Particle(float x, float y, float z,
                        float vx, float vy, float vz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
        }
    }
}
