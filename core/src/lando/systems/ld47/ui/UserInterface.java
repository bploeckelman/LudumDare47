package lando.systems.ld47.ui;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld47.Assets;
import lando.systems.ld47.GameState;

public abstract class UserInterface {

    protected final GameState gameState;
    protected final Assets assets;
    protected final TweenManager tween;
    protected final GlyphLayout layout;
    protected final Rectangle bounds;
    protected final Vector3 touchPos;

    private boolean visible;

    public UserInterface(GameState gameState) {
        this.gameState = gameState;
        this.assets = gameState.assets;
        this.tween = gameState.tween;

        this.layout = new GlyphLayout();
        this.bounds = new Rectangle();
        this.touchPos = new Vector3(-1, -1, 0);
        this.visible = false;
    }

    public abstract void update(float dt);
    public abstract void draw(SpriteBatch batch, Rectangle hudBounds);

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public boolean isShown() {
        return visible;
    }

    public boolean isHidden() {
        return !visible;
    }

}
