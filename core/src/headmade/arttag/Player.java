package headmade.arttag;

import headmade.arttag.screens.ArtTagScreen;
import box2dLight.ConeLight;
import box2dLight.PointLight;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Player {
	private static final String	TAG				= Player.class.getName();

	public static final Player	instance		= new Player();			// Singleton

	private static final float	SPEED_WALK		= 3;
	private static final float	SPEED_RUN		= SPEED_WALK * 2;
	private static final float	LIGHT_DISTANCE	= 5f;
	private static final float	PLAYER_RADIUS	= 0.25f;

	public static Body			body;
	public static boolean		isInitialised	= false;
	public static boolean		isMoveLeft;
	public static boolean		isMoveRight;
	public static boolean		isMoveUp;
	public static boolean		isMoveDown;
	public static boolean		isRunning;

	private static ConeLight	playerLight;

	private Player() {

	}

	public static void createBody(ArtTagScreen artTagScreen) {

		final float x = 10f;
		final float y = 5f;
		final CircleShape circle = new CircleShape();
		circle.setRadius(PLAYER_RADIUS);

		final BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.fixedRotation = true;
		bd.position.set(x, y);

		body = artTagScreen.world.createBody(bd);
		final Fixture fix = body.createFixture(circle, 1f);
		fix.setFriction(1);
		// fixtureDef.filter.categoryBits = ArtTag.CAT_PLAYER;
		// fixtureDef.filter.maskBits = ArtTag.MASK_PLAYER;
		circle.dispose();

		// final BodyDef playerDef = new BodyDef();
		// final Shape shape = new CircleShape();
		// final FixtureDef fixtureDef = new FixtureDef();
		// shape.setRadius(PLAYER_RADIUS * 2);
		// fixtureDef.shape = shape;
		// fixtureDef.density = 1;
		// // fixtureDef.filter.categoryBits = ArtTag.CAT_PLAYER;
		// // fixtureDef.filter.maskBits = ArtTag.MASK_PLAYER;
		// playerDef.type = BodyType.DynamicBody;
		// playerDef.position.x = 10f;
		// playerDef.position.y = 10f;
		// Player.body = artTagScreen.world.createBody(playerDef);

		// final PolygonShape shape = new PolygonShape();
		// // Math.toDegrees(1.0 / Math.tan(height / width));
		// shape.setAsBox(PLAYER_RADIUS / 2f, PLAYER_RADIUS / 2f, new Vector2(PLAYER_RADIUS / 2f, PLAYER_RADIUS / 2f), 0f);
		// final BodyDef bd = new BodyDef();
		// bd.type = BodyType.StaticBody;
		// bd.position.set(10f - PLAYER_RADIUS / 2f, 10f);
		// final Body rampBody = artTagScreen.world.createBody(bd);
		// final Fixture rampFix = rampBody.createFixture(shape, 1f);
		// rampFix.setFriction(2);
		// // rampBody.setUserData(boardSprite);
		// // rampFix.setUserData(boardSprite);
		// shape.dispose();

		// final float width = 10f;
		// final float height = 10f;
		// final float x = 10f;
		// final float y = 10f;
		// final PolygonShape shape = new PolygonShape();
		// shape.setAsBox(width / 2, 0.1f, new Vector2(width / 2, height / 2), 0);
		//
		// final BodyDef bd = new BodyDef();
		// bd.type = BodyType.DynamicBody;
		// bd.position.set(x - width * 0.53f, y + height);
		//
		// final Body boardBody = artTagScreen.world.createBody(bd);
		// final Fixture boardFix = boardBody.createFixture(shape, 1f);
		// boardFix.setFriction(2);
		// shape.dispose();

		playerLight = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, null, LIGHT_DISTANCE, 0, 0, 0f, 35);//
		// MathUtils.random(30f, 50f));
		playerLight.attachToBody(Player.body, 0f, PLAYER_RADIUS, 90);
		playerLight.setSoftnessLength(0.5f);
		playerLight.setColor(1f, 0.9f, 0.7f, 1f);
		playerLight.setContactFilter(ArtTag.CAT_LIGHT, ArtTag.GROUP_LIGHT, ArtTag.MASK_LIGHT);
		artTagScreen.lights.add(playerLight);

		// PointLight
		final PointLight light2 = new PointLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM);
		light2.setPosition(Player.body.getWorldCenter());
		light2.setDistance(LIGHT_DISTANCE / 5f);
		light2.attachToBody(Player.body, 0f, 0f);
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

	public static void update() {
		if (body == null) {
			return;
		}
		final Vector2 moveVec = new Vector2();
		final float moveSpeed = isRunning ? SPEED_RUN : SPEED_WALK;
		if (isMoveDown) {
			moveVec.add(0, -moveSpeed);
		}
		if (isMoveUp) {
			moveVec.add(0, moveSpeed);
		}
		if (isMoveLeft) {
			moveVec.add(-moveSpeed, 0);
		}
		if (isMoveRight) {
			moveVec.add(moveSpeed, 0);
		}
		body.setLinearVelocity(moveVec);
		if (!MathUtils.isEqual(moveVec.len2(), 0f)) {
			body.setTransform(body.getPosition(), moveVec.rotate90(-1).angleRad());
		}
	}
}
