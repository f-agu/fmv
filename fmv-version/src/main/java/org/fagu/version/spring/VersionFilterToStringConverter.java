package org.fagu.version.spring;

import org.fagu.version.range.VersionFilter;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;


/**
 * @author f.agu
 */
@ConfigurationPropertiesBinding
public class VersionFilterToStringConverter implements Converter<VersionFilter, String> {

	@Override
	public String convert(VersionFilter source) {
		return source != null ? source.toString() : "";
	}

}
