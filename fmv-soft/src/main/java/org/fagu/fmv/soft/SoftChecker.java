package org.fagu.fmv.soft;

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
 * @author Oodrive
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
