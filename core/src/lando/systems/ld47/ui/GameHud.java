package lando.systems.ld47.ui;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import lando.systems.ld47.GameState;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class GameHud {

    private final GameState gameState;
    private final OrthographicCamera camera;
    private final Rectangle bounds;

    private NextUI nextUI;
    private GameInfoUI gameInfoUI;

    private final Array<UserInterface> uiElements = new Array<>();

    private StringBuilder controllerText;

    public GameHud(GameState gameState) {
        this.gameState = gameState;
        this.camera = gameState.gameScreen.hudCamera;
        this.bounds = new Rectangle(0, 0, this.camera.viewportWidth, this.camera.viewportHeight);

//        uiElements.add(new ScoreUI(gameState));
//        uiElements.add(nextUI = new NextUI(gameState, this.camera.viewportWidth - 130, bounds.height - 50));
        uiElements.add(new LeaderboardUI(gameState));
        uiElements.add(gameInfoUI = new GameInfoUI(gameState));

        controllerText = new StringBuilder();
    }

    public Vector2 getNextPosition() {
        return new Vector2(gameInfoUI.nextUI.bounds.x, gameInfoUI.nextUI.bounds.y);
    }

    public Vector2 getHoldPosition() {
        return new Vector2(gameInfoUI.holdUI.bounds.x, gameInfoUI.holdUI.bounds.y);
    }

    public void update(float dt) {
        for (UserInterface ui : uiElements) {
            ui.update(dt);
        }

        // DEBUG --------------------------------
        controllerText.clear();
        controllerText.append("[");
        Array<Controller> controllers = Controllers.getControllers();
        for (int i = 0; i < controllers.size; ++i) {
            controllerText.append(controllers.get(i).getName());
            if (i != controllers.size - 1) {
                controllerText.append(", ");
            }
        }
        controllerText.append("]");
        // DEBUG --------------------------------
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.WHITE);

        for (UserInterface ui : uiElements) {
            ui.draw(batch, bounds);
        }

        GlyphLayout layout = gameState.assets.layout;
        layout.setText(gameState.assets.font, controllerText.toString());
        gameState.assets.font.draw(batch, layout, 10, layout.height + 10);

        batch.setColor(Color.WHITE);
    }
}
