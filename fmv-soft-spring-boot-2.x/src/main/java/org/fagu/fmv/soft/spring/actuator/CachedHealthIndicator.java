package org.fagu.fmv.soft.spring.actuator;

/*-
 * #%L
 * fmv-soft-spring-boot-2.x
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
