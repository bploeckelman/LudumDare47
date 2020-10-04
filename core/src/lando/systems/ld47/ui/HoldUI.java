package lando.systems.ld47.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.Tetrad;

public class HoldUI extends UserInterface {

    private Tetrad hold;
    private float size;
    private Vector2 center;

    public HoldUI(GameState gameState, float x, float y) {
        super(gameState);

        size = Tetrad.POINT_WIDTH * 5;
        bounds.set(x, y - size, size, size);
        center = new Vector2(x + size / 2, y - size / 2);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void draw(SpriteBatch batch, Rectangle hudBounds) {
        batch.setColor(Color.WHITE);
        assets.screws.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        assets.font.getData().setScale(.7f);
        layout.setText(assets.font, "HOLD", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.font.draw(batch, layout, bounds.x - 85f, bounds.y + size + 20f);

    }
}
