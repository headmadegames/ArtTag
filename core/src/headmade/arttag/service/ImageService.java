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

import headmade.arttag.assets.AssetTextures;
import headmade.arttag.utils.RandomUtil;

import java.util.ArrayList;

public class ImageService {
	private static final String	TAG			= ImageService.class.getName();

	public static ImageService	instance	= new ImageService();

	ArrayList<String>			allImages	= new ArrayList<String>();
	ArrayList<String>			imagesUsed	= new ArrayList<String>();

	private ImageService() {
		for (final String image : AssetTextures.ALL_IMAGES) {
			allImages.add(image);
		}
	}

	public String getUnusedImage() {
		final ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(allImages);
		tempList.removeAll(imagesUsed);
		if (tempList.isEmpty()) {
			return RandomUtil.random(allImages);
		} else {
			final String img = RandomUtil.random(tempList);
			imagesUsed.add(img);
			return img;
		}
	}
}
