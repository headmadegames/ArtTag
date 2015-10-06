package headmade.arttag.actors;

import headmade.arttag.service.FlickrService;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.math.Rectangle;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.tags.Tag;

public class ArtTest extends BaseTest {
	private static final String	TAG	= ArtTest.class.getName();

	@Test
	public void testIsTaggedCorrectly() {
		final String tag = "map";
		final String tag2 = "portrait";
		final Photo photo = new Photo();
		photo.setId("11290775593");
		final WebArt webart = new WebArt(photo, null);
		FlickrService.instance.fetchTags(photo, webart);

		assertContainsTag(webart.getPhoto().getTags(), tag);

		final Rectangle rect = new Rectangle(1, 1, 1, 1);
		final Art art = new Art(rect);
		art.setWebArt(webart);

		art.setFitsTag(tag);
		Assert.assertTrue(art.isCorrectlyTagged());
		Assert.assertFalse(art.isIncorrectlyTagged());

		art.setFitsTag(tag2);
		Assert.assertFalse(art.isCorrectlyTagged());
		Assert.assertFalse(art.isIncorrectlyTagged());

		art.setFitsTag(null);
		art.getFitsTagNot().add(tag);
		Assert.assertFalse(art.isCorrectlyTagged());
		Assert.assertTrue(art.isIncorrectlyTagged());

		art.getFitsTagNot().clear();
		art.getFitsTagNot().add(tag2);
		Assert.assertFalse(art.isCorrectlyTagged());
		Assert.assertFalse(art.isIncorrectlyTagged());
	}

	private void assertContainsTag(Collection<Tag> tags, String expectedTag) {
		for (final Tag tag : tags) {
			if (tag.getRaw().toLowerCase().contains(expectedTag.toLowerCase())) {
				return;
			}
		}
		Assert.fail("Tag " + expectedTag + " is not contained in " + tags);
	}
}
