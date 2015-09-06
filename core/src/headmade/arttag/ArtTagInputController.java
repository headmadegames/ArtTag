package headmade.arttag;

import headmade.arttag.screens.ArtTagScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

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
			Player.isMoveLeft = true;
			return true;
		} else if (keycode == Keys.RIGHT || keycode == Keys.D) {
			Player.isMoveRight = true;
			return true;
		} else if (keycode == Keys.UP || keycode == Keys.W) {
			Player.isMoveUp = true;
			return true;
		} else if (keycode == Keys.DOWN || keycode == Keys.S) {
			Player.isMoveDown = true;
			return true;
		} else if (keycode == Keys.ALT_LEFT) {
			// action button 1
			return true;
		} else if (keycode == Keys.CONTROL_LEFT) {
			// action button 2
			return true;
		} else if (keycode == Keys.J) {
			final Camera cam = artTagScreen.getStage().getCamera();
			cam.translate(-1 * movementSpeed, 0, 0);
			cam.update();
			return true;
		} else if (keycode == Keys.L) {
			final Camera cam = artTagScreen.getStage().getCamera();
			cam.translate(1 * movementSpeed, 0, 0);
			cam.update();
			return true;
		} else if (keycode == Keys.I) {
			final Camera cam = artTagScreen.getStage().getCamera();
			cam.translate(0, 1 * movementSpeed, 0);
			cam.update();
			return true;
		} else if (keycode == Keys.K) {
			final Camera cam = artTagScreen.getStage().getCamera();
			cam.translate(1, -1 * movementSpeed, 0);
			cam.update();
			return true;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean scrolled(int amount) {
		final OrthographicCamera cam = (OrthographicCamera) artTagScreen.getStage().getCamera();
		cam.zoom += amount * 0.5f;
		cam.zoom = MathUtils.clamp(cam.zoom, 0.5f, 10f);
		cam.update();
		Gdx.app.log(TAG, "Zoom changed to " + cam.zoom);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT || keycode == Keys.A) {
			Player.isMoveLeft = false;
			return true;
		} else if (keycode == Keys.RIGHT || keycode == Keys.D) {
			Player.isMoveRight = false;
			return true;
		} else if (keycode == Keys.UP || keycode == Keys.W) {
			Player.isMoveUp = false;
			return true;
		} else if (keycode == Keys.DOWN || keycode == Keys.S) {
			Player.isMoveDown = false;
			return true;
		} else if (keycode == Keys.ALT_LEFT) {
			// action button 1
			return true;
		} else if (keycode == Keys.CONTROL_LEFT) {
			// action button 2
			return true;
		}
		return super.keyUp(keycode);
	}

}
