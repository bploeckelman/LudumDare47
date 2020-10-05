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

    private static final float RANK_CHANGE_TIMER = 5f;

    private final Array<LeaderboardScore> allScores;
    private final MutableFloat pulse;

    private LeaderboardScore currentLeaderboardScore;
    public int currentScore;
    public int currentRank;
    private float currentScoreLabel;
    private float rankChangeCountdown;
    private boolean rankChanged;

    public LeaderboardUI(GameState gameState) {
        super(gameState.gameScreen);
        this.allScores = new Array<>();
        this.pulse = new MutableFloat(0f);
        this.rankChangeCountdown = RANK_CHANGE_TIMER;
        this.currentLeaderboardScore = new LeaderboardScore("YOU", currentScore, true);
        this.allScores.add(currentLeaderboardScore);
        this.currentRank = 1;
        this.currentScore = 0;
        this.rankChanged = false;

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
        assets.screws.draw(batch, 50, 50, gameState.gameScreen.hudCamera.viewportWidth / 4, gameState.gameScreen.hudCamera.viewportHeight - 150f);

        assets.bladeFont64.getData().setScale(.7f);
        layout.setText(assets.bladeFont64, title, Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth / 4 - 20f, Align.center, false);
        assets.bladeFont64.draw(batch, layout, 60, gameState.gameScreen.hudCamera.viewportHeight - 60);
        assets.bladeFont64.getData().setScale(1f);

        float scoreScale = 1f;
        float rankColumnX = 60;
        float nameColumnX = 85;
        float scoreColumnX = 280;
        float startingY = 125;
        assets.bladeFont16.getData().setScale(scoreScale);
        for (int i = 0; i < Math.min(scores.size, leaderboardCount); i++){
            LeaderboardScore score = scores.get(i);
            layout.setText(assets.bladeFont16, (i+1) + ": ", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.bladeFont16.draw(batch, layout, rankColumnX, gameState.gameScreen.hudCamera.viewportHeight - startingY - (30*i));

            assets.bladeFont16.getData().setScale(scoreScale);
            if (score.isCurrentUser()) {
                if (rankChanged) {
                    assets.bladeFont16.getData().setScale(scoreScale + pulse.floatValue());
                }
                layout.setText(assets.bladeFont16, score.getName().toLowerCase(), Color.RED, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
                assets.bladeFont16.draw(batch, layout, nameColumnX, gameState.gameScreen.hudCamera.viewportHeight - startingY - (30*i));
            }
            else {
                layout.setText(assets.bladeFont16, score.getName().toLowerCase(), Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
                assets.bladeFont16.draw(batch, layout, nameColumnX, gameState.gameScreen.hudCamera.viewportHeight - startingY - (30*i));
            }

            assets.bladeFont16.getData().setScale(scoreScale);
            layout.setText(assets.bladeFont16, String.valueOf(score.getScore()), Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.bladeFont16.draw(batch, layout, scoreColumnX, gameState.gameScreen.hudCamera.viewportHeight - startingY - (30*i));
        }

        if (currentRank >= 19) {
//            layout.setText(assets.bladeFont16, currentRank + ": ", Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
//            assets.bladeFont16.draw(batch, layout, rankColumnX, gameState.gameScreen.hudCamera.viewportHeight - startingY - (30*17));

            layout.setText(assets.bladeFont16, currentLeaderboardScore.getName().toLowerCase(), Color.RED, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.bladeFont16.draw(batch, layout, nameColumnX, gameState.gameScreen.hudCamera.viewportHeight - startingY - (30*17));

            layout.setText(assets.bladeFont16, String.valueOf(currentLeaderboardScore.getScore()), Color.WHITE, gameState.gameScreen.hudCamera.viewportWidth, Align.left, false);
            assets.bladeFont16.draw(batch, layout, scoreColumnX, gameState.gameScreen.hudCamera.viewportHeight - startingY - (30*17));
        }
        assets.bladeFont16.getData().setScale(1);
    }

    public void updateScores(Array<LeaderboardScore> scores) {
        allScores.clear();
        allScores.add(currentLeaderboardScore);
        allScores.addAll(scores);
        currentRank = allScores.size;
    }

}
