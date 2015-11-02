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

import headmade.arttag.JobDescription;
import headmade.arttag.utils.RandomUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public class RandomService {
	private static final String	TAG					= RandomService.class.getName();

	public static RandomService	instance			= new RandomService();

	private static final String	COMMENTED_REGEX		= "//(.*?)\\\n";
	private static final String	NOUN				= "#N";
	private static final String	ADJECTIVE			= "#adj";
	private static final String	VERB				= "#v";
	private static final String	SUFFIX				= "#suffix";

	private static String[]		nouns;
	private static String[]		adjectives;
	private static String[]		verbs;
	private static String[]		patterns;
	private static String[]		surnames;
	private static String[]		maleNames;
	private static String[]		femaleNames;
	private static String[]		suffixes;

	private static String[]		nickNamePatterns	= { "'#N' ", "'The #N' " };

	private RandomService() {
		final String noun = Gdx.files.internal("text/noun.txt").readString("utf-8");
		final String adj = Gdx.files.internal("text/adj.txt").readString("utf-8");
		final String verb = Gdx.files.internal("text/verb.txt").readString("utf-8");
		final String pattern = Gdx.files.internal("text/pattern.txt").readString("utf-8");
		final String suffix = Gdx.files.internal("text/suffix.txt").readString("utf-8");
		final String surname = Gdx.files.internal("text/names/surnames.txt").readString("utf-8");
		final String maleName = Gdx.files.internal("text/names/malenames.txt").readString("utf-8");
		final String femaleName = Gdx.files.internal("text/names/femalenames.txt").readString("utf-8");
		nouns = noun.replaceAll(COMMENTED_REGEX, "").split("\n");
		adjectives = adj.replaceAll(COMMENTED_REGEX, "").split("\n");
		verbs = verb.replaceAll(COMMENTED_REGEX, "").split("\n");
		patterns = pattern.replaceAll(COMMENTED_REGEX, "").split("\n");
		suffixes = suffix.replaceAll(COMMENTED_REGEX, "").split("\n");
		surnames = surname.replaceAll(COMMENTED_REGEX, "").split("\n");
		maleNames = maleName.replaceAll(COMMENTED_REGEX, "").split("\n");
		femaleNames = femaleName.replaceAll(COMMENTED_REGEX, "").split("\n");
	}

	public String generateArtName() {
		final StringBuilder result = new StringBuilder();
		final String pattern = RandomUtil.random(patterns);
		// System.out.println("pattern " + pattern);
		for (final String part : pattern.split(" ")) {
			result.append(replacePlaceHolders(part)).append(" ");
		}
		// System.out.println(result.toString());
		return beautifyArtName(result.toString());
	}

	public String generateArtistName() {
		final StringBuilder result = new StringBuilder();
		result.append(RandomUtil.random() > 0.5 ? RandomUtil.random(femaleNames) : RandomUtil.random(maleNames)).append(" ");
		if (RandomUtil.random() < 0.2) {
			// Nickname
			result.append(replacePlaceHolders(RandomUtil.random(nickNamePatterns)));
		}
		result.append(RandomUtil.random(surnames));
		return result.toString();
	}

	public int generateYearRange(int minYear, int range) {
		return MathUtils.clamp(RandomUtil.random(range), 1, 1900 - minYear);
	}

	public int generateYear() {
		return Math.round(1800 + RandomUtil.random(80));
	}

	public String generateJobDescription(JobDescription job) {
		final StringBuilder result = new StringBuilder();
		result.append("Dear Maggy,\n\nI have a new job for you. Please bring me \nan artwork with ");
		if (job.artTag != null) {
			result.append(job.artTag).append(" on it.\nIt must be from ");
		} else {
			result.append("anything but ");
			int i = 0;
			for (final String tagNot : job.artTagNot) {
				if (job.artTagNot.size() > 1) {
					if (i == job.artTagNot.size() - 1) {
						result.append(" or ");
					} else if (i > 0) {
						result.append(", ");
					}
				}
				result.append(tagNot);
				i++;
			}
			result.append(".\nMake sure it's from ");
		}
		if (job.artArtist == null) {
			result.append("the years ").append(job.artYearFrom).append(" to ").append(job.artYearFrom + job.artYearRange).append(".");
		} else {
			result.append(job.artArtist).append(".");
		}
		result.append("\n\nBe careful!\nFinch");
		return result.toString();
	}

	private String beautifyArtName(String string) {
		// add space after comma
		string = string.replaceAll(",", ", ");
		// "an" not "a" before word starting with vocal
		string = string.replaceAll(" a a", " an a");
		string = string.replaceAll(" a A", " an A");
		string = string.replaceAll(" a e", " an e");
		string = string.replaceAll(" a E", " an E");
		string = string.replaceAll(" a i", " an i");
		string = string.replaceAll(" a I", " an I");
		string = string.replaceAll(" a o", " an o");
		string = string.replaceAll(" a O", " an O");
		string = string.replaceAll(" a u", " an u");
		string = string.replaceAll(" a U", " an U");
		// No double spaces
		string = string.replaceAll(" +", " ");
		// first letter capital
		string = string.substring(0, 1).toUpperCase() + string.substring(1);
		return string;
	}

	private String replacePlaceHolders(String part) {
		if (part.contains(NOUN)) {
			return part.replaceAll(NOUN, RandomUtil.random(nouns));
		} else if (part.contains(ADJECTIVE)) {
			return part.replaceAll(ADJECTIVE, RandomUtil.random(adjectives));
		} else if (part.contains(VERB)) {
			String verb = RandomUtil.random(verbs);
			if (part.endsWith("ing")) {
				// if (verb.endsWith("n")
				// || verb.endsWith("m")) {
				// part = part.replace("ing", verb.substring(verb.length() - 1) + "ing");
				// } else
				if (verb.endsWith("e") || verb.endsWith("i")) {
					// cut off last char
					verb = verb.substring(0, verb.length() - 1);
				}
			} else if (part.startsWith(VERB + "er")) {
				verb = verb.substring(0, 1).toUpperCase() + verb.substring(1);
				while (verb.endsWith("e") && verb.length() > 2) {
					verb = verb.substring(0, verb.length() - 1);
				}
			} else if (part.endsWith("s")) {
				if (verb.endsWith("s")) {
					verb = verb + "e";
				}
			}
			return part.replaceAll(VERB, verb);
		} else if (part.contains(SUFFIX)) {
			return part.replaceAll(SUFFIX, RandomUtil.random(suffixes));
		} else {
			return part;
		}
	}
}
