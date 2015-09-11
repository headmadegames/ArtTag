package headmade.arttag.actors;

import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class HowToActor extends BaseMenuContainer {
	private static final String	TAG	= HowToActor.class.getName();
	private Cell<Label>			descCell;

	public HowToActor() {
		this.setSkin(Assets.instance.skin);

		setFillParent(false);
		// setWidth(Gdx.graphics.getWidth() / 2);
		// setHeight(Gdx.graphics.getHeight() / 2);

		if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS) {
			add("Control by tilting your device.");
			add("Tap to switch Flashlight on and off.");
		} else {
			final Image joystick = new Image(Assets.instance.skin.getRegion(AssetTextures.joystick));
			final Image button = new Image(Assets.instance.skin.getRegion(AssetTextures.button));
			final Image button2 = new Image(Assets.instance.skin.getRegion(AssetTextures.button));

			descCell = add("Use your flashlight to illuminate Art. \n" + "Identify if the art matches your job description.\n"
					+ "If it matches, scan it to reveal more info about it.\n" + "If it matches all the criteria, steal it.");
			descCell.pad(20f).align(Align.center).colspan(3).row();
			add(joystick).bottom();
			add(button).bottom();
			add(button2).bottom().row();
			add("Cursor Keys").padBottom(20f);
			add("Shift Key").padBottom(20f);
			add("Spacebar").padBottom(20f).row();

			add("Move");
			add("Sprint");
			add("Flashlight on/off").row();

			add();
			add("Scan");
			add("Cancel").row();

			add();
			add("Confirm");
			add().row();
			row();
		}
		// setDebug(true);
		setBackground(Assets.instance.skin.getDrawable(AssetTextures.paper));
	}
}
