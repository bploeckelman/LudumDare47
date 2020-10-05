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

public class SettingsUI extends UserInterface {

    private static final float margin_horizontal = 60f;
    private static final float margin_vertical   = 60f;
    private static final float margin_button     = 20f;

    private static final Color dark_violet   = new Color(150f / 255f, 0f, 1f, 1f);
    private static final Color deep_pink     = new Color(1f, 0f, 193f / 255f, 1f);
    private static final Color deep_sky_blue = new Color(0f, 184f / 255f, 1f, 1f);
    private static final Color aqua          = new Color(0f, 1f, 249f / 255f, 1f);
    private static final Color blue          = new Color(73f / 255f, 0f, 1f, 1f);

    private final Camera camera;
    private final Vector3 mousePos;
    private final MutableFloat alpha;
    private final Rectangle boundsButtonOk;
    private final Rectangle finalWindowBounds;

    private final Color backgroundColor  = blue;
    private final Color headerTextColor  = deep_sky_blue;
    private final Color scoreTextColor   = aqua;
    private final Color buttonColor      = deep_pink;
    private final Color buttonHoverColor = dark_violet;

    private boolean buttonHoveredOk;
    private boolean transitionComplete;

    private final String headerText = "Settings";
    private final String buttonTextOk = "Ok";

    public SettingsUI(BaseScreen screen) {
        super(screen);

        this.camera = screen.hudCamera;

        this.mousePos = new Vector3();
        this.alpha = new MutableFloat(0f);

        this.buttonHoveredOk = false;
        this.transitionComplete = false;

        this.bounds.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f, 0f);

        this.finalWindowBounds = new Rectangle(
                margin_horizontal, margin_vertical,
                camera.viewportWidth - 2f * margin_horizontal,
                camera.viewportHeight - 2f * margin_vertical);

        float buttonWidth = (1f / 3f) * finalWindowBounds.width;
        this.boundsButtonOk = new Rectangle(
                finalWindowBounds.x + finalWindowBounds.width / 2f - buttonWidth / 2f,
                finalWindowBounds.y + margin_button, buttonWidth, 80f);
    }

    @Override
    public void update(float dt) {
        if (isHidden()) return;

        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(mousePos);

        buttonHoveredOk = boundsButtonOk.contains(mousePos.x, mousePos.y);

        // don't allow input until show() tween is fully complete
        if (!transitionComplete) return;

        if (Gdx.input.isTouched()) {
            touchPos.set(mousePos);

            if (boundsButtonOk.contains(touchPos.x, touchPos.y)) {
                gameState.gameScreen.unpause();
                hide();
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, Rectangle hudBounds) {
        if (isHidden()) return;

        // draw background
        {
            batch.setColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, alpha.floatValue());
            batch.draw(assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);
            batch.setColor(Color.WHITE);

            batch.setColor(1f, 1f, 1f, alpha.floatValue());
            assets.screws.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
            batch.setColor(Color.WHITE);
        }

        // draw content
        if (transitionComplete) {
            // header text
            float headerPosY, headerHeight;
            {
                assets.font.getData().setScale(1.5f);
                layout.setText(assets.font, headerText, scoreTextColor, bounds.width, Align.center, false);
                headerHeight = layout.height;
                headerPosY = bounds.y + bounds.height - headerHeight / 2f;
                assets.font.draw(batch, layout, bounds.x, headerPosY);
                assets.font.getData().setScale(0.7f);
            }

            // actual settings toggles
            {
                // TODO
            }

            // ok button
            {
                batch.setColor(buttonHoveredOk ? buttonHoverColor : buttonColor);
                batch.draw(assets.whitePixel, boundsButtonOk.x, boundsButtonOk.y, boundsButtonOk.width, boundsButtonOk.height);
                assets.screws.draw(batch, boundsButtonOk.x, boundsButtonOk.y, boundsButtonOk.width, boundsButtonOk.height);
                batch.setColor(1f, 1f, 1f, 1f);

                assets.font.getData().setScale(1.1f);
                assets.font.setColor(Color.WHITE);
                layout.setText(assets.font, buttonTextOk);
                assets.font.draw(batch, buttonTextOk, boundsButtonOk.x, boundsButtonOk.y + boundsButtonOk.height / 2f + layout.height / 2f, boundsButtonOk.width, Align.center, false);
                assets.font.setColor(1f, 1f, 1f, 1f);
                assets.font.getData().setScale(0.7f);
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
                                                .target(finalWindowBounds.x, finalWindowBounds.y, finalWindowBounds.width, finalWindowBounds.height)
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
                .setCallback((i, baseTween) -> SettingsUI.super.hide())
                .start(tween);
    }

}
