package headmade.arttag.screens;

import headmade.arttag.ArtTag;
import headmade.arttag.DirectedGame;
import headmade.arttag.JobDescription;
import headmade.arttag.Player;
import headmade.arttag.actions.ActionFactory;
import headmade.arttag.actors.Art;
import headmade.arttag.actors.JigglyImageTextButton;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.service.TagService;
import headmade.arttag.vo.TagVo;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

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
		this.jobDesc = jobDescription;

		final Table rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);
		rootTable.setBackground(Assets.instance.skin.getDrawable(AssetTextures.paper));

		int cash = 0;
		artContainer = new HorizontalGroup();
		for (int i = 0; i < Player.instance.inventory.size; i++) {
			final Art art = Player.instance.inventory.get(i);
			artContainer.addActor(new Image(art.image));
			cash += 100;
			if (art.matchesDescription(jobDescription)) {
				cash += 1000 + MathUtils.random(300);
			}
		}
		artContainer.pad(5f);

		jobDescActor = new Label(jobDescription.desc, Assets.instance.skin, "jobDesc");
		jobDescActor.setWrap(true);

		finchActor = new Image(Assets.assetsManager.get(AssetTextures.portrait4, Texture.class));

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

		rootTable.setFillParent(true);
		rootTable.add(jobDescActor).pad(10f).width((camera.viewportWidth / ArtTag.UNIT_SCALE) / 4);
		rootTable.add(finchActor).center().expand();
		rootTable.add(earningActor).pad(10f).width((camera.viewportWidth / ArtTag.UNIT_SCALE) / 4);
		rootTable.row();
		rootTable.add(artContainer).center().colspan(3);

		rootTable.row();
		rootTable.add().colspan(2);
		rootTable.add(continueButton);

		// buildTagTable(rootTable);

		stage.addActor(rootTable);
	}

	protected void nextScreen() {
		// TODO Auto-generated method stub

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
