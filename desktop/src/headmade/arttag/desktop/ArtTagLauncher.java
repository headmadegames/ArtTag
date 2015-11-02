/*******************************************************************************
 *    Copyright 2015 Headmade Games
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package headmade.arttag.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import headmade.arttag.ArtTag;
import headmade.arttag.assets.Assets;

public class ArtTagLauncher {

	private static final String TAG = ArtTagLauncher.class.getName();

	// private static boolean rebuildAtlas = true;
	private static boolean	rebuildAtlas		= false;
	private static boolean	drawDebugOutline	= false;

	public static void main(String[] arg) {
		if (rebuildAtlas) {
			final Settings settings = new Settings();
			settings.maxWidth = 512;
			settings.maxHeight = 512;
			settings.debug = drawDebugOutline;
			// settings.duplicatePadding = true;
			settings.grid = true;
			settings.square = true;
			settings.useIndexes = true;
			settings.bleed = true;
			settings.paddingX = 2;
			settings.paddingY = 2;
			settings.wrapX = TextureWrap.MirroredRepeat;
			settings.wrapY = TextureWrap.MirroredRepeat;

			// TexturePacker.processIfModified(settings, "assets-raw/images", "../android/assets/" + Assets.PACKS_BASE, Assets.PACK);
			TexturePacker.process(settings, "assets-raw/images", "../android/assets/" + Assets.PACKS_BASE, Assets.PACK);
		}

		// final JoglNewtApplicationConfiguration config = new JoglNewtApplicationConfiguration();
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Headmade Game";
		// config.foregroundFPS = 30;
		config.width = 1280;
		config.height = 1024;
		// config.fullscreen = true;
		// config.samples = 4;
		final LwjglApplication app = new LwjglApplication(new ArtTag(), config);
		// final JoglNewtApplication app = new JoglNewtApplication(new ArtTag(), config);
	}

}
