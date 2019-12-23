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
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.SoftFound;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;


/**
 * @author f.agu
 */
public class SoftFoundHealthIndicator extends AbstractHealthIndicator {

	public SoftFoundHealthIndicator() {
		super("soft check failed");
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		Collection<Soft> softs = Softs.getHealthIndicators();
		if(softs.isEmpty()) {
			builder.unknown();
			return;
		}

		builder.up();
		for(Soft soft : softs) {
			String msg = soft.toString();

			SoftFound softFound = soft.getFounds().getFirstFound();
			if(soft.isFound()) {
				// recheck soft
				softFound = soft.reFind();
			}
			if(softFound != null && ! softFound.isFound()) {
				builder.down();

				StringJoiner joiner = new StringJoiner(", ");
				soft.getFounds().forEach(f -> {
					String reason = f.getReason();
					if(StringUtils.isNotBlank(reason)) {
						joiner.add(reason);
					}
				});
				if(joiner.length() > 0) {
					msg += ": " + joiner.toString();
				}
			} else if(softFound == null) {
				builder.down();
			}
			builder.withDetail(soft.getName(), msg);
		}
	}

}
