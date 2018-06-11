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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import com.diffplug.common.base.Preconditions;

/**
 * Makes it easy to setup a set of executors,
 * block those executors, and then run them at a
 * defined time.
 * 
 * Helpful for testing async logic.
 */
public class ManualExecutor {
	public static ManualExecutor createImmediate() {
		return create(toRun -> toRun.run());
	}

	public static ManualExecutor createAsync() {
		return create(ForkJoinPool.commonPool());
	}

	public static ManualExecutor create(Executor delegate) {
		return new ManualExecutor(delegate);
	}

	final Executor delegate;
	final Map<String, QueuedExecutor> map = new HashMap<>();

	ManualExecutor(Executor delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	class QueuedExecutor implements Executor {
		private List<Runnable> toRun = new ArrayList<>();

		@Override
		public synchronized void execute(Runnable command) {
			if (toRun != null) {
				toRun.add(command);
			} else {
				delegate.execute(command);
			}
		}

		public synchronized void unblock() {
			for (Runnable runnable : toRun) {
				delegate.execute(runnable);
			}
			toRun = null;
		}
	}

	public Executor create(String key) {
		Objects.requireNonNull(key);
		QueuedExecutor executor = new QueuedExecutor();
		Executor previous = map.put(key, executor);
		Preconditions.checkArgument(previous == null, "Can only call create() once per key!");
		return executor;
	}

	public void run(String key) {
		Objects.requireNonNull(key);
		QueuedExecutor value = map.remove(key);
		Objects.requireNonNull(value, "Can only call run() once per key!");
		value.unblock();
	}
}
