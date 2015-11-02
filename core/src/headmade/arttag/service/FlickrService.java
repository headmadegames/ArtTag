/*******************************************************************************
 *    Copyright 2015 Headmade Games
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package headmade.arttag.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import headmade.arttag.Player;
import headmade.arttag.actors.WebArt;
import headmade.arttag.utils.RandomUtil;

public class FlickrService {
	private static final String TAG = FlickrService.class.getName();

	private static final int	BATCH_SIZE	= 10;
	// TODO find out actual size of image catalogue
	private static final int	MAX_PAGE	= 1000000 / BATCH_SIZE;

	public static FlickrService instance = new FlickrService();

	private final Flickr			flickr;
	private final ExecutorService	executor;
	private final ExecutorService	executor2;

	private final Array<Integer>					usedPages		= new Array<Integer>();
	private final ArrayList<WebArt>					availableWebArt	= new ArrayList<WebArt>();
	private final Map<String, ArrayList<WebArt>>	controlWebArt	= new HashMap<String, ArrayList<WebArt>>();

	private FlickrService() {
		flickr = new Flickr("b906a04cd9cd1b76c8809a01cb66611d", "135a0371bf2e8c02", new REST());
		executor = Executors.newFixedThreadPool(1);
		executor2 = Executors.newFixedThreadPool(1);
	}

	public void init() {
		fetchMorePhotos();
	}

	private void fetchMorePhotos() {
		Gdx.app.log(TAG, "Starting to fetch more images from flickr");

		int rand = RandomUtil.random(MAX_PAGE);
		int countBatches = 0;
		while (countBatches < 3) {
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
								// Gdx.app.log(TAG, "Successfully downloaded image from flickr " + url);
								final Texture image = new Texture(pixmap);
								final WebArt webart = new WebArt(photo, image);
								if (tags != null && tags.length > 0) {
									for (final String tag : tags) {
										Gdx.app.log(TAG, "Putting photo as control art for tag" + tag);
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
			// Gdx.app.log(TAG, "Flickr returned tags for " + photo.getMedium640Url());
			webart.getPhoto().setTags(flickrTags.getTags());
		} catch (final FlickrException e) {
			Gdx.app.error(TAG, "Failed fetching tags for " + photo.getId());
		}
	}

	private int download(byte[] out, String url) {
		InputStream in = null;
		try {
			// Gdx.app.log(TAG, "Fetching image from flickr " + url);
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
				if (length == -1) {
					break;
				}
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
					// for (final Photo photo : photos) {
					// Gdx.app.log(TAG, "flickr delivered photo: " + photo.getId() + ", " + photo.getUrl());
					// }
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
		if (availableWebArt.size() == BATCH_SIZE || availableWebArt.size() < BATCH_SIZE / 2) {
			fetchMorePhotos();
		}
		final int size = availableWebArt.size();
		if (size < 1) {
			return null;
		} else {
			try {
				return availableWebArt.remove(RandomUtil.random(size - 1));
			} catch (final Exception e) {
				Gdx.app.error(TAG, "Error getting webart", e);
				return null;
			}
		}
	}

	public WebArt getControlWebArt(String tag) {
		final ArrayList<WebArt> controlArt = controlWebArt.get(tag);
		if (controlArt != null && controlArt.size() > 0) {
			Gdx.app.log(TAG, "returning controlArt " + controlArt + " for tag " + tag);
			Player.instance.setControlArtCount(Player.instance.getControlArtCount() + 1);
			try {
				return controlArt.get(RandomUtil.random(controlArt.size() - 1));
			} catch (final Exception e) {
				Gdx.app.error(TAG, "Error getting webart", e);
			}
		}
		return null;
	}

	public void dispose() {
		shutdownExecutor();
		for (final WebArt webArt : availableWebArt) {
			webArt.dispose();
		}
	}

	private void shutdownExecutor() {
		Gdx.app.log(TAG, "Shutting down FlickrService Executor");
		executor.shutdownNow();
		executor2.shutdownNow();
	}

	public Map<String, ArrayList<WebArt>> getControlWebArt() {
		return controlWebArt;
	}
}
