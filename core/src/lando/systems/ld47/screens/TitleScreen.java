package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Game;
import lando.systems.ld47.entities.TitleCar;

public class TitleScreen extends BaseScreen {

    private TitleCar car;

    public TitleScreen(Game game) {
        super(game);
        playMusic(Audio.Musics.intro_track);
        playerInput.recheckController();

        car = new TitleCar(game);
        car.position.set(500, 250);
        car.setState(TitleCar.State.idle);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        car.update(dt);

        if (Gdx.input.justTouched()
         || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)
         || playerInput.isAnyButtonPressed()){
            game.setScreen(new StoryScreen(game), assets.blindsShader, 2f);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.setColor(Color.WHITE);
        batch.draw(assets.titleImage, 0,0, hudCamera.viewportWidth, hudCamera.viewportHeight);

        car.render(batch);

        batch.end();
    }
}
