package org.fagu.fmv.soft.gpac;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.io.File;
import java.util.Collections;
import java.util.Objects;
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
public abstract class GPACSoftProvider extends SoftProvider {

	private final String foundParameter;

	public GPACSoftProvider(String name, SoftPolicy softPolicy, String foundParameter) {
		super(name, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(0, 7))));
		this.foundParameter = Objects.requireNonNull(foundParameter);
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		// GPAC version 0.7.2-DEV-rev1143-g1c540fec-master
		final Pattern pattern = Pattern.compile(".*GPAC version ([\\d+\\.]+).*");
		return prepareSoftFoundFactory()
				.withParameters(foundParameter)
				.parseVersion(line -> {
					Matcher matcher = pattern.matcher(line);
					if(matcher.matches()) {
						return VersionParserManager.parse(matcher.group(1));
					}
					return null;
				})
				.build();
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.find(programFile -> {
						File gpacFolder = new File(programFile, "GPAC");
						return gpacFolder.exists() ? Collections.singleton(gpacFolder) : Collections.emptyList();
					})
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;

	}

	@Override
	public String getDownloadURL() {
		return "https://gpac.wp.imt.fr/downloads/gpac-nightly-builds/";
	}

}
