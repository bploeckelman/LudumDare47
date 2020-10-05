package lando.systems.ld47.ui;

import com.badlogic.gdx.Gdx;
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
    private float time = 0;

    public ScoreUI(GameState gameState, float x, float y) {
        super(gameState.gameScreen);
        this.x = x;
        this.y = y;
    }

    public void spawnPointAddedParticle(int point) {
        gameState.gameScreen.particles.pointIncreaseIndicator(x + 80f, y - 40f, 30f, point);
    }

    public void update(float dt) {
        score = gameState.getScore();
        scoreLabel = MathUtils.lerp(scoreLabel, score, 0.1f);
        lineCleared = gameState.getLineCleared();
        lineClearedLabel = MathUtils.lerp(lineClearedLabel, lineCleared, 0.5f);
        combo = gameState.getCombo();
        if (!gameState.gameScreen.isPaused()) {
            time += dt;
        }
    }

    public void draw(SpriteBatch batch, Rectangle bounds) {
        String timerText = "Timer: ";
        int timer = Math.round(time);
        int hours = timer / 3600;
        int minutes = (timer % 3600) / 60;
        int seconds = timer % 60;
        if (hours > 0) {
            timerText += hours + "h " + minutes + "m " + seconds + "s";
        }
        else if(minutes > 0) {
            timerText += minutes + "m " + seconds + "s";
        }
        else {
            String tempTime = Float.toString(Math.round(time * 100f) / 100f);
            if (tempTime.contains(".")) {
                String tempInteger = tempTime.split("\\.")[0];
                String tempDecimal = tempTime.split("\\.")[1];
                switch (tempDecimal.length()) {
                    case 0:
                        tempDecimal +="00";
                        break;
                    case 1:
                        tempDecimal += "0";
                        break;
                    default:
                        break;
                }
                timerText += tempInteger + "." + tempDecimal + "s";
            }
            else {
                timerText += tempTime + "s";
            }
        }
        assets.bladeFont32.getData().setScale(.7f);
        layout.setText(assets.bladeFont32, timerText, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.left, false);
        assets.bladeFont32.draw(batch, layout, x, y);
        String text = "score: " + Math.round(scoreLabel);
        layout.setText(assets.bladeFont32, text, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.left, false);
        assets.bladeFont32.draw(batch, layout, x, y - 50f);
        String lineClearText = "line cleared: " + Math.round(lineClearedLabel);
        layout.setText(assets.bladeFont32, lineClearText, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.left, false);
        assets.bladeFont32.draw(batch, layout, x, y - 100f);
        String currentComboText = "current combo: " + combo;
        layout.setText(assets.bladeFont32, currentComboText, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.left, false);
        assets.bladeFont32.draw(batch, layout, x, y - 150f);
        assets.bladeFont32.getData().setScale(1f);

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
