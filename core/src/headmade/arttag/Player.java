package headmade.arttag;

import headmade.arttag.actors.Art;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.ArtTagScreen;
import headmade.arttag.service.TagService;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import box2dLight.ConeLight;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class Player {

	private static final String		TAG								= Player.class.getName();

	public static final Player		instance						= new Player();														// Singleton

	private static final String[]	SCANNING_PROGRESS				= { "/", "/\\\\\\", "/\\\\\\/", "/\\\\\\/\\\\\\", "/\\\\\\/\\\\\\/" };

	private static final float		PLAYER_RADIUS					= 0.25f;
	private static final float		PLAYERLIGHT_CONE_LENGTH_FACTOR	= 0.7f;
	private static final float		MAX_RUN_FACTOR					= 2f;
	private static final float		MAX_SPEED_WALK					= 3f;
	private static final float		MAX_PLAYERLIGHT_ANGLE			= 35f;
	private static final float		MAX_PLAYERLIGHT_LENGTH			= 5f;
	private static final float		MAX_PLAYERLIGHT_CONE_LENGTH		= MAX_PLAYERLIGHT_LENGTH * 0.7f;
	private static final float		MAX_REACTION_TIME				= 0.1f;
	private static final float		MIN_SCAN_TIME					= 1f;
	private static final float		MAX_SCAN_TIME					= 2f;

	public Body						body;
	public ConeLight				playerLight;
	public boolean					isInitialised					= false;
	public boolean					isMoveLeft;
	public boolean					isMoveRight;
	public boolean					isMoveUp;
	public boolean					isMoveDown;
	public boolean					isRunning;
	public boolean					isAbleToSteal;
	public boolean					isAbleToScan;
	public boolean					isLightOn;
	public boolean					isScanning;

	public float					imageAlpha;
	public Array<Fixture>			artInView						= new Array<Fixture>();
	public Array<Art>				inventory						= new Array<Art>();

	private final Vector2			targetMoveVec					= new Vector2();
	private final Vector2			moveVec							= new Vector2();
	private float					sumDeltaSinceMoveChange;
	private float					sumDeltaScan;

	private final float				playerlightAngle				= MAX_PLAYERLIGHT_ANGLE;
	// upgradable stats
	private float					runFactor						= MAX_RUN_FACTOR * 0.75f;
	private float					maxMoveSpeed					= MAX_SPEED_WALK;
	private float					playerLightLength				= MAX_PLAYERLIGHT_LENGTH / 2;
	private float					reactionTime					= MAX_REACTION_TIME;
	private float					scanTime						= MAX_SCAN_TIME;
	private int						scanProgress;

	private Player() {

	}

	public void createBody(ArtTagScreen artTagScreen) {

		final float x = 2f;
		final float y = 2f;
		{
			final CircleShape circle = new CircleShape();
			circle.setRadius(PLAYER_RADIUS);

			final BodyDef bd = new BodyDef();
			bd.type = BodyType.DynamicBody;
			bd.fixedRotation = true;
			bd.position.set(x, y);
			final FixtureDef fd = new FixtureDef();
			fd.shape = circle;
			fd.filter.categoryBits = ArtTag.CAT_PLAYER;
			fd.filter.maskBits = ArtTag.MASK_PLAYER;

			body = artTagScreen.world.createBody(bd);
			final Fixture fix = body.createFixture(fd);

			final TextureRegion t = Assets.instance.skin.getRegion(AssetTextures.player);
			final Box2DSprite playerSprite = new Box2DSprite(t);
			fix.setUserData(playerSprite);
			// body.setUserData(playerSprite);

			circle.dispose();
		}

		{ // Player vision cone
			final float playerLightConeLength = playerLightLength * PLAYERLIGHT_CONE_LENGTH_FACTOR;
			final float coneHalfWidth = MathUtils.sinDeg(playerlightAngle) * playerLightConeLength;

			final PolygonShape shape = new PolygonShape();
			final Vector2[] vertices = { new Vector2(0, PLAYER_RADIUS * 0.9f), new Vector2(coneHalfWidth, playerLightConeLength),
					new Vector2(0, playerLightConeLength * 1.2f), new Vector2(-coneHalfWidth, playerLightConeLength) };
			shape.set(vertices);

			final BodyDef bd = new BodyDef();
			bd.type = BodyType.DynamicBody;
			bd.fixedRotation = true;
			bd.position.set(x, y);

			final FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.isSensor = true;
			fd.filter.categoryBits = ArtTag.CAT_PLAYERLIGHT;
			fd.filter.maskBits = ArtTag.MASK_PLAYERLIGHT;

			final Fixture fix = body.createFixture(fd);

			shape.dispose();
		}

		{ // player Flashlight
			playerLight = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, null, playerLightLength, 0, 0, 0f, playerlightAngle);//
			// MathUtils.random(30f, 50f));
			playerLight.attachToBody(body, 0f, 0f, 90);
			playerLight.setIgnoreAttachedBody(true);
			playerLight.setSoftnessLength(0.5f);
			playerLight.setColor(1f, 0.9f, 0.7f, 1f);
			playerLight.setContactFilter(ArtTag.CAT_LIGHT, ArtTag.GROUP_LIGHT, ArtTag.MASK_LIGHT);
			artTagScreen.lights.add(playerLight);

			// PointLight
			final PointLight light2 = new PointLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM);
			light2.setPosition(body.getWorldCenter());
			light2.setDistance(0.5f);
			light2.attachToBody(body, 0f, 0f);
			light2.setColor(0.8f, 0.8f, 1f, 0.5f);
			light2.setSoftnessLength(0.5f);
			artTagScreen.lights.add(light2);

			// final PointLight light2 = new PointLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, null, LIGHT_DISTANCE / 2, x, y);
			// light2.attachToBody(body, PLAYER_RADIUS / 1.5f, PLAYER_RADIUS * 0.9f);
			// light2.setSoftnessLength(0.5f);
			// light2.setColor(1f, 0.8f, 0.3f, 1f);
			// artTagScreen.lights.add(light2);

			// final ConeLight light2 = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, null, LIGHT_DISTANCE, 0, 0, 0f, 90);//
			// // MathUtils.random(30f, 50f));
			// light2.attachToBody(Player.body, PLAYER_RADIUS / 2f, PLAYER_RADIUS * 0.9f, 90);
			// light2.setSoftnessLength(0.5f);
			// light2.setColor(1f, 0.9f, 0.7f, 1f);
			// artTagScreen.lights.add(light2);
		}
	}

	public void update(ArtTagScreen artTag, float delta) {
		if (body == null) {
			return;
		}
		final float moveSpeed = isRunning ? maxMoveSpeed * runFactor : maxMoveSpeed;
		final Vector2 oldMoveVec = targetMoveVec.cpy();
		targetMoveVec.x = 0f;
		targetMoveVec.y = 0f;
		if (isMoveDown) {
			targetMoveVec.add(0, -moveSpeed);
		}
		if (isMoveUp) {
			targetMoveVec.add(0, moveSpeed);
		}
		if (isMoveLeft) {
			targetMoveVec.add(-moveSpeed, 0);
		}
		if (isMoveRight) {
			targetMoveVec.add(moveSpeed, 0);
		}
		targetMoveVec.nor().scl(moveSpeed);

		if (MathUtils.isEqual(0f, oldMoveVec.angle(targetMoveVec))) {
			sumDeltaSinceMoveChange += delta;
		} else {
			// movment direction changed
			sumDeltaSinceMoveChange = 0f;
		}

		final float alpha = reactionTime > 0 ? MathUtils.clamp(sumDeltaSinceMoveChange / reactionTime, 0.1f, 1f) : 1f;
		// Gdx.app.log(TAG, "oldMoveVec " + oldMoveVec + " targetMoveVec" + targetMoveVec + "sumDeltaSinceMoveChange "
		// + sumDeltaSinceMoveChange + " - alpha " + alpha);
		// moveVec.interpolate(targetMoveVec, alpha, Interpolation.linear);
		body.setLinearVelocity(targetMoveVec);

		// final float angleDiff = moveVec.angleRad(targetMoveVec);
		if (!MathUtils.isEqual(targetMoveVec.len2(), 0f)) {
			body.setTransform(body.getPosition(), targetMoveVec.cpy().rotate90(-1).angleRad());
		}

		playerLight.setActive(isLightOn);

		imageAlpha = 0f;
		if (isLightOn) {
			for (final Fixture fixture : artInView) {
				final Vector2 point = fixture.getBody().getWorldCenter();
				final boolean isInLight = Player.instance.playerLight.contains(point.x, point.y);
				if (isInLight || isAbleToScan) {
					final float distance = point.cpy().dst(body.getWorldCenter());
					final float newAlpha = 1 - distance / (playerLightLength * PLAYERLIGHT_CONE_LENGTH_FACTOR);
					if (newAlpha > imageAlpha) {
						// Gdx.app.log(TAG, "distance " + distance + " alpha " + newAlpha);
						imageAlpha = newAlpha;
						artTag.currentArt = (Art) fixture.getBody().getUserData();
					}
				}
			}
		}

		if (isScanning) {
			scanProgress = (scanProgress + 1) % SCANNING_PROGRESS.length;
			artTag.setResult(SCANNING_PROGRESS[scanProgress] + " Scanning " + SCANNING_PROGRESS[scanProgress]);
			sumDeltaScan += delta;
			if (sumDeltaScan > scanTime) {
				artTag.setResult(artTag.currentArt.resultText());
				isScanning = false;
				artTag.currentArt.isScanned = true;
				artTag.setInstruction("Is this what our client is looking for?\n Leave with " + ArtTag.BUTTON_B + " Button.\n"
						+ "Take it with the " + ArtTag.BUTTON_A + " Button");
			}
		}
		isAbleToScan = isAbleToScan && artTag.currentArt.isScanned;
		isAbleToSteal = isAbleToScan && artTag.currentArt.isScanned;
	}

	public void upgradeSpeed() {
		maxMoveSpeed = MathUtils.clamp(1.2f * maxMoveSpeed, 0, MAX_SPEED_WALK);
	}

	public void upgradeLightDistance() {
		playerLightLength = MathUtils.clamp(1.2f * playerLightLength, 0, MAX_PLAYERLIGHT_LENGTH);
	}

	public void upgradeRunFactor() {
		runFactor = MathUtils.clamp(1.2f * runFactor, 1f, MAX_RUN_FACTOR);
	}

	public void upgradeReactionSpeed() {
		reactionTime = MathUtils.clamp(reactionTime * 0.8f, 0f, MAX_REACTION_TIME);
	}

	public void upgradeScanTime() {
		scanTime = MathUtils.clamp(scanTime * 0.9f, MIN_SCAN_TIME, MAX_SCAN_TIME);
	}

	public void steal(ArtTagScreen artTagScreen) {
		if (!isAbleToSteal) {
			return;
		}
		Gdx.app.log(TAG, "Steal");
		inventory.add(artTagScreen.currentArt);
		artTagScreen.artList.removeValue(artTagScreen.currentArt, true);
		artTagScreen.currentArt = null;
		TagService.instance.tag(artTagScreen.currentArt, artTagScreen.jobDescription);
		isAbleToSteal = false;
	}

	public void scan(ArtTagScreen artTagScreen) {
		isScanning = true;
		sumDeltaScan = 0f;
		Gdx.app.log(TAG, "Starting scan");
	}
}
