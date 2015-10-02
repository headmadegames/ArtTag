package headmade.arttag.actors;

import headmade.arttag.JobDescription;
import headmade.arttag.assets.AssetTextures;
import headmade.arttag.assets.Assets;
import headmade.arttag.service.RandomService;
import headmade.arttag.utils.RandomUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.flickr4java.flickr.tags.Tag;

public class Art {

	private static final String	TAG				= Art.class.getName();

	private static final String	DATE_TAG_PREFIX	= "date:";

	private final String		name;
	private final String		artistName;
	private int					year;

	private final Rectangle		rectangle;
	private NinePatch			frame;
	private final TextureRegion	placeholder;
	private Drawable			drawable;

	private Texture				image;
	private WebArt				webArt;

	private String				imageId;

	private String				fitsTag;
	private Set<String>			fitsTagNot		= new HashSet<String>();

	private Body				artTrigger;

	private boolean				isScanned;
	private boolean				isStolen;

	private final Array<String>	knownTags		= new Array<String>();

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
		// set year from tag
		final Collection<Tag> tags = webArt.getPhoto().getTags();
		for (final Tag tag : tags) {
			if (tag.getValue().startsWith(DATE_TAG_PREFIX)) {
				try {
					year = Integer.parseInt(tag.getValue().replaceAll(DATE_TAG_PREFIX, ""));
				} catch (final Exception e) {
					Gdx.app.log(TAG, "Error parsing tag date " + tag.getValue());
				}

			}
		}
	}

	public Array<String> getKnownTags() {
		return knownTags;
	}

	public boolean isCorrectlyTagged() {
		if (fitsTag != null) {
			if (knownTags != null && knownTags.size > 0) {
				for (final String knownTag : knownTags) {
					if (knownTag.toLowerCase().contains(fitsTag.toLowerCase())) {
						return true;
					}
				}
			}
			if (webArt != null) {
				final Collection<Tag> tags = webArt.getPhoto().getTags();
				for (final Tag tag : tags) {
					if (tag.getValue().toLowerCase().contains(fitsTag.toLowerCase())) {
						return true;
					}
				}
			}
		} else if (fitsTagNot != null && fitsTagNot.size() > 0) {
			if (knownTags != null && knownTags.size > 0) {
				for (final String tag : fitsTagNot) {
					for (final String knownTag : knownTags) {
						if (knownTag.toLowerCase().contains(tag.toLowerCase())) {
							return false;
						}
					}
				}
				// nothing matches so it's correctly tagged
				return true;
			}
		}
		return false;
	}

	public boolean isIncorrectlyTagged() {
		if (fitsTag != null) {
			if (knownTags != null && knownTags.size > 0) {
				for (final String knownTag : knownTags) {
					if (knownTag.toLowerCase().contains(fitsTag.toLowerCase())) {
						return false;
					}
				}
				return true;
			}
			if (webArt != null) {
				final Collection<Tag> tags = webArt.getPhoto().getTags();
				for (final Tag tag : tags) {
					if (tag.getValue().toLowerCase().contains(fitsTag.toLowerCase())) {
						return false;
					}
				}
			}
		} else if (fitsTagNot != null && fitsTagNot.size() > 0) {
			if (knownTags != null && knownTags.size > 0) {
				for (final String tag : fitsTagNot) {
					for (final String knownTag : knownTags) {
						if (knownTag.toLowerCase().contains(tag.toLowerCase())) {
							return true;
						}
					}
					return false;
				}
			}
			if (webArt != null) {
				final Collection<Tag> tags = webArt.getPhoto().getTags();
				for (final Tag tag : tags) {
					if (tag.getValue().toLowerCase().contains(fitsTag.toLowerCase())) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
