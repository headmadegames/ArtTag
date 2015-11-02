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
package headmade.arttag.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.flickr4java.flickr.photos.Photo;

public class WebArt {

	private static final String		TAG	= WebArt.class.getName();

	private Photo					photo;
	private final Texture			image;
	private final String			imageId;
	private TextureRegionDrawable	drawable;

	public WebArt(Photo photo, Texture image) {
		this.photo = photo;
		this.imageId = photo.getId();
		this.image = image;
		if (image != null) {
			this.drawable = new TextureRegionDrawable(new TextureRegion(image));
		}
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	// private void downloadImage() {
	// if (photo != null) {
	// Gdx.app.log(TAG, "Starting to download image " + photo.getMedium640Url());
	// httpRequest = new HttpRequest("GET");
	// httpRequest.setUrl(photo.getMedium640Url());
	// Gdx.net.sendHttpRequest(httpRequest, WebArt.this);
	// } else {
	// Gdx.app.log(TAG, "No more unused photos left?");
	// }
	// }
	//
	// @Override
	// public void handleHttpResponse(HttpResponse httpResponse) {
	// Gdx.app.log(TAG, "Download of image successfull " + photo.getMedium640Url());
	// final HttpStatus status = httpResponse.getStatus();
	// if (status.getStatusCode() >= 200 && status.getStatusCode() < 300) {
	// // it was successful
	// final byte[] data = httpResponse.getResult();
	// final Pixmap pixmap = new Pixmap(data, 0, data.length);
	// // final int originalWidth = pixmap.getWidth();
	// // final int originalHeight = pixmap.getHeight();
	// // final int width = MathUtils.nextPowerOfTwo(pixmap.getWidth());
	// // final int height = MathUtils.nextPowerOfTwo(pixmap.getHeight());
	// // final Pixmap potPixmap = new Pixmap(width, height, pixmap.getFormat());
	// // potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
	// // pixmap.dispose();
	// Gdx.app.postRunnable(new Runnable() {
	//
	// @Override
	// public void run() {
	// image = new Texture(pixmap);
	// drawable = new TextureRegionDrawable(new TextureRegion(image, 0, 0, pixmap.getWidth(), pixmap.getHeight()));
	// imageId = photo.getId();
	// }
	// });
	// } else {
	// Gdx.app.log(TAG, "Remote host returned " + status.getStatusCode());
	// }
	// }
	//
	// @Override
	// public void failed(Throwable t) {
	// Gdx.app.error(TAG, "Failed image load ", t);
	// downloadImage();
	// }
	//
	// @Override
	// public void cancelled() {
	// Gdx.app.log(TAG, "Cancelled image load ");
	// }

	public void dispose() {
		image.dispose();
	}

	public Texture getImage() {
		return image;
	}

	public String getImageId() {
		return imageId;
	}

	public TextureRegionDrawable getDrawable() {
		return drawable;
	}
}
