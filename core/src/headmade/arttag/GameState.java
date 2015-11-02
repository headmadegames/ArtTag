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
package headmade.arttag;

import headmade.arttag.service.RandomService;

public class GameState {
	private static final String	TAG	= GameState.class.getName();

	private String				playerId;
	private int					correctTagCount;
	private int					incorrectTagCount;

	public GameState() {
		// String name = System.getProperty("user.name");
		// if (null == name || name.length() == 0) {
		final String name = RandomService.instance.generateArtName().replaceAll(" ", "_").replaceAll(",", "_");
		// }
		playerId = name + "_" + System.currentTimeMillis();
	}

	public float getAccuracy() {
		return new Float(correctTagCount) / new Float(incorrectTagCount + correctTagCount);
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public int getCorrectTagCount() {
		return correctTagCount;
	}

	public void setCorrectTagCount(int correctTagCount) {
		this.correctTagCount = correctTagCount;
	}

	public int getIncorrectTagCount() {
		return incorrectTagCount;
	}

	public void setIncorrectTagCount(int incorrectTagCount) {
		this.incorrectTagCount = incorrectTagCount;
	}

}
