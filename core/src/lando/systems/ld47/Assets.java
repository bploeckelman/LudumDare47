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
    public TextureRegion tetradSquare;

    public NinePatch border;
    public NinePatch inset;
    public NinePatch screws;
    public NinePatch speech;

    // audio
    public Sound sampleSound;
    public Music sampleMusic;

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
        mgr.load("audio/sample-music.wav", Music.class);

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

        tetradSquare = atlas.findRegion("tetrad-square");

        border = new NinePatch(atlas.findRegion("ninepatches/border"), 9, 9, 9, 9);
        inset  = new NinePatch(atlas.findRegion("ninepatches/inset"), 9, 9, 9, 9);
        screws = new NinePatch(atlas.findRegion("ninepatches/screws"), 7, 7, 7, 7);
        speech = new NinePatch(atlas.findRegion("ninepatches/speech-bubble"), 12, 9, 12, 9);

        loadAudio();
        return 1;
    }

    private void loadAudio() {
        // sounds
        sampleSound = mgr.get("audio/sample-sound.wav", Sound.class);

        // music
        sampleMusic = mgr.get("audio/sample-music.wav", Music.class);
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
