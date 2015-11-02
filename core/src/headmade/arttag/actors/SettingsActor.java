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
