package headmade.arttag.actors;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.tags.Tag;

import headmade.arttag.BaseTest;
import headmade.arttag.service.FlickrService;

public class ArtTest extends BaseTest {
	private static final String TAG = ArtTest.class.getName();

	@Test
	public void testTemp() {
		final Vector2 vec1 = new Vector2(0, 1f);
		final Vector2 vec2 = new Vector2(1f, 1f);

		System.out.println(vec1.angle(vec2));
		System.out.println(vec2.angle(vec1));
	}

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
