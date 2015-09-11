package headmade.arttag.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class BaseMenuContainer extends Table {
	private static final String	TAG	= BaseMenuContainer.class.getName();

	@Override
	public boolean isVisible() {
		if (getColor().a < 0.5f) {
			return false;
		}
		return super.isVisible();
	}
}
