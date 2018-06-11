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
import com.diffplug.common.debug.StepProfiler;

public class StepProfilerTest {
	@Test
	public void test() throws InterruptedException {
		int numTrials = 100;
		StepProfiler profiler = new StepProfiler(LapTimer.createNanoWrap2Sec());
		for (int i = 0; i < numTrials; ++i) {
			profiler.startStep("init");
			Thread.sleep(1);
			profiler.startStep("run");
			Thread.sleep(9);
		}
		profiler.finish();

		double initTotal = profiler.steps.get("init").stats.total;
		double runTotal = profiler.steps.get("run").stats.total;
		assertThat(initTotal).isWithin(0.1).of(0.1);
		assertThat(runTotal).isWithin(0.1).of(0.9);
	}
}
