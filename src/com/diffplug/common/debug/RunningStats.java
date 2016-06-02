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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.google.common.primitives.Doubles;

import com.diffplug.common.base.Unhandled;

/** Calculates the running mean/median/min/max of a sampled signal. */
public class RunningStats {
	double[] samples = new double[16];
	int num = 0;

	double min = Double.POSITIVE_INFINITY;
	double max = Double.NEGATIVE_INFINITY;
	double total = 0;
	int numNanOrInfinite = 0;

	/** Adds the given sample to the running stats. */
	public synchronized void add(double sample) {
		if (Double.isFinite(sample)) {
			samples = Doubles.ensureCapacity(samples, num + 1, samples.length);
			samples[num] = sample;
			total += sample;
			if (sample < min) {
				min = sample;
			}
			if (sample > max) {
				max = sample;
			}
			++num;
		} else {
			++numNanOrInfinite;
		}
	}

	/** Returns these stats at this time. */
	public synchronized Stat getStat() {
		if (num == 0) {
			return new Stat(numNanOrInfinite);
		} else {
			double mean = total / num;
			Arrays.sort(samples, 0, num);
			int middle = Math.floorDiv(num, 2);
			double median;
			if (num % 2 == 0) {
				median = (samples[middle - 1] + samples[middle]) / 2.0;
			} else {
				median = samples[middle];
			}
			return new Stat(min, max, mean, median, total, num, numNanOrInfinite);
		}
	}

	/** The stats at a given instant. */
	public static class Stat {
		public final double min, max, mean, median, total;
		public final int num, numNanOrInfinite;

		public Stat(int numNanOrInfinite) {
			this(0, 0, 0, 0, 0, 0, numNanOrInfinite);
		}

		public Stat(double min, double max, double mean, double median, double total, int num, int numNanOrInfinite) {
			this.min = min;
			this.max = max;
			this.mean = mean;
			this.median = median;
			this.total = total;
			this.num = num;
			this.numNanOrInfinite = numNanOrInfinite;
		}

		@Override
		public String toString() {
			return toString(TimeUnit.MILLISECONDS);
		}

		public String toString(TimeUnit unit) {
			if (num == 0) {
				if (numNanOrInfinite == 0) {
					return "No samples yet";
				} else {
					return "No valid samples, " + numNanOrInfinite + " were NaN or infinite";
				}
			} else {
				StringBuilder builder = new StringBuilder(256);
				builder.append("median=").append(formatUnit(median, unit));
				builder.append(" mean=").append(formatUnit(mean, unit));
				builder.append(" min=").append(formatUnit(min, unit));
				builder.append(" max=").append(formatUnit(max, unit));
				builder.append(" num=").append(num);
				if (numNanOrInfinite > 0) {
					builder.append(" numNANorINFINITE=" + numNanOrInfinite);
				}
				return builder.toString();
			}
		}
	}

	/** Formats the given elapsed time in seconds with the given precision. */
	public static String formatUnit(double elapsedSec, TimeUnit precision) {
		long multiplier = precision.convert(1, TimeUnit.SECONDS);
		int elapsedUnits = (int) Math.round(elapsedSec * multiplier);
		return Integer.toString(elapsedUnits) + units(precision);
	}

	private static String units(TimeUnit unit) {
		switch (unit) {
		case NANOSECONDS:
			return "ns";
		case MICROSECONDS:
			return "us";
		case MILLISECONDS:
			return "ms";
		case SECONDS:
			return "s";
		case MINUTES:
			return "m";
		case HOURS:
			return "h";
		case DAYS:
			return "d";
		default:
			throw Unhandled.enumException(unit);
		}
	}

	/** Adds a time to the running stats. */
	public synchronized Stat addAndGetStat(double time) {
		add(time);
		return getStat();
	}
}
