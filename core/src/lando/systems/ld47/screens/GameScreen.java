package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Game;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.GameBoard;
import lando.systems.ld47.entities.Sasquatch;
import lando.systems.ld47.leaderboard.LeaderboardService;
import lando.systems.ld47.particles.Particles;
import lando.systems.ld47.ui.GameHud;

public class GameScreen extends BaseScreen{

    public final GameState gameState;
    public final GameBoard gameBoard;
    public final GameHud gameHud;
    public final LeaderboardService leaderboardService;

    public Sasquatch sasquatch;
    float accum;

    public GameScreen(Game game) {
        super(game);

        gameState = new GameState(this);
        gameBoard = new GameBoard(gameState);
        gameHud = new GameHud(gameState);
        leaderboardService = new LeaderboardService();

        sasquatch = new Sasquatch(this);

        playMusic(Audio.Musics.mood_track);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        accum += dt;
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            game.setScreen(new EndScreen(game));
        }
        // TESTING ---------------
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            leaderboardService.getScores();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            leaderboardService.postScore(leaderboardService.getRandomName(), MathUtils.random(1000L, 50000L));
        }
        // TESTING ---------------
        gameBoard.update(dt);
        sasquatch.update(dt);
        gameHud.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(shaker.getCombinedMatrix());
        batch.setShader(assets.cityShader);
        batch.begin();
        {
            assets.cityShader.setUniformf("iTime", accum);
            assets.cityShader.setUniformf("iResolution", worldCamera.viewportWidth, worldCamera.viewportHeight);
            batch.draw(assets.pixel, 0, 0, worldCamera.viewportWidth, worldCamera.viewportHeight, -.5f, -.5f, worldCamera.viewportWidth-.5f, worldCamera.viewportHeight - .5f);
        }
        batch.end();
        batch.setShader(null);

        batch.begin();
        {
            gameBoard.render(batch);
            //sasquatch.render(batch);
            particles.draw(batch, Particles.Layer.front);
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            gameHud.render(batch);
            // render dude over the hud - so he can punch em
            sasquatch.render(batch);
        }
        batch.end();
    }
}
