package org.fagu.version.spring;

import org.fagu.version.Version;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;


/**
 * @author f.agu
 */
@ConfigurationPropertiesBinding
public class StringToVersionConverter implements Converter<String, Version> {

	@Override
	public Version convert(String source) {
		return Version.parse(source);
	}

}
