package org.fagu.version.spring;

import org.fagu.version.Version;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;


/**
 * @author f.agu
 */
@ConfigurationPropertiesBinding
public class VersionToStringConverter implements Converter<Version, String> {

	@Override
	public String convert(Version source) {
		return source != null ? source.toString() : "";
	}

}
