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
