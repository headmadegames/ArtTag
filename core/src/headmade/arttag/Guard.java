package headmade.arttag;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;

import box2dLight.ConeLight;
import headmade.arttag.assets.AssetSounds;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.ArtTagScreen;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class Guard {

	private static final String TAG = Guard.class.getName();

	private static final float	STEP_VOLUME					= 0.3f;
	private static final float	BODY_RADIUS					= 0.25f;
	private static final float	LIGHT_CONE_LENGTH_FACTOR	= 0.7f;
	private static final float	MAX_RUN_FACTOR				= 2f;
	private static final float	MAX_SPEED_WALK				= 1f;
	private static final float	MAX_LIGHT_ANGLE				= 35f;
	private static final float	MAX_LIGHT_LENGTH			= 8f;
	private static final float	MAX_LIGHT_CONE_LENGTH		= MAX_LIGHT_LENGTH * 0.7f;
	private static final float	MAX_REACTION_TIME			= 0.1f;
	private static final float	MAX_ROTATION_SPEED			= 1f;

	public Body				body;
	public Array<Vector2>	path			= new Array<Vector2>();
	public Array<Fixture>	playerInView	= new Array<Fixture>();
	public ConeLight		light;
	public boolean			isInitialised;
	public boolean			isRunning;
	public boolean			isAlert;
	public boolean			isSuspicious;
	public boolean			isTouchingPlayerLightCone;
	public boolean			isLightOn		= true;

	private final Array<Vector2>	backToPath		= new Array<Vector2>();
	private Vector2					lastVisibleVecBackToPath;
	private Vector2					playerLastSeenVec;
	private Vector2					targetMoveVec	= new Vector2();
	private float					sumDeltaSinceMoveChange;
	private int						currentPathIndex;

	private final float	lightAngle		= MAX_LIGHT_ANGLE;
	// upgradable stats
	private float		runFactor		= MAX_RUN_FACTOR;
	private float		maxMoveSpeed	= MAX_SPEED_WALK;
	private float		lightLength		= MAX_LIGHT_LENGTH / 2;
	private float		reactionTime	= MAX_REACTION_TIME;

	private final long	stepSoundId;
	private final Sound	sound;

	public Guard() {
		sound = Assets.assetsManager.get(AssetSounds.step, Sound.class);
		stepSoundId = sound.loop(0f);
	}

	public void createBody(ArtTagScreen artTagScreen, float x, float y) {
		Gdx.app.log(TAG, "Creating Guard body at " + x + ", " + y);
		{ // body
			final CircleShape circle = new CircleShape();
			circle.setRadius(BODY_RADIUS);

			final BodyDef bd = new BodyDef();
			bd.type = BodyType.DynamicBody;
			bd.fixedRotation = true;
			bd.position.set(x, y);
			final FixtureDef fd = new FixtureDef();
			fd.shape = circle;
			fd.restitution = 1.5f;
			fd.friction = 0.0f;
			fd.filter.categoryBits = ArtTag.CAT_GUARD;
			fd.filter.maskBits = ArtTag.MASK_GUARD;

			body = artTagScreen.world.createBody(bd);
			final Fixture fix = body.createFixture(fd);

			final Sprite t = Assets.instance.skin.getSprite(AssetTextures.guard);
			final Box2DSprite playerSprite = new Box2DSprite(t);
			fix.setUserData(playerSprite);
			body.setUserData(this);

			circle.dispose();
		}

		{ // vision cone
			final float playerLightConeLength = lightLength * LIGHT_CONE_LENGTH_FACTOR;
			final float coneHalfWidth = MathUtils.sinDeg(lightAngle) * playerLightConeLength;

			final PolygonShape shape = new PolygonShape();
			final Vector2[] vertices = { new Vector2(0, BODY_RADIUS * 0.9f), new Vector2(coneHalfWidth, playerLightConeLength),
					new Vector2(0, playerLightConeLength * 1.2f), new Vector2(-coneHalfWidth, playerLightConeLength) };
			shape.set(vertices);

			final BodyDef bd = new BodyDef();
			bd.type = BodyType.DynamicBody;
			bd.fixedRotation = true;
			bd.position.set(x, y);

			final FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.isSensor = true;
			fd.filter.categoryBits = ArtTag.CAT_GUARDLIGHT;
			fd.filter.maskBits = ArtTag.MASK_GUARDLIGHT;

			final Fixture fix = body.createFixture(fd);
			fix.setUserData(this);

			shape.dispose();
		}

		{ // player Flashlight
			light = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, null, lightLength, 0, 0, 0f, lightAngle);//
			light.attachToBody(body, 0f, 0f, 90);
			light.setIgnoreAttachedBody(true);
			light.setSoftnessLength(0.5f);
			light.setColor(1f, 0.9f, 0.7f, 1f);
			light.setContactFilter(ArtTag.CAT_LIGHT, ArtTag.GROUP_LIGHT, ArtTag.MASK_LIGHT);
			artTagScreen.lights.add(light);

			// LASER!
			// playerLight = new ConeLight(artTagScreen.rayHandler, 3, null, 10f, 0, 0, 0f, 2f);//
			// // MathUtils.random(30f, 50f));
			// playerLight.attachToBody(body, 0f, 0f, 90);
			// playerLight.setIgnoreAttachedBody(true);
			// playerLight.setSoftnessLength(1f);
			// playerLight.setColor(1f, 0.0f, 0.0f, 1f);
			// playerLight.setContactFilter(ArtTag.CAT_LIGHT, ArtTag.GROUP_LIGHT, ArtTag.MASK_LIGHT);
			// artTagScreen.lights.add(playerLight);

		}
	}

	public void update(ArtTagScreen artTag, float delta) {
		if (body == null) {
			return;
		}
		isRunning = isAlert || isSuspicious;
		final float moveSpeed = isRunning ? maxMoveSpeed * runFactor : maxMoveSpeed;
		final Vector2 oldMoveVec = targetMoveVec.cpy();

		Vector2 targetPoint = null;
		if (isLightOn) {
			for (final Fixture fixture : playerInView) {
				targetPoint = fixture.getBody().getWorldCenter();
				final Vector2 diffVec = body.getWorldCenter().cpy().sub(targetPoint);
				boolean seesPlayer = false;
				if (diffVec.len() < 1f) {
					seesPlayer = true;
				} else {
					final Vector2 outOfBodyVec = diffVec.scl(fixture.getShape().getRadius() * 1.3f);
					targetPoint.add(outOfBodyVec);
					seesPlayer = light.contains(targetPoint.x, targetPoint.y);
					if (seesPlayer) {
						playerLastSeenVec = fixture.getBody().getWorldCenter().cpy();
					}
				}
				// Gdx.app.log(TAG, light.contains(targetPoint.x, targetPoint.y) + " diffVec.length " + diffVec.len());
				if (seesPlayer) {
					isAlert = true;
					Player.instance.isSpotted = true;
					reactionTime = 0f;
				} else {
					if (isAlert) {
						// TODO play huh sound
						reactionTime = MAX_REACTION_TIME;
						isSuspicious = true;
					}
					isAlert = false;
				}
			}
		}

		if (isTouchingPlayerLightCone && Player.instance.isLightOn) {
			// isAlert = true;
		}

		if (isAlert || isSuspicious) {
			if (lastVisibleVecBackToPath == null) {
				lastVisibleVecBackToPath = body.getWorldCenter();
			}

			Vector2 checkPoint = path.get(currentPathIndex);
			if (backToPath.size > 0) {
				checkPoint = backToPath.get(backToPath.size - 1);
			}
			if (BODY_RADIUS < checkPoint.dst(body.getWorldCenter())) {
				// not touching checkpoint
				final RayCastCallback callback = new RayCastCallback() {
					@Override
					public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
						// Gdx.app.log(TAG, "reportRayFixture adds new backToPathPoint" + lastVisibleVecBackToPath);
						backToPath.add(lastVisibleVecBackToPath.cpy());
						return 0;
					}
				};
				try {
					artTag.world.rayCast(callback, body.getWorldCenter(), checkPoint);
				} catch (final Exception e) {
					Gdx.app.error(TAG, "Error Raycasting :(", e);
				}
			}
			lastVisibleVecBackToPath = body.getWorldCenter();
		}

		if (isAlert && playerInView.size > 0 && Player.instance.body != null) {
			targetPoint = Player.instance.body.getWorldCenter();
		} else if (isSuspicious && playerLastSeenVec != null) {
			targetPoint = playerLastSeenVec;
			if (BODY_RADIUS / 10 > targetPoint.dst2(body.getPosition())) {
				// Lost player
				Gdx.app.log(TAG, "Guard no longer suspicious");
				isSuspicious = false;
			}
		} else {
			lastVisibleVecBackToPath = null;
			if (backToPath.size > 0) {
				// following Path back to path
				targetPoint = backToPath.get(backToPath.size - 1);
				if (BODY_RADIUS / 10 > targetPoint.dst(body.getPosition())) {
					// Gdx.app.log(TAG, "Guard reached target back to path point " + targetPoint);
					backToPath.pop();
				}
			} else {
				// following path
				targetPoint = path.get(currentPathIndex);
				if (BODY_RADIUS > targetPoint.dst(body.getPosition())) {
					// Gdx.app.log(TAG, "Guard reached target point " + targetPoint);
					currentPathIndex++;
					if (currentPathIndex >= path.size) {
						currentPathIndex = 0;
					}
					targetPoint = path.get(currentPathIndex);
					// Gdx.app.log(TAG, "New target point " + targetPoint);
				}
			}

		}

		targetMoveVec = targetPoint.cpy().sub(body.getPosition());
		targetMoveVec.nor().scl(moveSpeed);

		if (MathUtils.isEqual(0f, oldMoveVec.angle(targetMoveVec))) {
			sumDeltaSinceMoveChange += delta;
		} else {
			// movment direction changed
			sumDeltaSinceMoveChange = 0f;
		}

		final float alpha = reactionTime > 0 ? MathUtils.clamp(sumDeltaSinceMoveChange / reactionTime, 0.1f, 1f) : 1f;
		body.setLinearVelocity(targetMoveVec);

		final Vector2 bodyRotVec = new Vector2(1f, 0f);
		bodyRotVec.setAngleRad(body.getAngle());
		final float angleDiff = bodyRotVec.angleRad(targetMoveVec.cpy().rotate90(-1));
		final float rotByRad = MathUtils.clamp(angleDiff, -(MAX_ROTATION_SPEED * delta) / reactionTime,
				MAX_ROTATION_SPEED * delta / reactionTime);
				// Gdx.app.log(TAG, "angleDiff: " + angleDiff + " rotByRad: " + rotByRad + " bodyRotVec: " + bodyRotVec + " - targetMoveVec:
				// "
				// + targetMoveVec);

		// is moving?
		if (!MathUtils.isEqual(targetMoveVec.len2(), 0f)) {
			if (Player.instance.body != null) {
				final float dist = body.getPosition().dst(Player.instance.body.getPosition());
				float volume = isRunning ? STEP_VOLUME * 2 : STEP_VOLUME;
				if (dist > 1) {
					volume = volume / dist;
				}
				sound.setVolume(stepSoundId, volume);
				sound.setPitch(stepSoundId, isRunning ? runFactor : 1f);
				body.setTransform(body.getPosition(), body.getAngle() + rotByRad);
			}
		} else {
			sound.setVolume(stepSoundId, 0f);
			sound.setPitch(stepSoundId, 1f);
		}

		light.setActive(isLightOn);
	}

	public void upgradeSpeed() {
		maxMoveSpeed = MathUtils.clamp(1.2f * maxMoveSpeed, 0, MAX_SPEED_WALK);
	}

	public void upgradeLightDistance() {
		lightLength = MathUtils.clamp(1.2f * lightLength, 0, MAX_LIGHT_LENGTH);
	}

	public void upgradeRunFactor() {
		runFactor = MathUtils.clamp(1.2f * runFactor, 1f, MAX_RUN_FACTOR);
	}

	public void upgradeReactionSpeed() {
		reactionTime = MathUtils.clamp(reactionTime * 0.8f, 0f, MAX_REACTION_TIME);
	}

	public Array<Vector2> getBackToPath() {
		return backToPath;
	}
}
