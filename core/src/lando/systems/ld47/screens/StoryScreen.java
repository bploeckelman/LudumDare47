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
    public float debounceTime;

    public StoryScreen(Game game) {
        super(game);

        String storyText = "{FAST}    Early in the 21st century, {GRADIENT=purple;cyan}Lando Systems{ENDGRADIENT} advanced the development of block-clearing mechanics into the Tetroid phase - creating a game virtually identical to Tetris - known as an {FADE}{COLOR=red}Homage.{COLOR=white}{ENDFADE}\n"
                + "    The {FADE}{COLOR=red}Homage{COLOR=white}{ENDFADE} was more advanced in its presentation, and at least equal in its mechanics, to the original, and was celebrated as an achievement by the team who created it.\n"
                + "    After a heated exchange among members of the {GRADIENT=purple;cyan}Lando Systems{ENDGRADIENT} team, the {FADE}{COLOR=red}Homage{COLOR=white}{ENDFADE} was deemed insufficiently original and dismissed with claims of being {SICK}{COLOR=blue}a clone{COLOR=white}{ENDSICK}.\n"
                + "    Special new gameplay elements were added. These augmented the existing systems enough to relieve most concerns about its resemblance to its predecessor.\n"
                + "    This was no longer called an {FADE}{COLOR=red}homage...{COLOR=white}{ENDFADE}\n\n                {SLOWER}It was called {WAVE}{FADE}{GRADIENT=purple;cyan}Block Runner{ENDGRADIENT}{ENDFADE}{ENDWAVE}{NORMAL}"
                ;

        float margin = 60f;
        this.storyLabel = new TypingLabel(assets.bladeFont32, storyText.toLowerCase(), margin, hudCamera.viewportHeight / 2f + 260f);
        this.storyLabel.setWidth(hudCamera.viewportWidth - (2f * margin));
        this.storyLabel.setLineAlign(Align.left);
        this.storyLabel.setFontScale(0.9f);

        this.fullyTyped = false;
        this.debounceTime = 2f;
        playerInput.recheckController();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        debounceTime -= dt;

        fullyTyped = storyLabel.hasEnded();

        if (debounceTime <= 0 && Gdx.input.justTouched() || playerInput.isAnyButtonPressed()){
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