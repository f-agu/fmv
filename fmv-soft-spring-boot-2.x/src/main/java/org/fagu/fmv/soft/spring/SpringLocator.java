package org.fagu.fmv.soft.spring;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.spring.config.FmvProperties;
import org.fagu.fmv.soft.spring.config.FmvProperties.Soft;


/**
 * @author f.agu
 * @created 7 août 2020 11:32:23
 */
public class SpringLocator implements Locator {

	private final FmvProperties properties;

	public SpringLocator(FmvProperties properties) {
		this.properties = Objects.requireNonNull(properties);
	}

	@Override
	public List<File> locate(String softName) {
		Soft soft = properties.getSoft().get(softName);
		if(soft == null) {
			return Collections.emptyList();
		}
		String path = soft.getPath();
		if(StringUtils.isBlank(path)) {
			return Collections.emptyList();
		}
		return Collections.singletonList(new File(path));
	}

}
