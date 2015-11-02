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
package headmade.arttag.service;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.TimeUtils;

import headmade.arttag.ArtTag;
import headmade.arttag.Player;
import headmade.arttag.assets.AssetMusic;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.ArtTagScreen;
import headmade.arttag.screens.IntroScreen;
import headmade.arttag.screens.MenuScreen;
import headmade.arttag.screens.RatingScreen;

public class MusicService {

	private static final String TAG = MusicService.class.getName();

	private static final float MUSIC_VOLUME = 0.5f;

	public static MusicService instance = new MusicService();

	private boolean	isMute	= false;
	private ArtTag	game;
	private String	currentMusicName;
	private Music	currentMusic;

	private long millisStarted = 0;

	private MusicService() {
	}

	public void update() {
		if (game != null) {
			if (game.getScreen() instanceof MenuScreen) {
				shouldPlay(AssetMusic.intro);
			} else if (game.getScreen() instanceof IntroScreen) {
				shouldPlay(AssetMusic.intro);
			} else if (game.getScreen() instanceof RatingScreen) {
				shouldPlay(AssetMusic.intro);
			} else if (game.getScreen() instanceof ArtTagScreen) {
				if (Player.instance.isSpotted) {
					shouldPlay(AssetMusic.spotted);
				} else if (Player.instance.isCaught) {
					shouldPlay(AssetMusic.gameOver);
				} else {
					shouldPlay(AssetMusic.normal);
				}
			} else {
				shouldPlay(AssetMusic.normal);
			}
		}
	}

	private void shouldPlay(String musicName) {
		// Gdx.app.log(TAG, "millisStarted - TimeUtils.millis() " + (millisStarted - TimeUtils.millis()));
		if (!musicName.equals(AssetMusic.spotted) && millisStarted - TimeUtils.millis() > -2000) {
			return;
		}

		millisStarted = TimeUtils.millis();
		if (currentMusic != null && !musicName.equals(currentMusicName)) {
			if (currentMusic != null) {
				currentMusic.stop();
			}
		}

		if (!musicName.equals(currentMusicName) || currentMusic == null) {
			if (Assets.assetsManager.isLoaded(musicName)) {
				currentMusic = Assets.instance.assetsManager.get(musicName, Music.class);
				currentMusic.setVolume(isMute ? 0f : 0.5f);
				currentMusic.play();
				currentMusic.setLooping(true);
				currentMusicName = musicName;
			}
		}

	}

	public void init(ArtTag game) {
		this.game = game;
		this.currentMusicName = AssetMusic.intro;
	}

	public boolean isMute() {
		return isMute;
	}

	public void setMute(boolean isMute) {
		this.isMute = isMute;
		if (currentMusic != null) {
			currentMusic.setVolume(isMute ? 0f : MUSIC_VOLUME);
		}
	}

	public void forceMusicChange() {
		millisStarted = 0;
	}
}
