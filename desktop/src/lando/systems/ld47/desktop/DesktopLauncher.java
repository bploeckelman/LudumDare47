package lando.systems.ld47.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import lando.systems.ld47.Config;
import lando.systems.ld47.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle(Config.title);
		config.setWindowedMode(Config.windowWidth, Config.windowHeight);
		config.setResizable(Config.resizable);
		config.setBackBufferConfig(8,8,8,8,16,0,16);
		new Lwjgl3Application(new Game(), config);
	}
}
