package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.Game;
import lando.systems.ld47.ui.typinglabel.TypingLabel;

public class StoryScreen extends BaseScreen {

    private final TypingLabel storyLabel;
    private boolean fullyTyped;

    public StoryScreen(Game game) {
        super(game);

        String storyText = "this is the story, its a long story, and there's lots of text in it\nits a super hassle to get into a java string properly formatted and such, but oh well i mean its not like this is a ludum dare fuck it do it live situation here, oh damn it is!";

        float margin = 20f;
        this.storyLabel = new TypingLabel(assets.bladeFont32, storyText.toLowerCase(), margin, hudCamera.viewportHeight / 2f);
        this.storyLabel.setWidth(hudCamera.viewportWidth - (2f * margin));
        this.storyLabel.setLineAlign(Align.left);
        this.storyLabel.setFontScale(1f);

        this.fullyTyped = false;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        fullyTyped = storyLabel.hasEnded();

        if (Gdx.input.justTouched() || playerInput.isAnyButtonPressed()){
            if (fullyTyped) {
                game.setScreen(new ControlsScreen(game));
            } else {
                storyLabel.skipToTheEnd();
            }
        }

        storyLabel.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);
        {
            // background
            batch.setColor(Color.BLACK);
            batch.draw(assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(Color.WHITE);

            // text
            storyLabel.render(batch);
        }
        batch.end();
    }

}