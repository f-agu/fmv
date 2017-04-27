package org.fagu.fmv.soft.spring.actuator;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */

import java.util.Collection;

import org.fagu.fmv.soft.Soft;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;


/**
 * @author f.agu
 */
public class SoftHealthIndicator extends AbstractHealthIndicator {

	/**
	 * @see org.springframework.boot.actuate.health.AbstractHealthIndicator#doHealthCheck(org.springframework.boot.actuate.health.Health.Builder)
	 */
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		Collection<Soft> softs = Softs.getHealthIndicators();
		if(softs.isEmpty()) {
			builder.unknown();
			return;
		}
		if(softs.stream().anyMatch(s -> ! s.isFound())) {
			builder.down();
		} else {
			builder.up();
		}
		for(Soft soft : softs) {
			builder.withDetail(soft.getName(), soft.toString());
		}
	}

}
