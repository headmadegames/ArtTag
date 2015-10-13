package headmade.arttag.assets;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

import headmade.arttag.ArtTag;
import headmade.arttag.spriter.Data;
import headmade.arttag.spriter.Entity;
import headmade.arttag.spriter.LibGdxAtlasLoader;
import headmade.arttag.spriter.SCMLReader;

//import com.kotcrab.vis.ui.VisUI;

public class Assets implements Disposable, AssetErrorListener {

	private static final String TAG = Assets.class.getName();

	public static final String	HEADMADE_LOGO	= "headmade_large.png";
	public static final String	PACKS_BASE		= "packs/";						// + File.separator;
	public static final String	PACK			= "pack";
	public static final String	GAME_ATLAS		= PACKS_BASE + PACK + ".atlas";
	public static final String	RAW_ASSETS		= "assets-raw/images";

	public static final Assets instance = new Assets(); // Singleton

	public static AssetManager assetsManager;

	public TextureAtlas	atlas;	// Don't make this static!!!
	public Skin			skin;

	private Data				spriterData;
	private LibGdxAtlasLoader	spriterLoader;
	private Entity				maggieSpriterData;

	// singleton: prevent instantiation from other classes
	private Assets() {
		Gdx.app.log(TAG, "File.seperator is " + File.separator);

		// assetsManager = new AnnotationAssetManager();
		assetsManager = new AssetManager(new FileHandleResolver() {

			@Override
			public FileHandle resolve(String fileName) {
				if (fileName.contains("assets-raw")) {
					final String path = fileName.substring(fileName.lastIndexOf(File.separator), fileName.lastIndexOf('.'));
					return Gdx.files.internal(path);
				}
				return Gdx.files.internal(fileName);
			}
		});
		// set asset manager error handler
		assetsManager.setErrorListener(this);
		assetsManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		// assetsManager.setLoader(Texture.class, new TextureLoader(new FileHandleResolver() {
		// @Override
		// public FileHandle resolve(String fileName) {
		// if (fileName.contains("assets-raw")) {
		// final String path = fileName.substring(fileName.lastIndexOf(File.separator), fileName.lastIndexOf('.'));
		// return Gdx.files.internal(path);
		// }
		// return Gdx.files.internal(fileName);
		// }
		// }));

	}

	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "Disposing assets...");
		assetsManager.dispose();
		spriterLoader.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Error in Assets", throwable);
	}

	/**
	 * Loads minimal stuff
	 */
	public void init() {
		Gdx.app.debug(TAG, "Init minimal assets...");

		assetsManager.load(HEADMADE_LOGO, Texture.class);

		assetsManager.finishLoading();

		final Texture logo = assetsManager.get(HEADMADE_LOGO, Texture.class);
		logo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}

	/**
	 * Load all assets using the {@link AssetManager#load}. It blocks until all loading is finished. This method must be called before
	 * accessing any asset.
	 */
	public void loadAll() {
		Gdx.app.debug(TAG, "Init assets...");

		for (final String image : AssetTextures.ALL_IMAGES) {
			assetsManager.load(image, Texture.class);
		}

		assetsManager.load(AssetMusic.gameOver, Music.class);
		assetsManager.load(AssetMusic.intro, Music.class);
		assetsManager.load(AssetMusic.normal, Music.class);
		assetsManager.load(AssetMusic.spotted, Music.class);

		assetsManager.load(AssetSounds.cough, Sound.class);
		assetsManager.load(AssetSounds.hm, Sound.class);
		assetsManager.load(AssetSounds.huh, Sound.class);
		assetsManager.load(AssetSounds.scan, Sound.class);
		assetsManager.load(AssetSounds.smoke, Sound.class);
		assetsManager.load(AssetSounds.steal, Sound.class);
		assetsManager.load(AssetSounds.step, Sound.class);
		assetsManager.load(AssetSounds.stop, Sound.class);
		assetsManager.load(AssetSounds.whosThere, Sound.class);

		assetsManager.load(AssetTextures.skin, Skin.class, AssetTextures.skinParameter);

		for (final String map : AssetMaps.ALL_MAPS) {
			assetsManager.load(map, TiledMap.class);
		}

	}

	public void onFinishLoading() {
		atlas = assetsManager.get(GAME_ATLAS, TextureAtlas.class);
		skin = assetsManager.get(AssetTextures.skin, Skin.class);
		setTextureFilter(atlas, TextureFilter.Nearest);

		try {
			spriterData = new SCMLReader(Gdx.files.internal("maggie.scml").read()).getData();
			spriterLoader = new LibGdxAtlasLoader(spriterData, atlas);
			spriterLoader.load(Gdx.files.internal("maggie.scml").file());
			maggieSpriterData = spriterData.getEntity(0);
		} catch (final Exception e) {
			Gdx.app.error(TAG, "Error loading spriter stuff", e);
		}
	}

	public void playSound(String name) {
		playSound(name, 1f);
	}

	public void playSound(String name, float volume) {
		playSound(name, volume, 1f);
	}

	public void playSound(String name, float volume, float pitch) {
		Gdx.app.log(TAG, "Playing sound " + name + "with valume " + volume);
		final Sound sound = assetsManager.get(name, Sound.class);
		if (sound != null) {
			sound.play(volume * 0.5f, pitch, 0);
		} else {
			Gdx.app.error(TAG, "No Sound with name " + name);
		}
	}

	/**
	 * // enable texture filtering for pixel smoothing
	 *
	 * @param atlas
	 * @param typeOfFilter
	 */
	private void setTextureFilter(TextureAtlas atlas, TextureFilter typeOfFilter) {
		Gdx.app.log(TAG, "setting filter for textures " + atlas.getTextures().size);
		for (final Texture t : atlas.getTextures()) {
			t.setFilter(typeOfFilter, typeOfFilter); // min=mag
		}

		final Skin skin = assetsManager.get(AssetTextures.skin, Skin.class);
		final BitmapFont font = skin.getFont("default-font");
		for (int i = 0; i < font.getRegions().size; i++) {
			font.getRegion(i).getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		}

		NinePatch ninepatch = skin.get(AssetTextures.frame, NinePatch.class);
		ninepatch.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		ninepatch.scale(ArtTag.UNIT_SCALE * 0.75f, ArtTag.UNIT_SCALE * 0.75f);

		ninepatch = skin.get(AssetTextures.frame2, NinePatch.class);
		ninepatch.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		ninepatch.scale(ArtTag.UNIT_SCALE / 3, ArtTag.UNIT_SCALE / 3);
	}

	public Entity getMaggieSpriterData() {
		return maggieSpriterData;
	}

	public LibGdxAtlasLoader getSpriterLoader() {
		return spriterLoader;
	}
}
