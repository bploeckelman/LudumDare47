package lando.systems.ld47.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.screens.BaseScreen;
import lando.systems.ld47.utils.accessors.*;

public class ScoreEntryUI extends UserInterface {

    private static final float margin_horizontal = 50f;
    private static final float margin_vertical   = 100f;
    private static final float margin_button = 20f;
    private static final float header_height = 40f;

    private static final String headerText = "Final Score";
    private static final String buttonText = "Submit Score";

    private Camera camera;
    private Vector3 mousePos;
    private MutableFloat alpha;
    private Rectangle boundsButton;
    private Rectangle boundsNameEntry;
    private String name;

    private boolean transitionComplete;

    // 4 rows
    // ---------------------------
    // - header
    // - score achieved (rank X/Y)
    // - name entry
    // - submit button

    public ScoreEntryUI(BaseScreen screen) {
        super(screen);

        this.camera = screen.hudCamera;

        this.mousePos = new Vector3();
        this.alpha = new MutableFloat(0f);

        this.transitionComplete = false;

        this.bounds.setPosition(camera.viewportWidth / 2f, camera.viewportHeight / 2f);

        this.boundsButton = new Rectangle(
                bounds.x + margin_button, bounds.y + margin_button,
                bounds.width - 2f * margin_button, 150f);

        this.boundsNameEntry = new Rectangle(
                bounds.x + margin_button, boundsButton.y + margin_button,
                bounds.width - 2f * margin_button, 100f);

        this.name = "Anonymous";
    }

    public void update(float dt) {
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(mousePos);

        // TODO: set hover flags based on mouse pos

        // don't allow input until show() tween is fully complete
        if (!transitionComplete) return;

        if (Gdx.input.isTouched()) {
            touchPos.set(mousePos);

            if (boundsButton.contains(touchPos.x, touchPos.y)) {
                screen.leaderboardService.postScore(name, gameState.getScore());
                hide();
            }
        }
    }

    public void draw(SpriteBatch batch, Rectangle hudBounds) {
        // draw background
        batch.setColor(0x30 / 255f, 0x30 / 255f, 0x30 / 255f, alpha.floatValue());
        batch.draw(assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);

        // draw border
        batch.setColor(1f, 1f, 1f, alpha.floatValue());
        assets.border.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);

        // show content
        if (transitionComplete) {
            // header text
            {
                layout.setText(assets.font, headerText, Color.LIGHT_GRAY, bounds.width, Align.center, false);
                assets.font.draw(batch, layout, bounds.x, bounds.y + bounds.height - layout.height / 2f);
            }

            // TODO: score achieved (and rank X/Y)

            // name entry field
            {
                assets.border.draw(batch, boundsNameEntry.x, boundsNameEntry.y, boundsNameEntry.width, boundsNameEntry.height);
                assets.font.setColor(Color.BLACK);

                assets.layout.setText(assets.font, name);
                assets.font.draw(batch, name, boundsNameEntry.x, boundsNameEntry.y + boundsNameEntry.height / 2f + assets.layout.height / 2f, boundsNameEntry.width, Align.center, false);
                assets.font.setColor(Color.WHITE);
            }

            // score submission button
            {
                assets.border.draw(batch, boundsButton.x, boundsButton.y, boundsButton.width, boundsButton.height);
                assets.font.setColor(Color.BLACK);

                assets.layout.setText(assets.font, buttonText);
                assets.font.draw(batch, buttonText, boundsButton.x, boundsButton.y + boundsButton.height / 2f + assets.layout.height / 2f, boundsButton.width, Align.center, false);
                assets.font.setColor(Color.WHITE);
            }
        }
    }

    public void toggle() {
        if (isHidden()) {
            show();
        } else {
            hide();
        }
    }

    @Override
    public void show() {
        super.show();

        float finalWindowX = margin_horizontal;
        float finalWindowY = margin_vertical;
        float finalWindowW = camera.viewportWidth  - 2f * margin_horizontal;
        float finalWindowH = camera.viewportHeight - 2f * margin_vertical;

        alpha.setValue(0f);

        transitionComplete = false;
        Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                                .push(
                                        Tween.to(alpha, -1, 0.4f).target(1f)
                                )
                                .push(
                                        Tween.to(bounds, RectangleAccessor.XYWH, 0.5f)
                                                .target(finalWindowX, finalWindowY, finalWindowW, finalWindowH)
                                                .ease(Bounce.OUT)
                                )
                )
                .setCallback((i, baseTween) -> transitionComplete = true)
                .start(tween);
    }


    @Override
    public void hide() {
        float centerX = camera.viewportWidth  / 2f;
        float centerY = camera.viewportHeight / 2f;

        alpha.setValue(1f);

        transitionComplete = false;
        Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                        .push(
                                Tween.to(alpha, -1, 0.4f).target(0f)
                        )
                        .push(
                                Tween.to(bounds, RectangleAccessor.XYWH, 0.5f)
                                        .target(centerX, centerY, 0f, 0f)
                                        .ease(Quad.OUT)
                        )
                )
                .setCallback((i, baseTween) -> ScoreEntryUI.super.hide())
                .start(tween);
    }

}
