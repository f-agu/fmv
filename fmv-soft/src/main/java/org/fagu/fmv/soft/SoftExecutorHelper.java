package org.fagu.fmv.soft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 août 2020 16:37:36
 */
public abstract class SoftExecutorHelper<H extends SoftExecutorHelper<?>> {

	private final List<Consumer<SoftExecutor>> softExecutorConsumers;

	public SoftExecutorHelper() {
		this.softExecutorConsumers = new ArrayList<>();
	}

	public H customizeExecutor(Consumer<SoftExecutor> consumer) {
		if(consumer != null) {
			softExecutorConsumers.add(consumer);
		}
		return getThis();
	}

	public void populate(SoftExecutor executor) {
		softExecutorConsumers.forEach(c -> c.accept(executor));
	}

	// ********************************

	@SuppressWarnings("unchecked")
	private H getThis() {
		return (H)this;
	}

}
