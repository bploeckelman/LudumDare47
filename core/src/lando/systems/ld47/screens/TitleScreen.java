package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Game;

public class TitleScreen extends BaseScreen {
    public TitleScreen(Game game) {
        super(game);
        playMusic(Audio.Musics.mood_track2);
        playerInput.recheckController();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (Gdx.input.justTouched()
         || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)
         || playerInput.isAnyButtonPressed()){
            game.setScreen(new StoryScreen(game), assets.cubeShader, 3f);
        }
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
