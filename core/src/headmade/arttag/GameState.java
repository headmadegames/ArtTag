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
