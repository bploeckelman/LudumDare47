package lando.systems.ld47.particles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

public class ParticleDecal implements Pool.Poolable {

    public Decal decal;
    private boolean billboard;
    private Interpolation interpolation;

    private float x;
    private float y;
    private float z;

    private float xVel;
    private float yVel;
    private float zVel;

    private float xAccel;
    private float yAccel;
    private float zAccel;

    private float accDamp;

    private float rStart;
    private float gStart;
    private float bStart;
    private float aStart;

    private float rEnd;
    private float gEnd;
    private float bEnd;
    private float aEnd;

    private float widthStart;
    private float widthEnd;
    private float heightStart;
    private float heightEnd;

    private float ttl;
    private float maxTTL;
    public boolean ready;


    private ParticleDecal(){
    }

    private void setTexture(TextureRegion texture) {
        this.decal = Decal.newDecal(texture, true);
    }

    public void update(float dt, Camera camera) {

        float lifetime, progress;
        ttl -= dt;
        lifetime = MathUtils.clamp(ttl / maxTTL , 0f, 1f);
        progress = interpolation.apply(0f, 1f, MathUtils.clamp(1f - lifetime, 0f, 1f));

        xAccel *= accDamp;
        yAccel *= accDamp;
        zAccel *= accDamp;
        if (MathUtils.isEqual(xAccel, 0f, 0.01f)) xAccel = 0f;
        if (MathUtils.isEqual(yAccel, 0f, 0.01f)) yAccel = 0f;
        if (MathUtils.isEqual(zAccel, 0f, 0.01f)) zAccel = 0f;

        xVel += xAccel * dt;
        yVel += yAccel * dt;
        zVel += zAccel * dt;

        x += xVel * dt;
        y += yVel * dt;
        z += zVel * dt;

        float width  = MathUtils.lerp(widthStart,  widthEnd,  progress);
        float height = MathUtils.lerp(heightStart, heightEnd, progress);

        float r = MathUtils.lerp(rStart, rEnd, progress);
        float g = MathUtils.lerp(gStart, gEnd, progress);
        float b = MathUtils.lerp(bStart, bEnd, progress);
        float a = MathUtils.lerp(aStart, aEnd, progress);

        decal.setPosition(x, y, z);
        decal.setDimensions(width, height);
        decal.setColor(r, g, b, a);

        if (billboard){
            decal.lookAt(camera.position, camera.up);
        }
        ready = true;
    }

    public boolean isAlive(){
        return ttl > 0;
    }

    @Override
    public void reset() {
        decal = null;
        ready = false;
    }

    public static class Builder {

        private TextureRegion texture;
        private boolean billboard = true;
        private Interpolation interpolation = Interpolation.linear;
        private float x = 0;
        private float y = 0;
        private float z = 0;

        private float xVel = 0;
        private float yVel = 0;
        private float zVel = 0;

        private float xAccel = 0;
        private float yAccel = 0;
        private float zAccel = 0;

        private float accDamp = 0;

        private float rStart = 0;
        private float gStart = 0;
        private float bStart = 0;
        private float aStart = 0;

        private float rEnd = 0;
        private float gEnd = 0;
        private float bEnd = 0;
        private float aEnd = 0;

        private float widthStart = 1;
        private float widthEnd = 1;
        private float heightStart = 1;
        private float heightEnd = 1;

        private float ttl = 0;
        private float maxTTL = 0;
        private ParticleDecal particle;

        public Builder(ParticleDecal particle) {
            this.particle = particle;
            this.particle.reset();
        }

        public Builder texture(TextureRegion texture){
            this.texture = texture;
            return this;
        }

        public Builder interpolation(Interpolation interpolation) {
            this.interpolation = interpolation;
            return this;
        }

        public Builder pos(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Builder vel(float x, float y, float z) {
            this.xVel = x;
            this.yVel = y;
            this.zVel = z;
            return this;
        }

        public Builder acc(float x, float y, float z) {
            this.xAccel = x;
            this.yAccel = y;
            this.zAccel = z;
            return this;
        }

        public Builder dampening(float accDamp) {
            this.accDamp = accDamp;
            return this;
        }

        public Builder colorStart(float r, float g, float b, float a) {
            this.rStart = r;
            this.gStart = g;
            this.bStart = b;
            this.aStart = a;
            return this;
        }

        public Builder colorStart(Color c) {
            return colorStart(c.r, c.g, c.b, c.a);
        }

        public Builder colorEnd(float r, float g, float b, float a) {
            this.rEnd = r;
            this.gEnd = g;
            this.bEnd = b;
            this.aEnd = a;
            return this;
        }

        public Builder colorEnd(Color c) {
            return colorEnd(c.r, c.g, c.b, c.a);
        }

        public Builder startSize(float width, float height) {
            this.widthStart = width;
            this.heightStart = height;
            return this;
        }

        public Builder endSize(float width, float height) {
            this.widthEnd = width;
            this.heightEnd = height;
            return this;
        }

        public Builder time(float time) {
            this.ttl = time;
            this.maxTTL = time;
            return this;
        }

        public ParticleDecal build() {
            particle.setTexture(texture);
            particle.interpolation = this.interpolation;
            particle.x = this.x;
            particle.y = this.y;
            particle.z = this.z;
            particle.xVel = this.xVel;
            particle.yVel = this.yVel;
            particle.zVel = this.zVel;
            particle.xAccel = this.xAccel;
            particle.yAccel = this.yAccel;
            particle.zAccel = this.zAccel;
            particle.accDamp = this.accDamp;
            particle.rStart = this.rStart;
            particle.gStart = this.gStart;
            particle.bStart = this.bStart;
            particle.aStart = this.aStart;
            particle.rEnd = this.rEnd;
            particle.gEnd = this.gEnd;
            particle.bEnd = this.bEnd;
            particle.aEnd = this.aEnd;
            particle.widthStart = this.widthStart;
            particle.widthEnd = this.widthEnd;
            particle.heightStart = this.heightStart;
            particle.heightEnd = this.heightEnd;
            particle.ttl = this.ttl;
            particle.maxTTL = this.maxTTL;
            particle.billboard = this.billboard;

            return particle;
        }
    }

}
