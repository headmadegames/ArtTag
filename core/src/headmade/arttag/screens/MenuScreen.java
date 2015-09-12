package headmade.arttag.screens;

import headmade.arttag.DirectedGame;
import headmade.arttag.actions.ActionFactory;
import headmade.arttag.actors.CreditsActor;
import headmade.arttag.actors.HowToActor;
import headmade.arttag.actors.JigglyImageTextButton;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransition;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import headmade.arttag.service.MusicService;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class MenuScreen extends StageScreen {
	private static final String					TAG					= MenuScreen.class.getName();
	private final CreditsActor					credits;
	private final HowToActor					howTo;

	private int									activeButtonIndex	= 0;
	private int									clearActionsButtonIndex;
	private Action								jiggleAction;
	private JigglyImageTextButton				playButton;
	private JigglyImageTextButton				muteButton;
	private JigglyImageTextButton				howtoButton;
	private JigglyImageTextButton				creditsButton;

	private final Array<JigglyImageTextButton>	buttons				= new Array<JigglyImageTextButton>();

	public MenuScreen(DirectedGame game) {
		super(game);
		jiggleAction = ActionFactory.wiggleRepeat(1f, 0.8f);

		final Table rootTable = new Table();
		rootTable.setFillParent(true);

		howTo = new HowToActor();
		credits = new CreditsActor();
		final Actor artTreachery = new Image(Assets.instance.skin.getRegion(AssetTextures.artTreachery));

		credits.getColor().a = 0f;

		final Stack mainContainer = new Stack();
		mainContainer.add(howTo);
		mainContainer.add(credits);

		rootTable.add(artTreachery).colspan(2).padTop(20f).row();
		rootTable.add(mainContainer).expand();// .fill(1f, 1f);
		rootTable.add(buildMenu()).expandY().center().padRight(20f);
		rootTable.row();
		// rootTable.setDebug(true);

		this.stage.addActor(rootTable);

		stage.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.LEFT || keycode == Keys.A) {
					activatePrevButton();
					return true;
				} else if (keycode == Keys.RIGHT || keycode == Keys.D) {
					activateNextButton();
					return true;
				} else if (keycode == Keys.UP || keycode == Keys.W) {
					activatePrevButton();
					return true;
				} else if (keycode == Keys.DOWN || keycode == Keys.S) {
					activateNextButton();
					return true;
				} else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT || keycode == Keys.Z || keycode == Keys.SPACE) {
					// action button 1
					useActiveButton();
					return true;
				} else if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT || keycode == Keys.X || keycode == Keys.SHIFT_LEFT
						|| keycode == Keys.SHIFT_RIGHT) {
					// action button 2
					useActiveButton();
					return true;
				}
				return super.keyDown(event, keycode);
			}

			private void useActiveButton() {
				final InputEvent event = new InputEvent();
				event.setPointer(0);
				event.setType(Type.touchDown);
				buttons.get(activeButtonIndex).fire(event);
			}

			private void activatePrevButton() {
				InputEvent event = new InputEvent();
				event.setPointer(-1);
				event.setType(Type.exit);
				buttons.get(activeButtonIndex).fire(event);

				buttons.get(activeButtonIndex).removeAction(jiggleAction);
				if (activeButtonIndex == 0) {
					activeButtonIndex = buttons.size - 1;
				} else {
					activeButtonIndex--;
				}
				jiggleAction = ActionFactory.wiggleRepeat(1f, 0.8f);
				buttons.get(activeButtonIndex).addAction(jiggleAction);

				event = new InputEvent();
				event.setPointer(-1);
				event.setType(Type.enter);
				buttons.get(activeButtonIndex).fire(event);
			}

			private void activateNextButton() {
				InputEvent event = new InputEvent();
				event.setPointer(-1);
				event.setType(Type.exit);
				buttons.get(activeButtonIndex).fire(event);

				buttons.get(activeButtonIndex).removeAction(jiggleAction);
				if (activeButtonIndex == buttons.size - 1) {
					activeButtonIndex = 0;
				} else {
					activeButtonIndex++;
				}
				jiggleAction = ActionFactory.wiggleRepeat(1f, 0.8f);
				buttons.get(activeButtonIndex).addAction(jiggleAction);

				event = new InputEvent();
				event.setPointer(-1);
				event.setType(Type.enter);
				buttons.get(activeButtonIndex).fire(event);
			}

		});

		// ((OrthographicCamera) stage.getCamera()).zoom = 0.5f;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
	}

	private Table buildMenu() {
		playButton = new JigglyImageTextButton("Play", Assets.instance.skin, "play", jiggleAction);
		playButton.setName("play");
		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				play();
				return true;
			}
		});
		playButton.getLabelCell().padRight(10f);

		muteButton = new JigglyImageTextButton("Mute music", Assets.instance.skin, "settings", null);
		muteButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				MusicService.instance.setMute(!MusicService.instance.isMute());
				if (MusicService.instance.isMute()) {
					muteButton.setText("Unmute music");
				} else {
					muteButton.setText("Mute music");
				}
				return true;
			}
		});

		howtoButton = new JigglyImageTextButton("How to", Assets.instance.skin, "howto", null);
		howtoButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showHowto();
				return true;
			}
		});

		creditsButton = new JigglyImageTextButton("Credits", Assets.instance.skin, "howto", null);
		creditsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showCredits();
				return true;
			}
		});

		final Table menu = new Table();
		menu.add(playButton).fill().row();
		menu.add(muteButton).fill().space(5).padRight(20f).row();
		menu.add(creditsButton).fill().space(5).padRight(20f).row();
		menu.add(howtoButton).fill().space(5).padRight(20f).row();

		buttons.add(playButton);
		buttons.add(muteButton);
		buttons.add(creditsButton);
		buttons.add(howtoButton);
		return menu;
	}

	protected void play() {
		final ScreenTransition transition = ScreenTransitionFade.init(0.3f);
		// game.setScreen(new MenuScreen(game), transition);
		game.setScreen(new ArtTagScreen(game), transition);
	}

	protected void showCredits() {
		credits.addAction(Actions.fadeIn(0.3f));
		howTo.addAction(Actions.fadeOut(0.3f));
	}

	protected void showHowto() {
		howTo.addAction(Actions.fadeIn(0.3f));
		credits.addAction(Actions.fadeOut(0.3f));
	}
}
