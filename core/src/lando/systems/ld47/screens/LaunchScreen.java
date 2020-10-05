package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.Config;
import lando.systems.ld47.Game;
import lando.systems.ld47.ui.typinglabel.TypingLabel;

public class LaunchScreen extends BaseScreen {
    private TypingLabel titleLabel;
    static String title = "{JUMP=.2}{GRADIENT=purple;cyan}click to launch{ENDGRADIENT}{ENDJUMP}";
    private final ShaderProgram shader;
    private float accum = 0f;
    private float debounce;
    public LaunchScreen(Game game) {
        super(game);
        titleLabel = new TypingLabel(assets.bladeFont64, title, 0f, Config.windowHeight / 2f + 50f);
        titleLabel.setWidth(Config.windowWidth);
        titleLabel.setFontScale(2f);
        shader = game.assets.cityShader;
        debounce = 1f;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        debounce -= dt;
        if (Gdx.input.justTouched()&& debounce < 0){
            game.setScreen(new TitleScreen(game));
            debounce = 3f;
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

        BitmapFont chromeFont = assets.bladeFont16;
        assets.layout.setText(chromeFont, "Chrome needs this.", Color.WHITE, hudCamera.viewportWidth, Align.center, true);
        chromeFont.draw(batch, assets.layout, 0, 15);
        batch.end();
    }
}