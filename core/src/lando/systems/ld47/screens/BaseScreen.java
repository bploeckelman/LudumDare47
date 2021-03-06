package lando.systems.ld47.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Assets;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Config;
import lando.systems.ld47.Game;
import lando.systems.ld47.input.PlayerInput;
import lando.systems.ld47.leaderboard.LeaderboardService;
import lando.systems.ld47.particles.Particles;
import lando.systems.ld47.utils.screenshake.ScreenShakeCameraController;

public abstract class BaseScreen extends InputAdapter {
    public final Game game;
    public final Assets assets;
    public final TweenManager tween;
    public final SpriteBatch batch;
    public final Particles particles;

    public OrthographicCamera worldCamera;
    public OrthographicCamera hudCamera;
    public ScreenShakeCameraController shaker;
    public LeaderboardService leaderboardService;

    public final PlayerInput playerInput = new PlayerInput();

    public BaseScreen(Game game) {
        this.game = game;
        this.assets = game.assets;
        this.tween = game.tween;
        this.batch = assets.batch;
        this.particles = new Particles(assets);

        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, Config.windowWidth, Config.windowHeight);
        this.worldCamera.update();

        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Config.windowWidth, Config.windowHeight);
        this.hudCamera.update();
        this.shaker = new ScreenShakeCameraController(worldCamera);

        // NOTE: this is dumb and janky because its ludum dare and it just needs to work
        GameScreen gameScreen = (this instanceof GameScreen) ? (GameScreen) this : null;
        this.leaderboardService = new LeaderboardService(gameScreen);
        this.leaderboardService.getScores();

        Controllers.clearListeners();
        Controllers.addListener(playerInput);
    }

    public void update(float dt) {
        shaker.update(dt);
        particles.update(dt);
        playerInput.update(dt);
        leaderboardService.update(dt);
    }


    public void renderFrameBuffers(SpriteBatch batch )  {
    }

    public abstract void render(SpriteBatch batch);

    public long playSound(Audio.Sounds sound) {
        return this.game.audio.playSound(sound);
    }

    public Music playMusic(Audio.Musics music) {
        return this.game.audio.playMusic(music, true, true);
    }

}
