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

import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;

import com.badlogic.gdx.utils.Align;

public class CreditsActor extends BaseMenuContainer {

	private static final String	TAG	= CreditsActor.class.getName();

	public CreditsActor() {
		this.setSkin(Assets.instance.skin);

		setFillParent(false);
		setBackground(Assets.instance.skin.getDrawable(AssetTextures.paper));

		add("Programming").align(Align.top);
		add("Headmade Games").colspan(2).align(Align.left).row();
		row().space(10f);

		add("Art").align(Align.top);
		add("Headmade Games \nThe British Library").colspan(2).align(Align.left).row();
		row().space(10f);

		add("Sounds").align(Align.top);
		add("Headmade Games").colspan(2).align(Align.left).row();
		row().space(10f);

		add("Music").align(Align.top);
		add("Cool Guys by DDmyzik \n" + "Super Chill by DDmyzik \n" + "Active Sports by DDmyzik \n" + "Farewell by DDmyzik").align(
				Align.left);
		add("https://www.jamendo.com/de/track/1232017/cool-guys \nhttps://www.jamendo.com/de/track/1177378/super-chill\n"
				+ "https://www.jamendo.com/de/track/1177371/active-sports \nhttps://www.jamendo.com/de/track/1238324/farewell");
		row();
		row().space(10f);

		add("Made with").align(Align.top);
		add("LibGDX \nTiled \nBox2dLights").colspan(2).align(Align.left).row();
	}

}
