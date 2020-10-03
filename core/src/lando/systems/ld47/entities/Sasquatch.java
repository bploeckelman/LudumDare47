package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.screens.GameScreen;

public class Sasquatch {

    public enum SasquatchState {
        idle, throwing, jump, land, stun
    }

    private float time = 0;

    public Vector2 position = new Vector2();
    public GameScreen screen;
    public SasquatchState state = SasquatchState.idle;

    private Rectangle imageBounds = new Rectangle();
    private Rectangle collisionBounds = new Rectangle();

    private Animation<TextureRegion> animation = null;

    public Sasquatch(GameScreen screen) {
        this.screen = screen;
        setState(SasquatchState.idle);
        position.set(490, 620);
    }

    public void setState(SasquatchState state) {
        this.state = state;

        switch (state) {
            case throwing:
                animation = screen.assets.sasquatch_throw;
                break;
            case jump:
                animation = screen.assets.sasquatch_jump;
                break;
            case land:
                animation = screen.assets.sasquatch_jump;
                break;
            case stun:
                animation = screen.assets.sasquatch_stun;
            default:
                animation = screen.assets.sasquatch;
        }

        time = 0;
    }

    public void update(float dt) {
        time += dt;
    }

    public void render(SpriteBatch batch) {

        imageBounds.setPosition(position.x - imageBounds.width / 2f, position.y - collisionBounds.height / 2f);
        collisionBounds.setPosition(position.x - collisionBounds.width/2f, position.y - collisionBounds.height/2f);

        if (animation != null) {
            TextureRegion texture = animation.getKeyFrame(time);
            batch.draw(texture, position.x, position.y);
        }
    }
}
