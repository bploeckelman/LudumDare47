package lando.systems.ld47.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.xpath.internal.operations.Or;
import lando.systems.ld47.GameState;

public class GameHud {

    private final GameState gameState;
    private final OrthographicCamera camera;
    private final Rectangle bounds;

    private final Array<UserInterface> uiElements = new Array<>();

    public GameHud(GameState gameState) {
        this.gameState = gameState;
        this.camera = gameState.gameScreen.hudCamera;
        this.bounds = new Rectangle(0, 0, this.camera.viewportWidth, this.camera.viewportHeight);

        uiElements.add(new ScoreUI(gameState));
    }

    public void update(float dt) {
        for (UserInterface ui : uiElements) {
            ui.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.WHITE);

        for (UserInterface ui : uiElements) {
            ui.draw(batch, bounds);
        }

        batch.setColor(Color.WHITE);
    }
}
