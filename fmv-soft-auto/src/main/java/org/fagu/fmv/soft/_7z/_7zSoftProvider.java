package org.fagu.fmv.soft._7z;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class _7zSoftProvider extends SoftProvider {

	public static final String NAME = "7z";

	/**
	 * 
	 */
	public _7zSoftProvider() {
		this(null);
	}

	/**
	 * @param softPolicy
	 */
	public _7zSoftProvider(SoftPolicy softPolicy) {
		super(NAME, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(16))));
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory(java.util.Properties)
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		// 7-Zip [64] 16.00 : Copyright (c) 1999-2016 Igor Pavlov : 2016-05-10
		final Pattern pattern = Pattern.compile("7-Zip \\[\\d+\\] (\\d+\\.\\d+) \\:.*");
		return prepareSoftFoundFactory()
				.withParameters("-version", "-h")
				.parseVersion(line -> {
					Matcher matcher = pattern.matcher(line);
					if(matcher.matches()) {
						return VersionParserManager.parse(matcher.group(1));
					}
					return null;
				})
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftLocator()
	 */
	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.findFolder("7-Zip")
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "http://www.7-zip.org/download.html";
	}

}
