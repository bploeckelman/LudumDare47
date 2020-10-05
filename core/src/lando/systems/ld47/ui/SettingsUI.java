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

    static class Toggle {
        public final String description;
        public Rectangle bounds;
        public Rectangle boundsText;
        public Rectangle boundsOn;
        public Rectangle boundsOff;
        public boolean enabled;
        public Toggle(String description) {
            this.description = description;
            this.bounds = new Rectangle();
            this.boundsText = new Rectangle();
            this.boundsOn = new Rectangle();
            this.boundsOff = new Rectangle();
            this.enabled = true;
        }
    }
    private final Toggle musicToggle;
    private final Toggle soundToggle;
    private final Toggle ghostToggle;
    private final float columnWidth;
    private final float columnHeight;

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

        int rows = 3;
        float headerHeight = 50f; // ??? just a guess
        float rowMargin = 20f;
        float yTop = finalWindowBounds.y + finalWindowBounds.height - headerHeight - rowMargin;
        float yBottom = boundsButtonOk.y + boundsButtonOk.height + rowMargin;
        float availableHeight = yTop - yBottom;
        float rowHeight = (availableHeight - ((rows - 1) * rowMargin)) / (float) rows;
        float rowWidth = (finalWindowBounds.width - 2f * rowMargin);
        float inset = 20f;
        this.columnWidth = (1f / 3f) * rowWidth;
        this.columnHeight = rowHeight;


        this.ghostToggle = new Toggle("Ghost outline");
        this.ghostToggle.bounds.set(finalWindowBounds.x + rowMargin, yBottom, rowWidth, rowHeight);
        this.ghostToggle.boundsText.set(ghostToggle.bounds.x + 0f * columnWidth + inset, ghostToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);
        this.ghostToggle.boundsOn  .set(ghostToggle.bounds.x + 1f * columnWidth + inset, ghostToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);
        this.ghostToggle.boundsOff .set(ghostToggle.bounds.x + 2f * columnWidth + inset, ghostToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);

        this.soundToggle = new Toggle("Sound fx");
        this.soundToggle.bounds.set(finalWindowBounds.x + rowMargin, yBottom + rowHeight, rowWidth, rowHeight);
        this.soundToggle.boundsText.set(soundToggle.bounds.x + 0f * columnWidth + inset, soundToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);
        this.soundToggle.boundsOn  .set(soundToggle.bounds.x + 1f * columnWidth + inset, soundToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);
        this.soundToggle.boundsOff .set(soundToggle.bounds.x + 2f * columnWidth + inset, soundToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);

        this.musicToggle = new Toggle("Music");
        this.musicToggle.bounds.set(finalWindowBounds.x + rowMargin, yBottom + 2f * rowHeight, rowWidth, rowHeight);
        this.musicToggle.boundsText.set(musicToggle.bounds.x + 0f * columnWidth + inset, musicToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);
        this.musicToggle.boundsOn  .set(musicToggle.bounds.x + 1f * columnWidth + inset, musicToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);
        this.musicToggle.boundsOff .set(musicToggle.bounds.x + 2f * columnWidth + inset, musicToggle.bounds.y + inset, columnWidth - 2f * inset, columnHeight - 2f * inset);
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

            if (soundToggle.boundsOn.contains(touchPos.x, touchPos.y)) {
                soundToggle.enabled = true;
                gameState.enableSounds();
            } else if (soundToggle.boundsOff.contains(touchPos.x, touchPos.y)) {
                soundToggle.enabled = false;
                gameState.disableSounds();
            }

            if (musicToggle.boundsOn.contains(touchPos.x, touchPos.y)) {
                musicToggle.enabled = true;
                gameState.startMusic();
            } else if (musicToggle.boundsOff.contains(touchPos.x, touchPos.y)) {
                musicToggle.enabled = false;
                gameState.stopMusic();
            }

            if (ghostToggle.boundsOn.contains(touchPos.x, touchPos.y)) {
                ghostToggle.enabled = true;
                gameState.showGhost();
            } else if (ghostToggle.boundsOff.contains(touchPos.x, touchPos.y)) {
                ghostToggle.enabled = false;
                gameState.hideGhost();
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
                // music toggle
                {
                    // description text
                    assets.font.getData().setScale(1.6f);
                    Color musicTextColor = (musicToggle.enabled) ? deep_sky_blue : Color.DARK_GRAY;
                    layout.setText(assets.font, musicToggle.description, musicTextColor, musicToggle.boundsText.width, Align.center, false);
                    assets.font.draw(batch, layout, musicToggle.boundsText.x, musicToggle.boundsText.y + musicToggle.boundsText.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);

                    // "on" button
                    if (musicToggle.enabled) {
                        batch.setColor(aqua);
                        batch.draw(assets.whitePixel, musicToggle.boundsOn.x, musicToggle.boundsOn.y, musicToggle.boundsOn.width, musicToggle.boundsOn.height);
                    }
                    batch.setColor(Color.WHITE);
                    assets.screws.draw(batch, musicToggle.boundsOn.x, musicToggle.boundsOn.y, musicToggle.boundsOn.width, musicToggle.boundsOn.height);

                    // "on" button text
                    assets.font.getData().setScale(1f);
                    Color musicButtonOnTextColor = (musicToggle.enabled) ? deep_pink : Color.DARK_GRAY;
                    layout.setText(assets.font, "On", musicButtonOnTextColor, musicToggle.boundsOn.width, Align.center, false);
                    assets.font.draw(batch, layout, musicToggle.boundsOn.x, musicToggle.boundsOn.y + musicToggle.boundsOn.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);

                    // "off" button
                    if (!musicToggle.enabled) {
                        batch.setColor(aqua);
                        batch.draw(assets.whitePixel, musicToggle.boundsOff.x, musicToggle.boundsOff.y, musicToggle.boundsOff.width, musicToggle.boundsOff.height);
                    }
                    batch.setColor(Color.WHITE);
                    assets.screws.draw(batch, musicToggle.boundsOff.x, musicToggle.boundsOff.y, musicToggle.boundsOff.width, musicToggle.boundsOff.height);

                    // "off" button text
                    assets.font.getData().setScale(1f);
                    Color musicButtonOffTextColor = (!musicToggle.enabled) ? deep_pink : Color.DARK_GRAY;
                    layout.setText(assets.font, "Off", musicButtonOffTextColor, musicToggle.boundsOff.width, Align.center, false);
                    assets.font.draw(batch, layout, musicToggle.boundsOff.x, musicToggle.boundsOff.y + musicToggle.boundsOff.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);
                }

                // sound fx toggle
                {
                    // description text
                    assets.font.getData().setScale(1.6f);
                    Color soundTextColor = (soundToggle.enabled) ? deep_sky_blue : Color.DARK_GRAY;
                    layout.setText(assets.font, soundToggle.description, soundTextColor, soundToggle.boundsText.width, Align.center, false);
                    assets.font.draw(batch, layout, soundToggle.boundsText.x, soundToggle.boundsText.y + soundToggle.boundsText.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);

                    // "on" button
                    if (soundToggle.enabled) {
                        batch.setColor(aqua);
                        batch.draw(assets.whitePixel, soundToggle.boundsOn.x, soundToggle.boundsOn.y, soundToggle.boundsOn.width, soundToggle.boundsOn.height);
                    }
                    batch.setColor(Color.WHITE);
                    assets.screws.draw(batch, soundToggle.boundsOn.x, soundToggle.boundsOn.y, soundToggle.boundsOn.width, soundToggle.boundsOn.height);

                    // "on" button text
                    assets.font.getData().setScale(1f);
                    Color soundButtonOnTextColor = (soundToggle.enabled) ? deep_pink : Color.DARK_GRAY;
                    layout.setText(assets.font, "On", soundButtonOnTextColor, soundToggle.boundsOn.width, Align.center, false);
                    assets.font.draw(batch, layout, soundToggle.boundsOn.x, soundToggle.boundsOn.y + soundToggle.boundsOn.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);

                    // "off" button
                    if (!soundToggle.enabled) {
                        batch.setColor(aqua);
                        batch.draw(assets.whitePixel, soundToggle.boundsOff.x, soundToggle.boundsOff.y, soundToggle.boundsOff.width, soundToggle.boundsOff.height);
                    }
                    batch.setColor(Color.WHITE);
                    assets.screws.draw(batch, soundToggle.boundsOff.x, soundToggle.boundsOff.y, soundToggle.boundsOff.width, soundToggle.boundsOff.height);

                    // "off" button text
                    assets.font.getData().setScale(1f);
                    Color soundButtonOffTextColor = (!soundToggle.enabled) ? deep_pink : Color.DARK_GRAY;
                    layout.setText(assets.font, "Off", soundButtonOffTextColor, soundToggle.boundsOff.width, Align.center, false);
                    assets.font.draw(batch, layout, soundToggle.boundsOff.x, soundToggle.boundsOff.y + soundToggle.boundsOff.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);
                }

                // - ghost outline toggle (on, off)
                {
                    // description text
                    assets.font.getData().setScale(1.25f);
                    Color ghostTextColor = (ghostToggle.enabled) ? deep_sky_blue : Color.DARK_GRAY;
                    layout.setText(assets.font, ghostToggle.description, ghostTextColor, ghostToggle.boundsText.width, Align.center, false);
                    assets.font.draw(batch, layout, ghostToggle.boundsText.x, ghostToggle.boundsText.y + ghostToggle.boundsText.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);

                    // "on" button
                    if (ghostToggle.enabled) {
                        batch.setColor(aqua);
                        batch.draw(assets.whitePixel, ghostToggle.boundsOn.x, ghostToggle.boundsOn.y, ghostToggle.boundsOn.width, ghostToggle.boundsOn.height);
                    }
                    batch.setColor(Color.WHITE);
                    assets.screws.draw(batch, ghostToggle.boundsOn.x, ghostToggle.boundsOn.y, ghostToggle.boundsOn.width, ghostToggle.boundsOn.height);

                    // "on" button text
                    assets.font.getData().setScale(1f);
                    Color ghostButtonOnTextColor = (ghostToggle.enabled) ? deep_pink : Color.DARK_GRAY;
                    layout.setText(assets.font, "On", ghostButtonOnTextColor, ghostToggle.boundsOn.width, Align.center, false);
                    assets.font.draw(batch, layout, ghostToggle.boundsOn.x, ghostToggle.boundsOn.y + ghostToggle.boundsOn.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);

                    // "off" button
                    if (!ghostToggle.enabled) {
                        batch.setColor(aqua);
                        batch.draw(assets.whitePixel, ghostToggle.boundsOff.x, ghostToggle.boundsOff.y, ghostToggle.boundsOff.width, ghostToggle.boundsOff.height);
                    }
                    batch.setColor(Color.WHITE);
                    assets.screws.draw(batch, ghostToggle.boundsOff.x, ghostToggle.boundsOff.y, ghostToggle.boundsOff.width, ghostToggle.boundsOff.height);

                    // "off" button text
                    assets.font.getData().setScale(1f);
                    Color ghostButtonOffTextColor = (!ghostToggle.enabled) ? deep_pink : Color.DARK_GRAY;
                    layout.setText(assets.font, "Off", ghostButtonOffTextColor, ghostToggle.boundsOff.width, Align.center, false);
                    assets.font.draw(batch, layout, ghostToggle.boundsOff.x, ghostToggle.boundsOff.y + ghostToggle.boundsOff.height / 2f + layout.height / 2f);
                    assets.font.getData().setScale(0.7f);

                }
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
