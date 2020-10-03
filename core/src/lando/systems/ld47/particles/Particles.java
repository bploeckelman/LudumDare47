package lando.systems.ld47.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.*;
import lando.systems.ld47.Assets;

public class Particles implements Disposable {

    public enum Layer { back, front, overlay }

    private final Assets assets;

    private final ObjectMap<Layer, Array<Particle>> activeParticles;

    private final Pool<Particle> particlePool = Pools.get(Particle.class, 3000);

    public Particles(Assets assets) {
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
    public void addPieceDeleteParticles(float x, float y, Color c) {
        int sparks = 100;
        for (int i = 0; i < sparks; i++) {
            float speed = MathUtils.random(10, 80);
            float dir = MathUtils.random(360);
            activeParticles.get(Layer.front).add(Particle.initializer(particlePool.obtain())
                .keyframe(assets.whitePixel)
                    .startPos(x, y)
                    .velocity(MathUtils.cosDeg(dir) *speed, MathUtils.sinDeg(dir) * speed)
                    .startSize(3, 3)
                    .endSize(1,1)
                    .startColor(c)
                    .endAlpha(0)
                    .timeToLive(.5f)
                .init());
        }
    }

}
