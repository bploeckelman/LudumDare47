package lando.systems.ld47.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.LeaderboardScore;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardUI extends UserInterface {
    private int score = 0;
    private float moneyLabel;
    Array<LeaderboardScore> allScores;


    public LeaderboardUI(GameState gameState) {
        super(gameState);
        allScores = new Array<>();
        allScores.add(new LeaderboardScore(1, "TET", 15200));
        allScores.add(new LeaderboardScore(2, "RIS", 14400));
        allScores.add(new LeaderboardScore(3, "TET", 15200));
        allScores.add(new LeaderboardScore(4, "RIS", 14400));
        allScores.add(new LeaderboardScore(5, "TET", 15200));
        allScores.add(new LeaderboardScore(6, "RIS", 14400));
        allScores.add(new LeaderboardScore(7, "TET", 15200));
        allScores.add(new LeaderboardScore(8, "RIS", 14400));
        allScores.add(new LeaderboardScore(9, "TET", 15200));
        allScores.add(new LeaderboardScore(10, "RIS", 14400));
        allScores.add(new LeaderboardScore(11, "TET", 15200));
        allScores.add(new LeaderboardScore(12, "RIS", 14400));
        allScores.add(new LeaderboardScore(13, "TET", 15200));
        allScores.add(new LeaderboardScore(14, "RIS", 14400));

    }

    public void update(float dt) {
        score = gameState.getScore();
        moneyLabel = MathUtils.lerp(moneyLabel, score, 0.1f);
    }

    public void draw(SpriteBatch batch, Rectangle bounds) {
        renderPanel(allScores, batch);
    }
    private void renderPanel(Array<LeaderboardScore> scores, SpriteBatch batch){
        String title = "High Score";

        batch.setColor(Color.WHITE);
        assets.screws.draw(batch, 50, 50, gameState.gameScreen.hudCamera.viewportWidth / 4, gameState.gameScreen.hudCamera.viewportHeight - 100f);
        assets.font.getData().setScale(1.2f);
        layout.setText(assets.font, title, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.font.draw(batch, layout, 60, gameState.gameScreen.hudCamera.viewportHeight - 60);
        assets.font.getData().setScale(.7f);
        for (int i = 0; i < Math.min(scores.size, 20); i++){
            LeaderboardScore score = scores.get(i);
            layout.setText(assets.font, (i+1) + ": ", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 80, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
            layout.setText(assets.font, score.name + " - " + score.score, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 140, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
        }
    }
}
