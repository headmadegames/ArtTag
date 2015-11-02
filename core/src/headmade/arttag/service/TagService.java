package headmade.arttag.service;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import headmade.arttag.ArtTag;
import headmade.arttag.GameState;
import headmade.arttag.JobDescription;
import headmade.arttag.Player;
import headmade.arttag.actors.Art;
import headmade.arttag.vo.ImageTagVo;
import headmade.arttag.vo.TagVo;

public class TagService {
	private static final String TAG = TagService.class.getName();

	public static final String[] TAGS = { "architecture", "decoration", "fauna", "typography", "map", "music", "people", "flora",
			"portrait", "ship" };

	public static TagService instance = new TagService();

	public HashMap<String, ImageTagVo> tagVos = new HashMap<String, ImageTagVo>();

	private int								correctTagCount;
	private int								incorrectTagCount;
	private final HashMap<String, String>	labels;

	private TagService() {
		labels = new HashMap<String, String>();
		labels.put("architecture", "Architecture");
		labels.put("advertisement", "Advertisement");
		labels.put("cover", "a cover");
		labels.put("decoration", "decoration");
		labels.put("diagram", "a diagram");
		labels.put("fashion", "Fashion");
		labels.put("fauna", "Fauna");
		labels.put("typography", "Typography");
		labels.put("map", "a Map");
		labels.put("music", "Music");
		labels.put("people", "People");
		labels.put("flora", "Flora");
		labels.put("portrait", "a Portrait");
		labels.put("ship", "a Ship");
	}

	public void tag(String imageId, String tag) {
		if (tag == null) {
			return;
		}
		final TagVo tagVo = getTagVo(imageId, tag);
		tagVo.countTag += 1;
		Gdx.app.log(TAG, "Tagging " + imageId + " as " + tagVo);
	}

	public void tagNotMatched(String imageId, String tag) {
		if (tag == null) {
			return;
		}
		final TagVo tagVo = getTagVo(imageId, tag);
		tagVo.countTagNotMatched += 1;
		Gdx.app.log(TAG, "Tagging " + imageId + " as NOT a " + tagVo);
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
				for (final String tag : art.getFitsTagNot()) {
					if (tag.equals(jobDescription.artTag)) {
						return;
					}
				}
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
			if (art.isCorrectlyTagged()) {
				correctTagCount++;
				Gdx.app.log(TAG, "Correctly Tagged " + art);
			} else if (art.isIncorrectlyTagged()) {
				incorrectTagCount++;
				Gdx.app.log(TAG, "Incorrectly Tagged " + art);
			}
			if (art.getFitsTagNot() != null && art.getFitsTagNot().size() > 0) {
				for (final String tagNot : art.getFitsTagNot()) {
					tagNotMatched(art.getImageId(), tagNot);
				}
			} else {
				tag(art.getImageId(), art.getFitsTag());
			}
		}
		writeToFile();
	}

	public void writeToFile() {
		if (Gdx.files.isExternalStorageAvailable()) {
			final FileHandle csv = Gdx.files.external("art-treachery-tagging.csv");
			Gdx.app.log(TAG, "CSV located at " + csv.file().getAbsolutePath());

			Float accuracy = new Float(correctTagCount) / new Float(correctTagCount + incorrectTagCount);
			if (accuracy.isNaN()) {
				accuracy = 0.5f;
			}
			Player.instance.setAccuracy(accuracy);

			if (!csv.exists()) {
				csv.writeString(buildCsvHeader(), false);
			}
			for (final String imageId : tagVos.keySet()) {
				writeCsvLine(csv, imageId, ArtTag.gameState, accuracy, tagVos.get(imageId));
			}
		}
	}

	private void writeCsvLine(FileHandle csv, String imageId, GameState gameState, float accuracy, ImageTagVo imageTagVo) {
		final StringBuilder sb = new StringBuilder(imageId);
		sb.append(',').append(gameState.getPlayerId());
		sb.append(',').append("" + System.currentTimeMillis());
		sb.append(',').append("" + accuracy);
		sb.append(',').append("" + gameState.getAccuracy());
		for (final String tag : TAGS) {
			final TagVo tagVo = imageTagVo.tags.get(tag);
			if (tagVo != null) {
				if (tagVo.countTag > 0) {
					sb.append(',').append("1");
				} else if (tagVo.countTagNotMatched > 0) {
					sb.append(',').append("-1");
				}
			} else {
				sb.append(',');
			}

		}
		csv.writeString(sb.append('\n').toString(), true);
	}

	private String buildCsvHeader() {
		final StringBuilder sb = new StringBuilder("image_id,player_id,timestamp,accuracy,accuracy_alltime");
		for (final String tag : TAGS) {
			sb.append(',').append(tag);
		}
		return sb.append('\n').toString();
	}

	public String getLabel(String tag) {
		return labels.get(tag);
	}
}
