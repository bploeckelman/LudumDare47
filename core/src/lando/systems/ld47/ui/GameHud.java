package lando.systems.ld47.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.Config;
import lando.systems.ld47.GameState;
import lando.systems.ld47.leaderboard.LeaderboardScore;

public class GameHud {

    private static final Color dark_violet   = new Color(150f / 255f, 0f, 1f, 1f);
    private static final Color deep_pink     = new Color(1f, 0f, 193f / 255f, 1f);

    private final GameState gameState;
    private final OrthographicCamera camera;
    private final Rectangle bounds;
    private final Vector3 mousePos;
    private final Vector3 touchPos;

    private final Array<UserInterface> uiElements;
    private final GameInfoUI gameInfoUI;
    private final LeaderboardUI leaderboardUI;
    private final SettingsUI settingsUI;

    private Rectangle settingsButtonBounds;
    private boolean settingsButtonHovered;

    public GameHud(GameState gameState) {
        this.gameState = gameState;
        this.camera = gameState.gameScreen.hudCamera;
        this.bounds = new Rectangle(0, 0, this.camera.viewportWidth, this.camera.viewportHeight);
        this.mousePos = new Vector3();
        this.touchPos = new Vector3();

        this.uiElements = new Array<>();
//        uiElements.add(new ScoreUI(gameState));
//        uiElements.add(nextUI = new NextUI(gameState, this.camera.viewportWidth - 130, bounds.height - 50));
        this.uiElements.add(gameInfoUI = new GameInfoUI(gameState));
        this.uiElements.add(leaderboardUI = new LeaderboardUI(gameState));

        this.settingsUI = new SettingsUI(gameState.gameScreen);

        float margin = 10f;
        this.settingsButtonBounds = new Rectangle(margin,
                camera.viewportHeight - gameState.assets.settingsIcon.getRegionHeight() - margin,
                gameState.assets.settingsIcon.getRegionWidth(), gameState.assets.settingsIcon.getRegionHeight());
        this.settingsButtonHovered = false;
    }

    public HoldUI getNextBox() {
        return gameInfoUI.nextUI;
    }

    public HoldUI getHoldBox() {
        return gameInfoUI.holdUI;
    }

    public ScoreUI getScoreUI() { return gameInfoUI.scoreUI; }

    public void update(float dt) {
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(mousePos);

        settingsButtonHovered = settingsButtonBounds.contains(mousePos.x, mousePos.y);

        if (Gdx.input.justTouched()) {
            touchPos.set(mousePos);

            if (settingsButtonBounds.contains(touchPos.x, touchPos.y)) {
                settingsUI.toggle();
            }
        }

        for (UserInterface ui : uiElements) {
            ui.update(dt);
        }
        settingsUI.update(dt);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.WHITE);

        for (UserInterface ui : uiElements) {
            ui.draw(batch, bounds);
        }

        // settings icon
        batch.setColor((settingsButtonHovered) ? deep_pink : dark_violet);
        batch.draw(gameState.assets.settingsIcon, settingsButtonBounds.x, settingsButtonBounds.y);
        batch.setColor(Color.WHITE);

        // gamepad icon
        batch.setColor((gameState.gameScreen.isGamepadConnected()) ? Color.GREEN : Color.DARK_GRAY);
        batch.draw(gameState.assets.gamepadIcon, 10f, 10f);
        batch.setColor(Color.WHITE);


        if (gameState.gameScreen.game.idkfa) {
            GlyphLayout layout = gameState.assets.layout;
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
