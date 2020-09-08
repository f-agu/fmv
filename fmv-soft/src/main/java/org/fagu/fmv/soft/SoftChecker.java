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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.fagu.fmv.soft.spring.actuator.Softs;


/**
 * @author f.agu
 * @created 24 juin 2019 12:39:35
 */
public class SoftChecker {

	// -------------------------------------------------------------------------------

	public static class Builder {

		private final List<Supplier<Soft>> softSuppliers;

		private boolean supplyInfoContributor = true;

		private boolean supplyHealthIndicator = true;

		public Builder() {
			softSuppliers = new ArrayList<>();
		}

		public Builder add(Supplier<Soft> softSupplier) {
			if(softSupplier != null) {
				softSuppliers.add(softSupplier);
			}
			return this;
		}

		public Builder add(Soft soft) {
			return add(() -> soft);
		}

		public Builder withSupplyInfoContributor(boolean enabled) {
			supplyInfoContributor = enabled;
			return this;
		}

		public Builder withSupplyHealthIndicator(boolean enabled) {
			supplyHealthIndicator = enabled;
			return this;
		}

		public SoftChecker build() {
			return new SoftChecker(this);
		}

	}

	// -------------------------------------------------------------------------------

	private final List<Supplier<Soft>> softSuppliers;

	private final boolean supplyInfoContributor;

	private final boolean supplyHealthIndicator;

	private final AtomicReference<List<Soft>> softs = new AtomicReference<>();

	private SoftChecker(Builder builder) {
		softSuppliers = Collections.unmodifiableList(new ArrayList<>(builder.softSuppliers));
		supplyInfoContributor = builder.supplyInfoContributor;
		supplyHealthIndicator = builder.supplyHealthIndicator;
	}

	public List<Soft> getNotFoundSofts() {
		return getSofts().stream()
				.filter(s -> ! s.isFound())
				.collect(Collectors.toList());
	}

	public List<Soft> getSofts() {
		return softs.updateAndGet(prev -> {
			if(prev != null) {
				return prev;
			}
			return Collections.unmodifiableList(softSuppliers.stream()
					.map(Supplier::get)
					.peek(s -> {
						if(supplyInfoContributor) {
							Softs.contributeInfo(s);
						}
						if(supplyHealthIndicator) {
							Softs.indicateHealth(s);
						}
					})
					.sorted(Comparator.comparing(Soft::getName))
					.collect(Collectors.toList()));
		});
	}

	public void ifAnyNotFound(Consumer<List<Soft>> consumer) {
		List<Soft> notFoundSofts = getNotFoundSofts();
		if( ! notFoundSofts.isEmpty()) {
			consumer.accept(notFoundSofts);
		}
	}

}
