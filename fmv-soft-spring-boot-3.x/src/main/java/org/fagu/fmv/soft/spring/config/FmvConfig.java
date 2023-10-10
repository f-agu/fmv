package org.fagu.fmv.soft.spring.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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

import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.spring.SpringLocator;
import org.fagu.fmv.soft.spring.config.FmvProperties.SoftProperties;
import org.fagu.fmv.soft.spring.config.FmvProperties.SoftProperties.SearchProperties;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author f.agu
 * @created 7 août 2020 11:37:41
 */
@Configuration
public class FmvConfig {

	public static final String BEAN_SPRING_LOCATOR = "fmvSpringLocator";

	@Bean
	FmvProperties fmvProperties() {
		return new FmvProperties();
	}

	@Bean(BEAN_SPRING_LOCATOR)
	SpringLocator springLocator(FmvProperties properties) {
		SpringLocator springLocator = new SpringLocator(properties);
		SoftLocator.addDefaultLocator(springLocator);

		// properties
		SearchPropertiesHelper.setGlobalProperties(toGlobalProperties(properties));

		return springLocator;
	}

	// **********************************************************

	private static Properties toGlobalProperties(FmvProperties fmvProperties) {
		Properties properties = new Properties();
		for(Entry<String, Map<String, SoftProperties>> groupEntry : fmvProperties.getSoft().entrySet()) {
			for(Entry<String, SoftProperties> softEntry : groupEntry.getValue().entrySet()) {

				String prefix = FmvProperties.getDefaultPropertyKeys(groupEntry.getKey(), softEntry.getKey());
				SearchProperties search = softEntry.getValue().getSearch();
				String versionPattern = search.getVersionPattern();
				if(versionPattern != null) {
					properties.put(prefix + ".search.versionPattern", versionPattern);
				}
				String datePattern = search.getDatePattern();
				if(datePattern != null) {
					properties.put(prefix + ".search.datePattern", datePattern);
				}
			}
		}
		return properties;
	}
}
