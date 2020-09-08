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

import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.spring.SpringLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author f.agu
 * @created 7 août 2020 11:37:41
 */
@Configuration
public class FmvConfig {

	@Bean
	FmvProperties fmvProperties() {
		return new FmvProperties();
	}

	@Bean
	SpringLocator springLocator(FmvProperties properties) {
		SpringLocator springLocator = new SpringLocator(properties);
		SoftLocator.addDefaultLocator(springLocator);
		return springLocator;
	}
}
