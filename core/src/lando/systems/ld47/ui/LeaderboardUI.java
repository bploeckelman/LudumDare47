package lando.systems.ld47.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.LeaderboardScore;

import java.util.*;

public class LeaderboardUI extends UserInterface {
    private int currentScore = 0;
    private float currentScoreLabel;
    ArrayList<LeaderboardScore> allScores;
    private MutableFloat pulse;
    private int currentRank;
    private LeaderboardScore currentLeaderboardScore;
    private boolean rankChanged = false;
    private float rankChangeCountdown;
    private static float RANK_CHANGE_TIMER = 5f;

    public LeaderboardUI(GameState gameState) {
        super(gameState);
        allScores = new ArrayList<>();
        this.pulse = new MutableFloat(0f);
        Tween.to(pulse, -1, 0.66f)
                .target(.3f)
                .repeatYoyo(-1, 0.33f)
                .start(tween);
        this.rankChangeCountdown = RANK_CHANGE_TIMER;
        currentLeaderboardScore = new LeaderboardScore("YOU", currentScore, true);
        allScores.add(currentLeaderboardScore);
        allScores.add(new LeaderboardScore(1, "TET", 5200));
        allScores.add(new LeaderboardScore(2, "RIS", 100));
        allScores.add(new LeaderboardScore(3, "TET", 100));
        allScores.add(new LeaderboardScore(4, "RIS", 900));
        allScores.add(new LeaderboardScore(5, "TET", 3200));
        allScores.add(new LeaderboardScore(6, "RIS", 800));
        allScores.add(new LeaderboardScore(7, "TET", 700));
        allScores.add(new LeaderboardScore(8, "RIS", 600));
        allScores.add(new LeaderboardScore(9, "TET", 500));
        allScores.add(new LeaderboardScore(10, "RIS", 300));
        allScores.add(new LeaderboardScore(11, "TET", 200));
        allScores.add(new LeaderboardScore(12, "RIS", 400));
        allScores.add(new LeaderboardScore(13, "TET", 2200));
        allScores.add(new LeaderboardScore(14, "RIS", 4400));
        allScores.add(new LeaderboardScore(13, "TET", 5200));
        allScores.add(new LeaderboardScore(14, "RIS", 200));
        allScores.add(new LeaderboardScore(15, "TET", 300));
        allScores.add(new LeaderboardScore(16, "RIS", 200));
        allScores.add(new LeaderboardScore(17, "TET", 100));
        allScores.add(new LeaderboardScore(18, "RIS", 100));
        allScores.add(new LeaderboardScore(19, "TET", 100));
        allScores.add(new LeaderboardScore(20, "RIS", 100));
        currentRank = allScores.size();
    }

    public void update(float dt) {
        currentScore = gameState.getScore();
        currentScoreLabel = MathUtils.lerp(currentScoreLabel, currentScore, 0.1f);
        for (LeaderboardScore score : allScores) {
            if (score.isCurrentUser == true) {
                currentLeaderboardScore = score;
                score.score = Math.round(currentScoreLabel);
            }
        }
        Collections.sort(allScores);
        int previousRank = currentRank;
        currentRank = allScores.indexOf(currentLeaderboardScore) + 1;
        if (previousRank != currentRank) {
            rankChanged = true;
        }

        if (rankChanged && rankChangeCountdown > 0) {
            rankChangeCountdown-=dt;
        } else {
            rankChanged = false;
            rankChangeCountdown = RANK_CHANGE_TIMER;
        }
    }

    public void draw(SpriteBatch batch, Rectangle bounds) {
        renderPanel(allScores, batch);
    }
    private void renderPanel(ArrayList<LeaderboardScore> scores, SpriteBatch batch){
        String title = "High Score";

        batch.setColor(Color.WHITE);
        assets.screws.draw(batch, 50, 50, gameState.gameScreen.hudCamera.viewportWidth / 4, gameState.gameScreen.hudCamera.viewportHeight - 100f);
        assets.font.getData().setScale(1.2f);
        layout.setText(assets.font, title, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.font.draw(batch, layout, 60, gameState.gameScreen.hudCamera.viewportHeight - 60);
        assets.font.getData().setScale(.7f);
        for (int i = 0; i < Math.min(scores.size(), 18); i++){
            LeaderboardScore score = scores.get(i);
            layout.setText(assets.font, (i+1) + ": ", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 80, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
            assets.font.getData().setScale(.7f);
            if (score.isCurrentUser) {
                if (rankChanged) {
                    assets.font.getData().setScale(.7f + pulse.floatValue());
                }
                layout.setText(assets.font, score.name, Color.RED, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
                assets.font.draw(batch, layout, 140, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
            }
            else {
                layout.setText(assets.font, score.name, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
                assets.font.draw(batch, layout, 140, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
            }
            assets.font.getData().setScale(.7f);
            layout.setText(assets.font, String.valueOf(score.score), Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 240, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
        }
    }
}
