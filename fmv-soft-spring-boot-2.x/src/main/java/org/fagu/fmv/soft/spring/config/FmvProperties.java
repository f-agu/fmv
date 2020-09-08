package org.fagu.fmv.soft.spring.config;

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
