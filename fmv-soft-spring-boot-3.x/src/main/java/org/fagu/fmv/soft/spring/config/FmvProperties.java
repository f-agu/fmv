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
import java.util.Objects;
import java.util.stream.Stream;

import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.fagu.fmv.utils.PlaceHolder;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author f.agu
 * @created 7 août 2020 11:28:54
 */
@ConfigurationProperties("fmv")
public class FmvProperties {

	public static final String PATTERN = "fmv.soft.${group.name}.${soft.name}";

	// Map<group-name, Map<soft-name, SoftProperties>>
	private Map<String, Map<String, SoftProperties>> soft = new HashMap<>();

	public Map<String, Map<String, SoftProperties>> getSoft() {
		return soft;
	}

	public void setSoft(Map<String, Map<String, SoftProperties>> soft) {
		this.soft = soft;
	}

	public Stream<SoftProperties> findSoftByName(String name) {
		return soft.values().stream()
				.map(m -> m.get(name))
				.filter(Objects::nonNull);
	}

	public static String getDefaultPropertyKeys(String groupName, String softName) {
		return PlaceHolder.with(PATTERN).format(SearchPropertiesHelper.replacer(groupName, softName));
	}

	// -------------------------------------

	public static class SoftProperties {

		private String path;

		private SearchProperties search = new SearchProperties();

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public SearchProperties getSearch() {
			return search;
		}

		public void setSearch(SearchProperties search) {
			this.search = search;
		}

		// -------------------------------------

		public static class SearchProperties {

			private String versionPattern;

			private String datePattern;

			public String getVersionPattern() {
				return versionPattern;
			}

			public void setVersionPattern(String versionPattern) {
				this.versionPattern = versionPattern;
			}

			public String getDatePattern() {
				return datePattern;
			}

			public void setDatePattern(String datePattern) {
				this.datePattern = datePattern;
			}
		}
	}

}
