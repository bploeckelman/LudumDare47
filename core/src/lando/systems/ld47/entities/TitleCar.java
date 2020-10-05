package lando.systems.ld47.entities;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.Game;

public class TitleCar {

    public enum State {
        idle, moving, stun, ramming
    }

    public enum Direction {
        right, left
    }

    public enum AnimDirection {
        level, up, down
    }

    public Game game;

    private float animationTime = 0;
    private float idleTime = 0;
    private float offsetY = 0; // for idle hovering

    public final Vector2 position = new Vector2();
    public Vector2 size = new Vector2();
    public Direction direction = Direction.right;

    private State state = State.idle;

    private Animation<TextureRegion> animation = null;

    public AnimDirection animState = AnimDirection.level;

    public final float carScale = 0.7f;

    public TitleCar(Game game) {
        this.game = game;
        TextureRegion texture = game.assets.car.getKeyFrame(0);
        size.set(new Vector2(texture.getRegionWidth() * carScale, texture.getRegionHeight() * carScale));
        animation = game.assets.car;
    }

    private float lastY = -1;
    private float lastX = -1;
    public void update(float dt) {
        animationTime += dt;

        if ((position.x - lastX) < 10) {
            idleTime += dt;
            offsetY = MathUtils.sin(idleTime * 2f) * 5;
        }


        if (position.x != lastX) {
            direction = (position.x > lastX) ? Direction.right : Direction.left;
        }
        setAnimState();
        lastY = position.y;
        lastX = position.x;

        switch (animState) {
            case up:
                animation = game.assets.carUp;
                break;
            case down:
                animation = game.assets.carDown;
                break;
            default:
                animation = game.assets.car;
        }
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

            TextureRegion texture = animation.getKeyFrame(animationTime);
            batch.draw(texture, position.x, position.y + offsetY, size.x / 2, size.y / 2, size.x, size.y,
                    (direction == Direction.right) ? -1 : 1, 1, 0);
        }
    }

    public float getWidth() {
        return (animation != null) ? animation.getKeyFrame(0).getRegionWidth() : 0;
    }
}
