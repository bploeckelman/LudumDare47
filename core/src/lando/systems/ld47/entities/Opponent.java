package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.screens.GameScreen;

public class Opponent {

    public enum State {
        idle, moving, stun, punch
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

    public Opponent(GameScreen screen) {
        this.screen = screen;
        setState(State.idle);
        size.set(new Vector2(128, 32));
        this.ai = new SassiAI(screen, this);
    }

    public void setState(State state) {
        this.state = state;

        switch (state) {
            case punch:
                animation = screen.assets.sasquatch_punch;
                break;
            case stun:
                animation = screen.assets.sasquatch_stun;
                break;
            case moving:
                lastX = position.x;
                lastY = position.y;
                animation = screen.assets.sasquatch_idle;
                break;
            default:
                animation = screen.assets.sasquatch_idle;
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
                    animation = screen.assets.sasquatch_up;
                    break;
                case down:
                    animation = screen.assets.sasquatch_down;
                    break;
                default:
                    animation = screen.assets.sasquatch_idle;
            }
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
            TextureRegion texture = animation.getKeyFrame(animationTime);
            batch.draw(texture, position.x, position.y + offsetY, size.x / 2, size.y / 2, size.x, size.y,
                    (direction == Direction.right) ? -1 : 1, 1, 0);
        }
    }

    public float getWidth() {
        return (animation != null) ? animation.getKeyFrame(0).getRegionWidth() : 0;
    }
}
