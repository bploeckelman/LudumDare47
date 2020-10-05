package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Game;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.GameBoard;
import lando.systems.ld47.entities.Opponent;
import lando.systems.ld47.particles.Particles;
import lando.systems.ld47.ui.GameHud;

public class GameScreen extends BaseScreen{

    public final GameState gameState;
    public final GameBoard gameBoard;
    public final GameHud gameHud;

    public Opponent opponent;
    float accum;
    ShaderProgram shader;

    public GameScreen(Game game) {
        super(game);

        gameState = new GameState(this);
        gameBoard = new GameBoard(gameState);
        gameHud = new GameHud(gameState);

        opponent = new Opponent(this);
        shader = game.idkfa ? game.assets.cityShader2 : game.assets.cityShader;
//        playMusic(Audio.Musics.blade_runner);
        // HACK to get settings to work first time
        gameState.stopMusic();
        gameState.startMusic();
        gameState.disableSounds();
        gameState.enableSounds();

        leaderboardService.getScores();

        playerInput.recheckController();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || playerInput.isPauseButtonJustPressed()) {
            gameHud.toggleSettings();
            gameBoard.togglePause();
        }

        shader = game.idkfa ? game.assets.cityShader2 : game.assets.cityShader;
        accum += dt;

        gameBoard.update(dt);
        opponent.update(dt);
        gameHud.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(shaker.getCombinedMatrix());
        batch.setShader(shader);
        batch.begin();
        {
            shader.setUniformf("iTime", accum);
            shader.setUniformf("iResolution", worldCamera.viewportWidth, worldCamera.viewportHeight);
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
            opponent.render(batch);
            // render settings over dude - so they can't be punched
            gameHud.renderSettings(batch);
            particles.draw(batch, Particles.Layer.overlay);
        }
        batch.end();
    }

    public void gameOver() {
        game.setScreen(new EndScreen(game, gameHud.getCurrentScore(), gameHud.getCurrentRank()));
    }

    public boolean isPaused() {
        return gameBoard.isPaused();
    }

    public void unpause() {
        gameBoard.unpause();
    }

    public void pause() {
        gameBoard.pause();
    }

    public boolean isGamepadConnected() {
        return playerInput.isGamepadConnected();
    }
}
