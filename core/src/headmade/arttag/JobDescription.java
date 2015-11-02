package headmade.arttag;

import java.util.HashSet;
import java.util.Set;

import headmade.arttag.service.RandomService;
import headmade.arttag.service.TagService;
import headmade.arttag.utils.RandomUtil;

public class JobDescription {
	private static final String TAG = JobDescription.class.getName();

	public String		desc;
	public String		artTag;
	public Set<String>	artTagNot	= new HashSet<String>();
	public String		artArtist;
	public Integer		artYearFrom;
	public Integer		artYearRange;

	public JobDescription() {
		// if (RandomUtil.random() > 0.2f) {
		artTag = RandomUtil.random(TagService.TAGS);
		artTag = TagService.instance.getLabel(artTag);
		// } else {
		// for (int i = 0; i < 1 + RandomUtil.random(2); i++) {
		// artTagNot.add(RandomUtil.random(TagService.TAGS));
		// }
		// }

		// if (RandomUtil.random() > 0.5f) {
		// artArtist = RandomService.instance.generateArtistName();
		// } else {
		artYearFrom = RandomService.instance.generateYear();
		artYearRange = 30 + RandomUtil.random(10);
		// artYearRange = RandomService.instance.generateYearRange(artYearFrom, 40);
		// }
		desc = RandomService.instance.generateJobDescription(this);
	}
}
