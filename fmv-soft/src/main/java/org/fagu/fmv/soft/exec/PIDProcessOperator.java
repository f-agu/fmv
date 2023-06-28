package org.fagu.fmv.soft.exec;

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

	public PIDProcessOperator() {
		pidConsumers = Collections.emptyList();
	}

	public PIDProcessOperator(LongConsumer pidConsumer) {
		pidConsumers = Collections.singletonList(Objects.requireNonNull(pidConsumer));
	}

	public PIDProcessOperator(Collection<LongConsumer> pidConsumers) {
		this.pidConsumers = Collections.unmodifiableList(new ArrayList<>(pidConsumers));
	}

	@Override
	public Process operate(Process process) {
		try {
			pid = process.pid();
		} catch(UnsupportedOperationException e) {
			// ignore
		}
		for(LongConsumer pidConsumer : pidConsumers) {
			pidConsumer.accept(pid);
		}
		return process;
	}

	public OptionalLong getPID() {
		return pid != null ? OptionalLong.of(pid) : OptionalLong.empty();
	}

}
