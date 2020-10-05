package lando.systems.ld47.entities;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.Audio;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class Opponent {

    public enum State {
        idle, moving, stun, ramming
    }

    public enum Direction {
        right, left
    }

    public enum AnimDirection {
        level, up, down
    }

    public GameScreen screen;

    private float animationTime = 0;
    private float idleTime = 0;
    private float offsetY = 0; // for idle hovering

    public final Vector2 position = new Vector2();
    public Vector2 size = new Vector2();
    public Direction direction = Direction.right;

    private State state = State.idle;

    private Animation<TextureRegion> animation = null;
    private final SassiAI ai;

    public AnimDirection animState = AnimDirection.level;

    private boolean isShooting = false;

    private final Vector2 shotOffset;
    public final float carScale = 0.5f;

    public Opponent(GameScreen screen) {
        this.screen = screen;
        setState(State.idle);

        TextureRegion texture = screen.assets.car.getKeyFrame(0);
        size.set(new Vector2(texture.getRegionWidth() * carScale, texture.getRegionHeight() * carScale));
        shotOffset = new Vector2(41, 39).scl(carScale);

        this.ai = new SassiAI(screen, this);
    }

    public void setState(State state) {
        this.state = state;
        switch (state) {
            case stun:
                animation = screen.assets.carStun;
                break;
            case moving:
                lastX = position.x;
                lastY = position.y;
                animation = screen.assets.car;
                break;
            default:
                animation = (isShooting) ? screen.assets.carShoot : screen.assets.car;
        }
        animationTime = 0;
    }

    private float lastX = -1;
    private float lastY = -1;
    public void update(float dt) {
        animationTime += dt;

        if (state == State.idle) {
            idleTime += dt;
            offsetY = MathUtils.sin(idleTime * 2f) * 5;
        } else if (state == State.moving) {
            if (position.x != lastX) {
                direction = (position.x > lastX) ? Direction.right : Direction.left;
            }
            setAnimState();
            lastY = position.y;
            lastX = position.x;

            switch (animState) {
                case up:
                    animation = screen.assets.carUp;
                    break;
                case down:
                    animation = screen.assets.carDown;
                    break;
                default:
                    animation = screen.assets.car;
            }
        } else if (isShooting) {
            animation = screen.assets.carShoot;
            isShooting = !animation.isAnimationFinished(animationTime);
        }

        if (fireball != null) {
            fireball.update(dt);
        }


        ai.update(dt);
    }

    private void setAnimState() {
        float dy = position.y - lastY;
        if (Math.abs(dy) < 2f) {
            animState = AnimDirection.level;
        } else {
            animState = (dy) > 0 ? AnimDirection.up : AnimDirection.down;
        }
    }

    public void render(SpriteBatch batch) {
        if (animation != null) {

            batch.setColor(Color.WHITE);
            if (fireball != null) {
                fireball.render(batch);
            }
            // batch.draw(screen.assets.whitePixel, position.x, position.y + offsetY, size.x, size.y);

            TextureRegion texture = animation.getKeyFrame(animationTime);
            batch.draw(texture, position.x, position.y + offsetY, size.x / 2, size.y / 2, size.x, size.y,
                    (direction == Direction.right) ? -1 : 1, 1, 0);
        }
    }

    public Vector2 getFireballCenter() {
        float x = (direction == Direction.left) ? shotOffset.x : size.x - shotOffset.x;
        return new Vector2(position.x + x, position.y + offsetY + shotOffset.y);
    }

    public float getWidth() {
        return (animation != null) ? animation.getKeyFrame(0).getRegionWidth() : 0;
    }

    private Fireball fireball;
    private IShootable target;
    public void shoot(IShootable target) {
        if (isShooting) {
            return;
        }

        this.target = target;
        animationTime = 0;
        isShooting = true;
        screen.playSound(Audio.Sounds.dec_shoot);

        Vector2 t = target.getTarget();
        fireball = new Fireball(this, t);

        float duration = Math.abs(t.x - fireball.position.x) / 200;

        Tween.to(fireball.position, Vector2Accessor.XY, duration)
                .target(t.x, t.y)
                .start(screen.tween)
                .setCallback((i, s)-> {
                    screen.playSound(Audio.Sounds.fireballExplosion);
                    target.hit();
                    fireball = null;
                    isShooting = false;
                });
    }
}
