package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import lando.systems.ld47.Config;
import lando.systems.ld47.Game;
import lando.systems.ld47.ui.typinglabel.TypingLabel;

public class LaunchScreen extends BaseScreen {
    private TypingLabel titleLabel;
    static String title = "{JUMP=.2}{WAVE=0.9;1.2;1.75}{RAINBOW}click to launch{ENDRAINBOW}{ENDWAVE}{ENDJUMP}";
    private final ShaderProgram shader;
    private float accum = 0f;
    public LaunchScreen(Game game) {
        super(game);
        titleLabel = new TypingLabel(assets.bladeFont64, title, 0f, Config.windowHeight / 2f + 50f);
        titleLabel.setWidth(Config.windowWidth);
        titleLabel.setFontScale(2.5f);
        shader = game.assets.cityShader;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (Gdx.input.justTouched()){
            game.setScreen(new TitleScreen(game));
        }
        titleLabel.update(dt);
        accum += dt;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.setShader(shader);
        batch.begin();
        {
            shader.setUniformf("iTime", accum);
            shader.setUniformf("iResolution", hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.draw(assets.pixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight, -.5f, -.5f, hudCamera.viewportWidth-.5f, hudCamera.viewportHeight - .5f);
        }
        batch.end();

        batch.begin();
        batch.setShader(null);
        titleLabel.render(batch);
        batch.setColor(Color.WHITE);

        batch.end();
    }
}