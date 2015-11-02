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
package headmade.arttag.vo;

public class GameSettings {
	private static final String TAG = GameSettings.class.getName();

	public int		screenWidth		= 1280;
	public int		screenHeight	= 1024;
	public int		rays			= 256;
	public int		blur			= 3;
	public boolean	fullscreen;
	public boolean	handleResAuto	= true;

}
