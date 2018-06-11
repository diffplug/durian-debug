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

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Profiles the various steps of code.
 * 
 * Example usage:
 * 
 * ```java
 * public static final StepProfiler stepProfiler = new StepProfiler(LapTimer.createNonWrap2Sec());
 * ...
 * public void methodWhichNeedsProfiling();
 *     stepProfiler.startStep("A");
 *     ...
 *     stepProfiler.startStep("B");
 * }
 * ...
 * @Test
 * public void profile() {
 *     [run test to exercise the steps]
 *     stepProfiler.printResults();
 * }
 * ```
 */
public class StepProfiler {
	private final LapTimer timer;
	private final String prefix;

	private Optional<Step> currentStep = Optional.empty();
	LinkedHashMap<String, Step> steps = new LinkedHashMap<>();

	public StepProfiler(LapTimer timer, String prefix) {
		this.timer = Objects.requireNonNull(timer);
		this.prefix = Objects.requireNonNull(prefix);
	}

	public StepProfiler(LapTimer timer) {
		this(timer, "");
	}

	/** Starts accumulating time to the given step. If a step was already accumulating time, it is stopped. */
	public synchronized void startStep(String name) {
		Objects.requireNonNull(name);
		finish();

		Step step = steps.computeIfAbsent(name, Step::new);
		currentStep = Optional.of(step);
	}

	/** Stops accumulating time to the current step (if any). */
	public synchronized void finish() {
		double elapsed = timer.lap();
		if (currentStep.isPresent()) {
			currentStep.get().addTime(elapsed);
		}
		currentStep = Optional.empty();
	}

	/**
	 * Prints the percentage of time being consumed in each step, along with
	 * min/max/mean/median per step.
	 */
	public void printResults() {
		finish();
		double allTotal = 0;
		for (Step step : steps.values()) {
			allTotal += step.stats.getStat().total;
		}

		System.out.println("------------------");
		System.out.println("Total elapsed: " + RunningStats.formatUnit(allTotal, TimeUnit.MILLISECONDS));

		for (Step step : steps.values()) {
			step.printResults(allTotal);
		}
	}

	/** Wraps up a single ITimed under test. */
	class Step {
		final String name;
		final RunningStats stats = new RunningStats();

		public Step(String name) {
			this.name = name;
		}

		public void addTime(double time) {
			stats.add(time);
		}

		public void printResults(double allTotal) {
			RunningStats.Stat stat = stats.getStat();
			System.out.println(prefix + name + ": percent=" + formatPercent(stat.total / allTotal) + " " + stats.getStat().toString(TimeUnit.MILLISECONDS));
		}
	}

	/** Formats the given percent. */
	private static String formatPercent(double percent) {
		return Integer.toString((int) Math.round(percent * 100)) + "%";
	}
}
