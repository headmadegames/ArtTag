package headmade.arttag.service;

import headmade.arttag.ArtTag;
import headmade.arttag.Player;
import headmade.arttag.assets.AssetMusic;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.ArtTagScreen;
import headmade.arttag.screens.IntroScreen;
import headmade.arttag.screens.MenuScreen;
import headmade.arttag.screens.RatingScreen;

import com.badlogic.gdx.audio.Music;

public class MusicService {

	private static final String	TAG				= MusicService.class.getName();

	private static final float	MUSIC_VOLUME	= 0.5f;

	public static MusicService	instance		= new MusicService();

	private boolean				isMute			= false;
	private ArtTag				game;
	private String				currentMusicName;
	private Music				currentMusic;

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
}
