package lando.systems.ld47.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.screens.BaseScreen;
import lando.systems.ld47.utils.accessors.*;

public class ScoreEntryUI extends UserInterface implements InputProcessor {

    private static final float margin_horizontal = 60f;
    private static final float margin_vertical   = 140f;
    private static final float margin_button     = 20f;
    private static final int   max_name_length   = 17;

    private static final Color dark_violet   = new Color(150f / 255f, 0f, 1f, 1f);
    private static final Color deep_pink     = new Color(1f, 0f, 193f / 255f, 1f);
    private static final Color deep_sky_blue = new Color(0f, 184f / 255f, 1f, 1f);
    private static final Color aqua          = new Color(0f, 1f, 249f / 255f, 1f);
    private static final Color blue          = new Color(73f / 255f, 0f, 1f, 1f);

    private static final String buttonText = "submit score";
    private static final String namePromptText = "type your name and click 'submit':";

    private final int score;
    private final int rank;

    private Camera camera;
    private Vector3 mousePos;
    private MutableFloat alpha;
    private Rectangle finalWindowBounds;
    private Rectangle boundsNameEntry;
    private Rectangle boundsButtonSubmit;
    private Rectangle boundsButtonCancel;

    private Color backgroundColor  = blue;
    private Color headerTextColor  = deep_sky_blue;
    private Color scoreTextColor   = aqua;
    private Color buttonColor      = deep_pink;
    private Color buttonHoverColor = dark_violet;

    private String name;
    private boolean buttonHoveredSubmit;
    private boolean buttonHoveredCancel;
    private boolean scoreSubmitted;
    private boolean transitionComplete;

    // 4 rows
    // ---------------------------
    // - header
    // - score achieved (rank X/Y)
    // - name entry
    // - submit button

    public ScoreEntryUI(BaseScreen screen, int score, int rank) {
        super(screen);

        this.camera = screen.hudCamera;
        this.score = score;
        this.rank = rank;

        this.mousePos = new Vector3();
        this.alpha = new MutableFloat(0f);

        this.buttonHoveredSubmit = false;
        this.buttonHoveredCancel = false;
        this.scoreSubmitted = false;
        this.transitionComplete = false;

        this.name = screen.assets.playerName;

        this.bounds.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f, 0f);

        this.finalWindowBounds = new Rectangle(
                margin_horizontal, margin_vertical,
                camera.viewportWidth - 2f * margin_horizontal,
                camera.viewportHeight - 2f * margin_vertical);

        float buttonWidth = (1f / 3f) * finalWindowBounds.width;
        this.boundsButtonSubmit = new Rectangle(
                finalWindowBounds.x + finalWindowBounds.width / 2f - buttonWidth,
                finalWindowBounds.y + margin_button, buttonWidth, 80f);

        this.boundsButtonCancel = new Rectangle(
                finalWindowBounds.x + finalWindowBounds.width / 2f,
                finalWindowBounds.y + margin_button, buttonWidth, 80f);

        float entryHeight = 120f;
        float entryWidth = (4f / 5f) * finalWindowBounds.width;
        this.boundsNameEntry = new Rectangle(
                finalWindowBounds.x + finalWindowBounds.width / 2f - entryWidth / 2f,
                bounds.y + bounds.height / 2f - entryHeight / 2f - margin_button,
                entryWidth, entryHeight);
    }

    public void update(float dt) {
        if (isHidden()) return;

        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(mousePos);

        buttonHoveredSubmit = boundsButtonSubmit.contains(mousePos.x, mousePos.y);
        buttonHoveredCancel = boundsButtonCancel.contains(mousePos.x, mousePos.y);

        // don't allow input until show() tween is fully complete
        if (!transitionComplete) return;

        if (Gdx.input.justTouched()) {
            touchPos.set(mousePos);

            if (boundsButtonCancel.contains(touchPos.x, touchPos.y)) {
                // disable this class as an input processor
                Gdx.input.setInputProcessor(null);
                hide();
            } else if (!scoreSubmitted && boundsButtonSubmit.contains(touchPos.x, touchPos.y)) {
                screen.leaderboardService.postScore(name, score);
                scoreSubmitted = true;
                screen.assets.savePlayerName(name);
                // disable this class as an input processor
                Gdx.input.setInputProcessor(null);
                hide();
            }
        }
    }

    public void draw(SpriteBatch batch, Rectangle hudBounds) {
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
            float headerPosY, headerHeight;
            // header text
            {
                // TODO: rank is a hassle, either figure it out later or skip it
                String headerText = "final score: " + score;// + "    rank: " + rank;
                assets.bladeFont64.getData().setScale(1f);
                layout.setText(assets.bladeFont64, headerText, scoreTextColor, bounds.width, Align.center, false);
                headerHeight = layout.height;
                headerPosY = bounds.y + bounds.height - headerHeight / 2f;
                assets.bladeFont64.draw(batch, layout, bounds.x, headerPosY);
                assets.bladeFont64.getData().setScale(1f);
            }

            // score and rank
            {
                assets.bladeFont64.getData().setScale(.7f);
                layout.setText(assets.bladeFont64, namePromptText, Color.BLACK, bounds.width, Align.center, false);
                float scoreRankPosY = headerPosY - headerHeight - margin_button - layout.height;
                assets.bladeFont64.draw(batch, layout, bounds.x, scoreRankPosY);
                assets.bladeFont64.getData().setScale(1f);
            }

            // name entry field
            {
                batch.setColor(Color.WHITE);
                batch.draw(assets.whitePixel, boundsNameEntry.x, boundsNameEntry.y, boundsNameEntry.width, boundsNameEntry.height);
                assets.screws.draw(batch, boundsNameEntry.x, boundsNameEntry.y, boundsNameEntry.width, boundsNameEntry.height);

                assets.bladeFont64.getData().setScale(1.1f);
                assets.bladeFont64.setColor(Color.DARK_GRAY);
                layout.setText(assets.bladeFont64, name.toLowerCase());
                assets.bladeFont64.draw(batch, name.toLowerCase(), boundsNameEntry.x, boundsNameEntry.y + boundsNameEntry.height / 2f + layout.height / 2f, boundsNameEntry.width, Align.center, false);
                assets.bladeFont64.setColor(Color.WHITE);
                assets.bladeFont64.getData().setScale(1f);
            }

            // score submission & cancel buttons
            {
                batch.setColor(buttonHoveredSubmit ? buttonHoverColor : buttonColor);
                batch.draw(assets.whitePixel, boundsButtonSubmit.x, boundsButtonSubmit.y, boundsButtonSubmit.width, boundsButtonSubmit.height);
                assets.screws.draw(batch, boundsButtonSubmit.x, boundsButtonSubmit.y, boundsButtonSubmit.width, boundsButtonSubmit.height);
                batch.setColor(1f, 1f, 1f, 1f);

                assets.bladeFont64.getData().setScale(.5f);
                assets.bladeFont64.setColor(Color.WHITE);
                layout.setText(assets.bladeFont64, buttonText);
                assets.bladeFont64.draw(batch, buttonText, boundsButtonSubmit.x, boundsButtonSubmit.y + boundsButtonSubmit.height / 2f + layout.height / 2f, boundsButtonSubmit.width - 25f, Align.right, false);
                assets.bladeFont64.setColor(1f, 1f, 1f, 1f);
                assets.bladeFont64.getData().setScale(1f);
                batch.draw(assets.uploadIcon, boundsButtonSubmit.x + 25f, boundsButtonSubmit.y + boundsButtonSubmit.height / 2f - assets.uploadIcon.getRegionHeight() / 2f);

                batch.setColor(buttonHoveredCancel ? buttonHoverColor : buttonColor);
                batch.draw(assets.whitePixel, boundsButtonCancel.x, boundsButtonCancel.y, boundsButtonCancel.width, boundsButtonCancel.height);
                assets.screws.draw(batch, boundsButtonCancel.x, boundsButtonCancel.y, boundsButtonCancel.width, boundsButtonCancel.height);
                batch.setColor(1f, 1f, 1f, 1f);

                assets.bladeFont64.getData().setScale(.6f);
                assets.bladeFont64.setColor(Color.WHITE);
                layout.setText(assets.bladeFont64, "Cancel");
                assets.bladeFont64.draw(batch, "Cancel", boundsButtonCancel.x, boundsButtonCancel.y + boundsButtonCancel.height / 2f + layout.height / 2f, boundsButtonCancel.width, Align.center, false);
                assets.bladeFont64.setColor(1f, 1f, 1f, 1f);
                assets.bladeFont64.getData().setScale(1f);
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
                .setCallback((i, baseTween) -> ScoreEntryUI.super.hide())
                .start(tween);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // handle backspace
        if (character == (char) 8 && !name.isEmpty()) {
            name = name.substring(0, name.length() - 1);
            if (name.isEmpty()) {
                name = "anonymous";
            }
            return true;
        }

        // handle delete
        if (character == (char) 127) {
            name = "anonymous";
            return true;
        }

        // sanity checks
        if (!Character.isLetterOrDigit(character) && character != ' ') return false;
        if (name.length() >= max_name_length) return false;

        // clear the placeholder if they start typing
        if (name.equalsIgnoreCase("anonymous")) {
            name = "";
        }

        // actually add the new character
        name += character;

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
