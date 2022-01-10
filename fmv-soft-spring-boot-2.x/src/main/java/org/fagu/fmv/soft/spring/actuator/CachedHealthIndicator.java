package org.fagu.fmv.soft.spring.actuator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;


/**
 * @author f.agu
 * @created 7 août 2020 11:06:05
 */
public class CachedHealthIndicator implements HealthIndicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(CachedHealthIndicator.class);

	private final HealthIndicator delegated;

	private final Duration cacheTimeOut;

	private LocalDateTime lastCheck;

	private Health cached;

	public CachedHealthIndicator(HealthIndicator delegated, Duration cacheTimeOut) {
		this.delegated = Objects.requireNonNull(delegated);
		this.cacheTimeOut = cacheTimeOut;
	}

	@Override
	public Health health() {
		boolean toCheck = false;
		if(cached == null) {
			LOGGER.trace("Loading cache");
			toCheck = true;
		} else if(hasFailStatus()) {
			LOGGER.trace("Refresh cache by failed status: {}", cached.getStatus().getCode());
			toCheck = true;
		} else if(isTimeOut()) {
			LOGGER.trace("Refresh cache by timeout");
			toCheck = true;
		} else {
			LOGGER.trace("Don't refresh cache");
		}
		if(toCheck) {
			Health tmp = delegated.health();
			lastCheck = LocalDateTime.now();
			Map<String, Object> details = new HashMap<>(tmp.getDetails());
			details.put("cacheLastCheck", lastCheck.toString());
			if(cacheTimeOut != null) {
				details.put("cacheTimeOut", lastCheck.plus(cacheTimeOut).toString());
			}
			cached = Health.status(tmp.getStatus())
					.withDetails(details)
					.build();
		}
		return cached;
	}

	// ******************************************

	private boolean isTimeOut() {
		if(cacheTimeOut == null) {
			return false;
		}
		if(lastCheck == null) {
			return true;
		}
		return lastCheck.plus(cacheTimeOut).isBefore(LocalDateTime.now());
	}

	private boolean hasFailStatus() {
		if(cached == null) {
			return true;
		}
		return ! Status.UP.getCode().equalsIgnoreCase(cached.getStatus().getCode());
	}

}
