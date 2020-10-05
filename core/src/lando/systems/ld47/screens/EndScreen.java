package lando.systems.ld47.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld47.Config;
import lando.systems.ld47.Game;
import lando.systems.ld47.ui.ScoreEntryUI;
import lando.systems.ld47.ui.typinglabel.TypingLabel;

public class EndScreen extends BaseScreen {

    private static final Color dark_violet   = new Color(150f / 255f, 0f, 1f, 1f);
    private static final Color deep_pink     = new Color(1f, 0f, 193f / 255f, 1f);

    private TypingLabel titleLabel;
    private TypingLabel themeLabel;
    private TypingLabel leftCreditLabel;
    private TypingLabel rightCreditLabel;
    private TypingLabel thanksLabel;
    private TypingLabel disclaimerLabel;

    private Animation<TextureRegion> catAnimation;
    private Animation<TextureRegion> dogAnimation;

    static String title = "{GRADIENT=purple;cyan}Block Runner 2049{ENDGRADIENT}";
    static String theme = "Made for Ludum Dare 47: Stuck in a Loop";

    static String thanks = "{GRADIENT=purple;cyan}Thanks for playing our game!{ENDGRADIENT}";
    static String developers = "{COLOR=gray}Developed by:{COLOR=white}\n Doug Graham\n Brian Ploeckelman\n Brian Rossman\n Jeffrey Hwang";
    static String artists = "{COLOR=gray}Art by:{COLOR=white}\n Matt Neumann";
    static String emotionalSupport = "{COLOR=cyan}Emotional Support:{COLOR=white}\n Asuka and  Cherry";
    static String music = "{COLOR=gray}Sound by:{COLOR=white}\n Pete Valeo";
    static String libgdx = "Made with {COLOR=red}<3{COLOR=white} and LibGDX";
    static String disclaimer = "Disclaimer!!!\nNo animals were harmed in the making of this game (they got lots of pets tho)";//Pets were not harmed in making of this game.";

    private final ScoreEntryUI scoreEntryUI;
    private final ShaderProgram shader;
    private float accum = 0f;

    private final Vector3 mousePos;
    private final Vector3 touchPos;
    private final Rectangle boundsButtonRestart;
    private boolean buttonHoveredRestart;

    public EndScreen(Game game, int currentScore, int currentRank) {
        super(game);
        titleLabel = new TypingLabel(assets.bladeFont64, title.toLowerCase(), 0f, Config.windowHeight / 2f + 290f);
        titleLabel.setWidth(Config.windowWidth);
        titleLabel.setFontScale(1f);

        themeLabel = new TypingLabel(assets.bladeFont32, theme.toLowerCase(), 0f, Config.windowHeight / 2f + 220f);
        themeLabel.setWidth(Config.windowWidth);
        themeLabel.setFontScale(1f);

        leftCreditLabel = new TypingLabel(assets.bladeFont32, developers.toLowerCase() + "\n\n" + emotionalSupport.toLowerCase() + "\n\n", 75f, Config.windowHeight / 2f + 135f);
        leftCreditLabel.setWidth(Config.windowWidth / 2f - 150f);
        leftCreditLabel.setLineAlign(Align.left);
        leftCreditLabel.setFontScale(1f);
        catAnimation = assets.cat;
        dogAnimation = assets.dog;

        rightCreditLabel = new TypingLabel(assets.bladeFont32, artists.toLowerCase() + "\n\n" + music.toLowerCase() + "\n\n" + libgdx.toLowerCase(), Config.windowWidth / 2 + 75f, Config.windowHeight / 2f + 135f);
        rightCreditLabel.setWidth(Config.windowWidth / 2f - 150f);
        rightCreditLabel.setLineAlign(Align.left);
        rightCreditLabel.setFontScale(1f);

        thanksLabel = new TypingLabel(assets.bladeFont32, thanks.toLowerCase(), 0f, 115f);
        thanksLabel.setWidth(Config.windowWidth);
        thanksLabel.setLineAlign(Align.center);
        thanksLabel.setFontScale(1f);

        disclaimerLabel = new TypingLabel(assets.bladeFont32, "{JUMP=.2}{WAVE=0.9;1.2;1.75}{RAINBOW}" + disclaimer + "{ENDRAINBOW}{ENDWAVE}{ENDJUMP}", 0f, 60f);
        disclaimerLabel.setWidth(Config.windowWidth);
        thanksLabel.setLineAlign(Align.center);
        disclaimerLabel.setFontScale(.7f);

        scoreEntryUI = new ScoreEntryUI(this, currentScore, currentRank);
        if (currentScore != 0) {
            scoreEntryUI.show();
            Gdx.input.setInputProcessor(scoreEntryUI);
        }

        shader = game.assets.cityShader;

        this.mousePos = new Vector3();
        this.touchPos = new Vector3();
        this.buttonHoveredRestart = false;
        float margin = 10f;
        float buttonSize = 80f;
        this.boundsButtonRestart = new Rectangle(margin, hudCamera.viewportHeight - buttonSize - margin, buttonSize, buttonSize);
    }

    @Override
    public void update(float dt) {
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        hudCamera.unproject(mousePos);

        buttonHoveredRestart = boundsButtonRestart.contains(mousePos.x, mousePos.y);

        if (scoreEntryUI.isHidden() && Gdx.input.justTouched()) {
            touchPos.set(mousePos);

            if (boundsButtonRestart.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new GameScreen(game));
            }
        }

        accum += dt;
        titleLabel.update(dt);
        themeLabel.update(dt);
        leftCreditLabel.update(dt);
        rightCreditLabel.update(dt);
        thanksLabel.update(dt);
        disclaimerLabel.update(dt);
        scoreEntryUI.update(dt);
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

        batch.setShader(null);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            batch.setColor(0f, 0f, 0f, 0.6f);
            batch.draw(assets.whitePixel, 25f, 130f, Config.windowWidth / 2f - 50f, 400f);
            batch.draw(assets.whitePixel, Config.windowWidth / 2f + 25f, 130f, Config.windowWidth / 2f - 50f, 400f);

            batch.setColor(Color.WHITE);
            titleLabel.render(batch);
            themeLabel.render(batch);
            leftCreditLabel.render(batch);
            rightCreditLabel.render(batch);
            thanksLabel.render(batch);
            disclaimerLabel.render(batch);
            if (accum > 7.5) {
                TextureRegion catTexture = catAnimation.getKeyFrame(accum);
                TextureRegion dogTexture = dogAnimation.getKeyFrame(accum);
                batch.draw(catTexture, 330f, 215f);
                batch.draw(dogTexture, 60f, 210f);
            }

            batch.setColor((buttonHoveredRestart) ? deep_pink : dark_violet);
            batch.draw(assets.restartIcon, boundsButtonRestart.x, boundsButtonRestart.y, boundsButtonRestart.width, boundsButtonRestart.height);
            batch.setColor(Color.WHITE);

            scoreEntryUI.draw(batch, null);
        }
        batch.end();
    }

}
