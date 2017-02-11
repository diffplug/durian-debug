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

import java.lang.reflect.Constructor;
import java.util.Objects;

class AssertMisc {
	static void assertNull(Object value, String message) {
		if (value != null) {
			throw new AssertionError(message);
		}
	}

	static void assertTrue(boolean value) {
		if (!value) {
			throw new AssertionError("Expected true, was false");
		}
	}

	static void assertEquals(Object expected, Object actual) {
		if (!Objects.equals(expected, actual)) {
			throw createAssertionError(expected, actual);
		}
	}

	private static final String[] exceptionTypes = new String[]{
			"org.junit.ComparisonFailure",
			"junit.framework.ComparisonFailure"
	};

	private static AssertionError createAssertionError(Object expected, Object actual) {
		if (expected instanceof String && actual instanceof String) {
			for (String exceptionType : exceptionTypes) {
				try {
					return createComparisonFailure(exceptionType, (String) expected, (String) actual);
				} catch (Exception e) {}
			}
		}
		// we'll have to settle for a plain-jane AssertionError
		return new AssertionError("Expected:\n" + expected + "\n\nActual:\n" + actual);
	}

	/** Attempts to create an instance of junit's ComparisonFailure exception using reflection. */
	private static AssertionError createComparisonFailure(String className, String expected, String actual) throws Exception {
		Class<?> clazz = Class.forName(className);
		Constructor<?> constructor = clazz.getConstructor(String.class, String.class, String.class);
		return (AssertionError) constructor.newInstance("", expected, actual);
	}
}
