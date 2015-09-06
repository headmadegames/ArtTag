package headmade.arttag.utils;

import headmade.arttag.ArtTag;
import headmade.arttag.Player;
import headmade.arttag.assets.Assets;
import headmade.arttag.screens.ArtTagScreen;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser.Listener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

public class MapUtils {
	private static final String	TAG			= MapUtils.class.getName();

	private static final String	OBJ_PLAYER	= "player";
	private static final String	OBJ_GUARD	= "guard";
	private static final String	OBJ_DOOR	= "door";
	private static final String	OBJ_ART		= "art";

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
			if (OBJ_PLAYER.equals(mapObject.getName())) {

			} else if (OBJ_GUARD.equals(mapObject.getName())) {

			}
		}

		if (null == Player.body) {
			Player.createBody(artTagScreen);
		}
	}

	private static void unloadMap(ArtTagScreen artTagScreen) {
		artTagScreen.map = null;
		artTagScreen.world.clearForces();

		final Array<Body> bodies = new Array<Body>();
		artTagScreen.world.getBodies(bodies);
		for (final Body body : bodies) {
			for (final Fixture fixture : body.getFixtureList()) {
				body.destroyFixture(fixture);
			}
			artTagScreen.world.destroyBody(body);
		}

		artTagScreen.world.step(1f / 60f, 1, 1);
	}
}
