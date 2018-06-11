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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.diffplug.common.base.Throwing;

/**
 * Profiles some code by running the test code one
 * after the other over and over.
 */
public class JuxtaProfiler {
	private List<Test> tests = new ArrayList<>();

	/** Adds the given test to the profiler. */
	public void addTest(String name, ITimed test) {
		tests.add(new Test(name, test));
	}

	/** Adds the given runnable as a test to the profiler, with millisecond accuracy. */
	public void addTestMs(String name, Throwing.Runnable toTest) {
		addTest(name, toTest, LapTimer.createMs());
	}

	/** Adds the given runnable as a test to the profiler, with nanosecond accuracy. */
	public void addTestNanoWrap2Sec(String name, Throwing.Runnable toTest) {
		addTest(name, toTest, LapTimer.createNanoWrap2Sec());
	}

	/** Adds the given runnable as a test to the profiler. */
	public void addTest(String name, Throwing.Runnable toTest, LapTimer timer) {
		Objects.requireNonNull(toTest);
		addTest(name, new InitTimedCleanup(timer) {
			@Override
			protected void init() {}

			@Override
			protected void timed() throws Throwable {
				toTest.run();
			}

			@Override
			protected void cleanup() {}
		});
	}

	/**
	 * Runs the tests added with {@link #addTest(String, ITimed)} `numTrials` times.  Prints the
	 * progress as it goes, and finally prints the statistics for the entire run.
	 * 
	 * Shuffles the order of the tests between each trial.
	 */
	public void runRandomTrials(int numTrials) {
		List<Test> shuffledTests = new ArrayList<>(tests);
		LapTimer timer = LapTimer.createMs();
		for (int i = 0; i < numTrials; ++i) {
			Collections.shuffle(shuffledTests);
			System.out.print("Running trial " + (i + 1) + " of " + numTrials + " ... ");
			timer.lap();
			for (Test test : shuffledTests) {
				test.runTrial();
			}
			System.out.println(" complete after " + format(timer.lap()) + ".");

			// print the results after every run, in case it crashes
			printResults();
		}
	}

	/** Prints the results of the trials. */
	private void printResults() {
		for (Test test : tests) {
			test.printResults();
		}
	}

	/** Wraps up a single ITimed under test. */
	private static class Test {
		private final String name;
		private final ITimed underTest;
		private final RunningStats stats = new RunningStats();

		public Test(String name, ITimed test) {
			this.name = Objects.requireNonNull(name);
			this.underTest = Objects.requireNonNull(test);
		}

		public void runTrial() {
			try {
				stats.add(underTest.time());
			} catch (Throwable e) {
				stats.add(Double.NaN);
				e.printStackTrace();
			}
		}

		public void printResults() {
			System.out.println(name + ": " + stats.getStat());
		}
	}

	/** Formats the given double. */
	private static String format(double elapsedSec) {
		int elapsedMs = (int) Math.round(elapsedSec * 1000);
		return Integer.toString(elapsedMs) + " ms";
	}

	/** Interface for presenting a profiled action. */
	public interface ITimed {
		/**
		 * Runs the action to be profiled, and returns the elapsed time of the
		 * profiled section in seconds.
		 */
		double time() throws Throwable;
	}

	/**
	 * Default implementation of {@link ITimed} which includes an untimed
	 * `init()` and `cleanup()` phase wrapped around the actually profiled `timed()` method.
	 */
	public static abstract class InitTimedCleanup implements ITimed {
		private final LapTimer timer;

		protected InitTimedCleanup(LapTimer timer) {
			this.timer = Objects.requireNonNull(timer);
		}

		/** Setup a single test. */
		protected abstract void init() throws Throwable;

		/** Executes the timed portion of a test. */
		protected abstract void timed() throws Throwable;

		/** Performs cleanup on a given test. */
		protected abstract void cleanup() throws Throwable;

		/** Runs `init()`, `timed()`, then `cleanup()`, and returns the elapsed time of `timed()`. */
		@Override
		public final double time() throws Throwable {
			init();
			timer.lap();
			timed();
			double time = timer.lap();
			cleanup();
			return time;
		}
	}
}
