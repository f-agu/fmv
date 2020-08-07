package org.fagu.fmv.soft.spring.config;

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

	}

	// -----------------------------------------

	private static class Common {

		private boolean cacheEnabled = RunWithDocker.isInDocker();

		public boolean getCacheEnabled() {
			return cacheEnabled;
		}

		public void setCacheEnabled(boolean cacheEnabled) {
			this.cacheEnabled = cacheEnabled;
		}

	}

}
