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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class IntroScreen extends StageScreen {

	private static final String	TAG	= IntroScreen.class.getName();

	private final Table			rootTable;

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
		if (MathUtils.isEqual(1, Assets.assetsManager.getProgress())) {
			// done loading eh
			Assets.instance.onFinishLoading();

			final TextButton startButton = new TextButton("Start", Assets.instance.skin);
			startButton.addListener(new InputListener() {

				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					startGame();
					return super.touchDown(event, x, y, pointer, button);
				}
			});

			startGame();
		}
	}

	protected void startGame() {
		Gdx.app.log(TAG, "Start Button clicked");

		final ScreenTransition transition = ScreenTransitionFade.init(0.0f);
		game.setScreen(new ArtTagScreen(game), transition);
	}
}
