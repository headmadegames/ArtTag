package headmade.arttag.service;

import headmade.arttag.JobDescription;
import headmade.arttag.actors.Art;
import headmade.arttag.vo.ImageTagVo;
import headmade.arttag.vo.TagVo;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class TagService {
	private static final String			TAG			= TagService.class.getName();

	public static final String[]		TAGS		= { "Architecture", "a Drawing", "Fauna", "Heraldy", "Typography", "a Map",
			"Sheet Music", "a Painting", "People", "Flora", "a Portrait", "a Symbol", "a Vehicle" };

	public static TagService			instance	= new TagService();

	public HashMap<String, ImageTagVo>	tagVos		= new HashMap<String, ImageTagVo>();

	private TagService() {
	}

	public void tag(String imageId, String tag) {
		if (tag == null) {
			return;
		}
		final TagVo tagVo = getTagVo(imageId, tag);
		tagVo.countTag += 1;
		Gdx.app.log(TAG, "Tagging " + imageId + " as " + tag);
	}

	public void tagNotMatched(String imageId, String tag) {
		if (tag == null) {
			return;
		}
		final TagVo tagVo = getTagVo(imageId, tag);
		tagVo.countTagNotMatched += 1;
		Gdx.app.log(TAG, "Tagging " + imageId + " as NOT a " + tag);
	}

	private TagVo getTagVo(String imageId, String tag) {
		ImageTagVo imageTagVo = tagVos.get(imageId);
		if (imageTagVo == null) {
			imageTagVo = new ImageTagVo(imageId);
			tagVos.put(imageId, imageTagVo);
		}
		TagVo tagVo = imageTagVo.tags.get(tag);
		if (tagVo == null) {
			tagVo = new TagVo(tag);
			imageTagVo.tags.put(tag, tagVo);
		}
		return tagVo;
	}

	/**
	 * sets the tags in the art according to job description
	 *
	 * @param art
	 * @param jobDescription
	 */
	public void tag(Art art, JobDescription jobDescription) {
		Gdx.app.log(TAG, "Tagging " + art);
		if (jobDescription.artTagNot != null && jobDescription.artTagNot.size() > 0) {
			for (final String tagNnot : jobDescription.artTagNot) {
				Gdx.app.log(TAG, "Tagging " + art + " as NOT a " + jobDescription.artTag);
				art.getFitsTagNot().add(tagNnot);
			}
			art.setFitsTag(null);
		} else {
			Gdx.app.log(TAG, "Tagging " + art + " as " + jobDescription.artTag);
			art.setFitsTag(jobDescription.artTag);
			art.getFitsTagNot().clear();
		}
	}

	/**
	 * sets the tagsNotMatched in the art according to job description
	 *
	 * @param art
	 * @param jobDescription
	 */
	public void tagNotMatched(Art art, JobDescription jobDescription) {
		if (jobDescription.artTagNot != null && jobDescription.artTagNot.size() > 0) {
			// we can't say for sure which tag it matches
		} else {
			// tag it only if it wasn't positivley tagged before
			if (art.getFitsTag() == null) {
				Gdx.app.log(TAG, "Tagging " + art + " as NOT a" + jobDescription.artTag);
				art.getFitsTagNot().add(jobDescription.artTag);
			}
		}
	}

	/**
	 * saves all tags from the given artList
	 *
	 * @param artList
	 */
	public void tag(Array<Art> artList) {
		for (final Art art : artList) {
			if (art.getFitsTagNot() != null && art.getFitsTagNot().size() > 0) {
				for (final String tagNot : art.getFitsTagNot()) {
					tagNotMatched(art.getImageId(), tagNot);
				}
			} else {
				tag(art.getImageId(), art.getFitsTag());
			}
		}
	}

}
