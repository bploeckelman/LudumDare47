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
import lando.systems.ld47.leaderboard.LeaderboardScore;

public class LeaderboardUI extends UserInterface {

    private int currentScore = 0;
    private float currentScoreLabel;
    private Array<LeaderboardScore> allScores;
    private MutableFloat pulse;
    private int currentRank;
    private LeaderboardScore currentLeaderboardScore;
    private boolean rankChanged = false;
    private float rankChangeCountdown;
    private static float RANK_CHANGE_TIMER = 5f;

    public LeaderboardUI(GameState gameState) {
        super(gameState);
        this.allScores = new Array<>();
        this.pulse = new MutableFloat(0f);
        this.rankChangeCountdown = RANK_CHANGE_TIMER;
        this.currentLeaderboardScore = new LeaderboardScore("YOU", currentScore, true);
        this.allScores.add(currentLeaderboardScore);
        this.currentRank = 1;

        Tween.to(pulse, -1, 0.66f)
                .target(.3f)
                .repeatYoyo(-1, 0.33f)
                .start(tween);
    }

    public void update(float dt) {
        currentScore = gameState.getScore();
        currentScoreLabel = MathUtils.lerp(currentScoreLabel, currentScore, 0.1f);
        for (LeaderboardScore score : allScores) {
            if (score.isCurrentUser()) {
                currentLeaderboardScore = score;
                score.setScore(Math.round(currentScoreLabel));
            }
        }
        allScores.sort();
        int previousRank = currentRank;
        currentRank = allScores.indexOf(currentLeaderboardScore, true) + 1;
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

    private void renderPanel(Array<LeaderboardScore> scores, SpriteBatch batch){
        String title = "High Score";
        int leaderboardCount = currentRank < 19 ? 18 : 17;

        batch.setColor(Color.WHITE);
        assets.screws.draw(batch, 50, 50, gameState.gameScreen.hudCamera.viewportWidth / 4, gameState.gameScreen.hudCamera.viewportHeight - 100f);

        assets.font.getData().setScale(1.2f);
        layout.setText(assets.font, title, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.font.draw(batch, layout, 60, gameState.gameScreen.hudCamera.viewportHeight - 60);

        float scoreScale = 0.4f;
        assets.font.getData().setScale(scoreScale);
        for (int i = 0; i < Math.min(scores.size, leaderboardCount); i++){
            LeaderboardScore score = scores.get(i);
            layout.setText(assets.font, (i+1) + ": ", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 80, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));

            assets.font.getData().setScale(scoreScale);
            if (score.isCurrentUser()) {
                if (rankChanged) {
                    assets.font.getData().setScale(scoreScale + pulse.floatValue());
                }
                layout.setText(assets.font, score.getName(), Color.RED, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
                assets.font.draw(batch, layout, 110, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
            }
            else {
                layout.setText(assets.font, score.getName(), Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
                assets.font.draw(batch, layout, 110, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
            }

            assets.font.getData().setScale(scoreScale);
            layout.setText(assets.font, String.valueOf(score.getScore()), Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 260, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*i));
        }

        if (currentRank >= 19) {
            layout.setText(assets.font, currentRank + ": ", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 80, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*17));

            layout.setText(assets.font, currentLeaderboardScore.getName(), Color.RED, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 110, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*17));

            layout.setText(assets.font, String.valueOf(currentLeaderboardScore.getScore()), Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.font.draw(batch, layout, 260, gameState.gameScreen.hudCamera.viewportHeight - 120 - (30*17));
        }
    }

    public void updateScores(Array<LeaderboardScore> scores) {
        allScores.clear();
        allScores.add(currentLeaderboardScore);
        allScores.addAll(scores);
        currentRank = allScores.size;
    }

}
