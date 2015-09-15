package headmade.arttag.screens;

import headmade.arttag.DirectedGame;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.service.TagService;
import headmade.arttag.vo.TagVo;

import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class RatingScreen extends StageScreen {
	private static final String	TAG	= RatingScreen.class.getName();

	public RatingScreen(DirectedGame game) {
		super(game);

		final Table rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);
		rootTable.setBackground(Assets.instance.skin.getDrawable(AssetTextures.paper));

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

		stage.addActor(rootTable);
	}
}
