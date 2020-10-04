package lando.systems.ld47.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.GameState;

public class ScoreUI extends UserInterface {

    private int score = 0;
    private int lineCleared = 0;
    private int combo = 0;
    private float scoreLabel;
    private float lineClearedLabel;
    private float x;
    private float y;

    public ScoreUI(GameState gameState, float x, float y) {
        super(gameState);
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
        score = gameState.getScore();
        scoreLabel = MathUtils.lerp(scoreLabel, score, 0.1f);
        lineCleared = gameState.getLineCleared();
        lineClearedLabel = MathUtils.lerp(lineClearedLabel, lineCleared, 0.5f);
        combo = gameState.getCombo();
    }

    public void draw(SpriteBatch batch, Rectangle bounds) {
        String text = "Score: " + Math.round(scoreLabel);
        layout.setText(assets.font, text, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.left, false);
        assets.font.draw(batch, layout, x, y);
        String lineClearText = "Line Cleared: " + Math.round(lineClearedLabel);
        layout.setText(assets.font, lineClearText, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.left, false);
        assets.font.draw(batch, layout, x, y - 50f);
        String currentComboText = "Current Combo: " + combo;
        layout.setText(assets.font, currentComboText, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.left, false);
        assets.font.draw(batch, layout, x, y - 100f);

    }

//    public void resetScore() {
//        Tween.to(mutScore, 1, 1f).target(0).ease(Sine.OUT).start(super.tween);
//    }
//
//    public void addScore(int amount) {
//        mutScore.setValue(mutScore.intValue() + amount);
//    }
//
//    public void subScore(int amount) {
//        mutScore.setValue(mutScore.intValue() - amount);
//    }

}
