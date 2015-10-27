package headmade.arttag.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import headmade.arttag.ArtTag;
import headmade.arttag.DirectedGame;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransition;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import headmade.arttag.vo.GameSettings;

public class IntroScreen extends StageScreen {

	private static final String TAG = IntroScreen.class.getName();

	private final Table rootTable;

	private boolean	isStarted;
	private boolean	isInitialised	= false;

	public IntroScreen(final DirectedGame game) {
		super(game);

		rootTable = new Table();
		rootTable.setFillParent(true);
		final Actor logo = new Image(Assets.assetsManager.get(Assets.HEADMADE_LOGO, Texture.class));
		// logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);
		// logo.scaleBy(2f);
		logo.setColor(Color.BLACK);

		rootTable.add(logo).center().expand();
		rootTable.row();

		// rootTable.setDebug(true);
		this.stage.addActor(rootTable);

		Assets.instance.loadAll();
	}

	@Override
	public void preDraw(float delta) {
		super.preDraw(delta);

		if (!isInitialised) {
			isInitialised = true;
			ArtTag.gameSettings = new GameSettings();
			final Preferences prefs = Gdx.app.getPreferences("ArtTreacheryPrefs");
			ArtTag.gameSettings.screenWidth = prefs.getInteger("screenWidth", 1280);
			ArtTag.gameSettings.screenHeight = prefs.getInteger("screenHeight", 1024);
			ArtTag.gameSettings.fullscreen = prefs.getBoolean("fullscreen", false);
			ArtTag.gameSettings.blur = prefs.getInteger("blur", 1);
			ArtTag.gameSettings.rays = prefs.getInteger("rays", 64);

			prefs.putInteger("screenWidth", ArtTag.gameSettings.screenWidth);
			prefs.putInteger("screenHeight", ArtTag.gameSettings.screenHeight);
			prefs.putBoolean("fullscreen", ArtTag.gameSettings.fullscreen);
			prefs.putInteger("blur", ArtTag.gameSettings.blur);
			prefs.putInteger("rays", ArtTag.gameSettings.rays);
			prefs.flush();

			Gdx.graphics.setDisplayMode(ArtTag.gameSettings.screenWidth, ArtTag.gameSettings.screenHeight, ArtTag.gameSettings.fullscreen);
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		}

		Assets.assetsManager.update();
		if (!isStarted && MathUtils.isEqual(1f, Assets.assetsManager.getProgress())) {
			// done loading eh
			Assets.instance.onFinishLoading();

			startGame();
			isStarted = true;

			// final TextButton startButton = new TextButton("Start", Assets.instance.skin);
			// startButton.addListener(new InputListener() {
			//
			// @Override
			// public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			// startGame();
			// return super.touchDown(event, x, y, pointer, button);
			// }
			// });
		}
	}

	protected void startGame() {
		Gdx.app.log(TAG, "Start Button clicked");

		final ScreenTransition transition = ScreenTransitionFade.init(.3f);
		// game.setScreen(new ArtTagScreen(game), transition);
		game.setScreen(new MenuScreen(game));

		// final JobDescription jobDescription = new JobDescription();
		// final Art art1 = new Art(new Rectangle(1, 1, 1, 1));
		// final Art art2 = new Art(new Rectangle(1, 1, 1, 1));
		// art1.init();
		// art2.init();
		// Player.instance.inventory.add(art1);
		// Player.instance.inventory.add(art2);
		// game.setScreen(new RatingScreen(game, jobDescription));
	}

}
