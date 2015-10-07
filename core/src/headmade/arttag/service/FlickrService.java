package headmade.arttag.service;

import headmade.arttag.actors.WebArt;
import headmade.arttag.utils.RandomUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StreamUtils;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;

public class FlickrService {
	private static final String						TAG				= FlickrService.class.getName();

	private static final int						BATCH_SIZE		= 40;
	// TODO find out actual size of image catalogue
	private static final int						MAX_PAGE		= 1000000 / BATCH_SIZE;

	public static FlickrService						instance		= new FlickrService();

	private final Flickr							flickr;
	private final ExecutorService					executor;
	private final ExecutorService					executor2;

	private final Array<Integer>					usedPages		= new Array<Integer>();
	private final ArrayList<WebArt>					availableWebArt	= new ArrayList<WebArt>();
	private final Map<String, ArrayList<WebArt>>	controlWebArt	= new HashMap<String, ArrayList<WebArt>>();

	private FlickrService() {
		flickr = new Flickr("b906a04cd9cd1b76c8809a01cb66611d", "135a0371bf2e8c02", new REST());
		executor = Executors.newFixedThreadPool(3);
		executor2 = Executors.newFixedThreadPool(2);
	}

	public void init() {
		fetchMorePhotos();
	}

	private void fetchMorePhotos() {
		Gdx.app.log(TAG, "Starting to fetch more images from flickr");

		int rand = RandomUtil.random(MAX_PAGE);
		int countBatches = 0;
		while (countBatches < 4) {
			if (!usedPages.contains(rand, false)) {
				usedPages.add(rand);
				countBatches++;
				fetchPhotos(BATCH_SIZE, rand);
			}
			rand = RandomUtil.random(MAX_PAGE);
		}
	}

	private void downloadAvailablePhotos(PhotoList<Photo> photos, final String... tags) {
		for (final Photo photo : photos) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					final byte[] bytes = new byte[1024 * 1024]; // assuming the content is not bigger than 1mb.
					final String url = photo.getMedium640Url();
					final int numBytes = download(bytes, url);
					if (numBytes != 0) {
						// load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
						final Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
						// final int originalWidth = pixmap.getWidth();
						// final int originalHeight = pixmap.getHeight();
						// final int width = MathUtils.nextPowerOfTwo(pixmap.getWidth());
						// final int height = MathUtils.nextPowerOfTwo(pixmap.getHeight());
						// final Pixmap potPixmap = new Pixmap(width, height, pixmap.getFormat());
						// potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
						// pixmap.dispose();
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								Gdx.app.log(TAG, "Successfully downloaded image from flickr " + url);
								final Texture image = new Texture(pixmap);
								final WebArt webart = new WebArt(photo, image);
								if (tags != null && tags.length > 0) {
									for (final String tag : tags) {
										if (controlWebArt.get(tag) == null) {
											controlWebArt.put(tag, new ArrayList<WebArt>());
										}
										controlWebArt.get(tag).add(webart);
									}
								} else {
									availableWebArt.add(webart);
								}

								executor2.execute(new Runnable() {
									@Override
									public void run() {
										fetchTags(photo, webart);
									}
								});
							}
						});
					}
				}
			});
		}
	}

	/**
	 * fetches Tags synchronously
	 * 
	 * @param photo
	 *            for which tags will be fetched
	 * @param webart
	 *            the tags will be put into
	 */
	public void fetchTags(final Photo photo, final WebArt webart) {
		try {
			final Photo flickrTags = flickr.getTagsInterface().getListPhoto(photo.getId());
			webart.getPhoto().setTags(flickrTags.getTags());
		} catch (final FlickrException e) {
			Gdx.app.error(TAG, "Failed fetching tags for " + photo.getId());
		}
	}

	private int download(byte[] out, String url) {
		InputStream in = null;
		try {
			Gdx.app.log(TAG, "Fetching image from flickr " + url);
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
			conn.setUseCaches(true);
			conn.connect();
			in = conn.getInputStream();
			int readBytes = 0;
			while (true) {
				final int length = in.read(out, readBytes, out.length - readBytes);
				if (length == -1)
					break;
				readBytes += length;
			}
			return readBytes;
		} catch (final Exception ex) {
			return 0;
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}

	public void fetchPhotos(final int count, final int page, final String... tags) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				Gdx.app.log(TAG, "Fetching " + BATCH_SIZE + " more images from flickr page " + page);
				try {
					final SearchParameters params = new SearchParameters();
					params.setUserId("britishlibrary");
					if (tags != null && tags.length > 0) {
						params.setTags(tags);
					}
					final PhotoList<Photo> photos = flickr.getPhotosInterface().search(params, count, page);
					for (final Photo photo : photos) {
						Gdx.app.log(TAG, "flickr delivered photo: " + photo.getId() + ", " + photo.getUrl());
					}
					downloadAvailablePhotos(photos, tags);
				} catch (final Exception e) {
					Gdx.app.error(TAG, "failed contacting flickr for page " + page, e);
				}
			}
		});
	}

	public int getWebArtCount() {
		return availableWebArt.size();
	}

	public WebArt getWebArt() {
		if (availableWebArt.size() == BATCH_SIZE) {
			fetchMorePhotos();
		}
		return availableWebArt.remove(RandomUtil.random(availableWebArt.size() - 1));
	}

	public void dispose() {
		shutdownExecutor();
		for (final WebArt webArt : availableWebArt) {
			webArt.dispose();
		}
	}

	private void shutdownExecutor() {
		Gdx.app.log(TAG, "Shutting down FlickrService Executor");
		final long timeStarted = System.currentTimeMillis();
		executor.shutdown();
		try {
			if (executor.awaitTermination(3, TimeUnit.SECONDS)) {
				Gdx.app.log(TAG, "All workers finished. Shutdown complete after " + (System.currentTimeMillis() - timeStarted) + "ms");
				return;
			}
		} catch (final InterruptedException e) {
			Gdx.app.error(TAG, "Error while awaiting termination of Flickr executor", e);
		}

		Gdx.app.log(TAG, "...some Flickr workers did not finish, shutting down now");
		final List<Runnable> workers = executor.shutdownNow();
		Gdx.app.log(TAG, "workers left: " + workers.size());
		for (final Runnable runnable : workers) {
			Gdx.app.log(TAG, runnable.toString());
		}
		if (executor.isTerminated()) {
			Gdx.app.log(TAG, "...Flickr ExecutorService shutdown complete.");
		} else {
			Gdx.app.error(TAG, "...FAILED Flickr ExecutorService shutdown.");
		}
	}

	public Map<String, ArrayList<WebArt>> getControlWebArt() {
		return controlWebArt;
	}
}
