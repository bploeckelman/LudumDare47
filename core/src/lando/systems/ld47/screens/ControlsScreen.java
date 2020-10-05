package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Game;

public class ControlsScreen extends BaseScreen {

    public ControlsScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (Gdx.input.justTouched() || playerInput.isAnyButtonPressed()){
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);
        {
            batch.draw(assets.titleSillyImage, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
        }
        batch.end();
    }
}
