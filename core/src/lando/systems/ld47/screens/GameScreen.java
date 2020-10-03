package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Game;
import lando.systems.ld47.entities.GameBoard;

public class GameScreen extends BaseScreen{

    GameBoard gameBoard;

    public GameScreen(Game game) {
        super(game);
        gameBoard = new GameBoard(game, worldCamera);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            game.setScreen(new EndScreen(game));
        }
        gameBoard.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(worldCamera.combined);

        gameBoard.render(batch);

        batch.end();

    }
}
