package headmade.arttag;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

import headmade.arttag.actors.Art;
import headmade.arttag.assets.Assets;

public class Room {
	private static final String TAG = Room.class.getName();

	private static final int MAX_SIZE = 9;

	private String			id;
	private final TiledMap	map;
	private Array<Art>		artList	= new Array<Art>();

	public Room(String mapName) {
		this.id = mapName;
		this.map = Assets.assetsManager.get(mapName, TiledMap.class);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Array<Art> getArtList() {
		return artList;
	}

	public void setArtList(Array<Art> artList) {
		this.artList = artList;
	}

	public TiledMap getMap() {
		return map;
	}

}
