package org.fagu.fmv.soft.spring.config;

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
