package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Game;

public class ControlsScreen extends BaseScreen {

    public float debounceTime;

    public ControlsScreen(Game game) {
        super(game);
        playerInput.recheckController();
        this.debounceTime = 1f;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        debounceTime -= dt;

        if (debounceTime <= 0 && Gdx.input.justTouched() || playerInput.isAnyButtonPressed()){
            debounceTime = 5f;
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);
        {
            batch.draw(assets.controlsImage, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
        }
        batch.end();
    }
}
