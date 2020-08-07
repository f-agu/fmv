package org.fagu.fmv.soft.spring.config;

import org.fagu.fmv.soft.spring.actuator.CachedHealthIndicator;
import org.fagu.fmv.soft.spring.actuator.SoftFoundHealthIndicator;
import org.fagu.fmv.soft.spring.actuator.SoftInfoContributor;
import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Contributor;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author f.agu
 */
@Configuration
public class ActuatorConfig {

	@Bean
	SoftIndicatorProperties softIndicatorProperties() {
		return new SoftIndicatorProperties();
	}

	@Bean
	@ConditionalOnEnabledHealthIndicator("soft-info")
	SoftInfoContributor softVersionInfoContributor(SoftIndicatorProperties softIndicatorProperties) {
		Contributor contributor = softIndicatorProperties.getContributor();
		return new SoftInfoContributor(contributor.getCacheEnabled());
	}

	@Bean
	@ConditionalOnEnabledHealthIndicator("soft-found")
	HealthIndicator softHealthIndicator(SoftIndicatorProperties softIndicatorProperties) {
		if(softIndicatorProperties.getHealth().getCacheEnabled()) {
			return new CachedHealthIndicator(new SoftFoundHealthIndicator());
		}
		return new SoftFoundHealthIndicator();
	}

}
