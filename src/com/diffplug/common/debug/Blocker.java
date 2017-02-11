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
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.junit.Assert;

import com.diffplug.common.base.Consumers;
import com.diffplug.common.base.Either;
import com.diffplug.common.base.Errors;

/**
 * Blocks until a value is set.
 */
public class Blocker<T> {
	public static final int DEFAULT_TIMEOUT = 1000;

	private final Consumer<T> onSet;
	private Either<T, Throwable> result;

	public static <T> Blocker<T> create() {
		return create(Consumers.doNothing());
	}

	public static <T> Blocker<T> create(Consumer<T> onSet) {
		return new Blocker<>(onSet);
	}

	public Blocker(Consumer<T> onSet) {
		this.onSet = Objects.requireNonNull(onSet);
	}

	public synchronized void set(T value) {
		onSet.accept(value);
		result = Either.createLeft(value);
		notifyAll();
	}

	public void setException(Throwable exception) {
		result = Either.createRight(exception);
		notifyAll();
	}

	public synchronized boolean isWaiting() {
		return result == null;
	}

	public void assertWaiting() {
		Assert.assertTrue(isWaiting());
	}

	public T assertDone() {
		Assert.assertTrue(!isWaiting());
		return unfold(result);
	}

	public void assertDoneEquals(Object expected) {
		Assert.assertEquals(expected, assertDone());
	}

	public synchronized T get() {
		return getWithTimeout(DEFAULT_TIMEOUT);
	}

	public synchronized T getWithTimeout(long timeout) {
		try {
			if (result == null) {
				wait(timeout);
			}
			if (result == null) {
				throw new AssertionError("Value was not set");
			}
			return unfold(result);
		} catch (InterruptedException e) {
			throw Errors.asRuntime(e);
		}
	}

	public void getAndAssert(T expected) {
		getAndAssertWithTimeout(expected, DEFAULT_TIMEOUT);
	}

	public void getAndAssertWithTimeout(T expected, long timeout) {
		T actual = getWithTimeout(timeout);
		Assert.assertEquals(expected, actual);
	}

	public static <T> T getFuture(CompletionStage<T> stage) {
		return getFutureWithTimeout(stage, DEFAULT_TIMEOUT);
	}

	public static <T> T getFutureWithTimeout(CompletionStage<T> stage, int timeout) {
		Blocker<T> blocker = Blocker.create();
		stage.whenComplete((value, error) -> {
			if (error == null) {
				blocker.set(value);
			} else {
				blocker.setException(error);
			}
		});
		return blocker.getWithTimeout(timeout);
	}

	private static <T> T unfold(Either<T, Throwable> result) {
		if (result.isLeft()) {
			return result.getLeft();
		} else {
			throw new AssertionError(result.getRight());
		}
	}
}
