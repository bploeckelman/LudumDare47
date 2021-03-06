package lando.systems.ld47.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Game;
import lando.systems.ld47.entities.TitleCar;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class TitleScreen extends BaseScreen {

    private TitleCar car;
    float debounce;

    public TitleScreen(Game game) {
        super(game);
        debounce = 1f;
        playMusic(Audio.Musics.intro_track);
        playerInput.recheckController();

        car = new TitleCar(game);
        car.position.set(-500, 400);

        Timeline.createSequence()
                .pushPause(3)
                .push(Tween.to(car.position, Vector2Accessor.XY, 4).target(500, 225).ease(Quad.INOUT))
                .pushPause(5)
                .push(Tween.to(car.position, Vector2Accessor.XY, 3.5f)
                        .waypoint(850, 350).target(-500, 650).ease(Quad.INOUT))
                .repeat(100, 0)
                .start(game.tween);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        debounce -= dt;
        car.update(dt);

        if (debounce <= 0 &&Gdx.input.justTouched()
         || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)
         || playerInput.isAnyButtonPressed()){
            game.setScreen(new StoryScreen(game), assets.blindsShader, 2f);
            debounce = 3f;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.setColor(Color.WHITE);
        batch.draw(assets.titleImage, 0,0, hudCamera.viewportWidth, hudCamera.viewportHeight);

        GlyphLayout layout = game.assets.layout;
        layout.setText(game.assets.bladeFont64, "click to launch", Color.BLACK, 664, Align.left, false);
        game.assets.bladeFont64.draw(batch, layout, 308, 550);


        car.render(batch);


        layout.setText(game.assets.bladeFont64, "click to", Color.BLACK, 664, Align.left, false);
        game.assets.bladeFont64.draw(batch, layout, 308, 550);


        batch.end();
    }
}
