package lando.systems.ld47.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.Tetrad;

public class NextUI extends UserInterface {

    private Tetrad next;
    private float size;
    private Vector2 center;

    public NextUI(GameState gameState, float x, float y) {
        super(gameState);

        next = gameState.viewNext();
        size = Tetrad.POINT_WIDTH * 5;
        bounds.set(x, y - size, size, size);
        center = new Vector2(x + size / 2, y - size / 2);
    }

    @Override
    public void update(float dt) {
        next = gameState.viewNext();
        next.center(center);
    }

    @Override
    public void draw(SpriteBatch batch, Rectangle hudBounds) {
        batch.setColor(Color.WHITE);
        batch.draw(assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
        //if (next != null) {
            next.render(batch);
        //}
    }
}
