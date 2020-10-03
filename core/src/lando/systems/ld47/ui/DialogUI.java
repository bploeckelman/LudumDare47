package lando.systems.ld47.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.Assets;
import lando.systems.ld47.utils.accessors.*;

public class DialogUI extends UserInterface {

    private static final float margin_horizontal = 50f;
    private static final float margin_vertical   = 100f;
    private static final float margin_buttons = 20f;
    private static final float padding_button = 10f;
    private static final float header_height = 40f;

    Camera camera;

    private Vector3 mousePos;
    private MutableFloat alpha;

    private boolean transitionComplete;

    public DialogUI(Assets assets, TweenManager tween, Camera camera) {
        super(assets, tween);

        this.camera = camera;

        this.mousePos = new Vector3();
        this.alpha = new MutableFloat(0f);

        this.transitionComplete = false;

        this.bounds.setPosition(camera.viewportWidth / 2f, camera.viewportHeight / 2f);
    }

    public void update(float dt) {
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(mousePos);

        // TODO: set hover flags based on mouse pos

        // don't allow input until show() tween is fully complete
        if (!transitionComplete) return;

        if (Gdx.input.isTouched()) {
            touchPos.set(mousePos);

            // TODO: do something maybe, if clicked in the right place
        }
    }

    public void draw(SpriteBatch batch) {
        // draw background
        batch.setColor(0x30 / 255f, 0x30 / 255f, 0x30 / 255f, alpha.floatValue());
        batch.draw(assets.whitePixel, bounds.x, bounds.y, bounds.width, bounds.height);

        // draw border
        batch.setColor(1f, 1f, 1f, alpha.floatValue());
        assets.border.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);

        // show inventory contents
        if (transitionComplete) {
            // draw header text
            String headerText = "Header";
            layout.setText(assets.font, headerText, Color.LIGHT_GRAY, bounds.width, Align.center, false);
            assets.font.draw(batch, layout, bounds.x, bounds.y + bounds.height - layout.height / 2f);

            // TODO: move the icon layout to update (when its visible)
            //   so that we have button rectangles to test for clicks

            // draw icons
            int cols = 4;
            float width = bounds.width - (2f * margin_buttons);
            float size =  (width - (cols - 1) * padding_button) / cols;
            float x = bounds.x + margin_buttons;
            float y = bounds.y + bounds.height - header_height - margin_buttons - padding_button - size;
            int col = 0;
//            for (Icon icon : icons) {
//                // draw background
//                batch.draw(assets.whitePixel, x, y, size, size);
//                // draw icon
//                // ...
//                // draw border
//                assets.border.draw(batch, x, y, size, size);
//
//                // advance to the next column
//                col++;
//                x += size + padding_button;
//                if (col % cols == 0) {
//                    // reset column and advance to the next row
//                    col = 0;
//                    x = bounds.x + margin_buttons;
//                    y -= size + padding_button;
//                }
//            }
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
                .setCallback((i, baseTween) -> DialogUI.super.hide())
                .start(tween);
    }

}
