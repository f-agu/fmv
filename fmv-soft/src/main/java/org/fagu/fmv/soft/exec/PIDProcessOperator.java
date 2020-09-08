package org.fagu.fmv.soft.exec;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.function.LongConsumer;


/**
 * @author fagu
 */
public class PIDProcessOperator implements ProcessOperator {

	private final List<LongConsumer> pidConsumers;

	private Long pid;

	/**
	 * 
	 */
	public PIDProcessOperator() {
		pidConsumers = Collections.emptyList();
	}

	/**
	 * @param pidConsumer
	 */
	public PIDProcessOperator(LongConsumer pidConsumer) {
		pidConsumers = Collections.singletonList(Objects.requireNonNull(pidConsumer));
	}

	/**
	 * @param pidConsumers
	 */
	public PIDProcessOperator(Collection<LongConsumer> pidConsumers) {
		this.pidConsumers = Collections.unmodifiableList(new ArrayList<>(pidConsumers));
	}

	/**
	 * @see org.fagu.fmv.soft.exec.ProcessOperator#operate(java.lang.Process)
	 */
	@Override
	public Process operate(Process process) {
		Class<? extends Process> cls = process.getClass();
		String clsName = cls.getName();

		try {
			// unix
			if("java.lang.UNIXProcess".equals(clsName)) {
				consume(process, cls, "pid");
			}
			// windows or default
			else if("java.lang.Win32Process".equals(clsName) || "java.lang.ProcessImpl".equals(clsName)) {
				consume(process, cls, "handle");
			}
		} catch(Exception e) {
			// ignore
		}
		return process;
	}

	/**
	 * @return
	 */
	public OptionalLong getPID() {
		return pid != null ? OptionalLong.of(pid) : OptionalLong.empty();
	}

	// *********************************************

	/**
	 * @param process
	 * @param cls
	 * @param field
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private void consume(Process process, Class<? extends Process> cls, String field) throws NoSuchFieldException, IllegalAccessException {
		Field f = cls.getDeclaredField(field);
		f.setAccessible(true);
		pid = f.getLong(process);
		for(LongConsumer pidConsumer : pidConsumers) {
			pidConsumer.accept(pid);
		}
		f.setAccessible(false);
	}

}
