package headmade.arttag.screens;

import headmade.arttag.DirectedGame;
import headmade.arttag.JobDescription;
import headmade.arttag.Player;
import headmade.arttag.actions.ActionFactory;
import headmade.arttag.actors.Art;
import headmade.arttag.actors.JigglyImageTextButton;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import headmade.arttag.service.TagService;
import headmade.arttag.vo.TagVo;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

public class RatingScreen extends StageScreen {
	private static final String		TAG	= RatingScreen.class.getName();

	private final JobDescription	jobDesc;
	private final HorizontalGroup	artContainer;
	private final Label				jobDescActor;
	private final Image				finchActor;
	private final Actor				earningActor;
	private JigglyImageTextButton	continueButton;

	public RatingScreen(DirectedGame game, JobDescription jobDescription) {
		super(game);

		final OrthographicCamera cam = (OrthographicCamera) camera;
		cam.zoom = 1f;

		this.jobDesc = jobDescription;

		final Table rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);
		// rootTable.setBackground(Assets.instance.skin.getDrawable(AssetTextures.paper));

		int cash = 0;
		artContainer = new HorizontalGroup();
		for (int i = 0; i < Player.instance.inventory.size; i++) {
			final Art art = Player.instance.inventory.get(i);
			final Image img = new Image(art.getTexture());
			img.setScaling(Scaling.fit);
			// img.setWidth(camera.viewportWidth / Player.instance.inventory.size);
			// img.setHeight(camera.viewportHeight / 5);
			artContainer.addActor(img);
			cash += 100;
			if (art.matchesDescription(jobDescription)) {
				cash += 1000 + MathUtils.random(100);
			}
		}
		if (artContainer.getChildren().size == 0) {
			artContainer.addActor(new Label("You didn't steal anything", Assets.instance.skin));
		}
		artContainer.space(20f);
		artContainer.pad(20f);

		jobDescActor = new Label(jobDescription.desc, Assets.instance.skin, "jobDesc");
		jobDescActor.setWrap(true);

		finchActor = new Image(Assets.assetsManager.get(AssetTextures.portrait4, Texture.class));
		finchActor.setScaling(Scaling.fit);

		final String earnings = "Earnings:\n$" + cash;
		earningActor = new Label(earnings, Assets.instance.skin, "jobDesc");

		continueButton = new JigglyImageTextButton("Continue", Assets.instance.skin, "play", ActionFactory.wiggleRepeat(3f, 0.5f));
		continueButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				nextScreen();
				return true;
			}
		});
		continueButton.getLabelCell().padRight(10f);

		Gdx.app.log(TAG, "################# camera.viewportWidth " + camera.viewportWidth + " ###################");
		rootTable.setFillParent(true);
		rootTable.add(jobDescActor).pad(10f).width((camera.viewportWidth) / 4);
		rootTable.add(finchActor).center().expand();
		rootTable.add(earningActor).pad(10f).width((camera.viewportWidth) / 4);
		rootTable.row();
		rootTable.add(artContainer).height(camera.viewportHeight / 5).center().colspan(3);

		rootTable.row();
		rootTable.add().colspan(2);
		rootTable.add(continueButton);

		// buildTagTable(rootTable);
		rootTable.setDebug(true);
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
				}
				return super.keyDown(event, keycode);
			}

		});
	}

	protected void nextScreen() {
		Gdx.app.log(TAG, "Changing screen");
		game.setScreen(new ArtTagScreen(game), ScreenTransitionFade.init(1f));
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
