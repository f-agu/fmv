package org.fagu.fmv.soft.spring.actuator;

import java.util.Objects;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;


/**
 * @author f.agu
 * @created 7 août 2020 11:06:05
 */
public class CachedHealthIndicator implements HealthIndicator {

	private final HealthIndicator delegated;

	private Health cached;

	public CachedHealthIndicator(HealthIndicator delegated) {
		this.delegated = Objects.requireNonNull(delegated);
	}

	@Override
	public Health health() {
		if(cached == null) {
			cached = delegated.health();
		}
		return cached;
	}

}
