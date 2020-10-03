package lando.systems.ld47;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld47.screens.BaseScreen;
import lando.systems.ld47.screens.LaunchScreen;
import lando.systems.ld47.screens.TitleScreen;
import lando.systems.ld47.utils.accessors.*;

public class Game extends ApplicationAdapter {

	public TweenManager tween;
	public Assets assets;
	public Audio audio;

	public BaseScreen currentScreen;

	@Override
	public void create () {
		if (tween == null) {
			tween = new TweenManager();
			Tween.setWaypointsLimit(4);
			Tween.setCombinedAttributesLimit(4);
			Tween.registerAccessor(Color.class, new ColorAccessor());
			Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
			Tween.registerAccessor(Vector2.class, new Vector2Accessor());
			Tween.registerAccessor(Vector3.class, new Vector3Accessor());
			Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
		}

		if (assets == null) {
			assets = new Assets();
		}

		if (audio == null) {
			audio = new Audio(this);
		}



		if (Gdx.app.getType() == Application.ApplicationType.WebGL || Config.showLaunchScreen) {
			setScreen(new LaunchScreen(this));
		} else {
			setScreen(new TitleScreen(this));
		}
	}

	@Override
	public void render () {
	    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
	    	Gdx.app.exit();
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
		tween.update(dt);
		audio.update(dt);
		currentScreen.update(dt);

		currentScreen.render(assets.batch);
	}


	public void setScreen(BaseScreen screen) {
		currentScreen = screen;
	}

	@Override
	public void dispose () {
		assets.dispose();
	}

}
