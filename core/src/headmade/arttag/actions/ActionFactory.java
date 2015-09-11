package headmade.arttag.actions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;

public class ActionFactory {
	private static final String	TAG	= ActionFactory.class.getName();

	public static RepeatAction wiggleRepeat(Float rotationAmount, Float duration) {
		final RepeatAction wiggleAction = Actions.forever(Actions.sequence(
				Actions.rotateBy(rotationAmount, duration / 4, Interpolation.sineOut),
				Actions.rotateBy(-rotationAmount * 2, duration / 2, Interpolation.sine),
				Actions.rotateBy(rotationAmount, duration / 4, Interpolation.sineIn)));
		return wiggleAction;
	}
}
