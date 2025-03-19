package org.fagu.version.spring;

import org.fagu.version.range.VersionFilter;
import org.fagu.version.range.VersionRange;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;


/**
 * @author f.agu
 */
@ConfigurationPropertiesBinding
public class StringToVersionFilterConverter implements Converter<String, VersionFilter> {

	@Override
	public VersionFilter convert(String source) {
		return VersionRange.parse(source);
	}

}
