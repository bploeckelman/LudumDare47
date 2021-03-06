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

        shader = game.idkfa ? game.assets.cityShader2 : game.assets.cityShader;
        playMusic(Audio.Musics.blade_runner);
//        gameState.startMusic();


        leaderboardService.getScores();

        playerInput.recheckController();
    }

    private boolean opponentDisabled = true;
    private void checkBitchMode(boolean bitchMode) {
        if (opponentDisabled == bitchMode) {
            return;
        }

        if (bitchMode) {
            if (opponent != null) {
                opponent.disable();
                opponent = null;
            }
        } else {
            opponent = new Opponent(this);
        }
        opponentDisabled = bitchMode;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        checkBitchMode(gameState.isBitchMode());

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)
         || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)
         || playerInput.isPauseButtonJustPressed()) {
            gameHud.toggleSettings();
        }

        shader = game.idkfa ? game.assets.cityShader2 : game.assets.cityShader;
        accum += dt;

        gameBoard.update(dt);
        if (opponent != null) {
            opponent.update(dt);
        }
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
            particles.draw(batch, Particles.Layer.front);
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            gameHud.render(batch);
            // render dude over the hud - so he can punch em
            if (opponent != null) {
                opponent.render(batch);
            }
            // render settings over dude - so they can't be punched
            gameHud.renderSettings(batch);
            particles.draw(batch, Particles.Layer.overlay);
        }
        batch.end();
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {
        super.renderFrameBuffers(batch);
        gameBoard.renderFrameBuffer(batch);
    }

    boolean gameEnded = false;
    public void gameOver() {
        if (!gameEnded) {
            game.setScreen(new EndScreen(game, gameHud.getCurrentScore(), gameHud.getCurrentRank()));
            gameEnded = true;
        }
    }

    public boolean isPaused() {
        return gameBoard.isPaused();
    }

    public boolean isGamepadConnected() {
        return playerInput.isGamepadConnected();
    }
}
