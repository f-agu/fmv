package org.fagu.fmv.soft;

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

	protected SoftExecutorHelper() {
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
