package headmade.arttag;

import headmade.arttag.service.RandomService;
import headmade.arttag.service.TagService;
import headmade.arttag.utils.RandomUtil;

import java.util.HashSet;
import java.util.Set;

public class JobDescription {
	private static final String	TAG			= JobDescription.class.getName();

	public String				desc;
	public String				artTag;
	public Set<String>			artTagNot	= new HashSet<String>();
	public String				artArtist;
	public Integer				artYearFrom;
	public Integer				artYearRange;

	public JobDescription() {
		if (RandomUtil.random() > 0.2f) {
			artTag = RandomUtil.random(TagService.TAGS);
		} else {
			for (int i = 0; i < 1 + RandomUtil.random(2); i++) {
				artTagNot.add(RandomUtil.random(TagService.TAGS));
			}
		}
		// if (RandomUtil.random() > 0.5f) {
		// artArtist = RandomService.instance.generateArtistName();
		// } else {
		artYearFrom = RandomService.instance.generateYear();
		artYearRange = RandomService.instance.generateYearRange(artYearFrom, 10 + Math.round(RandomUtil.random(100)));
		// }
		desc = RandomService.instance.generateJobDescription(this);
	}
}
