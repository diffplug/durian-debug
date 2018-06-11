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

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import com.diffplug.common.debug.Histogram;

public class HistogramTest {
	@Test
	public void testEmpty() {
		Histogram<String> histogram = new Histogram<>();
		assertThat(histogram.getTopValues(3)).isEqualTo("");
	}

	@Test
	public void testSingle() {
		Histogram<String> histogram = new Histogram<>();
		histogram.add("A", 500);
		assertThat(histogram.getTopValues(3)).isEqualTo("A: 500\n");
	}

	@Test
	public void testFull() {
		Histogram<String> histogram = new Histogram<>();
		histogram.add("A", 500);
		histogram.add("B", 1000);
		histogram.add("C", 1500);
		histogram.add("D", 2000);
		histogram.add("E", 2500);
		histogram.add("F", 3000);
		assertThat(histogram.getTopValues(3)).isEqualTo(
				"F: 3000\n" +
						"E: 2500\n" +
						"D: 2000\n");
	}
}
