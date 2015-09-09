package headmade.arttag.utils;

import headmade.arttag.ArtTag;
import headmade.arttag.Player;
import headmade.arttag.actors.Art;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.ArtTagScreen;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser.Listener;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
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
	private static final String	OBJ_ART			= "art";

	public static void loadMap(ArtTagScreen artTagScreen, String mapName) {
		if (artTagScreen.map != null) {
			unloadMap(artTagScreen);
		}
		artTagScreen.map = Assets.assetsManager.get(mapName, TiledMap.class);
		final Box2DMapObjectParser parser = new Box2DMapObjectParser(ArtTag.UNIT_SCALE);
		final Listener listener = new Box2DMapObjectParser.Listener.Adapter() {

			@Override
			public void created(Fixture fixture, MapObject mapObject) {

				Gdx.app.log(TAG, "mapObject.getProperties()" + fixture.getFilterData().maskBits);
				super.created(fixture, mapObject);
			}

		};
		parser.setListener(listener);
		parser.load(artTagScreen.world, artTagScreen.map);
		if (null == artTagScreen.mapRenderer) {
			// artTagScreen.mapRenderer = new OrthogonalTiledMapRenderer(artTagScreen.map, artTagScreen.getGame().getBatch());
			artTagScreen.mapRenderer = new OrthogonalTiledMapRenderer(artTagScreen.map, parser.getUnitScale(), artTagScreen.getGame()
					.getBatch());
		} else {
			artTagScreen.mapRenderer.setMap(artTagScreen.map);
		}

		final MapLayer layer = artTagScreen.map.getLayers().get("objects");
		for (final MapObject mapObject : layer.getObjects()) {
			if (OBJ_ART.equals(mapObject.getName())) {
				if (mapObject instanceof RectangleMapObject) {
					createNewArt(artTagScreen, ((RectangleMapObject) mapObject).getRectangle(), parser.getUnitScale());
				} else {
					Gdx.app.error(TAG, OBJ_ART + " has to be a Rectangle");
				}

			} else if (OBJ_GUARD.equals(mapObject.getName())) {

			}
		}

		if (null == Player.instance.body) {
			Player.instance.createBody(artTagScreen);
		}
	}

	private static void createNewArt(ArtTagScreen artTagScreen, Rectangle rectangle, float unitScale) {
		rectangle.x *= unitScale;
		rectangle.y *= unitScale;
		rectangle.height *= unitScale;
		rectangle.width *= unitScale;
		final Art art = new Art(rectangle);
		artTagScreen.artList.add(art);
		Gdx.app.log(TAG, "Created new Art " + art);

		// art sensor
		final PolygonShape shape = new PolygonShape();
		final BodyDef bd = new BodyDef();
		final FixtureDef fd = new FixtureDef();
		bd.type = BodyType.StaticBody;
		bd.position.x = rectangle.x + rectangle.width / 2f;
		bd.position.y = MathUtils.floor(rectangle.y) + 0.3f;
		shape.setAsBox(rectangle.width / 2f, 0.25f);
		fd.isSensor = true;
		fd.shape = shape;
		fd.filter.categoryBits = ArtTag.CAT_ARTTRIGGER;
		fd.filter.maskBits = ArtTag.MASK_ARTTRIGGER;

		final Body artTrigger = artTagScreen.world.createBody(bd);
		artTrigger.createFixture(fd);
		artTrigger.setUserData(art);

		shape.dispose();
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
