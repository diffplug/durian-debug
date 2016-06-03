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

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Objects;

import com.diffplug.common.base.Errors;
import com.diffplug.common.base.Preconditions;
import com.diffplug.common.base.StringPrinter;

/**
 * Creates a StringPrinter that eagerly writes everything to a file.
 * Useful for debugging crashing things in IO-constrained environemnts (e.g. installers).
 */
public class DebugFileLogger {
	/** Writes to the given filename on the current user's Desktop. */
	public static StringPrinter writeToDesktop(String filename) {
		Objects.requireNonNull(filename);
		File file = new File(System.getProperty("user.home") + "/Desktop/" + filename);
		Preconditions.checkState(file.getParentFile().mkdirs());
		return writeTo(file);
	}

	/** Writes to the given file. */
	public static StringPrinter writeTo(File to) {
		Objects.requireNonNull(to);
		return Errors.rethrow().get(() -> {
			@SuppressWarnings("resource") // don't worry about the resource leak, it's just for debug stuff
			PrintStream stream = new PrintStream(to, StandardCharsets.UTF_8.name());
			stream.println("LOG START: " + DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
			return new StringPrinter(str -> {
				stream.print(str);
				stream.flush();
			});
		});
	}
}
