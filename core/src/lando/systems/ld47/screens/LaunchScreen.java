package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Config;
import lando.systems.ld47.Game;
import lando.systems.ld47.ui.typinglabel.TypingLabel;

public class LaunchScreen extends BaseScreen {
    private TypingLabel titleLabel;
    static String title = "{JUMP=.2}{WAVE=0.9;1.2;1.75}{RAINBOW}click to launch{ENDRAINBOW}{ENDWAVE}{ENDJUMP}";
    public LaunchScreen(Game game) {
        super(game);
        titleLabel = new TypingLabel(assets.bladeFont64, title, 0f, Config.windowHeight / 2f + 50f);
        titleLabel.setWidth(Config.windowWidth);
        titleLabel.setFontScale(2.5f);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (Gdx.input.justTouched()){
            game.setScreen(new TitleScreen(game));
        }
        titleLabel.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.setColor(Color.BLACK);
        batch.draw(assets.whitePixel, 0,0, shaker.getViewCamera().viewportWidth, shaker.getViewCamera().viewportHeight);
        titleLabel.render(batch);
        batch.setColor(Color.WHITE);

        batch.end();
    }
}