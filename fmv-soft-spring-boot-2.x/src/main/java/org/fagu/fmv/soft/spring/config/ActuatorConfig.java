package org.fagu.fmv.soft.spring.config;

import java.time.Duration;
import java.util.Optional;

import org.fagu.fmv.soft.spring.actuator.CachedHealthIndicator;
import org.fagu.fmv.soft.spring.actuator.SoftFoundHealthIndicator;
import org.fagu.fmv.soft.spring.actuator.SoftInfoContributor;
import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Cache;
import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Contributor;
import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author f.agu
 */
@Configuration
public class ActuatorConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorConfig.class);

	@Bean
	SoftIndicatorProperties softIndicatorProperties() {
		return new SoftIndicatorProperties();
	}

	@Bean
	@ConditionalOnEnabledHealthIndicator("soft-info")
	SoftInfoContributor softVersionInfoContributor(SoftIndicatorProperties softIndicatorProperties) {
		Contributor contributor = softIndicatorProperties.getContributor();
		Cache cache = contributor.getCache();
		if(LOGGER.isDebugEnabled()) {
			if(cache.getEnabled()) {
				LOGGER.debug("SoftInfoContributor with cache (timeout: {})",
						Optional.ofNullable(cache.getTimeOut()).map(Duration::toString).orElse("-"));
			} else {
				LOGGER.debug("SoftInfoContributor without cache");
			}
		}
		return new SoftInfoContributor(cache.getEnabled(), cache.getTimeOut());
	}

	@Bean
	@ConditionalOnEnabledHealthIndicator("soft-found")
	HealthIndicator softHealthIndicator(SoftIndicatorProperties softIndicatorProperties) {
		Health healthProps = softIndicatorProperties.getHealth();
		Cache cache = healthProps.getCache();
		if(cache.getEnabled()) {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("SoftFoundHealthIndicator with cache (timeout: {}, checkPolicy: {})",
						Optional.ofNullable(cache.getTimeOut()).map(Duration::toString).orElse("-"),
						healthProps.getCheckPolicy());
			}
			return new CachedHealthIndicator(new SoftFoundHealthIndicator(healthProps), cache.getTimeOut());
		}
		LOGGER.debug("SoftFoundHealthIndicator without cache");
		return new SoftFoundHealthIndicator(healthProps);
	}

}
