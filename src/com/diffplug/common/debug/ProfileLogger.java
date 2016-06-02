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

import java.util.Objects;

import com.diffplug.common.base.StringPrinter;
import com.diffplug.common.base.Unhandled;

/** Logs things with timestamps (either absolute or delta since last log statement). */
public class ProfileLogger {
	private long epoch = System.currentTimeMillis();

	public enum Mode {
		EPOCH, DELTA, OFF
	}

	private final StringPrinter printer;
	private Mode mode;

	public ProfileLogger(StringPrinter printer, Mode mode) {
		this.printer = printer;
		setMode(mode);
	}

	public void setMode(Mode mode) {
		this.mode = Objects.requireNonNull(mode);
	}

	/** Logs the given statement. */
	public void log(String txt) {
		switch (mode) {
		case EPOCH:
			long elapsed = System.currentTimeMillis() - epoch;
			printer.println(String.format("%04d ms %s", elapsed, txt));
			break;
		case DELTA:
			long newEpoch = System.currentTimeMillis();
			elapsed = newEpoch - epoch;
			epoch = newEpoch;
			printer.println(String.format("%04d ms %s", elapsed, txt));
			break;
		case OFF:
			break;
		default:
			throw Unhandled.enumException(mode);
		}
	}
}
