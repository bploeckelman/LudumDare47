package lando.systems.ld47.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld47.GameState;

public class ScoreUI extends UserInterface {

    private int score = 0;

    public ScoreUI(GameState gameState) {
        super(gameState);
    }

    public void update(float dt) {
        score = gameState.getScore();
    }

    public void draw(SpriteBatch batch, Rectangle bounds) {
        String text = "Score: " + score;
        layout.setText(assets.font, text);
        assets.font.draw(batch, layout,
                bounds.width - layout.width - 10f,
                bounds.height - 10f);
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
