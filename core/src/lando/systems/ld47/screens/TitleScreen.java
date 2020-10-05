package lando.systems.ld47.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Game;
import lando.systems.ld47.entities.TitleCar;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class TitleScreen extends BaseScreen {

    private TitleCar car;

    public TitleScreen(Game game) {
        super(game);
        playMusic(Audio.Musics.intro_track);
        playerInput.recheckController();

        car = new TitleCar(game);
        car.position.set(-300, 300);

        Timeline.createSequence()
                .pushPause(2)
                .push(Tween.to(car.position, Vector2Accessor.XY, 5).target(600, 225).ease(Quad.INOUT))
                .pushPause(5)
                .push(Tween.to(car.position, Vector2Accessor.XY, 3)
                        .waypoint(850, 350).target(-300, 500).ease(Quad.INOUT))
                .repeat(100, 0)
                .start(game.tween);
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
