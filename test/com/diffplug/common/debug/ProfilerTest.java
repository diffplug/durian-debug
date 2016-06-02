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

import java.io.PrintStream;

import org.junit.Test;

import com.google.common.collect.Range;

import com.diffplug.common.base.StringPrinter;
import com.diffplug.common.base.Throwing;

public class ProfilerTest {
	@Test
	public void runProfiler() throws Throwable {
		Profiler profiler = new Profiler();
		profiler.addTestMs("A", () -> Thread.sleep(100));
		profiler.addTestMs("B", () -> Thread.sleep(20));

		String captured = captureSysOut(() -> profiler.runRandomTrials(10));
		String[] lines = captured.split(System.lineSeparator());
		String lastSummary = lines[lines.length - 3];
		String lastA = lines[lines.length - 2];
		String lastB = lines[lines.length - 1];

		// test the messages
		assertThat(lastSummary).startsWith("Running trial 10 of 10 ...  complete after ");
		assertThat(lastA).startsWith("A: median=");
		assertThat(lastA).endsWith("ms num=10");
		assertThat(lastB).startsWith("B: median=");
		assertThat(lastB).endsWith("ms num=10");

		// and the medians
		int medianPrefix = "A: median=".length();
		int medianA = Integer.parseInt(lastA.substring(medianPrefix, medianPrefix + 3));
		int medianB = Integer.parseInt(lastB.substring(medianPrefix, medianPrefix + 2));
		assertThat(medianA).isIn(Range.closed(95, 105));
		assertThat(medianB).isIn(Range.closed(15, 25));
	}

	private String captureSysOut(Throwing.Runnable toRun) throws Throwable {
		PrintStream sysOut = System.out;
		try {
			StringBuilder builder = new StringBuilder();
			StringPrinter printer = new StringPrinter(builder::append);
			PrintStream stream = printer.toPrintStream();
			System.setOut(stream);

			toRun.run();

			stream.close();
			return builder.toString();
		} finally {
			System.setOut(sysOut);
		}
	}
}
