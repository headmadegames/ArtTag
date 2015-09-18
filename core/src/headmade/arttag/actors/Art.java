package headmade.arttag.actors;

import headmade.arttag.JobDescription;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Art {

	private static final String	TAG			= Art.class.getName();

	public String				name;
	public String				artistName;
	public int					year;

	public NinePatch			frame;
	public Texture				image;
	public Drawable				drawable;
	public Rectangle			rectangle;

	public String				assetName;
	public String				imageId;

	public String				fitsTag;
	public Set<String>			fitsTagNot	= new HashSet<String>();

	public Body					artTrigger;

	public boolean				isScanned;
	public boolean				isStolen;

	public Art(Rectangle rectangle) {
		super();
		this.imageId = ImageService.instance.getUnusedImage();
		this.image = Assets.assetsManager.get(imageId, Texture.class);
		this.drawable = new TextureRegionDrawable(new TextureRegion(image));
		this.rectangle = rectangle;
		this.name = RandomService.instance.generateArtName();
		this.artistName = RandomService.instance.generateArtistName();
		this.year = RandomService.instance.generateYear();

		if (RandomUtil.random() > 0.5) {
			this.frame = Assets.instance.skin.get(AssetTextures.frame, NinePatch.class);
		} else {
			this.frame = Assets.instance.skin.get(AssetTextures.frame2, NinePatch.class);
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

		// final float width = rectangle.width - frame.getPadLeft() - frame.getPadRight();
		// final float height = rectangle.height - frame.getPadTop() - frame.getPadBottom();
		// final float aspectRatio = new Float(image.getWidth()) / new Float(image.getHeight());
		// if (aspectRatio > 1) {
		// batch.draw(image, rectangle.x + frame.getPadLeft(), rectangle.y + frame.getPadBottom()
		// + (rectangle.height / 2 - (height / aspectRatio) / 2), width, height / aspectRatio);
		// } else {
		// batch.draw(image, rectangle.x + frame.getPadLeft() + (rectangle.width / 2 - (width * aspectRatio) / 2),
		// rectangle.y + frame.getPadBottom(), width * aspectRatio, height);
		// }

		if (!isStolen) {
			batch.draw(image, rectangle.x + frame.getPadLeft(), rectangle.y + frame.getPadBottom(), rectangle.width - frame.getPadLeft()
					- frame.getPadRight(), rectangle.height - frame.getPadTop() - frame.getPadBottom());
		}

		frame.draw(batch, rectangle.x, rectangle.y, rectangle.getWidth(), rectangle.height);
	}

	@Override
	public String toString() {
		return "Art [name=" + name + ", artistName=" + artistName + ", year=" + year + ", imageId=" + imageId + "]";
	}

	public String resultText() {
		return "From the year " + year;
	}

	public boolean matchesDescription(JobDescription jobDescription) {
		if (year <= jobDescription.artYearFrom + jobDescription.artYearRange && year >= jobDescription.artYearFrom) {
			return true;
		}
		return false;
	}
}
