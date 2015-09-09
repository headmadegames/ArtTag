package headmade.arttag.utils;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {
	private static final String	TAG		= RandomUtil.class.getName();

	private static Random		random	= new Random();

	/**
	 * Returns a random element of a Set.
	 */
	public static <T> T random(Set<T> set) {
		final int size = set.size();

		if (size <= 0)
			throw new IllegalStateException("Can't get a random item of an empty Set");

		final int randomIndex = random.nextInt(size);

		int i = 0;
		for (final T t : set) {
			if (i == randomIndex)
				return t;
			i++;
		}

		throw new IllegalStateException("This case should never happen if the Set has elements");
	}

	/**
	 * Provides a random element of an array.
	 */
	public static <T> T random(T[] array) {
		if (array.length == 0)
			throw new IllegalStateException("Can't get a random item of an empty array");
		return array[random.nextInt(array.length)];
	}

	public static <T> T random(List<T> list) {
		if (list.isEmpty())
			throw new IllegalStateException("Can't get a random item from an empty List<T>");
		return list.get(random.nextInt(list.size()));
	}

	public static float random() {
		return random.nextFloat();
	}

	public static int random(int max) {
		return random.nextInt(max);
	}

}
