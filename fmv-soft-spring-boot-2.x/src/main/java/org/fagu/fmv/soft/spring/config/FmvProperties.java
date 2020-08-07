package org.fagu.fmv.soft.spring.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author f.agu
 * @created 7 août 2020 11:28:54
 */
@ConfigurationProperties("fmv")
public class FmvProperties {

	private Map<String, Soft> soft = new HashMap<>();

	public Map<String, Soft> getSoft() {
		return soft;
	}

	public void setSoft(Map<String, Soft> soft) {
		this.soft = soft;
	}

	// -------------------------------------

	public static class Soft {

		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}

}
