package lando.systems.ld47.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableInteger;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.Assets;

public class ScoreUI extends UserInterface {
    public int score;
    public GameScreen screen;
    private MutableInteger mutScore;

    public ScoreUI(Assets assets, GameScreen screen) {
        super(assets, screen.game.tween);
        this.score = 0;
        this.screen = screen;
        this.mutScore = new MutableInteger(0);
    }

    public void update(float dt) {
        score = mutScore.intValue();
    }

    public void draw(SpriteBatch batch) {
        String text = "Score: " + score;
        layout.setText(assets.font, text);
        assets.font.draw(batch, layout,
                screen.hudCamera.viewportWidth - layout.width - 10f,
                screen.hudCamera.viewportHeight - 10f);
    }
    public void resetScore() {
        Tween.to(mutScore, 1, 1f).target(0).ease(Sine.OUT).start(super.tween);
    }

    public void addScore(int amount) {
        mutScore.setValue(mutScore.intValue() + amount);
    }

    public void subScore(int amount) {
        mutScore.setValue(mutScore.intValue() - amount);
    }

}
