package headmade.arttag.actors;

import headmade.arttag.JobDescription;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
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

public class Art {

	private static final String	TAG			= Art.class.getName();

	private final String		name;
	private final String		artistName;
	private final int			year;

	private final Rectangle		rectangle;
	private NinePatch			frame;
	private final TextureRegion	placeholder;
	private Drawable			drawable;

	private Texture				image;
	private WebArt				webArt;

	private String				imageId;

	private String				fitsTag;
	private Set<String>			fitsTagNot	= new HashSet<String>();

	private Body				artTrigger;

	private boolean				isScanned;
	private boolean				isStolen;

	public Art(Rectangle rectangle) {
		super();
		this.placeholder = Assets.instance.skin.getRegion(RandomUtil.random(AssetTextures.ALL_PLACEHOLDERS));
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
		if (!isStolen) {
			// drawable.draw(batch, rectangle.x + frame.getPadLeft(), rectangle.y + frame.getPadBottom(), rectangle.width -
			// frame.getPadLeft()
			// - frame.getPadRight(), rectangle.height - frame.getPadTop() - frame.getPadBottom());

			batch.draw(placeholder, rectangle.x + frame.getPadLeft(), rectangle.y + frame.getPadBottom(),
					rectangle.width - frame.getPadLeft() - frame.getPadRight(), rectangle.height - frame.getPadTop() - frame.getPadBottom());

			// batch.draw(draw, rectangle.x + frame.getPadLeft(), rectangle.y + frame.getPadBottom(), rectangle.width - frame.getPadLeft()
			// - frame.getPadRight(), rectangle.height - frame.getPadTop() - frame.getPadBottom());
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

	public Set<String> getFitsTagNot() {
		return fitsTagNot;
	}

	public String getFitsTag() {
		return fitsTag;
	}

	public String getImageId() {
		return webArt == null ? imageId : webArt.getImageId();
	}

	public Texture getTexture() {
		return webArt == null ? image : webArt.getImage();
	}

	public Drawable getDrawable() {
		return webArt == null ? drawable : webArt.getDrawable();
	}

	public boolean isScanned() {
		return isScanned;
	}

	public void dispose() {
		if (webArt != null) {
			webArt.dispose();
		}
	}

	public void setFitsTag(String fitsTag) {
		this.fitsTag = fitsTag;
	}

	public Body getArtTrigger() {
		return artTrigger;
	}

	public void setArtTrigger(Body artTrigger) {
		this.artTrigger = artTrigger;
	}

	public boolean isStolen() {
		return isStolen;
	}

	public void setStolen(boolean isStolen) {
		this.isStolen = isStolen;
	}

	public void setScanned(boolean isScanned) {
		this.isScanned = isScanned;
	}

	public WebArt getWebArt() {
		return webArt;
	}

	public void setWebArt(WebArt webArt) {
		this.webArt = webArt;
	}

}
