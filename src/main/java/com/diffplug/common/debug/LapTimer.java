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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/** Returns the amount of time in seconds since lap was last called. The first call returns a very large number. */
public class LapTimer {
	protected final double FACTOR;
	protected final LongSupplier tickFunction;
	protected final AtomicLong start;

	protected LapTimer(LongSupplier tickFunction, TimeUnit unit) {
		this.FACTOR = 1.0 / unit.convert(1, TimeUnit.SECONDS);
		this.tickFunction = tickFunction;
		this.start = new AtomicLong(tickFunction.getAsLong());
	}

	/** Returns the time elapsed since the last call to lap(), or the time elapsed since the timer was created for the first call. */
	public double lap() {
		long now = tickFunction.getAsLong();
		long then = start.getAndSet(now);
		return (now - then) * FACTOR;
	}

	/** Creates a LapTimer which is accurate to the millisecond. */
	public static LapTimer createMs() {
		return new LapTimer(System::currentTimeMillis, TimeUnit.MILLISECONDS);
	}

	/** Creates a LapTimer which is accurate to the nanosecond (wraps every ~2 seconds). */
	public static LapTimer createNanoWrap2Sec() {
		return new LapTimer(System::nanoTime, TimeUnit.NANOSECONDS);
	}
}
