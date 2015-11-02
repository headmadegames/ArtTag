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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import headmade.arttag.ArtTag;
import headmade.arttag.DirectedGame;
import headmade.arttag.Player;
import headmade.arttag.actions.ActionFactory;
import headmade.arttag.actors.JigglyImageTextButton;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransitionFade;

public class GameOverScreen extends StageScreen {
	private static final String TAG = GameOverScreen.class.getName();

	private JigglyImageTextButton continueButton;

	public GameOverScreen(DirectedGame game) {
		super(game);

		final OrthographicCamera cam = (OrthographicCamera) camera;
		cam.zoom = 1f;

		final Table rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);
		// rootTable.setBackground(Assets.instance.skin.getDrawable(AssetTextures.paper));

		continueButton = new JigglyImageTextButton("Continue", Assets.instance.skin, "play", ActionFactory.wiggleRepeat(3f, 0.5f));
		continueButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				nextScreen();
				return true;
			}
		});
		continueButton.getLabelCell().padRight(10f);

		final int cash = Player.instance.getCash();
		final Label score = new Label("$" + cash, Assets.instance.skin, "dollar");
		final Label highScore = new Label("$" + ArtTag.highScore, Assets.instance.skin, "dollar");

		rootTable.setFillParent(true);
		rootTable.add("GAME OVER").center().colspan(2).pad(20).getActor().scaleBy(2);
		rootTable.row();

		rootTable.add("Your Score").right();
		rootTable.add(score).space(20).pad(20);
		rootTable.row();

		rootTable.add("High Score").right();
		rootTable.add(highScore).space(20).pad(20);
		rootTable.row();

		if (cash > ArtTag.highScore) {
			ArtTag.highScore = cash;
			rootTable.add("Congratulations!\nYou set a new High Score").space(20).pad(20).colspan(2);
			rootTable.row();
		}

		rootTable.add(continueButton).center().colspan(2).space(20).pad(20);

		// buildTagTable(rootTable);
		// rootTable.setDebug(true);
		rootTable.layout();

		stage.addActor(rootTable);

		stage.addListener(new InputListener() {

			@Override
			public boolean scrolled(InputEvent event, float x, float y, int amount) {
				if (rootTable.getDebug()) {
					final OrthographicCamera cam = (OrthographicCamera) camera;
					cam.zoom += amount * 0.5f;
					cam.zoom = MathUtils.clamp(cam.zoom, 0.5f, 10f);
					cam.update();
				}
				return true;
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.F12) {
					rootTable.setDebug(!rootTable.getDebug());
					return true;
				} else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT || keycode == Keys.Z || keycode == Keys.SPACE) {
					nextScreen();
					return true;
				}
				return super.keyDown(event, keycode);
			}

		});
	}

	protected void nextScreen() {
		Gdx.app.log(TAG, "Changing screen");
		Player.instance.setCash(0);
		game.setScreen(new MenuScreen(game), ScreenTransitionFade.init(1f));
	}
}
