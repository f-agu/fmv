package org.fagu.fmv.soft.spring.config;

import java.time.Duration;

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

import org.fagu.fmv.soft.spring.actuator.RunWithDocker;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author f.agu
 * @created 7 août 2020 11:08:41
 */
@ConfigurationProperties("fmv.indicator")
public class SoftIndicatorProperties {

	private Contributor contributor = new Contributor();

	private Health health = new Health();

	public Contributor getContributor() {
		return contributor;
	}

	public void setContributor(Contributor contributor) {
		this.contributor = contributor;
	}

	public Health getHealth() {
		return health;
	}

	public void setHealth(Health health) {
		this.health = health;
	}

	// -----------------------------------------

	public static class Contributor extends Common {

	}

	// -----------------------------------------

	public static class Health extends Common {

		public enum CheckPolicy {
			ALWAYS,
			ON_FILE_CHANGE
		}

		private CheckPolicy checkPolicy = CheckPolicy.ON_FILE_CHANGE;

		public CheckPolicy getCheckPolicy() {
			return checkPolicy;
		}

		public void setCheckPolicy(CheckPolicy checkPolicy) {
			this.checkPolicy = checkPolicy;
		}
	}

	// -----------------------------------------

	private static class Common {

		private Cache cache = new Cache();

		public Cache getCache() {
			return cache;
		}

		public void setCache(Cache cache) {
			this.cache = cache;
		}
	}

	// -----------------------------------------

	public static class Cache {

		private boolean enabled = RunWithDocker.isInDocker();

		private Duration timeOut = Duration.ofMinutes(5);

		public boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public Duration getTimeOut() {
			return timeOut;
		}

		public void setTimeOut(Duration timeOut) {
			this.timeOut = timeOut;
		}

	}

}
