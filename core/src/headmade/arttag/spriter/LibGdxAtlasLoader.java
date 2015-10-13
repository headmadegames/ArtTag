package headmade.arttag.spriter;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

public class LibGdxAtlasLoader extends Loader<Sprite> {

	private final TextureAtlas atlas;

	public LibGdxAtlasLoader(Data data, TextureAtlas atlas, String indexPrefix) {
		super(data);
		this.atlas = atlas;
		final Array<AtlasRegion> array = this.atlas.getRegions();
		for (final AtlasRegion region : array) {
			if (region.index != -1) {
				region.name = region.name + indexPrefix + region.index;
			}
		}
	}

	public LibGdxAtlasLoader(Data data, TextureAtlas atlas) {
		this(data, atlas, "_");
	}

	@Override
	protected Sprite loadResource(FileReference ref) {
		return this.atlas.createSprite(data.getFile(ref).name.replace(".png", ""));
	}

	@Override
	public void dispose() {
		this.atlas.dispose();
	}
}