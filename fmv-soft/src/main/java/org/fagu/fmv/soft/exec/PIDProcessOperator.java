package org.fagu.fmv.soft.exec;

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
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void consume(Process process, Class<? extends Process> cls, String field) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException,
			IllegalAccessException {
		Field f = cls.getDeclaredField(field);
		f.setAccessible(true);
		pid = f.getLong(process);
		for(LongConsumer pidConsumer : pidConsumers) {
			pidConsumer.accept(pid);
		}
		f.setAccessible(false);
	}

}
