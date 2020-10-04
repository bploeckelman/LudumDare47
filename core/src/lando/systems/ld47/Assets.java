package lando.systems.ld47;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Assets implements Disposable {

    private final AssetDescriptor<TextureAtlas> atlasAsset = new AssetDescriptor<>("images/sprites.atlas", TextureAtlas.class);
    private final AssetDescriptor<Texture> pixelTextureAsset = new AssetDescriptor<>("images/pixel.png", Texture.class);
    private final AssetDescriptor<Texture> launchTextureAsset = new AssetDescriptor<>("images/launch.png", Texture.class);
    private final AssetDescriptor<Texture> titleTextureAsset = new AssetDescriptor<>("images/title.png", Texture.class);
    private final AssetDescriptor<BitmapFont> riseFont16Asset = new AssetDescriptor<>("fonts/chevyray-rise-16.fnt", BitmapFont.class);

    public enum Loading { SYNC, ASYNC }

    public AssetManager mgr;
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public GlyphLayout layout;
    public BitmapFont font;
    public boolean initialized;

    public Texture launchImage;
    public Texture titleImage;
    public Texture pixel;

    public TextureAtlas atlas;

    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;
    //public TextureRegion tetradSquare;
    public Animation<TextureRegion> orangeBlock;
    public Animation<TextureRegion> redBlock;
    public Animation<TextureRegion> blueBlock;
    public Animation<TextureRegion> greenBlock;
    public Animation<TextureRegion> shineBlock;

    public Animation<TextureRegion> sasquatch;
    public Animation<TextureRegion> sasquatch_jump;
    public Animation<TextureRegion> sasquatch_throw;
    public Animation<TextureRegion> sasquatch_stun;

    public NinePatch border;
    public NinePatch inset;
    public NinePatch screws;
    public NinePatch speech;

    public ShaderProgram bigBangShader;
    public ShaderProgram cityShader;

    // audio
    public Sound sampleSound;
    public Music moodTrack;

    public Assets() {
        this(Loading.SYNC);
    }

    public Assets(Loading loading) {
        initialized = false;

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();

        mgr = new AssetManager();

        mgr.load(atlasAsset);
        mgr.load(pixelTextureAsset);
        mgr.load(titleTextureAsset);
        mgr.load(launchTextureAsset);
        mgr.load(riseFont16Asset);

        // audio
        mgr.load("audio/sample-sound.wav", Sound.class);
        mgr.load("audio/track-mood.mp3", Music.class);

        if (loading == Loading.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;

        pixel = mgr.get(pixelTextureAsset);
        launchImage = mgr.get(launchTextureAsset);
        titleImage = mgr.get(titleTextureAsset);
        font = mgr.get(riseFont16Asset);

        atlas = mgr.get(atlasAsset);

        whitePixel = atlas.findRegion("white-pixel");
        whiteCircle = atlas.findRegion("white-circle");

        //tetradSquare = atlas.findRegion("tetrad-square");
        blueBlock = new Animation<>(0.1f, atlas.findRegions("block-blue"), Animation.PlayMode.LOOP);
        greenBlock = new Animation<>(0.1f, atlas.findRegions("block-green"), Animation.PlayMode.LOOP);
        shineBlock = new Animation<>(0.1f, atlas.findRegions("block-shine"), Animation.PlayMode.LOOP);
        orangeBlock = new Animation<>(0.1f, atlas.findRegions("block-orange"), Animation.PlayMode.LOOP);
        redBlock = new Animation<>(0.1f, atlas.findRegions("block-red"), Animation.PlayMode.LOOP);

        sasquatch = new Animation<>(0.1f, atlas.findRegions("dime_walk"), Animation.PlayMode.LOOP);
        sasquatch_jump = new Animation<>(0.1f, atlas.findRegions("dime_knock_back"), Animation.PlayMode.LOOP_PINGPONG);
        sasquatch_stun = new Animation<>(0.1f, atlas.findRegions("dime_stun"), Animation.PlayMode.LOOP);
        sasquatch_throw = new Animation<>(0.1f, atlas.findRegions("dime_dead"), Animation.PlayMode.LOOP);


        border = new NinePatch(atlas.findRegion("ninepatches/border"), 9, 9, 9, 9);
        inset  = new NinePatch(atlas.findRegion("ninepatches/inset"), 9, 9, 9, 9);
        screws = new NinePatch(atlas.findRegion("ninepatches/screws"), 7, 7, 7, 7);
        speech = new NinePatch(atlas.findRegion("ninepatches/speech-bubble"), 12, 9, 12, 9);

        bigBangShader = loadShader("shaders/standard.vert", "shaders/big_bang.frag");
        cityShader = loadShader("shaders/standard.vert", "shaders/city_descent.frag");
        loadAudio();
        return 1;
    }

    private void loadAudio() {
        // sounds
        sampleSound = mgr.get("audio/sample-sound.wav", Sound.class);

        // music
        moodTrack = mgr.get("audio/track-mood.mp3", Music.class);

    }

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else if (Config.shaderDebug){
            Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + shaderProgram.getLog());
        }

        return shaderProgram;
    }

    @Override
    public void dispose() {
        mgr.clear();
        batch.dispose();
        shapes.dispose();
    }

}
