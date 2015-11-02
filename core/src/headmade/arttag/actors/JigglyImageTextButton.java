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
package headmade.arttag.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class JigglyImageTextButton extends ImageTextButton {
	private static final String TAG = JigglyImageTextButton.class.getName();

	public JigglyImageTextButton(String text, Skin skin, Action action) {
		this(text, skin, "default", action);
	}

	public JigglyImageTextButton(String text, Skin skin, String styleName, Action action) {
		super(text, skin, styleName);
		setTransform(true);
		setOrigin(Align.center);
		align(Align.left);
		getImageCell().pad(10f);

		if (null != action) {
			addAction(action);
		}

		final Actor thisButton = this;
		addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer < 0 && (event.getRelatedActor() == null || !event.getRelatedActor().isDescendantOf(thisButton))) {
					// no button was pressed and exited Actor is not related to button
					thisButton.addAction(Actions.scaleBy(0.1f, 0.1f, 0.3f, Interpolation.bounceOut));
					event.stop();
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (event.getRelatedActor() == null || !event.getRelatedActor().isDescendantOf(thisButton)) {
					// entered Actor is not related to button
					thisButton.addAction(Actions.scaleBy(-0.1f, -0.1f, 0.2f, Interpolation.bounce));
					// event.stop();
				}
			}
		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

}
