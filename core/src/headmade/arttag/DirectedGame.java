package headmade.arttag;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import headmade.arttag.screens.AbstractGameScreen;
import headmade.arttag.screens.transitions.ScreenTransition;
import headmade.arttag.service.MusicService;

public abstract class DirectedGame extends Game {
	private static final String TAG = DirectedGame.class.getName();

	private boolean				init;
	private AbstractGameScreen	currScreen;
	private AbstractGameScreen	nextScreen;
	private FrameBuffer			currFbo;
	private FrameBuffer			nextFbo;
	private float				time;
	private ScreenTransition	screenTransition;

	protected SpriteBatch batch;

	/**
	 * Called on android when back button is pressed
	 */
	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "dispose() called on screen");

		if (currScreen != null) {
			currScreen.hide();
			// currScreen.dispose();
		}
		if (nextScreen != null) {
			nextScreen.hide();
			// nextScreen.dispose();
		}
		if (init) {
			currFbo.dispose();
			currScreen = null;
			nextFbo.dispose();
			nextScreen = null;
			batch.dispose();
			init = false;
		}

	}

	@Override
	public Screen getScreen() {
		return currScreen;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public void pause() {
		Gdx.app.debug(TAG, "pause() called on screen");

		if (currScreen != null) {
			currScreen.pause();
		}
	}

	@Override
	public void render() {
		// get delta time and ensure an upper limit of one 60th second
		final float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);
		if (nextScreen == null) {
			// no ongoing transition
			if (currScreen != null) {
				currScreen.render(deltaTime);
			}
		} else {
			// ongoing transition
			float duration = 0;
			if (screenTransition != null) {
				duration = screenTransition.getDuration();
			}
			// update progress of ongoing transition
			time = Math.min(time + deltaTime, duration);
			if (screenTransition == null || time >= duration) {
				// no transition effect set or transition has just finished
				if (currScreen != null) {
					currScreen.hide();
				}
				nextScreen.resume();
				// enable input for next screen
				Gdx.input.setInputProcessor(nextScreen.getInputProcessor());
				// switch screens
				currScreen = nextScreen;
				nextScreen = null;
				screenTransition = null;
			} else {
				// render screens to FBOs
				currFbo.begin();
				if (currScreen != null) {
					currScreen.render(deltaTime);
				}
				currFbo.end();
				nextFbo.begin();
				nextScreen.render(deltaTime);
				nextFbo.end();

				if (screenTransition != null) {
					// render transition effect to screen
					final float alpha = time / duration;
					screenTransition.render(batch, currFbo.getColorBufferTexture(), nextFbo.getColorBufferTexture(), alpha);
				}
			}
		}
		MusicService.instance.update();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.debug(TAG, "resize(): " + width + " " + height);
		if (currScreen != null) {
			currScreen.resize(width, height);
		}
		if (nextScreen != null) {
			nextScreen.resize(width, height);
		}
	}

	@Override
	public void resume() {

		Gdx.app.debug(TAG, "resume() called on screen");

		if (currScreen != null) {
			currScreen.resume();
		}
	}

	public void setScreen(AbstractGameScreen screen) {
		setScreen(screen, null);
	}

	public void setScreen(AbstractGameScreen screen, ScreenTransition screenTransition) {
		final int w = Gdx.graphics.getWidth();
		final int h = Gdx.graphics.getHeight();
		if (!init) {
			currFbo = new FrameBuffer(Format.RGB888, w, h, false);
			nextFbo = new FrameBuffer(Format.RGB888, w, h, false);
			init = true;
		}
		// start new transition
		nextScreen = screen;
		nextScreen.show(); // activate next screen
		nextScreen.resize(w, h);
		nextScreen.render(0); // let screen update() once
		if (currScreen != null) {
			currScreen.pause();
		}

		Gdx.input.setInputProcessor(null); // disable input
		// this.screenTransition = screenTransition;
		this.screenTransition = null;
		time = 0;
	}

}
