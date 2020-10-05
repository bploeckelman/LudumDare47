package lando.systems.ld47.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.Audio;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.IShootable;
import lando.systems.ld47.entities.Tetrad;

public class HoldUI extends UserInterface implements IShootable {

    public Tetrad hold;
    private float size;
    protected final Vector2 center;
    private String text;

    public HoldUI(GameState gameState, float x, float y, String text) {
        super(gameState.gameScreen);
        this.text = text;

        size = Tetrad.POINT_WIDTH * 3.5f;
        bounds.set(x, y - size, size, size);
        center = new Vector2(x + size / 2, y - size / 2);
    }

    @Override
    public void update(float dt) {
        if (gameState.gameScreen.playerInput.isHoldPressed()) {
            hold = gameState.gameScreen.gameBoard.swapActiveTetrad(hold);
        }

        if (hold != null) {
            hold.scale = .7f;
            hold.center(center);
        }

        if (Gdx.input.justTouched()) {
            Vector3 projection = new Vector3();
            projection = gameState.gameScreen.hudCamera.unproject(projection);
            projection.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            //System.out.println("{ " + projection.x + ", " + projection.y + "}");
        }
    }

    @Override
    public void draw(SpriteBatch batch, Rectangle hudBounds) {
        batch.setColor(Color.WHITE);
        assets.screws.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        assets.bladeFont32.getData().setScale(.7f);
        layout.setText(assets.bladeFont32, text, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.bladeFont32.draw(batch, layout, bounds.x - 85f, bounds.y + size + 20f);
        assets.bladeFont32.getData().setScale(1f);
        if (hold != null) {
            hold.render(batch);
        }
    }

    @Override
    public void hit() {
        if (hold != null) {
            gameState.gameScreen.playSound(Audio.Sounds.holdUIExplode);
            gameState.gameScreen.particles.addPiecePunchedParticles(center.x, center.y, hold.color);
            hold = null;
        }
    }

    @Override
    public Vector2 getTarget() {
        return new Vector2(center.x, center.y);
    }
}
