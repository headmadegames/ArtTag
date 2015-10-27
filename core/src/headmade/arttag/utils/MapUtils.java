package headmade.arttag.utils;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import headmade.arttag.ArtTag;
import headmade.arttag.Guard;
import headmade.arttag.Player;
import headmade.arttag.Room;
import headmade.arttag.actors.Art;
import headmade.arttag.screens.ArtTagScreen;
import headmade.arttag.vo.WarpVo;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public class MapUtils {

	private static final String TAG = MapUtils.class.getName();

	public static final int[]	MAP_LAYERS_LOW	= { 0, 1, 2 };
	public static final int[]	MAP_LAYERS_HIGH	= { 3 };

	private static final String	OBJ_PLAYER	= "player";
	private static final String	OBJ_GUARD	= "guard";
	private static final String	OBJ_PATH	= "path";
	private static final String	OBJ_WARP	= "warp";
	private static final String	OBJ_HINT	= "hint";
	private static final String	OBJ_EXIT	= "exit";
	private static final String	OBJ_ART		= "art";

	private static final String	PROP_ONGAMEOVER		= "onGameOver";
	private static final String	PROP_HIDEJOBDESC	= "hideJobDesc";
	private static final String	PROP_DIRECTION		= "direction";
	private static final String	PROP_TEXT			= "text";
	private static final String	PROP_ROOM			= "room";
	private static final String	DIRECTION_LEFT		= "left";
	private static final String	DIRECTION_RIGHT		= "right";
	private static final String	DIRECTION_TOP		= "top";
	private static final String	DIRECTION_BOTTOM	= "bottom";

	private static final String	LIGTH_POINT	= "point";
	private static final String	LIGTH_CONE	= "cone";

	public static void loadMap(ArtTagScreen artTagScreen, String mapName) {
		if (artTagScreen.currentRoom != null) {
			unloadMap(artTagScreen);
		}

		if (null != Player.instance.warpDirection) {
			Gdx.app.log(TAG, "Loading map " + mapName);
			if (DIRECTION_LEFT.equalsIgnoreCase(Player.instance.warpDirection)) {
				if (artTagScreen.currentRoomIndexX == 0) {
					artTagScreen.currentRoomIndexX = ArtTagScreen.MAX_ROOM_SIZE - 1;
				} else {
					artTagScreen.currentRoomIndexX--;
				}
			} else if (DIRECTION_RIGHT.equalsIgnoreCase(Player.instance.warpDirection)) {
				if (artTagScreen.currentRoomIndexX == ArtTagScreen.MAX_ROOM_SIZE - 1) {
					artTagScreen.currentRoomIndexX = 0;
				} else {
					artTagScreen.currentRoomIndexX++;
				}
			} else if (DIRECTION_TOP.equalsIgnoreCase(Player.instance.warpDirection)) {
				if (artTagScreen.currentRoomIndexY == ArtTagScreen.MAX_ROOM_SIZE - 1) {
					artTagScreen.currentRoomIndexY = 0;
				} else {
					artTagScreen.currentRoomIndexY++;
				}
			} else if (DIRECTION_BOTTOM.equalsIgnoreCase(Player.instance.warpDirection)) {
				if (artTagScreen.currentRoomIndexY == 0) {
					artTagScreen.currentRoomIndexY = ArtTagScreen.MAX_ROOM_SIZE - 1;
				} else {
					artTagScreen.currentRoomIndexY--;
				}
			}

		}

		boolean isNewRoom = true;
		if (artTagScreen.rooms[artTagScreen.currentRoomIndexX][artTagScreen.currentRoomIndexY] == null) {
			artTagScreen.currentRoom = new Room(mapName);
			artTagScreen.rooms[artTagScreen.currentRoomIndexX][artTagScreen.currentRoomIndexY] = artTagScreen.currentRoom;
		} else {
			artTagScreen.currentRoom = artTagScreen.rooms[artTagScreen.currentRoomIndexX][artTagScreen.currentRoomIndexY];
			isNewRoom = false;
		}

		final Box2DMapObjectParser parser = new Box2DMapObjectParser(ArtTag.UNIT_SCALE);
		// final Box2DMapObjectParser.Listener.Adapter listener = new Box2DMapObjectParser.Listener.Adapter() {
		//
		// @Override
		// public void created(Fixture fixture, MapObject mapObject) {
		//
		// Gdx.app.log(TAG, "mapObject.getProperties()" + fixture.getFilterData().maskBits);
		// super.created(fixture, mapObject);
		// }
		//
		// };
		// parser.setListener(listener);

		final TiledMap map = artTagScreen.currentRoom.getMap();

		final String onGameOver = map.getProperties().get(PROP_ONGAMEOVER, String.class);
		final String hideJobDesc = map.getProperties().get(PROP_ONGAMEOVER, String.class);
		artTagScreen.onGameOver = onGameOver;
		artTagScreen.isHideJobDesc = hideJobDesc == null ? false : true;

		parser.load(artTagScreen.world, map);
		if (null == artTagScreen.mapRenderer) {
			// artTagScreen.mapRenderer = new OrthogonalTiledMapRenderer(artTagScreen.map, artTagScreen.getGame().getBatch());
			artTagScreen.mapRenderer = new OrthogonalTiledMapRenderer(map, parser.getUnitScale(), artTagScreen.getGame().getBatch());
		} else {
			artTagScreen.mapRenderer.setMap(map);
		}

		MapLayer layer = map.getLayers().get("objects");
		for (final MapObject mapObject : layer.getObjects()) {
			if (OBJ_ART.equals(mapObject.getName())) {
				if (isNewRoom) {
					// add Art only if this a new room. If this is an old room the art was created before.
					if (mapObject instanceof RectangleMapObject) {
						createNewArt(artTagScreen, ((RectangleMapObject) mapObject).getRectangle(), parser.getUnitScale());
					} else {
						Gdx.app.error(TAG, OBJ_ART + " has to be a Rectangle");
					}
				}
			} else if (OBJ_WARP.equals(mapObject.getName())) {
				final Body warp = createWarp(artTagScreen, ((RectangleMapObject) mapObject).getRectangle(), parser.getUnitScale());
				final String direction = mapObject.getProperties().get(PROP_DIRECTION, String.class);
				final String room = mapObject.getProperties().get(PROP_ROOM, String.class);
				warp.setUserData(new WarpVo(direction, room));
			} else if (OBJ_EXIT.equals(mapObject.getName())) {
				createExit(artTagScreen, ((RectangleMapObject) mapObject).getRectangle(), parser.getUnitScale());
			} else if (OBJ_HINT.equals(mapObject.getName())) {
				final Body hintBody = createHint(artTagScreen, ((RectangleMapObject) mapObject).getRectangle(), parser.getUnitScale());
				final String hint = mapObject.getProperties().get(PROP_TEXT, String.class);
				hintBody.setUserData(hint);
			} else if (OBJ_PLAYER.equals(mapObject.getName())) {
				final Ellipse e = ((EllipseMapObject) mapObject).getEllipse();
				if (null == Player.instance.body) {
					final String direction = mapObject.getProperties().get("direction", String.class);
					if (null != Player.instance.warpDirection) {
						if (Player.instance.warpDirection.equalsIgnoreCase(direction)) {
							Player.instance.createBody(artTagScreen, (e.x + e.width / 2f) * parser.getUnitScale(),
									(e.y + e.height / 2f) * parser.getUnitScale());
							Player.instance.warpDirection = null;
						}
					} else if (direction == null) {
						Player.instance.createBody(artTagScreen, (e.x + e.width / 2f) * parser.getUnitScale(),
								(e.y + e.height / 2f) * parser.getUnitScale());
					}
				}
			}
		}

		if (!isNewRoom) {
			// create ArtSensors for old Art
			for (final Art art : artTagScreen.currentRoom.getArtList()) {
				createArtSensor(artTagScreen, art);
			}
		}

		{ // guards
			final HashMap<String, Guard> guards = new HashMap<String, Guard>();
			final Array<MapObject> paths = new Array<MapObject>();
			layer = map.getLayers().get("guards");
			for (final MapObject mapObject : layer.getObjects()) {
				if (mapObject.getName() != null && mapObject.getName().contains(OBJ_GUARD)) {
					final Ellipse e = ((EllipseMapObject) mapObject).getEllipse();
					final Guard g = new Guard();
					g.createBody(artTagScreen, (e.x + e.width / 2f) * parser.getUnitScale(), (e.y + e.height / 2f) * parser.getUnitScale());
					guards.put(mapObject.getName().trim(), g);
				} else if (mapObject.getName() != null && mapObject.getName().contains(OBJ_PATH)) {
					paths.add(mapObject);
				} else {
					Gdx.app.log(TAG, "WTF");
				}
			}
			for (final MapObject mapObject : paths) {
				final PolylineMapObject pl = (PolylineMapObject) mapObject;
				final String ownerName = pl.getProperties().get("owner", String.class);
				final Guard g = guards.get(ownerName.trim());
				Gdx.app.log(TAG, "guards " + guards.keySet());
				if (g != null) {
					final Polyline orgPolyline = pl.getPolyline();
					final Polyline p = new Polyline(orgPolyline.getVertices());
					p.setScale(parser.getUnitScale(), parser.getUnitScale());
					p.setPosition(orgPolyline.getX() * parser.getUnitScale(), orgPolyline.getY() * parser.getUnitScale());
					final float[] vertices = p.getTransformedVertices();
					for (int i = 0; i < vertices.length; i += 2) {
						g.path.add(new Vector2(vertices[i], vertices[i + 1]));
					}
					artTagScreen.guards.add(g);
				} else {
					Gdx.app.log(TAG, "No guard for path " + ownerName);
				}
			}

		}

		layer = map.getLayers().get("lights");
		for (final MapObject mapObject : layer.getObjects()) {
			if (mapObject.getProperties().get("type", String.class).contains(LIGTH_POINT)) {
				if (mapObject instanceof EllipseMapObject) {
					createPointLight(artTagScreen, (EllipseMapObject) mapObject, parser.getUnitScale());
				} else {
					Gdx.app.error(TAG, LIGTH_POINT + " light has to be a Circle not " + mapObject);
				}
			} else if (mapObject.getProperties().get("type", String.class).contains(LIGTH_CONE)) {
				if (mapObject instanceof PolygonMapObject) {
					createConeLight(artTagScreen, (PolygonMapObject) mapObject, parser.getUnitScale());
				} else {
					Gdx.app.error(TAG, LIGTH_CONE + " light has to be a Polygon");
				}
			}
		}

	}

	private static Body createHint(ArtTagScreen artTagScreen, Rectangle rectangle, float unitScale) {
		final Rectangle rect = toWorldScale(rectangle, unitScale);
		return createSensor(artTagScreen, rect, ArtTag.CAT_HINT, ArtTag.MASK_HINT);
	}

	private static Body createWarp(ArtTagScreen artTagScreen, Rectangle rectangle, float unitScale) {
		// Gdx.app.log(TAG, "Creating door");
		final Rectangle rect = toWorldScale(rectangle, unitScale);
		return createSensor(artTagScreen, rect, ArtTag.CAT_WARP, ArtTag.MASK_WARP);
	}

	private static void createExit(ArtTagScreen artTagScreen, Rectangle rectangle, float unitScale) {
		// Gdx.app.log(TAG, "Creating exit");
		final Rectangle rect = toWorldScale(rectangle, unitScale);
		createSensor(artTagScreen, rect, ArtTag.CAT_EXIT, ArtTag.MASK_EXIT);
	}

	private static void createPointLight(ArtTagScreen artTagScreen, EllipseMapObject mapObject, float unitScale) {
		final Ellipse e = mapObject.getEllipse();
		final Color color = getColor(mapObject);

		final PointLight light = new PointLight(artTagScreen.rayHandler, ArtTag.gameSettings.rays, color, unitScale * e.width,
				unitScale * (e.x + e.width / 2), unitScale * (e.y + e.width / 2));
		light.setContactFilter(ArtTag.CAT_LIGHT, ArtTag.GROUP_LIGHT, ArtTag.MASK_LIGHT);
		light.setSoftnessLength(0.5f);

		artTagScreen.lights.add(light);
	}

	private static void createConeLight(ArtTagScreen artTagScreen, PolygonMapObject mapObject, float unitScale) {
		final Polygon poly = mapObject.getPolygon();
		Float objRot = 0f;
		if (null != mapObject.getProperties()) {
			objRot = mapObject.getProperties().get("rotation", Float.class);
		}
		if (objRot == null) {
			objRot = 0f;
		}
		final float[] vertices = poly.getVertices();// getTransformedVertices();
		if (vertices.length < 6) {
			Gdx.app.error(TAG, "Invalid Polygon for conelight. It has less than 3 vertices " + mapObject);
			return;
		}
		final Array<Vector2> vecs = new Array<Vector2>();
		for (int i = 0; i < 6; i += 2) {
			vecs.add(new Vector2(vertices[i] * unitScale, vertices[i + 1] * unitScale));
		}

		final Color color = getColor(mapObject);
		final Vector2 halfBetweenV1AndV2 = vecs.get(2).cpy().add(vecs.get(1).cpy().sub(vecs.get(2)).scl(0.5f));
		final float length = vecs.get(2).dst(vecs.first());
		final float angle = Math.abs(vecs.get(1).angle(vecs.get(2)));
		final float rotation = halfBetweenV1AndV2.cpy().sub(vecs.first()).angle() - objRot;
		// final float rotation = poly.getRotation();
		// Gdx.app.log(TAG, "rotation " + rotation + " length: " + length + " angle:" + angle);
		final ConeLight light = new ConeLight(artTagScreen.rayHandler, ArtTag.gameSettings.rays, color, length, unitScale * poly.getX(),
				unitScale * poly.getY(), rotation, angle);
		light.setSoftnessLength(0.5f);
		light.setContactFilter(ArtTag.CAT_LIGHT, ArtTag.GROUP_LIGHT, ArtTag.MASK_LIGHT);

		artTagScreen.lights.add(light);
	}

	private static Color getColor(MapObject mapObject) {
		Color color = null;
		final String colorStr = mapObject.getProperties().get("color", "FFFFFFFF", String.class);
		if (colorStr != null && colorStr.length() > 0) {
			try {
				final int rgba = (int) Long.parseLong(colorStr, 16);
				color = new Color(rgba);
			} catch (final Exception e) {
				Gdx.app.error(TAG, "Light has invalid color value " + colorStr + " mapObj: " + mapObject, e);
			}
		}
		return color;
	}

	private static void createNewArt(ArtTagScreen artTagScreen, Rectangle rectangle, float unitScale) {
		final Rectangle rect = toWorldScale(rectangle, unitScale);

		final Art art = new Art(rect);
		art.init();
		artTagScreen.currentRoom.getArtList().add(art);
		// Gdx.app.log(TAG, "Created new Art " + art);

		createArtSensor(artTagScreen, art);

		// ConeLight artLight = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, new Color(0xFFFFFFFF), rectangle.width, rectangle.x,
		// rectangle.y, 45f, 45f);
		// artLight.setStaticLight(true);
		// artTagScreen.lights.add(artLight);
		// artLight = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, new Color(0xFFFFFFFF), rectangle.width, rectangle.x
		// + rectangle.width, rectangle.y, 135f, 45f);
		// artLight.setStaticLight(true);
		// artTagScreen.lights.add(artLight);

	}

	private static void createArtSensor(ArtTagScreen artTagScreen, final Art art) {
		Rectangle rectangle;
		// art sensor
		rectangle = new Rectangle(art.getRectangle());
		rectangle.y = MathUtils.floor(rectangle.y) + 0.1f;
		rectangle.height = 0.25f;
		final Body artTrigger = createSensor(artTagScreen, rectangle, ArtTag.CAT_ARTTRIGGER, ArtTag.MASK_ARTTRIGGER);
		artTrigger.setUserData(art);
		art.setArtTrigger(artTrigger);
	}

	private static Body createSensor(ArtTagScreen artTagScreen, Rectangle rectangle, short catBits, short maskBits) {
		final PolygonShape shape = new PolygonShape();
		final BodyDef bd = new BodyDef();
		final FixtureDef fd = new FixtureDef();
		bd.type = BodyType.StaticBody;
		bd.position.x = rectangle.x + rectangle.width / 2f;
		bd.position.y = rectangle.y + rectangle.height / 2f;
		shape.setAsBox(rectangle.width / 2f, rectangle.height / 2);
		fd.isSensor = true;
		fd.shape = shape;
		fd.filter.categoryBits = catBits;
		fd.filter.maskBits = maskBits;
		final Body body = artTagScreen.world.createBody(bd);
		body.createFixture(fd);
		shape.dispose();
		return body;
	}

	private static Rectangle toWorldScale(Rectangle orgRect, float unitScale) {
		final Rectangle rectangle = new Rectangle(orgRect);
		rectangle.x *= unitScale;
		rectangle.y *= unitScale;
		rectangle.height *= unitScale;
		rectangle.width *= unitScale;
		return rectangle;
	}

	private static void unloadMap(ArtTagScreen artTagScreen) {
		Gdx.app.log(TAG, "Unloading map");
		artTagScreen.currentRoom = null;
		artTagScreen.world.clearForces();

		final Array<Body> bodies = new Array<Body>();
		artTagScreen.world.getBodies(bodies);
		Gdx.app.log(TAG, "Destroying all fixtures and bodies");
		for (final Body body : bodies) {
			Box2DUtils.destroyFixtures(body);
			// for (final Fixture fixture : body.getFixtureList()) {
			// body.destroyFixture(fixture);
			// }
			artTagScreen.world.destroyBody(body);
		}
		Gdx.app.log(TAG, "Removing lights");
		for (final Light light : artTagScreen.lights) {
			light.remove(true);
		}
		artTagScreen.lights.clear();
		for (final Guard g : artTagScreen.guards) {
			g.dispose();
		}
		artTagScreen.guards.clear();

		Player.instance.body = null;
		// Player.instance.getStepSound().stop();

		artTagScreen.world.step(1f / 60f, 1, 1);
	}
}
