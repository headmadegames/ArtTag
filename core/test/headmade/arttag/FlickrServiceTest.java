package headmade.arttag;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;

import headmade.arttag.actors.WebArt;
import headmade.arttag.service.FlickrService;
import headmade.arttag.service.TagService;

public class FlickrServiceTest extends BaseTest {
	private static final String TAG = FlickrServiceTest.class.getName();

	@Test
	public void testIsTaggedCorrectly() throws InterruptedException, FlickrException {

		final Flickr flickr = new Flickr("b906a04cd9cd1b76c8809a01cb66611d", "135a0371bf2e8c02", new REST());
		for (final String tag : TagService.TAGS) {
			// checkTag(flickr, tag);
			FlickrService.instance.fetchPhotos(10, 1, tag);
		}

		Thread.sleep(20000);

		for (final String tag : TagService.TAGS) {
			Gdx.app.log(TAG, "Checking if webart exists for tag " + tag);
			final WebArt webArt = FlickrService.instance.getControlWebArt(tag);
			Assert.assertNotNull(webArt);
		}
	}

	private void checkTag(Flickr flickr, String... tags) throws FlickrException {
		final SearchParameters params = new SearchParameters();
		params.setUserId("britishlibrary");
		params.setTags(tags);
		final PhotoList<Photo> photos = flickr.getPhotosInterface().search(params, 10, 1);

		System.out.println(photos.getTotal() + " total of photos for tag " + tags[0]);
		Assert.assertTrue("Atleast on photo should exist for " + tags[0], photos.size() > 0);
	}
}
