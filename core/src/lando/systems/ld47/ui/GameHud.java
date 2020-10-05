package lando.systems.ld47.ui;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import lando.systems.ld47.Config;
import lando.systems.ld47.GameState;
import lando.systems.ld47.leaderboard.LeaderboardScore;

public class GameHud {

    private final GameState gameState;
    private final OrthographicCamera camera;
    private final Rectangle bounds;

    private GameInfoUI gameInfoUI;
    private LeaderboardUI leaderboardUI;
    private SettingsUI settingsUI;

    private final Array<UserInterface> uiElements = new Array<>();

    private StringBuilder controllerText;

    public GameHud(GameState gameState) {
        this.gameState = gameState;
        this.camera = gameState.gameScreen.hudCamera;
        this.bounds = new Rectangle(0, 0, this.camera.viewportWidth, this.camera.viewportHeight);

//        uiElements.add(new ScoreUI(gameState));
//        uiElements.add(nextUI = new NextUI(gameState, this.camera.viewportWidth - 130, bounds.height - 50));
        uiElements.add(gameInfoUI = new GameInfoUI(gameState));
        uiElements.add(leaderboardUI = new LeaderboardUI(gameState));
        settingsUI = new SettingsUI(gameState.gameScreen);

        controllerText = new StringBuilder();
    }

    public HoldUI getNextBox() {
        return gameInfoUI.nextUI;
    }

    public HoldUI getHoldBox() {
        return gameInfoUI.holdUI;
    }

    public ScoreUI getScoreUI() { return gameInfoUI.scoreUI; }

    public void update(float dt) {
        for (UserInterface ui : uiElements) {
            ui.update(dt);
        }
        settingsUI.update(dt);

        // DEBUG --------------------------------
        // TODO: replace me with a little icon or something
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

        // DEBUG --------------------------------
        // TODO: replace me with a little icon or something
        GlyphLayout layout = gameState.assets.layout;
        layout.setText(gameState.assets.font, controllerText.toString());
        gameState.assets.font.draw(batch, layout, 10, layout.height + 10);
        // DEBUG --------------------------------

        if (gameState.gameScreen.game.idkfa) {
            layout.setText(gameState.assets.font, "Wear a fucking mask");
            gameState.assets.font.draw(batch, layout, (Config.windowWidth - layout.width)*3/4, Config.windowHeight - layout.height);
            layout.setText(gameState.assets.font, "We do not support White Supremacists");
            gameState.assets.font.draw(batch, layout, (Config.windowWidth - layout.width)/4, Config.windowHeight - layout.height);
        }

        batch.setColor(Color.WHITE);
    }

    public void renderSettings(SpriteBatch batch) {
        settingsUI.draw(batch, null);
    }

    public void updateScores(Array<LeaderboardScore> scores) {
        leaderboardUI.updateScores(scores);
    }

    public int getCurrentRank() {
        return leaderboardUI.currentRank;
    }

    public int getCurrentScore() {
        return leaderboardUI.currentScore;
    }

    public void toggleSettings() {
        settingsUI.toggle();
    }
}
