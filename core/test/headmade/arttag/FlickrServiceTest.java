package headmade.arttag;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.tags.Tag;

import headmade.arttag.service.TagService;

public class FlickrServiceTest extends BaseTest {
	private static final String TAG = FlickrServiceTest.class.getName();

	@Test
	public void testIsTaggedCorrectly() throws InterruptedException, FlickrException {

		final Flickr flickr = new Flickr("b906a04cd9cd1b76c8809a01cb66611d", "135a0371bf2e8c02", new REST());
		for (final String tag : TagService.TAGS) {
			checkTag(flickr, tag);
			// FlickrService.instance.fetchPhotos(10, 1, tag);
		}

		// Thread.sleep(20000);

		// for (final String tag : TagService.TAGS) {
		// Gdx.app.log(TAG, "Checking if webart exists for tag " + tag);
		// final WebArt webArt = FlickrService.instance.getControlWebArt(tag);
		// Assert.assertNotNull(webArt);
		// }
	}

	@Test
	public void testTagging() throws InterruptedException, FlickrException, URISyntaxException, IOException {
		final Flickr flickr = new Flickr("b906a04cd9cd1b76c8809a01cb66611d", "135a0371bf2e8c02", new REST());

		final Auth auth = new Auth();
		auth.setPermission(Permission.WRITE);
		auth.setToken("b906a04cd9cd1b76c8809a01cb66611d");
		auth.setTokenSecret("135a0371bf2e8c02");

		final RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAuth(auth);
		flickr.setAuth(auth);

		final String[] newTags = new String[] { "Portrait" };

		final PhotosInterface iface = flickr.getPhotosInterface();
		final String id = "11165660374";
		final Photo info = iface.getInfo(id, null);

		iface.addTags(id, newTags);

		final Photo photo = iface.getPhoto(id);
		final Collection<Tag> tags = photo.getTags();
		System.out.println("11165660374 has the following tags:");
		for (final Tag tag : tags) {
			System.out.println(
					tag.getValue() + " (" + tag.getRaw() + " ," + tag.getCount() + ") by " + tag.getAuthor() + " - " + tag.getAuthorName());
		}

		final Token token = flickr.getAuthInterface().getRequestToken();
		final String authUrl = flickr.getAuthInterface().getAuthorizationUrl(token, Permission.WRITE);

		final Desktop desktop = Desktop.getDesktop();
		// https://www.flickr.com/services/oauth/authorize?oauth_token=72157659788980509-4e5097b2fa743813&perms=write
		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			System.out.println("Paste this URL into your browser");
			System.out.println(authUrl);
		} else {
			final URI uri = new URI(authUrl);
			desktop.browse(uri);
		}

		final Scanner in = new Scanner(System.in);
		System.out.println("Enter the given authorization code provided by Flickr auth");
		System.out.print(">>");
		final String code = in.nextLine();

		final Verifier verifier = new Verifier(code);// "627-447-751");
		final Token accessToken = flickr.getAuthInterface().getAccessToken(token, verifier);
		final Auth checkedAuth = flickr.getAuthInterface().checkToken(accessToken);

		Assert.assertEquals(Permission.WRITE, checkedAuth.getPermission());

		flickr.getPhotosInterface().addTags(id, newTags);
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
