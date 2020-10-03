package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Game;
import lando.systems.ld47.GameState;
import lando.systems.ld47.entities.GameBoard;
import lando.systems.ld47.ui.GameHud;

public class GameScreen extends BaseScreen{

    public final GameState gameState;
    public final GameBoard gameBoard;
    public final GameHud gameHud;

    public GameScreen(Game game) {
        super(game);

        gameState = new GameState(this);
        gameBoard = new GameBoard(gameState);
        gameHud = new GameHud(gameState);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            game.setScreen(new EndScreen(game));
        }
        gameBoard.update(dt);
        gameHud.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            gameBoard.render(batch);
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            gameHud.render(batch);
        }
        batch.end();
    }
}
