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
package headmade.arttag;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;

import headmade.arttag.assets.Assets;
import headmade.arttag.screens.IntroScreen;
import headmade.arttag.screens.transitions.ScreenTransition;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import headmade.arttag.service.FlickrService;
import headmade.arttag.service.MusicService;
import headmade.arttag.vo.GameSettings;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public class ArtTag extends DirectedGame {

	private static final String TAG = ArtTag.class.getName();

	private static final String SAVE_FILE = "art_treachery_save.dat";

	public static final String	BUTTON_A	= "Button A";
	public static final String	BUTTON_B	= "Button B";

	public final static int		VELOCITY_ITERS		= 3;
	public final static int		POSITION_ITERS		= 2;
	public final static int		MAX_FPS				= 60;
	public final static int		MIN_FPS				= 15;
	public final static float	MAX_STEPS			= 1f + MAX_FPS / MIN_FPS;
	public final static float	TIME_STEP			= 1f / MAX_FPS;
	public final static float	MAX_TIME_PER_FRAME	= TIME_STEP * MAX_STEPS;

	public static final float	UNIT_SCALE	= 1f / 64f;
	public static final int		RAYS_NUM	= 32;

	public static final short	CAT_LEVEL		= 0x0001;
	public static final short	CAT_PLAYER		= 0x0002;
	public static final short	CAT_GUARD		= 0x0004;
	public static final short	CAT_ARTTRIGGER	= 0x0008;
	public static final short	CAT_PLAYERLIGHT	= 0x0010;
	public static final short	CAT_LIGHT		= 0x0020;
	public static final short	CAT_EXIT		= 0x0040;
	public static final short	CAT_WARP		= 0x0080;
	public static final short	CAT_GUARDLIGHT	= 0x0100;
	public static final short	CAT_HINT		= 0x0200;

	public static final short	MASK_LEVEL			= -1;
	public static final short	MASK_PLAYER			= CAT_LEVEL | CAT_LIGHT | CAT_GUARD | CAT_ARTTRIGGER | CAT_EXIT | CAT_WARP
			| CAT_GUARDLIGHT | CAT_HINT;
	public static final short	MASK_PLAYERLIGHT	= CAT_LEVEL | CAT_GUARD | CAT_ARTTRIGGER;
	public static final short	MASK_LIGHT			= CAT_LEVEL | CAT_PLAYER | CAT_GUARD;
	public static final short	MASK_GUARD			= CAT_LEVEL | CAT_PLAYER | CAT_LIGHT | CAT_PLAYERLIGHT;
	public static final short	MASK_ARTTRIGGER		= CAT_PLAYER | CAT_PLAYERLIGHT;
	public static final short	MASK_EXIT			= CAT_PLAYER;
	public static final short	MASK_WARP			= MASK_EXIT;
	public static final short	MASK_HINT			= CAT_PLAYER;
	public static final short	MASK_GUARDLIGHT		= CAT_PLAYER;																// should
																																// notice
																																// missing
																																// art too

	public static final short GROUP_LIGHT = 0x0000;

	// settings
	public static final boolean TOGGLE_LIGHT = true;

	public static GameState		gameState;
	public static GameSettings	gameSettings	= new GameSettings();

	public static int highScore = 1000;

	@Override
	public void create() {
		Box2DUtils.autoCache = true;

		FlickrService.instance.init();

		batch = new SpriteBatch();

		// Load all assets
		Assets.instance.init();

		MusicService.instance.init(this);

		// Start game with Playground Screen
		final ScreenTransition transition = ScreenTransitionFade.init(0.0f);
		setScreen(new IntroScreen(this), transition);

		// load Game or create save
		final Json json = new Json();
		final FileHandle saveFile = Gdx.files.local(SAVE_FILE);
		if (saveFile.exists()) {
			gameState = json.fromJson(GameState.class, saveFile);
		} else {
			gameState = new GameState();
			saveGame();
		}
		Gdx.app.log(TAG, "Savefile located at " + saveFile.file().getAbsolutePath());
	}

	private void saveGame() {
		final Json json = new Json();
		final FileHandle saveFile = Gdx.files.local(SAVE_FILE);
		json.toJson(gameState, saveFile);
	}

	@Override
	public void dispose() {
		super.dispose();
		FlickrService.instance.dispose();
		Player.instance.dispose();
		Assets.instance.dispose();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		super.resize(width, height);
	}

}
