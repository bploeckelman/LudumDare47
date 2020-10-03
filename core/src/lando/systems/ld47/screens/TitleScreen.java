package lando.systems.ld47.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Game;

public class TitleScreen extends BaseScreen{
    public TitleScreen(Game game) {
        super(game);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.setColor(Color.WHITE);
        batch.draw(assets.titleImage, 0,0, hudCamera.viewportWidth, hudCamera.viewportHeight);

        batch.end();
    }
}
