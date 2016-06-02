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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Test;

import com.diffplug.common.base.Errors;
import com.diffplug.common.base.StringPrinter;
import com.diffplug.common.debug.ProfileLogger;
import com.diffplug.common.debug.ProfileLogger.Mode;

public class ProfileLoggerTest {
	@Test
	public void testModeOff() {
		assertThat(testCase(Mode.OFF)).isEmpty();
	}

	@Test
	public void testModeDelta() {
		List<Integer> delta = testCase(Mode.DELTA);
		assertThat(delta).containsExactly(0, 1, 1, 1);
	}

	@Test
	public void testModeEpoch() {
		List<Integer> epoch = testCase(Mode.EPOCH);
		assertThat(epoch).containsExactly(0, 1, 2, 3);
	}

	/**
	 * Logs out 4 events, separated by 100ms, and returns the timestamps
	 * parsed out from the log to the nearest 100ms.  
	 */
	private List<Integer> testCase(Mode mode) {
		String complete = StringPrinter.buildString(Errors.rethrow().wrap(printer -> {
			ProfileLogger logger = new ProfileLogger(printer, mode);
			logger.log("A");
			Thread.sleep(100);
			logger.log("B");
			Thread.sleep(100);
			logger.log("C");
			Thread.sleep(100);
			logger.log("D");
		}));
		String[] lines = complete.split("\n");
		return Arrays.asList(lines).stream().map(line -> {
			if (line.length() > 4) {
				int value = Integer.parseInt(line.substring(0, 4));
				return (int) Math.round(value / 100.0);
			} else {
				return null;
			}
		})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
