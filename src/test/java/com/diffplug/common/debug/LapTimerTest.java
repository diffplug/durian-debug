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

import com.diffplug.common.debug.LapTimer;

public class LapTimerTest {
	@Test
	public void testMs() throws InterruptedException {
		LapTimer timer = LapTimer.createMs();
		Thread.sleep(50);
		double elapsed = timer.lap();
		assertThat(elapsed).isWithin(0.01).of(0.05);
	}

	@Test
	public void testNs() throws InterruptedException {
		LapTimer timer = LapTimer.createNanoWrap2Sec();
		Thread.sleep(50);
		double elapsed = timer.lap();
		assertThat(elapsed).isWithin(0.01).of(0.05);
	}
}
