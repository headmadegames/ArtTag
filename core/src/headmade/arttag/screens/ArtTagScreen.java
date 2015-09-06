package headmade.arttag.screens;

import headmade.arttag.ArtTag;
import headmade.arttag.ArtTagInputController;
import headmade.arttag.DirectedGame;
import headmade.arttag.Player;
import headmade.arttag.assets.AssetMaps;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.utils.MapUtils;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import box2dLight.Light;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class ArtTagScreen extends StageScreen {

	private final PerspectiveCamera		perspectiveCam;
	private final CameraInputController	camController;
	private final Box2DDebugRenderer	box2dDebugRenderer;
	private final ArtTagInputController	inputController;
	private final OrthographicCamera	worldCam;

	public OrthogonalTiledMapRenderer	mapRenderer;
	public TiledMap						map;
	public final World					world;
	public final RayHandler				rayHandler;
	public Array<Light>					lights	= new Array<Light>();

	// private final Map map;

	public ArtTagScreen(DirectedGame game) {
		super(game);

		perspectiveCam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		perspectiveCam.position.set(300f, 3000f, 1000f);
		perspectiveCam.lookAt(300, 300, 0);
		perspectiveCam.near = 0.1f;
		perspectiveCam.far = 10000f;
		perspectiveCam.update();

		worldCam = new OrthographicCamera(camera.viewportWidth * ArtTag.UNIT_SCALE, camera.viewportHeight * ArtTag.UNIT_SCALE);

		this.world = new World(new Vector2(0f, 0f), true);

		/** BOX2D LIGHT STUFF BEGIN */
		// rayHandler = new RayHandler(world);
		// RayHandler.setGammaCorrection(true);
		// RayHandler.useDiffuseLight(true);

		// Shader
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.1f, 0.1f, 0.2f, 0.3f);
		rayHandler.setBlurNum(3);
		rayHandler.diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
		// RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		// rayHandler.setBlur(false);
		/** BOX2D LIGHT STUFF END */

		box2dDebugRenderer = new Box2DDebugRenderer();

		camController = new CameraInputController(perspectiveCam);
		inputController = new ArtTagInputController(game, this);

		MapUtils.loadMap(this, AssetMaps.map1);
	}

	@Override
	public void render(float delta) {
		world.step(ArtTag.TIME_STEP, ArtTag.VELOCITY_ITERS, ArtTag.POSITION_ITERS);

		Player.update();
		// worldCam.position.x = camera.position.x * ArtTag.UNIT_SCALE;
		// worldCam.position.y = camera.position.y * ArtTag.UNIT_SCALE;
		worldCam.zoom = ((OrthographicCamera) camera).zoom;
		worldCam.position.x = Player.body.getPosition().x;
		worldCam.position.y = Player.body.getPosition().y;
		worldCam.update();

		Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		camController.update();
		final SpriteBatch batch = game.getBatch();
		batch.setColor(Color.WHITE);
		batch.setProjectionMatrix(worldCam.combined);

		mapRenderer.setView(worldCam);
		mapRenderer.render();
		batch.begin();
		{
			batch.setProjectionMatrix(perspectiveCam.combined);
			Box2DSprite.draw(game.getBatch(), world);
			final Texture tex = Assets.assetsManager.get(AssetTextures.portrait1, Texture.class);
			final NinePatch frame = Assets.instance.skin.get(AssetTextures.frame2Large, NinePatch.class);

			tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			batch.draw(tex, 0, 0);
			frame.draw(batch, 0, 0, tex.getWidth(), tex.getHeight());

			batch.setProjectionMatrix(worldCam.combined);

			if (Gdx.input.isTouched()) {
				batch.draw(tex, frame.getPadLeft(), frame.getPadBottom());
				frame.draw(batch, 0, 0, tex.getWidth() + frame.getPadLeft() + frame.getPadRight(), tex.getHeight() + frame.getPadTop()
						+ frame.getPadBottom());
			}
		}
		batch.end();

		rayHandler.setCombinedMatrix(worldCam);
		rayHandler.updateAndRender();

		box2dDebugRenderer.render(world, worldCam.combined);

	}

	@Override
	public void dispose() {
		super.dispose();
		mapRenderer.dispose();
		rayHandler.dispose();
		world.dispose();
		box2dDebugRenderer.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		// return camController;
		return inputController;
	}

}
