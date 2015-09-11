package headmade.arttag;

import headmade.arttag.screens.ArtTagScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class ArtTagInputController extends InputAdapter {

	private static final String	TAG	= ArtTagInputController.class.getName();

	private final DirectedGame	game;
	private final ArtTagScreen	artTagScreen;

	public ArtTagInputController(DirectedGame game, ArtTagScreen artTagScreen) {
		this.game = game;
		this.artTagScreen = artTagScreen;
	}

	@Override
	public boolean keyDown(int keycode) {
		final float movementSpeed = 10f;
		if (keycode == Keys.LEFT || keycode == Keys.A) {
			Player.instance.isMoveLeft = true;
			return true;
		} else if (keycode == Keys.RIGHT || keycode == Keys.D) {
			Player.instance.isMoveRight = true;
			return true;
		} else if (keycode == Keys.UP || keycode == Keys.W) {
			Player.instance.isMoveUp = true;
			return true;
		} else if (keycode == Keys.DOWN || keycode == Keys.S) {
			Player.instance.isMoveDown = true;
			return true;
		} else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT || keycode == Keys.Z || keycode == Keys.SPACE) {
			// action button 1
			if (ArtTag.TOGGLE_LIGHT) {
				Player.instance.isLightOn = !Player.instance.isLightOn;
			} else {
				Player.instance.isLightOn = true;
			}
			return true;
		} else if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT || keycode == Keys.X || keycode == Keys.SHIFT_LEFT
				|| keycode == Keys.SHIFT_RIGHT) {
			// action button 2
			if (Player.instance.isAbleToSteal) {
				Player.instance.steal(artTagScreen);
				return true;
			} else if (Player.instance.isAbleToScan) {
				Player.instance.scan(artTagScreen);
				return true;
			} else if (Player.instance.isTouchingExit) {
				if (Player.instance.isExitActivated) {
					artTagScreen.endLevel();
				} else {
					Player.instance.activateExit(artTagScreen);
				}
				Player.instance.scan(artTagScreen);
				return true;
			} else if (Player.instance.isTouchingDoor) {
				artTagScreen.newLevel();
				return true;
			}

			// default action
			Player.instance.isRunning = true;
			return true;
		} else if (keycode == Keys.F12) {
			artTagScreen.debugEnabled = !artTagScreen.debugEnabled;
			return true;
		}
		if (artTagScreen.debugEnabled) {
			// debug inputs
			if (keycode == Keys.SPACE) {
				artTagScreen.newJob();
				return true;
			} else if (keycode == Keys.J) {
				final Camera cam = artTagScreen.camera;
				cam.translate(-1 * movementSpeed, 0, 0);
				cam.update();
				return true;
			} else if (keycode == Keys.L) {
				final Camera cam = artTagScreen.camera;
				cam.translate(1 * movementSpeed, 0, 0);
				cam.update();
				return true;
			} else if (keycode == Keys.I) {
				final Camera cam = artTagScreen.camera;
				cam.translate(0, 1 * movementSpeed, 0);
				cam.update();
				return true;
			} else if (keycode == Keys.K) {
				final Camera cam = artTagScreen.camera;
				cam.translate(1, -1 * movementSpeed, 0);
				cam.update();
				return true;
			}
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		final Vector3 worldClickVec = artTagScreen.camera.unproject(new Vector3(screenX, screenY, 0));
		Gdx.app.log(TAG, "Clicked at " + worldClickVec);
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean scrolled(int amount) {
		final OrthographicCamera cam = (OrthographicCamera) artTagScreen.camera;
		cam.zoom += amount * 0.5f;
		cam.zoom = MathUtils.clamp(cam.zoom, 0.005f, 10f);
		cam.update();
		Gdx.app.log(TAG, "Zoom changed to " + cam.zoom);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT || keycode == Keys.A) {
			Player.instance.isMoveLeft = false;
			return true;
		} else if (keycode == Keys.RIGHT || keycode == Keys.D) {
			Player.instance.isMoveRight = false;
			return true;
		} else if (keycode == Keys.UP || keycode == Keys.W) {
			Player.instance.isMoveUp = false;
			return true;
		} else if (keycode == Keys.DOWN || keycode == Keys.S) {
			Player.instance.isMoveDown = false;
			return true;
		} else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT || keycode == Keys.Z || keycode == Keys.SPACE) {
			// action button 1
			if (ArtTag.TOGGLE_LIGHT) {
			} else {
				Player.instance.isLightOn = false;
			}
			return true;
		} else if (keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT || keycode == Keys.X || keycode == Keys.SHIFT_LEFT
				|| keycode == Keys.SHIFT_RIGHT) {
			// action button 2
			Player.instance.isRunning = false;
			return true;
		}
		return super.keyUp(keycode);
	}

}
