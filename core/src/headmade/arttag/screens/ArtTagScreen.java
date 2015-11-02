package headmade.arttag.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.DestructionListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import box2dLight.Light;
import box2dLight.RayHandler;
import headmade.arttag.ArtTag;
import headmade.arttag.ArtTagContactListener;
import headmade.arttag.ArtTagInputController;
import headmade.arttag.DirectedGame;
import headmade.arttag.Guard;
import headmade.arttag.JobDescription;
import headmade.arttag.Player;
import headmade.arttag.Room;
import headmade.arttag.actors.Art;
import headmade.arttag.actors.WebArt;
import headmade.arttag.assets.AssetMaps;
import headmade.arttag.assets.AssetParticles;
import headmade.arttag.assets.AssetSounds;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.transitions.ScreenTransitionFade;
import headmade.arttag.service.FlickrService;
import headmade.arttag.service.MusicService;
import headmade.arttag.service.TagService;
import headmade.arttag.spriter.LibGdxDrawer;
import headmade.arttag.spriter.Loader;
import headmade.arttag.utils.MapUtils;
import headmade.arttag.utils.RandomUtil;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class ArtTagScreen extends StageScreen {

	private static final String TAG = ArtTagScreen.class.getName();

	private static final float	MIN_WORLD_WIDTH		= Gdx.graphics.getWidth() * ArtTag.UNIT_SCALE;
	private static final float	MIN_WORLD_HEIGHT	= Gdx.graphics.getHeight() * ArtTag.UNIT_SCALE;

	public static final int MAX_ROOM_SIZE = 9;

	private final ShapeRenderer			shapeRenderer;
	private final Box2DDebugRenderer	box2dDebugRenderer;
	private final ArtTagInputController	inputController;
	private final ContactListener		contactListener;
	private Art							currentArt;

	public OrthogonalTiledMapRenderer	mapRenderer;
	public Room[][]						rooms				= new Room[MAX_ROOM_SIZE][MAX_ROOM_SIZE];
	public Room							currentRoom;
	public int							currentRoomIndexX	= MAX_ROOM_SIZE / 2;
	public int							currentRoomIndexY	= MAX_ROOM_SIZE / 2;
	public final World					world;
	public final RayHandler				rayHandler;
	public Array<Light>					lights				= new Array<Light>();
	public Array<Guard>					guards				= new Array<Guard>();
	public JobDescription				jobDescription;
	public boolean						debugEnabled;
	public String						onGameOver;
	public boolean						isHideJobDesc;

	private final Table	rootTable;
	private final Image	imageActor;
	private final Label	jobDescActor;
	private final Label	instructionsActor;
	private final Label	resultActor;

	private float sumDeltaLookAtImage;

	private final Array<headmade.arttag.spriter.Player> players = new Array<headmade.arttag.spriter.Player>();

	private final LibGdxDrawer drawer;

	private final ParticleEffect		smokeEffect;
	private final ParticleEffectPool	smokeEffectPool;
	private final Array<PooledEffect>	effects	= new Array();

	private boolean isGameOver;

	private float gameOverDelta;

	private final FPSLogger fpsLogger;

	// private final Map map;

	public ArtTagScreen(DirectedGame game, String map) {
		super(game);// , new Stage(new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT), game.getBatch()));

		fpsLogger = new FPSLogger();

		Player.instance.inventory.clear();
		Player.instance.setControlArtCount(0);
		Player.instance.setArtScanCount(0);
		Player.instance.setArtViewCount(0);
		Player.instance.isMoveDown = false;
		Player.instance.isMoveUp = false;
		Player.instance.isMoveLeft = false;
		Player.instance.isMoveRight = false;

		camera = new OrthographicCamera(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT);
		((OrthographicCamera) camera).zoom = 0.5f;

		// perspectiveCam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// perspectiveCam.position.set(300f, 3000f, 1000f);
		// perspectiveCam.lookAt(300, 300, 0);
		// perspectiveCam.near = 0.1f;
		// perspectiveCam.far = 10000f;
		// perspectiveCam.update();

		contactListener = new ArtTagContactListener(this);

		this.world = new World(new Vector2(0f, 0f), true);
		world.setContactListener(contactListener);
		world.setDestructionListener(new DestructionListener() {

		});

		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.08f, 0.08f, 0.16f, 0.1f);
		rayHandler.setBlurNum(ArtTag.gameSettings.blur);
		rayHandler.diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
		// RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		// rayHandler.setBlur(false);
		/** BOX2D LIGHT STUFF END */

		shapeRenderer = new ShapeRenderer();
		box2dDebugRenderer = new Box2DDebugRenderer();
		inputController = new ArtTagInputController(game, this);

		jobDescActor = new Label("", Assets.instance.skin, "jobDesc");
		imageActor = new Image(Assets.assetsManager.get(AssetTextures.animal4, Texture.class));
		jobDescActor.setWrap(true);
		instructionsActor = new Label("Instructions", Assets.instance.skin, "info");
		instructionsActor.setWrap(true);
		instructionsActor.setVisible(false);
		resultActor = new Label("Result", Assets.instance.skin, "scanner");
		resultActor.setVisible(false);
		resultActor.setAlignment(Align.center);
		final Label scoreLabel = new Label("Your Score", Assets.instance.skin, "white");
		final Label highscoreLabel = new Label("Highscore", Assets.instance.skin, "white");
		final Label scoreActor = new Label("$" + Player.instance.getCash(), Assets.instance.skin, "dollar");
		final Label highscoreActor = new Label("$" + ArtTag.highScore, Assets.instance.skin, "dollar");

		Gdx.app.log(TAG, "camera.viewportWidth " + camera.viewportWidth + " Gdx.graphics.getWidth(): " + Gdx.graphics.getWidth());
		rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);
		rootTable.add(jobDescActor).pad(10f).width(camera.viewportWidth / ArtTag.UNIT_SCALE / 4);
		rootTable.add(imageActor).center().expand();
		rootTable.add(instructionsActor).top().right().pad(10f).width(camera.viewportWidth / ArtTag.UNIT_SCALE / 4);
		rootTable.row();
		rootTable.add(scoreLabel).left();
		rootTable.add();
		rootTable.add(highscoreLabel).right();
		rootTable.row();
		rootTable.add(scoreActor).left();
		rootTable.add(resultActor).center().width(camera.viewportWidth / ArtTag.UNIT_SCALE / 4);
		rootTable.add(highscoreActor).right();

		stage.addActor(rootTable);

		newJob();
		MapUtils.loadMap(this, map != null ? map : AssetMaps.map1);

		final Loader<Sprite> loader = Assets.instance.getSpriterLoader();
		drawer = new LibGdxDrawer(loader, game.getBatch(), shapeRenderer);
		final headmade.arttag.spriter.Player player = new headmade.arttag.spriter.Player(Assets.instance.getMaggieSpriterData());
		player.setAnimation("run");
		player.setScale(ArtTag.UNIT_SCALE * 0.8f);
		players.add(player);

		smokeEffect = new ParticleEffect();
		smokeEffect.load(Gdx.files.internal(AssetParticles.smoke), Assets.instance.atlas);
		smokeEffect.scaleEffect(ArtTag.UNIT_SCALE);
		smokeEffectPool = new ParticleEffectPool(smokeEffect, 2, 4);

	}

	@Override
	public void render(float delta) {
		fpsLogger.log();

		// while (FlickrService.instance.getWebArtCount() == 0) {
		// // wait
		// return;
		// }

		if (gameOverDelta > 3f) {
			endLevel();
			gameOverDelta = 0f;
		}

		if (Player.instance.body != null && Player.instance.isCaught) {
			final PooledEffect effect = smokeEffectPool.obtain();
			effect.setPosition(Player.instance.body.getWorldCenter().x, Player.instance.body.getWorldCenter().y);
			effects.add(effect);
			Assets.instance.playSound(AssetSounds.smoke);
			Assets.instance.playSound(AssetSounds.cough);
			Player.instance.isCaught = false;
			Player.instance.destroyBody(this);
			isGameOver = true;
		}

		if (!isGameOver) {
			// isSpotted will be set by guards
			Player.instance.isSpotted = false;
			for (final Guard g : guards) {
				g.update(this, delta);
			}
			Player.instance.update(this, delta);
			world.step(ArtTag.TIME_STEP, ArtTag.VELOCITY_ITERS, ArtTag.POSITION_ITERS);
		} else {
			gameOverDelta += delta;
		}

		if (Player.instance.isTouchingWarp) {
			newRoom();
		}

		// camera.position.x = camera.position.x * ArtTag.UNIT_SCALE;
		// camera.position.y = camera.position.y * ArtTag.UNIT_SCALE;
		if (Player.instance.body != null) {
			camera.position.x = Player.instance.body.getPosition().x;
			camera.position.y = Player.instance.body.getPosition().y;
			camera.update();
		}

		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		// Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
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
			shapeRenderer.begin(ShapeType.Line);

			Box2DSprite.draw(batch, world);

			// for (final headmade.arttag.spriter.Player player : players) {
			// player.update();
			// if (Player.instance.body != null) {
			// player.setPosition(Player.instance.body.getWorldCenter().x, Player.instance.body.getWorldCenter().y);
			// }
			// drawer.draw(player);
			// }

			batch.end();
			shapeRenderer.end();
		}

		mapRenderer.render(MapUtils.MAP_LAYERS_HIGH);

		{
			batch.begin();
			{
				// final Matrix4 oldTransMat = batch.getTransformMatrix();
				for (final Art art : currentRoom.getArtList()) {
					art.draw(batch);
					// art.drawFrame(batch);
				}

				for (int i = effects.size - 1; i >= 0; i--) {
					final PooledEffect effect = effects.get(i);
					effect.draw(batch, delta);
					if (effect.isComplete()) {
						effect.free();
						effects.removeIndex(i);
					}
				}
			}
			batch.end();
		}

		rayHandler.setCombinedMatrix((OrthographicCamera) camera);
		rayHandler.updateAndRender();

		// UI update
		if (Player.instance.artInView.size == 0) {
			currentArt = null;
		}
		if (currentArt != null) {
			imageActor.setDrawable(currentArt.getDrawable());
			rootTable.layout();
		} else {
			imageActor.setDrawable(null);
		}
		// show scan result?
		if (Player.instance.isTouchingArt && (Player.instance.isScanning || currentArt != null && currentArt.isScanned())) {
			resultActor.setVisible(true);
		} else {
			resultActor.setVisible(false);
		}
		// render UI
		if (currentArt != null) {
			final float alpha = Player.instance.imageAlpha;
			imageActor.getColor().a = alpha;
			if (alpha > 0f) {
				instructionsActor.setVisible(true);
				jobDescActor.setVisible(true);
			} else {
				instructionsActor.setVisible(false);
				jobDescActor.setVisible(false);
			}

			if (alpha > 0.3f) {
				if (sumDeltaLookAtImage > 0.3f) {
					currentArt.setSeen(true);
					TagService.instance.tagNotMatched(currentArt, jobDescription);
				} else {
					sumDeltaLookAtImage += delta;
				}
			} else {
				sumDeltaLookAtImage = 0f;
			}
		}

		if (isHideJobDesc) {
			jobDescActor.setVisible(false);
		}

		if (Player.instance.inventory.size >= Player.instance.getCarryCacity()) {
			instructionsActor.setVisible(true);
		}
		stage.act(delta);
		stage.draw();

		if (debugEnabled) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.setAutoShapeType(true);
			shapeRenderer.begin();
			for (final Guard g : guards) {
				{
					if (g.getBackToPath().size > 0) {
						final float[] vertices = new float[2 + g.getBackToPath().size * 2];
						vertices[0] = g.body.getPosition().x;
						vertices[1] = g.body.getPosition().y;
						int index = 1;
						for (int i = g.getBackToPath().size - 1; i >= 0; i--) {
							vertices[index * 2] = g.getBackToPath().get(i).x;
							vertices[index * 2 + 1] = g.getBackToPath().get(i).y;
							index++;
						}
						shapeRenderer.polyline(vertices);
					}
				}
				{
					if (g.path.size > 0) {
						final float[] vertices = new float[2 + g.path.size * 2];
						vertices[0] = g.body.getPosition().x;
						vertices[1] = g.body.getPosition().y;
						int index = 1;
						for (int i = g.path.size - 1; i >= 0; i--) {
							vertices[index * 2] = g.path.get(i).x;
							vertices[index * 2 + 1] = g.path.get(i).y;
							index++;
						}
						shapeRenderer.polyline(vertices);
					}
				}
			}
			shapeRenderer.end();
			box2dDebugRenderer.render(world, camera.combined);
		}
	}

	@Override
	public InputProcessor getInputProcessor() {
		// return camController;
		return inputController;
	}

	public void newJob() {
		final int contolGroupSize = 20;
		jobDescription = new JobDescription();
		jobDescActor.setText(jobDescription.desc);
		if (jobDescription.artTagNot != null && jobDescription.artTagNot.size() > 0) {
			final String[] tagsNotMatched = new String[jobDescription.artTagNot.size()];
			for (final String tagNotMatched : tagsNotMatched) {
				if (FlickrService.instance.getControlWebArt().get(tagNotMatched) == null
						|| FlickrService.instance.getControlWebArt().get(tagNotMatched).size() == 0) {
					FlickrService.instance.fetchPhotos(contolGroupSize, 1, tagNotMatched);
				}
			}
		} else {
			if (FlickrService.instance.getControlWebArt().get(jobDescription.artTag) == null
					|| FlickrService.instance.getControlWebArt().get(jobDescription.artTag).size() == 0) {
				FlickrService.instance.fetchPhotos(contolGroupSize, 1, jobDescription.artTag);
			}
		}
	}

	public void endLevel() {
		MusicService.instance.forceMusicChange();
		if (onGameOver != null) {
			TagService.instance.tag(currentRoom.getArtList());
			Gdx.app.log(TAG, TagService.instance.tagVos.toString());
			Player.instance.body = null;
			Player.instance.artInView.clear();
			// Gdx.app.exit();
			game.setScreen(new ArtTagScreen(game, this.currentRoom.getId()), ScreenTransitionFade.init(0f));
		} else if (isGameOver) {
			TagService.instance.tag(currentRoom.getArtList());
			Gdx.app.log(TAG, TagService.instance.tagVos.toString());
			Player.instance.body = null;
			Player.instance.artInView.clear();
			// Gdx.app.exit();
			game.setScreen(new GameOverScreen(game), ScreenTransitionFade.init(1f));
		} else {
			TagService.instance.tag(currentRoom.getArtList());
			Gdx.app.log(TAG, TagService.instance.tagVos.toString());
			Player.instance.body = null;
			Player.instance.artInView.clear();
			// Gdx.app.exit();
			game.setScreen(new RatingScreen(game, jobDescription), ScreenTransitionFade.init(1f));
		}
	}

	public void setInstruction(String text) {
		instructionsActor.setText(text);
		if (text != null) {
			instructionsActor.setVisible(true);
			// Assets.instance.playSound(AssetSounds.radio);
		} else {
			instructionsActor.setVisible(false);
		}
	}

	public void setResult(String text) {
		resultActor.setText(text);
	}

	public void newRoom() {
		MusicService.instance.forceMusicChange();
		Player.instance.isTouchingWarp = false;
		if (Player.instance.warpRoom != null) {
			MapUtils.loadMap(this, AssetMaps.MAPS_PATH + Player.instance.warpRoom);
			Player.instance.warpRoom = null;
		} else {
			MapUtils.loadMap(this, RandomUtil.random(AssetMaps.ALL_MAPS));
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		mapRenderer.dispose();
		rayHandler.dispose();
		world.dispose();
		smokeEffect.dispose();
		box2dDebugRenderer.dispose();

		for (final Guard g : guards) {
			g.dispose();
		}
		for (int x = 0; x < MAX_ROOM_SIZE; x++) {
			for (int y = 0; y < MAX_ROOM_SIZE; y++) {
				if (rooms[x][y] != null) {
					for (final Art art : rooms[x][y].getArtList()) {
						art.dispose();
					}
				}
			}
		}
	}

	public Art getCurrentArt() {
		return currentArt;
	}

	public void setCurrentArt(Art currentArt) {
		if (currentArt != null && currentArt.getWebArt() == null && !currentArt.isSeen()) {
			Player.instance.setArtViewCount(Player.instance.getArtViewCount() + 1);
			final int controlCountRand = Player.instance.getControlArtCount() + 2;
			final int controlArtScanCount = Player.instance.getArtScanCount() + 1;
			final int controlArtViewCount = Player.instance.getArtViewCount() + 1;
			if (currentArt.isShouldMatchTag() // this art should match the tag
					|| RandomUtil.random(controlCountRand * controlCountRand) == 1 // no or little control art sofar
					|| RandomUtil.random(controlArtScanCount) > 5 // many scans
					|| RandomUtil.random(controlArtViewCount) > 15) { // many photos looked at
				Gdx.app.log(TAG, "Adding control web art");
				WebArt webart = FlickrService.instance.getControlWebArt(jobDescription.artTag);
				if (webart == null) {
					Gdx.app.error(TAG, "Control Webart was null for tag " + jobDescription.artTag);
					webart = FlickrService.instance.getWebArt();
				}
				currentArt.setWebArt(webart);
			} else {
				currentArt.setWebArt(FlickrService.instance.getWebArt());
			}
		}
		this.currentArt = currentArt;
	}

}
