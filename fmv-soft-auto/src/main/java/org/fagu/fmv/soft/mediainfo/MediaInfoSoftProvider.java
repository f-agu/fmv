package org.fagu.fmv.soft.mediainfo;

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
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class MediaInfoSoftProvider extends SoftProvider {

	public static final String NAME = "mediainfo";

	/**
	 * 
	 */
	public MediaInfoSoftProvider() {
		this(null);
	}

	/**
	 * @param softPolicy
	 */
	public MediaInfoSoftProvider(SoftPolicy softPolicy) {
		super(NAME,
				ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy().onAllPlatforms(minVersion(18))));
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory(java.util.Properties)
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		final Pattern pattern = Pattern.compile("MediaInfoLib \\- v([0-9\\.\\-]+)");
		return prepareSoftFoundFactory()
				.withParameters("--Version")
				.parseVersion(line -> {
					Matcher matcher = pattern.matcher(line);
					if(matcher.matches()) {
						return new Version(VersionParserManager.parse(matcher.group(1)));
					}
					return null;
				})
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "https://mediaarea.net/fr/MediaInfo/Download";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftLocator()
	 */
	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.findFolder("MediaInfo")
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;
	}

}
