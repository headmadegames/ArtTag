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

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import headmade.arttag.DirectedGame;
import headmade.arttag.JobDescription;
import headmade.arttag.Player;
import headmade.arttag.actions.ActionFactory;
import headmade.arttag.actors.Art;
import headmade.arttag.actors.JigglyImageTextButton;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import headmade.arttag.service.TagService;
import headmade.arttag.vo.TagVo;

public class RatingScreen extends StageScreen {
	private static final String TAG = RatingScreen.class.getName();

	private final JobDescription	jobDesc;
	private final Label				jobDescActor;
	private JigglyImageTextButton	continueButton;

	public RatingScreen(DirectedGame game, JobDescription jobDescription) {
		super(game);

		final OrthographicCamera cam = (OrthographicCamera) camera;
		cam.zoom = 1f;

		this.jobDesc = jobDescription;

		final Table rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);
		// rootTable.setBackground(Assets.instance.skin.getDrawable(AssetTextures.paper));

		jobDescActor = new Label(jobDescription.desc, Assets.instance.skin, "jobDesc");
		// jobDescActor.setWrap(true);
		jobDescActor.setWidth(camera.viewportWidth / 4);

		continueButton = new JigglyImageTextButton("Continue", Assets.instance.skin, "play", ActionFactory.wiggleRepeat(3f, 0.5f));
		continueButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				nextScreen();
				return true;
			}
		});
		continueButton.getLabelCell().padRight(10f);

		rootTable.setFillParent(true);
		rootTable.add("Mission").space(20).pad(20).right();
		rootTable.add(jobDescActor).center().expand();
		rootTable.row();

		int cash = 100;
		if (Player.instance.inventory.size == 0) {
			cash = 0;
			rootTable.add("You did not steal anything and failed the mission.").center().colspan(2).space(20).pad(20);
			rootTable.row();
		} else {
			for (int i = 0; i < Player.instance.inventory.size; i++) {
				final Art art = Player.instance.inventory.get(i);
				final Image img = new Image(art.getTexture());
				img.setScaling(Scaling.fit);
				// img.setWidth(camera.viewportWidth / Player.instance.inventory.size);
				// img.setHeight(camera.viewportHeight / 5);
				if (art.matchesDescription(jobDescription)) {
					cash += 1000 + MathUtils.random(100);
				}
				if (art.isCorrectlyTagged()) {
					cash += 1000 + MathUtils.random(100);
				} else if (art.isIncorrectlyTagged()) {
					cash /= 10;
				}

				Gdx.app.log(TAG,
						"Cash reward " + cash + " art.matchesDescription() " + art.matchesDescription(jobDescription)
								+ " art.isCorrectlyTagged() " + art.isCorrectlyTagged() + " art.isIncorrectlyTagged() "
								+ art.isIncorrectlyTagged());
				Gdx.app.log(TAG, "Multiplying by Player Accuracy " + Player.instance.getAccuracy());
				cash *= (0.2f + Player.instance.getAccuracy());

				rootTable.add(i == 0 ? "Loot" : "").space(20).pad(20).right();
				rootTable.add(img).center().pad(10f).expand();
				rootTable.row();
			}
		}

		Player.instance.increaseCash(cash);
		final Label earningActor = new Label("$" + cash, Assets.instance.skin, "dollar");

		rootTable.add("Reward").space(20).pad(20);
		rootTable.add(earningActor).pad(10f);
		rootTable.row();

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
		game.setScreen(new ArtTagScreen(game, null), ScreenTransitionFade.init(1f));
	}

	private void buildTagTable(final Table rootTable) {
		rootTable.add("Image");
		rootTable.add("Tag");
		rootTable.add("Matched");
		rootTable.add("Not matched");
		rootTable.row();

		for (final String imageId : TagService.instance.tagVos.keySet()) {
			rootTable.add(imageId);
			final Map<String, TagVo> tags = TagService.instance.tagVos.get(imageId).tags;
			boolean first = true;
			for (final TagVo vo : tags.values()) {
				if (first) {
					first = false;
					rootTable.add(vo.tag);
				} else {
					rootTable.row();
					rootTable.add(vo.tag).colspan(2);
				}
				rootTable.add("" + vo.countTag);
				rootTable.add("" + vo.countTagNotMatched);
			}
			rootTable.row();
		}
	}
}
