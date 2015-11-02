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
