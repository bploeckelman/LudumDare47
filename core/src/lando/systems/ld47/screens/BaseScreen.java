package lando.systems.ld47.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld47.Assets;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Config;
import lando.systems.ld47.Game;
import lando.systems.ld47.particles.Particles;
import lando.systems.ld47.utils.screenshake.ScreenShakeCameraController;

public abstract class BaseScreen extends InputAdapter {
    public final Game game;
    public final Assets assets;
    public final SpriteBatch batch;
    public final Particles particles;

    public OrthographicCamera worldCamera;
    public OrthographicCamera hudCamera;
    public ScreenShakeCameraController shaker;

    public BaseScreen(Game game) {
        this.game = game;
        this.assets = game.assets;
        this.batch = assets.batch;
        this.particles = new Particles(assets);

        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, Config.windowWidth, Config.windowHeight);
        this.worldCamera.update();

        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Config.windowWidth, Config.windowHeight);
        this.hudCamera.update();
        this.shaker = new ScreenShakeCameraController(worldCamera);
    }

    public void update(float dt) {
        shaker.update(dt);
        particles.update(dt);
    }


    public void renderFrameBuffers(SpriteBatch batch )  {
    }

    public abstract void render(SpriteBatch batch);

    public long playSound(Audio.Sounds sound) {
        return this.game.audio.playSound(sound);
    }

    protected Music playMusic(Audio.Musics music) {
        return this.game.audio.playMusic(music, false, true);
    }

}
