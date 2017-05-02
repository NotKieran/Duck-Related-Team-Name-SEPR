/**
 * @author DRTN
 * Team Website with download:
 * https://misterseph.github.io/DuckRelatedFractalProject/
 *
 * This Class contains either modifications or is entirely new in Assessment 3
 *
 * If you are in any doubt a complete changelog can be found here:
 * https://github.com/NotKieran/DRTN-Fractal/compare/Fractal_Initial...development
 *
 * And a more concise report can be found in our Change3 document.
 **/

// For pre-compiled version, please see:
// https://github.com/TeamFractal/Roboticon-Quest/releases/download/v1.0.1/RoboticonQuest-1.0.1.zip

package io.github.teamfractal.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.teamfractal.RoboticonQuest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Duck-Related Roboticon Quest";
		config.addIcon("icon.png", Files.FileType.Internal);
		config.backgroundFPS = 1;
		config.vSyncEnabled = true;
		config.width = 1024;
		config.height = 512;
		config.resizable = false;

		new LwjglApplication(new RoboticonQuest(), config);
	}
}
