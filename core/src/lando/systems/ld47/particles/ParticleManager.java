package lando.systems.ld47.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.*;

public class ParticleManager implements Disposable {

    public enum Layer { back, front, overlay }

    private final Assets assets;

    private final ObjectMap<Layer, Array<Particle>> activeParticles;

    private final Pool<Particle> particlePool = Pools.get(Particle.class, 3000);

    public ParticleManager(Assets assets) {
        this.assets = assets;
        this.activeParticles = new ObjectMap<>();
        this.activeParticles.put(Layer.back,  new Array<>(false, 1000));
        this.activeParticles.put(Layer.front, new Array<>(false, 1000));
        this.activeParticles.put(Layer.overlay,   new Array<>(false, 1000));
    }

    public void update(float dt) {
        for (Layer layer : Layer.values()) {
            for (int i = activeParticles.get(layer).size - 1; i >= 0; --i) {
                Particle particle = activeParticles.get(layer).get(i);
                particle.update(dt);
                if (particle.isDead()) {
                    activeParticles.get(layer).removeIndex(i);
                    particlePool.free(particle);
                }
            }
        }
    }

    public void draw(SpriteBatch batch, Layer layer) {
        activeParticles.get(layer).forEach(particle -> particle.draw(batch));
    }

    public void clear() {
        for (Layer layer : Layer.values()) {
            particlePool.freeAll(activeParticles.get(layer));
            activeParticles.get(layer).clear();
        }
    }

    @Override
    public void dispose() {
        clear();
    }

    // ------------------------------------------------------------------------

    // add spawner methods here, or be smart and extract spawners out to their own classes

}
