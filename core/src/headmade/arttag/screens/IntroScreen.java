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
package headmade.arttag.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import headmade.arttag.ArtTag;
import headmade.arttag.DirectedGame;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransition;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import headmade.arttag.vo.GameSettings;

public class IntroScreen extends StageScreen {

	private static final String TAG = IntroScreen.class.getName();

	private final Table rootTable;

	private boolean	isStarted;
	private boolean	isInitialised	= false;

	public IntroScreen(final DirectedGame game) {
		super(game);

		rootTable = new Table();
		rootTable.setFillParent(true);
		final Actor logo = new Image(Assets.assetsManager.get(Assets.HEADMADE_LOGO, Texture.class));
		// logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);
		// logo.scaleBy(2f);
		logo.setColor(Color.BLACK);

		rootTable.add(logo).center().expand();
		rootTable.row();

		// rootTable.setDebug(true);
		this.stage.addActor(rootTable);

		stage.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					Gdx.app.exit();
					return true;
				}
				return super.keyDown(event, keycode);
			}

		});

		Assets.instance.loadAll();

	}

	@Override
	public void preDraw(float delta) {
		super.preDraw(delta);

		if (!isInitialised) {
			Gdx.app.log(TAG, "Loading Prefs");
			isInitialised = true;
			ArtTag.gameSettings = new GameSettings();
			final Preferences prefs = Gdx.app.getPreferences("ArtTreacheryPrefs");
			ArtTag.gameSettings.screenWidth = prefs.getInteger("screenWidth", 1280);
			ArtTag.gameSettings.screenHeight = prefs.getInteger("screenHeight", 1024);
			ArtTag.gameSettings.fullscreen = prefs.getBoolean("fullscreen", false);
			ArtTag.gameSettings.blur = prefs.getInteger("blur", 1);
			ArtTag.gameSettings.rays = prefs.getInteger("rays", 64);
			ArtTag.gameSettings.handleResAuto = prefs.getBoolean("handleResAuto", true);

			Gdx.app.log(TAG, "Saving Prefs");
			prefs.putInteger("screenWidth", ArtTag.gameSettings.screenWidth);
			prefs.putInteger("screenHeight", ArtTag.gameSettings.screenHeight);
			prefs.putBoolean("fullscreen", ArtTag.gameSettings.fullscreen);
			prefs.putInteger("blur", ArtTag.gameSettings.blur);
			prefs.putInteger("rays", ArtTag.gameSettings.rays);
			prefs.putBoolean("handleResAuto", ArtTag.gameSettings.handleResAuto);
			prefs.flush();

			if (Gdx.graphics.supportsDisplayModeChange() && ArtTag.gameSettings.handleResAuto == false) {
				Gdx.app.log(TAG, "Trying to set Displaymode");
				Gdx.graphics.setDisplayMode(ArtTag.gameSettings.screenWidth, ArtTag.gameSettings.screenHeight,
						ArtTag.gameSettings.fullscreen);

				// Gdx.gl.glViewport(0, 0, ArtTag.gameSettings.screenWidth, ArtTag.gameSettings.screenHeight);
			}

		}

		Assets.assetsManager.update();
		if (!isStarted && MathUtils.isEqual(1f, Assets.assetsManager.getProgress())) {
			// done loading eh
			Assets.instance.onFinishLoading();

			startGame();
			isStarted = true;

			// final TextButton startButton = new TextButton("Start", Assets.instance.skin);
			// startButton.addListener(new InputListener() {
			//
			// @Override
			// public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			// startGame();
			// return super.touchDown(event, x, y, pointer, button);
			// }
			// });
		}
	}

	protected void startGame() {
		Gdx.app.log(TAG, "Start Button clicked");

		final ScreenTransition transition = ScreenTransitionFade.init(.3f);
		// game.setScreen(new ArtTagScreen(game), transition);
		game.setScreen(new MenuScreen(game));

		// final JobDescription jobDescription = new JobDescription();
		// final Art art1 = new Art(new Rectangle(1, 1, 1, 1));
		// final Art art2 = new Art(new Rectangle(1, 1, 1, 1));
		// art1.init();
		// art2.init();
		// Player.instance.inventory.add(art1);
		// Player.instance.inventory.add(art2);
		// game.setScreen(new RatingScreen(game, jobDescription));
	}

}
