package org.fagu.version.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author f.agu
 */
@Configuration
public class FMVVersionConfiguration {

	@Bean
	StringToVersionConverter stringToVersionConverter() {
		return new StringToVersionConverter();
	}

	@Bean
	VersionToStringConverter versionToStringConverter() {
		return new VersionToStringConverter();
	}

	@Bean
	StringToVersionFilterConverter stringToVersionFilterConverter() {
		return new StringToVersionFilterConverter();
	}

	@Bean
	VersionFilterToStringConverter versionFilterToStringConverter() {
		return new VersionFilterToStringConverter();
	}

}
