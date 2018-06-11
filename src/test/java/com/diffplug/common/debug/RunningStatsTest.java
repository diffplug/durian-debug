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

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.diffplug.common.debug.RunningStats;

public class RunningStatsTest {
	@Test
	public void testNominal() {
		RunningStats stats = new RunningStats();
		assertThat(stats.getStat().toString()).isEqualTo("No samples yet");
		stats.add(1);
		assertThat(stats.getStat().toString()).isEqualTo("median=1000ms mean=1000ms min=1000ms max=1000ms num=1");
		assertThat(stats.getStat().toString(TimeUnit.SECONDS)).isEqualTo("median=1s mean=1s min=1s max=1s num=1");

		stats.add(3);
		assertThat(stats.getStat().toString()).isEqualTo("median=2000ms mean=2000ms min=1000ms max=3000ms num=2");

		stats.add(2.5);
		assertThat(stats.getStat().toString()).isEqualTo("median=2500ms mean=2167ms min=1000ms max=3000ms num=3");

		stats.add(2.5);
		assertThat(stats.getStat().toString()).isEqualTo("median=2500ms mean=2250ms min=1000ms max=3000ms num=4");

		stats.add(2.5);
		assertThat(stats.getStat().toString()).isEqualTo("median=2500ms mean=2300ms min=1000ms max=3000ms num=5");
	}

	@Test
	public void testNan() {
		RunningStats stats = new RunningStats();
		assertThat(stats.getStat().toString()).isEqualTo("No samples yet");

		stats.add(Double.NaN);
		assertThat(stats.getStat().toString()).isEqualTo("No valid samples, 1 were NaN or infinite");

		stats.add(Double.POSITIVE_INFINITY);
		assertThat(stats.getStat().toString()).isEqualTo("No valid samples, 2 were NaN or infinite");

		stats.add(Double.NEGATIVE_INFINITY);
		assertThat(stats.getStat().toString()).isEqualTo("No valid samples, 3 were NaN or infinite");

		stats.add(0);
		assertThat(stats.getStat().toString()).isEqualTo("median=0ms mean=0ms min=0ms max=0ms num=1 numNANorINFINITE=3");
	}
}
