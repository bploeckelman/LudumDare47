package lando.systems.ld47;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

public class Assets implements Disposable {

    private final AssetDescriptor<TextureAtlas> atlasAsset = new AssetDescriptor<>("images/sprites.atlas", TextureAtlas.class);
    private final AssetDescriptor<Texture> pixelTextureAsset = new AssetDescriptor<>("images/pixel.png", Texture.class);
    private final AssetDescriptor<Texture> blockTextureAsset = new AssetDescriptor<>("images/blocks.png", Texture.class);
    private final AssetDescriptor<Texture> titleTextureAsset = new AssetDescriptor<>("images/title.png", Texture.class);
    private final AssetDescriptor<Texture> titleSillyTextureAsset = new AssetDescriptor<>("images/title-block-and-deckard.png", Texture.class);
    private final AssetDescriptor<Texture> controlsTextureAsset = new AssetDescriptor<>("images/controls.png", Texture.class);
    private final AssetDescriptor<Texture> noiseTextureAsset = new AssetDescriptor<>("images/noise.png", Texture.class);
    private final AssetDescriptor<BitmapFont> riseFont16Asset = new AssetDescriptor<>("fonts/chevyray-rise-16.fnt", BitmapFont.class);
    private final AssetDescriptor<BitmapFont> bladeFont16Asset = new AssetDescriptor<>("fonts/blade-runner-16.fnt", BitmapFont.class);
    private final AssetDescriptor<BitmapFont> bladeFont32Asset = new AssetDescriptor<>("fonts/blade-runner-32.fnt", BitmapFont.class);
    private final AssetDescriptor<BitmapFont> bladeFont64Asset = new AssetDescriptor<>("fonts/blade-runner-64.fnt", BitmapFont.class);

    public enum Loading { SYNC, ASYNC }

    public Preferences prefs;
    public AssetManager mgr;
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public GlyphLayout layout;
    public BitmapFont font;
    public BitmapFont bladeFont16;
    public BitmapFont bladeFont32;
    public BitmapFont bladeFont64;
    public boolean initialized;

    public Texture titleImage;
    public Texture titleSillyImage;
    public Texture controlsImage;
    public Texture pixel;
    public Texture blockTextures;
    public Texture noiseTexture;

    public TextureAtlas atlas;

    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;
    public TextureRegion gameBoardTexture;
    public TextureRegion gamepadIcon;
    public TextureRegion settingsIcon;
    public TextureRegion uploadIcon;
    public TextureRegion restartIcon;
    public TextureRegion[] blockFaces;
    //public TextureRegion tetradSquare;
    public Animation<TextureRegion> orangeBlock;
    public Animation<TextureRegion> redBlock;
    public Animation<TextureRegion> blueBlock;
    public Animation<TextureRegion> greenBlock;
    public Animation<TextureRegion> shineBlock;

    public Animation<TextureRegion> car;
    public Animation<TextureRegion> carUp;
    public Animation<TextureRegion> carDown;
    public Animation<TextureRegion> carShoot;
    public Animation<TextureRegion> carStun;
    public Animation<TextureRegion> shot;

    public Animation<TextureRegion> cat;
    public Animation<TextureRegion> dog;

    public ObjectMap<Integer, Animation<TextureRegion>> fontPoints;

    public NinePatch border;
    public NinePatch inset;
    public NinePatch screws;
    public NinePatch speech;

    public ShaderProgram bigBangShader;
    public ShaderProgram cityShader;
    public ShaderProgram cityShader2;

    public ShaderProgram blockShader;
    public ShaderProgram boardShader;

    public Array<ShaderProgram> randomTransitions;
    public ShaderProgram blindsShader;
    public ShaderProgram fadeShader;
    public ShaderProgram radialShader;
    public ShaderProgram doomShader;
    public ShaderProgram pizelizeShader;
    public ShaderProgram doorwayShader;
    public ShaderProgram crosshatchShader;
    public ShaderProgram rippleShader;
    public ShaderProgram heartShader;
    public ShaderProgram stereoShader;
    public ShaderProgram circleCropShader;
    public ShaderProgram cubeShader;
    public ShaderProgram dreamyShader;

    // audio
    public Sound sampleSound;
    public Sound chaChingSound;
    public Sound rotateNew;
    public Sound hold;
    public Sound error;
    public Sound explode;
    public Sound crash;
    public Sound carTeleportWhooshUp;
    public Sound carFart;
    public Sound carShootCharge;
    public Sound blockLaserChargeRelease;
    public Sound blockMechanicalGrowl;
    public Sound blockLaserPluck;
    public Music introTrack;
    public Music moodTrack;
    public Music moodTrack2;
    public Music moodTrack3;
    public Music bladeRunner;

    public String playerName;

    public Assets() {
        this(Loading.SYNC);
    }

    public Assets(Loading loading) {
        initialized = false;

        prefs = Gdx.app.getPreferences("BlockRunner");

        this.playerName = prefs.getString("name", "anonymous");

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();

        mgr = new AssetManager();

        mgr.load(atlasAsset);
        mgr.load(pixelTextureAsset);
        mgr.load(blockTextureAsset);
        mgr.load(titleTextureAsset);
        mgr.load(titleSillyTextureAsset);
        mgr.load(controlsTextureAsset);
        mgr.load(noiseTextureAsset);
        mgr.load(riseFont16Asset);
        mgr.load(bladeFont16Asset);
        mgr.load(bladeFont32Asset);
        mgr.load(bladeFont64Asset);

        // audio
        mgr.load("audio/sample-sound.wav", Sound.class);
        mgr.load("audio/chaching.mp3",Sound.class);
        mgr.load("audio/track-mood.mp3", Music.class);
        mgr.load("audio/track-mood-2.mp3", Music.class);
        mgr.load("audio/track-mood-3.mp3", Music.class);
        mgr.load("audio/track-blade.mp3", Music.class);
        mgr.load("audio/track-blade-1-3.mp3", Music.class);
        mgr.load("audio/intro.mp3", Music.class);
        mgr.load("audio/block_sounds/rotate-new.wav", Sound.class);
        mgr.load("audio/block_sounds/hold.wav", Sound.class);
        mgr.load("audio/block_sounds/error.wav", Sound.class);
        mgr.load("audio/block_sounds/explode.wav", Sound.class);
        mgr.load("audio/car_ramming/crash5.wav", Sound.class);
        mgr.load("audio/car_teleport/car-whoosh-up.wav", Sound.class);
        mgr.load("audio/car_moving/carfart.wav", Sound.class);
        mgr.load("audio/car_shooting/charge2.wav", Sound.class);
        mgr.load("audio/block_sounds/laser-charge-release.wav", Sound.class);
        mgr.load("audio/block_sounds/mechanical-growl.wav", Sound.class);
        mgr.load("audio/block_sounds/laserpluck.wav", Sound.class);

        if (loading == Loading.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;

        pixel = mgr.get(pixelTextureAsset);
        titleImage = mgr.get(titleTextureAsset);
        titleSillyImage = mgr.get(titleSillyTextureAsset);
        controlsImage = mgr.get(controlsTextureAsset);
        font = mgr.get(riseFont16Asset);
        bladeFont16 = mgr.get(bladeFont16Asset);
        bladeFont32 = mgr.get(bladeFont32Asset);
        bladeFont64 = mgr.get(bladeFont64Asset);

        noiseTexture = mgr.get(noiseTextureAsset);
        noiseTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        blockTextures = mgr.get(blockTextureAsset);
        blockTextures.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        blockFaces = new TextureRegion[7];
        for (int i = 0; i < 7; i++){
            blockFaces[i] = new TextureRegion(blockTextures, (1 + 34f * i)/340f, 0, (34 * (i+1) -1 )/ 340f, 33/68f);
        }

        atlas = mgr.get(atlasAsset);

        whitePixel = atlas.findRegion("white-pixel");
        whiteCircle = atlas.findRegion("white-circle");
        gameBoardTexture = atlas.findRegion("backpanel");
        gamepadIcon = atlas.findRegion("icons/gamepad");
//        settingsIcon = atlas.findRegion("icons/settings");
        settingsIcon = atlas.findRegion("icons/gear");
        uploadIcon = atlas.findRegion("icons/upload");
        restartIcon = atlas.findRegion("icons/restart");

        //tetradSquare = atlas.findRegion("tetrad-square");
        blueBlock = new Animation<>(0.1f, atlas.findRegions("block-blue"), Animation.PlayMode.LOOP);
        greenBlock = new Animation<>(0.1f, atlas.findRegions("block-green"), Animation.PlayMode.LOOP);
        shineBlock = new Animation<>(0.1f, atlas.findRegions("block-shine"), Animation.PlayMode.LOOP);
        orangeBlock = new Animation<>(0.1f, atlas.findRegions("block-orange"), Animation.PlayMode.LOOP);
        redBlock = new Animation<>(0.1f, atlas.findRegions("block-red"), Animation.PlayMode.LOOP);

        car = new Animation<>(0.1f, atlas.findRegions("car-side"), Animation.PlayMode.LOOP);
        carUp = new Animation<>(0.1f, atlas.findRegions("car-up"), Animation.PlayMode.LOOP);
        carDown = new Animation<>(0.1f, atlas.findRegions("car-down"), Animation.PlayMode.LOOP);
        carShoot = new Animation<>(0.1f, atlas.findRegions("car-shoot"), Animation.PlayMode.NORMAL);
        carStun = new Animation<>(0.1f, atlas.findRegions("car-stun"), Animation.PlayMode.LOOP);
        shot = new Animation<>(0.1f, atlas.findRegions("fireball"), Animation.PlayMode.LOOP_PINGPONG);
        cat = new Animation<>(0.1f, atlas.findRegions("cat"), Animation.PlayMode.LOOP);
        dog = new Animation<>(0.1f, atlas.findRegions("dog"), Animation.PlayMode.LOOP);

        border = new NinePatch(atlas.findRegion("ninepatches/border"), 9, 9, 9, 9);
        inset  = new NinePatch(atlas.findRegion("ninepatches/inset"), 9, 9, 9, 9);
        screws = new NinePatch(atlas.findRegion("ninepatches/screws"), 7, 7, 7, 7);
        speech = new NinePatch(atlas.findRegion("ninepatches/speech-bubble"), 12, 9, 12, 9);

//        bigBangShader = loadShader("shaders/standard.vert", "shaders/big_bang.frag");
        cityShader = loadShader("shaders/standard.vert", "shaders/city_descent.frag");
        cityShader2 = loadShader("shaders/standard.vert", "shaders/city3.frag");
        blockShader = loadShader("shaders/standard3d.vert", "shaders/cube.frag");
        boardShader = loadShader("shaders/board.vert", "shaders/board.frag");


        randomTransitions = new Array<>();
        blindsShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/blinds.frag");
        fadeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dissolve.frag");
        radialShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/radial.frag");
        doomShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doomdrip.frag");
        pizelizeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/pixelize.frag");
        doorwayShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doorway.frag");
        crosshatchShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/crosshatch.frag");
        rippleShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/ripple.frag");
        heartShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/heart.frag");
        stereoShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/stereo.frag");
        circleCropShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/circlecrop.frag");
        cubeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/cube.frag");
        dreamyShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dreamy.frag");

        randomTransitions.add(radialShader);
        randomTransitions.add(pizelizeShader);

        fontPoints = new ObjectMap<>();
        for (int i = 0; i <= 9; ++i) {
            fontPoints.put(i, new Animation<>(0.1f, atlas.findRegions("particles/font-points-" + i)));
        }

        loadAudio();
        return 1;
    }

    private void loadAudio() {
        // sounds
        sampleSound = mgr.get("audio/sample-sound.wav", Sound.class);
        chaChingSound = mgr.get("audio/chaching.mp3", Sound.class);

        // music
        moodTrack = mgr.get("audio/track-mood.mp3", Music.class);
        moodTrack2 = mgr.get("audio/track-mood-2.mp3", Music.class);
        moodTrack3 = mgr.get("audio/track-mood-3.mp3", Music.class);
        introTrack = mgr.get("audio/intro.mp3", Music.class);
        bladeRunner = mgr.get("audio/track-blade-1-3.mp3", Music.class);
        rotateNew = mgr.get("audio/block_sounds/rotate-new.wav", Sound.class);
        hold = mgr.get("audio/block_sounds/hold.wav", Sound.class);
        error = mgr.get("audio/block_sounds/error.wav", Sound.class);
        explode = mgr.get("audio/block_sounds/explode.wav", Sound.class);
        crash = mgr.get("audio/car_ramming/crash5.wav", Sound.class);
        carTeleportWhooshUp = mgr.get("audio/car_teleport/car-whoosh-up.wav", Sound.class);
        carFart = mgr.get("audio/car_moving/carfart.wav", Sound.class);
        blockLaserChargeRelease = mgr.get("audio/block_sounds/laser-charge-release.wav", Sound.class);
        blockMechanicalGrowl = mgr.get("audio/block_sounds/mechanical-growl.wav", Sound.class);
        blockLaserPluck = mgr.get("audio/block_sounds/laserpluck.wav", Sound.class);

    }

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));

        if (!shaderProgram.isCompiled()) {
            // Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else if (Config.shaderDebug){
            // Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
            // Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + shaderProgram.getLog());
        }

        return shaderProgram;
    }

    public void savePlayerName(String name) {
        if (name != null){
            playerName = name;
            prefs.putString("name", name);
            prefs.flush();
        }
    }

    @Override
    public void dispose() {
        mgr.clear();
        batch.dispose();
        shapes.dispose();
    }

}
