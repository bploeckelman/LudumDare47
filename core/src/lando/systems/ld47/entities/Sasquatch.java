package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.screens.GameScreen;

public class Sasquatch {

    public enum SasquatchState {
        idle, throwing, stun, punch
    }

    public enum Direction {
        right, left
    }

    public GameScreen screen;

    private float animationTime = 0;

    public final Vector2 position = new Vector2();
    public Vector2 size = new Vector2();
    private Direction direction = Direction.right;

    private SasquatchState state = SasquatchState.idle;

    private Animation<TextureRegion> animation = null;
    private final SassiAI ai;

    public Sasquatch(GameScreen screen) {
        this.screen = screen;
        setState(SasquatchState.idle);
        size.set(new Vector2(128, 32));
        this.ai = new SassiAI(screen, this);
    }

    public void setState(SasquatchState state) {
        this.state = state;

        switch (state) {
            case throwing:
                animation = screen.assets.sasquatch_throw;
                break;
            case punch:
                animation = screen.assets.sasquatch_punch;
                break;
            case stun:
                animation = screen.assets.sasquatch_stun;
            default:
                animation = screen.assets.sasquatch;
        }

        animationTime = 0;
    }

    public void update(float dt) {
        animationTime += dt;

        ai.update(dt);
    }

    public void render(SpriteBatch batch) {

        if (animation != null) {
            //batch.setColor(Color.RED);
            //batch.draw(screen.assets.whitePixel, position.x, position.y, size.x, size.y);
            batch.setColor(Color.WHITE);
            TextureRegion texture = animation.getKeyFrame(animationTime);
            batch.draw(texture, position.x, position.y, size.x / 2, size.y / 2, size.x, size.y,
                    (direction == Direction.left) ? -1 : 1, 1,0);
        }
    }

    public float getWidth() {
        return (animation != null) ? animation.getKeyFrame(0).getRegionWidth() : 0;
    }
}
