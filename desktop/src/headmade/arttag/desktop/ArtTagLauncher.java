package headmade.arttag.desktop;

import headmade.arttag.ArtTag;
import headmade.arttag.assets.Assets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class ArtTagLauncher {

	private static boolean	rebuildAtlas		= true;
	// private static boolean rebuildAtlas = false;
	private static boolean	drawDebugOutline	= false;

	public static void main(String[] arg) {

		if (rebuildAtlas) {
			final Settings settings = new Settings();
			settings.maxWidth = 512;
			settings.maxHeight = 512;
			settings.debug = drawDebugOutline;
			settings.duplicatePadding = true;
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

		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Headmade Game";
		config.width = 1280;
		config.height = 720;
		config.samples = 4;
		new LwjglApplication(new ArtTag(), config);
	}
}
