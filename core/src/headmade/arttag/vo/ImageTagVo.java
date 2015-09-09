package headmade.arttag.vo;

import java.util.HashMap;
import java.util.Map;

public class ImageTagVo {

	private static final String	TAG		= ImageTagVo.class.getName();

	public String				imageId;
	public Map<String, TagVo>	tags	= new HashMap<String, TagVo>();

	public ImageTagVo(String imageId) {
		this.imageId = imageId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ImageTagVo other = (ImageTagVo) obj;
		if (imageId == null) {
			if (other.imageId != null)
				return false;
		} else if (!imageId.equals(other.imageId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ImageTagVo [imageId=" + imageId + ", tags=" + tags + "]";
	}
}
