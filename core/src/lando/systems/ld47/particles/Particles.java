package lando.systems.ld47.particles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import lando.systems.ld47.Assets;
import lando.systems.ld47.entities.Tetrad;
import lando.systems.ld47.entities.TetradPiece;
import lando.systems.ld47.utils.SimplePath;

public class Particles implements Disposable {

    public enum Layer { back, front, overlay }

    private final Assets assets;

    private final ObjectMap<Layer, Array<Particle>> activeParticles;

    private final Pool<Particle> particlePool = Pools.get(Particle.class, 3000);

    public final Array<ParticleDecal> activeDecals;
    private final Pool<ParticleDecal> decalPool = Pools.get(ParticleDecal.class, 6000);

    public Particles(Assets assets) {
        this.assets = assets;
        this.activeParticles = new ObjectMap<>();
        this.activeParticles.put(Layer.back,  new Array<>(false, 1000));
        this.activeParticles.put(Layer.front, new Array<>(false, 1000));
        this.activeParticles.put(Layer.overlay,   new Array<>(false, 1000));

        this.activeDecals = new Array<>();

        for (int i = 0; i < 6000; i ++) {
            ParticleDecal d = new ParticleDecal();
            decalPool.free(d);
        }
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

    public void updateDecals(float dt, Camera camera) {
        for (int i = activeDecals.size -1; i >= 0; i--) {
            ParticleDecal decal = activeDecals.get(i);
            decal.update(dt, camera);
            if (!decal.isAlive()){
                activeDecals.removeIndex(i);
                decalPool.free(decal);
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

    public void addPiecePunchedParticles(float x, float y, Color c) {
        int sparks = 100;
        for (int i = 0; i < sparks; i++) {
            float speed = MathUtils.random(10, 200);
            float dir = MathUtils.random(360);
            activeParticles.get(Layer.front).add(Particle.initializer(particlePool.obtain())
                    .keyframe(assets.whitePixel)
                    .startPos(x, y)
                    .velocity(MathUtils.cosDeg(dir) * speed, MathUtils.sinDeg(dir) * speed)
                    .startSize(3, 3)
                    .endSize(1, 1)
                    .startColor(c)
                    .endAlpha(0)
                    .timeToLive(.5f)
                    .init());
        }
    }

    public void addTeleportParticles(Vector2 startPos, Vector2 endPoint) {
        int sparks = 100;
        tempColor.fromHsv(MathUtils.random(180f, 330f), 1f, 1f);
        for (int i = 0; i < sparks; i++) {
            float sX = startPos.x + MathUtils.random(-3, 3);
            float sY = startPos.y + MathUtils.random(-3, 3);
            SimplePath path = new SimplePath(false,
                    sX, sY,
                    sX, sY,
                    MathUtils.random(1000f, 1050f), MathUtils.random(300f, 350f),
                    endPoint.x, endPoint.y,
                    endPoint.x, endPoint.y);
            activeParticles.get(Layer.front).add(Particle.initializer(particlePool.obtain())
                    .keyframe(assets.whiteCircle)
                    .path(path)
                    .startColor(tempColor)
                    .startSize(MathUtils.random(1f, 4f), MathUtils.random(1f, 4f))
                    .endSize(1f, 1f)
                    .timeToLive(MathUtils.random(.2f, .7f))
                    .init());
        }
    }

    Color tempColor = new Color(Color.WHITE);
    public void addPlummetParticles(Tetrad tetrad, float startOriginY){
        tempColor.fromHsv(MathUtils.random(180f, 330f), 1f, 1f);
        for (TetradPiece point : tetrad.points){
            float x = point.x + tetrad.origin.x;
            float endY = point.y + tetrad.origin.y;
            float startY = point.y + startOriginY;
            float delay = .01f;
            for (float i = startY; i >= endY + 1; i -= .1f) {
                delay += .01f;
                for (int j = 0; j < 10; j++) {
                    activeDecals.add(ParticleDecal.builder(decalPool.obtain())
                            .texture(assets.whiteCircle)
                            .pos(x + MathUtils.random( 1f), i + MathUtils.random(-.05f, .05f), .99f)
                            .vel(MathUtils.random(-.1f, .1f), -1, 0)
                            .acc(0, 2f, 0)
                            .colorStart(tempColor)
                            .colorEnd(0f, 0f, 0f, 0f)
                            .startSize(.04f, .08f)
                            .endSize(0f, 0f)
                            .time( delay)
                            .build());
                }
            }
        }
    }

    public void pointIncreaseIndicator(float x, float y, float size, int point) {
        String pointsStr = Integer.toString(point);
        float velocityY = 30f;
        float startSize = size;
        float ttl = 2f;
        for (int i = 0; i < pointsStr.length(); ++i) {
            activeParticles.get(Layer.overlay).add(Particle.initializer(particlePool.obtain())
                    .keyframe(assets.fontPoints.get(Character.digit(pointsStr.charAt(i), 10)).getKeyFrames()[0])
                    .timeToLive(ttl)
                    .startPos(x + i * (startSize - 3f), y)
                    .velocity(0f, velocityY)
                    .startSize(startSize, startSize)
                    //.endSize(5f, 5f)
//                            .interpolation(Interpolation.pow5Out)
                    .interpolation(Interpolation.exp5Out)
                    .startColor(Color.RED)
                    .endColor(Color.TEAL)
                    .startAlpha(1f)
                    .endAlpha(0.1f)
                    .init());
        }
    }
}
