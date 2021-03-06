/*
 * Copyright 2016 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.common.debug;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/** Stores histograms of things for measuring performance. */
public class Histogram<T> {
	/** Maintains counts for each key. */
	private ConcurrentHashMap<T, AtomicInteger> map = new ConcurrentHashMap<>();

	/** Increments the given key. */
	public int increment(T key) {
		return add(key, 1);
	}

	/** Increments the given key by the given amount. */
	public int add(T key, int count) {
		Objects.requireNonNull(key);
		return map.computeIfAbsent(key, u -> new AtomicInteger()).addAndGet(count);
	}

	/** Calls {@link #getTopValues(int, Function)} with {@link Object#toString()} as the `toString` argument. */
	public String getTopValues(int numValues) {
		return getTopValues(numValues, Object::toString);
	}

	/**
	 * Returns a string which shows the top n values.
	 *
	 * If the histogram is still being modified
	 * when this is called, then it might return
	 * an inconsistent view (only partly modified).
	 */
	public String getTopValues(int numValues, Function<? super T, String> toString) {
		Objects.requireNonNull(toString);
		Comparator<Map.Entry<Integer, String>> byKey = Comparator.comparing(Map.Entry::getKey);
		Comparator<Map.Entry<Integer, String>> byValue = Comparator.comparing(Map.Entry::getValue);
		// sort by frequency
		TreeSet<Map.Entry<Integer, String>> builder = new TreeSet<>(byKey.thenComparing(byValue));
		for (Map.Entry<T, AtomicInteger> entry : map.entrySet()) {
			builder.add(ImmutableEntry.create(entry.getValue().get(), toString.apply(entry.getKey())));
		}

		// find the length of the longestKey
		int longestKey = 0;
		int count = 0;
		for (Map.Entry<Integer, String> entry : builder.descendingSet()) {
			if (++count > numValues) {
				break;
			}
			longestKey = Math.max(longestKey, entry.getValue().length());
		}

		// iterate over the map
		StringBuilder output = new StringBuilder();
		count = 0;
		for (Map.Entry<Integer, String> entry : builder.descendingSet()) {
			if (++count > numValues) {
				break;
			}
			output.append(entry.getValue());
			for (int i = 0; i < longestKey - entry.getValue().length(); ++i) {
				output.append(' ');
			}
			output.append(": ");
			output.append(Integer.toString(entry.getKey()));
			output.append("\n");
		}
		return output.toString();
	}
}
