package headmade.arttag.screens;

import headmade.arttag.DirectedGame;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransition;
import headmade.arttag.screens.transitions.ScreenTransitionFade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class IntroScreen extends StageScreen {

	private static final String	TAG	= IntroScreen.class.getName();

	private final Table			rootTable;

	private boolean				isStarted;

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
		game.setScreen(new MenuScreen(game), transition);
	}
}
