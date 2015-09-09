package headmade.arttag.actors;

import headmade.arttag.ArtTag;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.service.ImageService;
import headmade.arttag.service.RandomService;
import headmade.arttag.utils.RandomUtil;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Rectangle;

public class Art {

	private static final String	TAG			= Art.class.getName();

	public String				name;
	public String				artistName;
	public int					year;

	public NinePatch			frame;
	public Texture				image;
	public Rectangle			rectangle;

	public String				assetName;
	public String				imageId;

	public String				fitsTag;
	public Set<String>			fitsTagNot	= new HashSet<String>();

	public boolean				isScanned;

	public Art(Rectangle rectangle) {
		super();
		this.image = Assets.assetsManager.get(ImageService.instance.getUnusedImage(), Texture.class);
		this.rectangle = rectangle;
		this.name = RandomService.instance.generateArtName();
		this.artistName = RandomService.instance.generateArtistName();
		this.year = RandomService.instance.generateYear();

		if (RandomUtil.random() > 0.5) {
			this.frame = Assets.instance.skin.get(AssetTextures.frame, NinePatch.class);
			frame.scale(ArtTag.UNIT_SCALE, ArtTag.UNIT_SCALE);
		} else {
			this.frame = Assets.instance.skin.get(AssetTextures.frame2, NinePatch.class);
			frame.scale(ArtTag.UNIT_SCALE / 2, ArtTag.UNIT_SCALE / 2);
		}
	}

	public void matchesTag(String tag) {
		fitsTag = tag;
	}

	public void matchesTagNot(Set<String> tag) {
		fitsTagNot = tag;
	}

	public void draw(Batch batch) {
		// batch.draw(image, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		// frame.draw(batch, rectangle.x, rectangle.y, rectangle.width, rectangle.height);

		// final Matrix4 oldTransMat = batch.getTransformMatrix();
		// batch.setTransformMatrix(stageCamera.combined);
		// frame.draw(batch, rectangle.x / ArtTag.UNIT_SCALE, rectangle.y / ArtTag.UNIT_SCALE, rectangle.width / ArtTag.UNIT_SCALE,
		// rectangle.height / ArtTag.UNIT_SCALE);
		// batch.setTransformMatrix(oldTransMat);

		batch.draw(image, rectangle.x + frame.getPadLeft(), rectangle.y + frame.getPadBottom(), rectangle.width - frame.getPadLeft()
				- frame.getPadRight(), rectangle.height - frame.getPadTop() - frame.getPadBottom());
		// frame.draw(batch, rectangle.x, rectangle.y, rectangle.getWidth(), rectangle.height);
	}

	@Override
	public String toString() {
		return "Art [name=" + name + ", artistName=" + artistName + ", year=" + year + ", assetName=" + assetName + "]";
	}

	public String resultText() {
		return "From the year " + year;
	}

	// public void drawFrame(SpriteBatch batch) {
	// frame.draw(batch, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	// // frame.draw(batch, rectangle.x / ArtTag.UNIT_SCALE, rectangle.y / ArtTag.UNIT_SCALE, rectangle.width / ArtTag.UNIT_SCALE,
	// // rectangle.height / ArtTag.UNIT_SCALE);
	// }

}
