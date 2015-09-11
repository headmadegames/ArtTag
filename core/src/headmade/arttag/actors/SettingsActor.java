package headmade.arttag.actors;

import headmade.arttag.assets.Assets;

public class SettingsActor extends BaseMenuContainer {

	private static final String	TAG	= SettingsActor.class.getName();

	public SettingsActor() {
		this.setSkin(Assets.instance.skin);

		add("Music").row();
		add("Sound").row();
		add("Resolution").row();
		add("Fullscreen").row();

	}
}
