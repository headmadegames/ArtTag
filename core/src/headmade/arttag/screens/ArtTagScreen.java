package headmade.arttag.screens;

import headmade.arttag.ArtTag;
import headmade.arttag.ArtTagContactListener;
import headmade.arttag.ArtTagInputController;
import headmade.arttag.DirectedGame;
import headmade.arttag.JobDescription;
import headmade.arttag.Player;
import headmade.arttag.actors.Art;
import headmade.arttag.assets.AssetMaps;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.service.TagService;
import headmade.arttag.utils.MapUtils;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import box2dLight.Light;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class ArtTagScreen extends StageScreen {

	private static final float			MIN_WORLD_WIDTH		= Gdx.graphics.getWidth() * ArtTag.UNIT_SCALE;
	private static final float			MIN_WORLD_HEIGHT	= Gdx.graphics.getHeight() * ArtTag.UNIT_SCALE;
	private final Box2DDebugRenderer	box2dDebugRenderer;
	private final ArtTagInputController	inputController;
	private final ContactListener		contactListener;

	public OrthogonalTiledMapRenderer	mapRenderer;
	public TiledMap						map;
	public final World					world;
	public final RayHandler				rayHandler;
	public Array<Light>					lights				= new Array<Light>();
	public JobDescription				jobDescription;
	public Array<Art>					artList				= new Array<Art>();
	public Art							currentArt;
	public boolean						debugEnabled;

	private final Table					rootTable;
	private final Image					imageActor;
	private final Label					jobDescActor;
	private final Label					instructionsActor;
	private final Label					resultActor;

	// private final Map map;

	public ArtTagScreen(DirectedGame game) {
		super(game);// , new Stage(new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT), game.getBatch()));
		camera = new OrthographicCamera(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT);

		// perspectiveCam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// perspectiveCam.position.set(300f, 3000f, 1000f);
		// perspectiveCam.lookAt(300, 300, 0);
		// perspectiveCam.near = 0.1f;
		// perspectiveCam.far = 10000f;
		// perspectiveCam.update();

		contactListener = new ArtTagContactListener(this);

		this.world = new World(new Vector2(0f, 0f), true);
		world.setContactListener(contactListener);

		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.1f, 0.1f, 0.2f, 0.3f);
		rayHandler.setBlurNum(3);
		rayHandler.diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
		// RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		// rayHandler.setBlur(false);
		/** BOX2D LIGHT STUFF END */

		box2dDebugRenderer = new Box2DDebugRenderer();
		inputController = new ArtTagInputController(game, this);

		jobDescription = new JobDescription();
		imageActor = new Image(Assets.assetsManager.get(AssetTextures.animal4, Texture.class));
		jobDescActor = new Label(jobDescription.desc, Assets.instance.skin, "jobDesc");
		jobDescActor.setWrap(true);
		instructionsActor = new Label("Instructions", Assets.instance.skin, "jobDesc");
		instructionsActor.setWrap(true);
		resultActor = new Label("Result", Assets.instance.skin, "jobDesc");
		resultActor.setVisible(false);
		resultActor.setAlignment(Align.center);

		rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);
		rootTable.add(jobDescActor).width(Gdx.graphics.getWidth() / 4).pad(10f);
		rootTable.add(imageActor).center().expand();
		rootTable.add(instructionsActor).bottom().right().width(Gdx.graphics.getWidth() / 4).pad(10f);
		rootTable.row();
		rootTable.add(resultActor).width(Gdx.graphics.getWidth() / 4).center().colspan(3);

		stage.addActor(rootTable);

		MapUtils.loadMap(this, AssetMaps.map1);
	}

	@Override
	public void render(float delta) {
		world.step(ArtTag.TIME_STEP, ArtTag.VELOCITY_ITERS, ArtTag.POSITION_ITERS);

		Player.instance.update(this, delta);
		// camera.position.x = camera.position.x * ArtTag.UNIT_SCALE;
		// camera.position.y = camera.position.y * ArtTag.UNIT_SCALE;
		camera.position.x = Player.instance.body.getPosition().x;
		camera.position.y = Player.instance.body.getPosition().y;
		camera.update();

		// UI update
		if (currentArt != null) {
			imageActor.setDrawable(new TextureRegionDrawable(new TextureRegion(currentArt.image)));
			rootTable.layout();
		} else {
			imageActor.setDrawable(null);
		}
		if (Player.instance.isScanning || (currentArt != null && currentArt.isScanned)) {
			resultActor.setVisible(true);
		}

		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		final SpriteBatch batch = game.getBatch();
		batch.setColor(Color.WHITE);
		batch.setProjectionMatrix(camera.combined);

		mapRenderer.setView((OrthographicCamera) camera);
		mapRenderer.render(MapUtils.MAP_LAYERS_LOW);

		{ // player and goards
			batch.begin();
			Box2DSprite.draw(batch, world);
			batch.end();
		}

		mapRenderer.render(MapUtils.MAP_LAYERS_HIGH);

		batch.begin();
		{
			// final Matrix4 oldTransMat = batch.getTransformMatrix();
			for (final Art art : artList) {
				art.draw(batch);
				// art.drawFrame(batch);
			}
			// batch.setTransformMatrix(stage.getCamera().combined);
			// batch.flush();
			// for (final Art art : artList) {
			// art.drawFrame(batch);
			// }
			// batch.setTransformMatrix(oldTransMat);
			// batch.flush();

			// final Texture tex = Assets.assetsManager.get(AssetTextures.portrait1, Texture.class);
			// final NinePatch frame = Assets.instance.skin.get(AssetTextures.frame2Large, NinePatch.class);
			// tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			// batch.draw(tex, 0, 0);
			// frame.draw(batch, 0, 0, tex.getWidth(), tex.getHeight());

		}
		batch.end();

		rayHandler.setCombinedMatrix((OrthographicCamera) camera);
		rayHandler.updateAndRender();

		if (currentArt != null) {
			imageActor.getColor().a = Player.instance.imageAlpha;
		}
		stage.act(delta);
		stage.draw();

		if (debugEnabled) {
			box2dDebugRenderer.render(world, camera.combined);
		}
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

	public void newJob() {
		jobDescription = new JobDescription();
		jobDescActor.setText(jobDescription.desc);
	}

	public void endLevel() {
		TagService.instance.tag(artList);
		System.out.println(TagService.instance.tagVos);
	}

	public void setInstruction(String text) {
		instructionsActor.setText(text);
	}

	public void setResult(String text) {
		resultActor.setText(text);
	}

}
