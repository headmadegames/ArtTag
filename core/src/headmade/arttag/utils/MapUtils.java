package headmade.arttag.utils;

import headmade.arttag.ArtTag;
import headmade.arttag.Player;
import headmade.arttag.actors.Art;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.ArtTagScreen;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;
import box2dLight.ConeLight;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class MapUtils {
	private static final String	TAG				= MapUtils.class.getName();

	public static final int[]	MAP_LAYERS_LOW	= { 0, 1, 2 };
	public static final int[]	MAP_LAYERS_HIGH	= { 3 };

	private static final String	OBJ_PLAYER		= "player";
	private static final String	OBJ_GUARD		= "guard";
	private static final String	OBJ_DOOR		= "door";
	private static final String	OBJ_EXIT		= "exit";
	private static final String	OBJ_ART			= "art";

	private static final String	LIGTH_POINT		= "point";
	private static final String	LIGTH_CONE		= "cone";

	public static void loadMap(ArtTagScreen artTagScreen, String mapName) {
		if (artTagScreen.map != null) {
			unloadMap(artTagScreen);
		}
		Gdx.app.log(TAG, "Loading map " + mapName);
		artTagScreen.map = Assets.assetsManager.get(mapName, TiledMap.class);
		final Box2DMapObjectParser parser = new Box2DMapObjectParser(ArtTag.UNIT_SCALE);
		// final Listener listener = new Box2DMapObjectParser.Listener.Adapter() {
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
		parser.load(artTagScreen.world, artTagScreen.map);
		if (null == artTagScreen.mapRenderer) {
			// artTagScreen.mapRenderer = new OrthogonalTiledMapRenderer(artTagScreen.map, artTagScreen.getGame().getBatch());
			artTagScreen.mapRenderer = new OrthogonalTiledMapRenderer(artTagScreen.map, parser.getUnitScale(), artTagScreen.getGame()
					.getBatch());
		} else {
			artTagScreen.mapRenderer.setMap(artTagScreen.map);
		}

		MapLayer layer = artTagScreen.map.getLayers().get("objects");
		for (final MapObject mapObject : layer.getObjects()) {
			if (OBJ_ART.equals(mapObject.getName())) {
				if (mapObject instanceof RectangleMapObject) {
					createNewArt(artTagScreen, ((RectangleMapObject) mapObject).getRectangle(), parser.getUnitScale());
				} else {
					Gdx.app.error(TAG, OBJ_ART + " has to be a Rectangle");
				}
			} else if (OBJ_GUARD.equals(mapObject.getName())) {

			} else if (OBJ_EXIT.equals(mapObject.getName())) {
				createExit(artTagScreen, ((RectangleMapObject) mapObject).getRectangle(), parser.getUnitScale());
			} else if (OBJ_PLAYER.equals(mapObject.getName())) {
				final Ellipse e = ((EllipseMapObject) mapObject).getEllipse();
				if (null == Player.instance.body) {
					Player.instance.createBody(artTagScreen, (e.x + e.width / 2f) * parser.getUnitScale(),
							(e.y + e.height / 2f) * parser.getUnitScale());
				}
			}
		}

		layer = artTagScreen.map.getLayers().get("lights");
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

	private static void createExit(ArtTagScreen artTagScreen, Rectangle rectangle, float unitScale) {
		toWorldScale(rectangle, unitScale);
		createSensor(artTagScreen, rectangle, ArtTag.CAT_EXIT, ArtTag.MASK_EXIT);
	}

	private static void createPointLight(ArtTagScreen artTagScreen, EllipseMapObject mapObject, float unitScale) {
		final Ellipse e = mapObject.getEllipse();
		final Color color = getColor(mapObject);

		final PointLight light = new PointLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, color, unitScale * e.width, unitScale
				* (e.x + e.width / 2), unitScale * (e.y + e.width / 2));
		light.setContactFilter(ArtTag.CAT_LIGHT, ArtTag.GROUP_LIGHT, ArtTag.MASK_LIGHT);
		light.setSoftnessLength(0.5f);

		artTagScreen.lights.add(light);
	}

	private static void createConeLight(ArtTagScreen artTagScreen, PolygonMapObject mapObject, float unitScale) {
		final Polygon poly = mapObject.getPolygon();
		final float[] vertices = poly.getTransformedVertices();
		if (vertices.length < 6) {
			Gdx.app.error(TAG, "Invalid Polygon for conelight. It has less than 3 vertices " + mapObject);
			return;
		}
		final Array<Vector2> vecs = new Array<Vector2>();
		for (int i = 0; i < 6; i += 2) {
			vecs.add(new Vector2(vertices[i] * unitScale, vertices[i + 1] * unitScale));
		}

		final Color color = getColor(mapObject);
		final float length = vecs.get(2).dst(vecs.first());
		final float angle = Math.abs(vecs.get(1).angle(vecs.get(2)));
		final float rotation = vecs.get(1).cpy().sub(vecs.get(2)).scl(0.5f).angle();
		// Gdx.app.log(TAG, "rotation " + rotation + " length: " + length + " angle:" + angle);
		final ConeLight light = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, color, length, unitScale * poly.getX(), unitScale
				* poly.getY(), rotation, angle);
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
		toWorldScale(rectangle, unitScale);

		final Art art = new Art(rectangle);
		artTagScreen.artList.add(art);
		Gdx.app.log(TAG, "Created new Art " + art);

		// art sensor
		rectangle = new Rectangle(rectangle);
		rectangle.y = MathUtils.floor(rectangle.y) + 0.1f;
		rectangle.height = 0.25f;
		final Body artTrigger = createSensor(artTagScreen, rectangle, ArtTag.CAT_ARTTRIGGER, ArtTag.MASK_ARTTRIGGER);
		artTrigger.setUserData(art);
		art.artTrigger = artTrigger;

		// ConeLight artLight = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, new Color(0xFFFFFFFF), rectangle.width, rectangle.x,
		// rectangle.y, 45f, 45f);
		// artLight.setStaticLight(true);
		// artTagScreen.lights.add(artLight);
		// artLight = new ConeLight(artTagScreen.rayHandler, ArtTag.RAYS_NUM, new Color(0xFFFFFFFF), rectangle.width, rectangle.x
		// + rectangle.width, rectangle.y, 135f, 45f);
		// artLight.setStaticLight(true);
		// artTagScreen.lights.add(artLight);

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
		final Body artTrigger = artTagScreen.world.createBody(bd);
		artTrigger.createFixture(fd);
		shape.dispose();
		return artTrigger;
	}

	private static void toWorldScale(Rectangle rectangle, float unitScale) {
		rectangle.x *= unitScale;
		rectangle.y *= unitScale;
		rectangle.height *= unitScale;
		rectangle.width *= unitScale;
	}

	private static void unloadMap(ArtTagScreen artTagScreen) {
		artTagScreen.map = null;
		artTagScreen.world.clearForces();

		final Array<Body> bodies = new Array<Body>();
		artTagScreen.world.getBodies(bodies);
		for (final Body body : bodies) {
			Box2DUtils.destroyFixtures(body);
			// for (final Fixture fixture : body.getFixtureList()) {
			// body.destroyFixture(fixture);
			// }
			artTagScreen.world.destroyBody(body);
		}

		artTagScreen.world.step(1f / 60f, 1, 1);
	}
}
