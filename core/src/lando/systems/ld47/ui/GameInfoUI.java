package lando.systems.ld47.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.GameState;

public class GameInfoUI extends UserInterface {

    public NextUI nextUI;
    public ScoreUI scoreUI;
    public HoldUI holdUI;

    public GameInfoUI(GameState gameState) {
        super(gameState.gameScreen);
        nextUI = new NextUI(gameState, gameState.gameScreen.hudCamera.viewportWidth / 8 * 6 - 35f, gameState.gameScreen.hudCamera.viewportHeight - 180f, "NEXT");
        scoreUI = new ScoreUI(gameState, gameState.gameScreen.hudCamera.viewportWidth / 4 * 3 -30f, gameState.gameScreen.hudCamera.viewportHeight - 470f);
        holdUI = new HoldUI(gameState, gameState.gameScreen.hudCamera.viewportWidth / 8 * 7 - 45f, gameState.gameScreen.hudCamera.viewportHeight - 180f, "HOLD");
    }

    public void update(float dt) {
        nextUI.update(dt);
        holdUI.update(dt);
        scoreUI.update(dt);
    }

    public void draw(SpriteBatch batch, Rectangle bounds) {
        renderPanel(batch);
        nextUI.draw(batch, bounds);
        holdUI.draw(batch, bounds);
        scoreUI.draw(batch, bounds);
    }
    private void renderPanel(SpriteBatch batch){
        String title = "Tetrad Runner";
        batch.setColor(Color.WHITE);
        assets.screws.draw(batch, gameState.gameScreen.hudCamera.viewportWidth / 4 * 3 - 50f, 50f, gameState.gameScreen.hudCamera.viewportWidth / 4, gameState.gameScreen.hudCamera.viewportHeight - 100f);
        assets.font.getData().setScale(1f);
        layout.setText(assets.font, title, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.font.draw(batch, layout, gameState.gameScreen.hudCamera.viewportWidth / 4 * 3 - 40f, gameState.gameScreen.hudCamera.viewportHeight - 60f);
        layout.setText(assets.font, "2020", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.font.draw(batch, layout, gameState.gameScreen.hudCamera.viewportWidth / 4 * 3 - 40f, gameState.gameScreen.hudCamera.viewportHeight - 100f);
        assets.font.getData().setScale(.7f);

    }
}
