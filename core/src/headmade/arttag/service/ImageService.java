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
		String img = RandomUtil.random(tempList);
		imagesUsed.add(img);
		return img;
	}
}
