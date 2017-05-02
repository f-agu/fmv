package org.fagu.fmv.soft.java;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class JavaSoftProvider extends SoftProvider {

	public static final String NAME = "java";

	/**
	 * 
	 */
	public JavaSoftProvider() {
		super(NAME);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		final Pattern pattern = Pattern.compile("(.*) version \"(.*)\"");
		return prepareSoftFoundFactory()
				.withParameters("-version")
				.parseVersion(line -> {
					Matcher matcher = pattern.matcher(line);
					return matcher.matches() ? VersionParserManager.parse(matcher.group(2)) : null;
				})
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftLocator()
	 */
	@Override
	public SoftLocator getSoftLocator() {
		return new SoftLocator(getName()) {

			/**
			 * @see org.fagu.fmv.soft.find.SoftLocator#getLocators(java.lang.String, org.fagu.fmv.soft.find.Locators,
			 *      java.io.FileFilter)
			 */
			@Override
			protected List<Locator> getLocators(Locators loc) {
				List<Locator> list = super.getLocators(loc);
				list.add(0, createLocators().byPropertyPath("java.home"));
				return list;
			}
		};
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "https://www.java.com/download/";
	}

	/**
	 * @return
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy()
				.onAllPlatforms().minVersion(new Version(1, 1));
	}

}
