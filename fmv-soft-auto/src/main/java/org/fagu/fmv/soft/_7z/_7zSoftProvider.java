package org.fagu.fmv.soft._7z;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class _7zSoftProvider extends SoftProvider {

	public static final String NAME = "7z";

	public _7zSoftProvider() {
		this(null);
	}

	public _7zSoftProvider(SoftPolicy softPolicy) {
		super(NAME, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(16))));
	}

	@Override
	public Optional<String> getGroupTitle() {
		return Optional.of("7 Zip");
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		return prepareSoftFoundFactory()
				.withParameters("-version", "-h")
				.parseFactory((file, softPolicy) -> createParser(file))
				.build();
	}

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

	@Override
	public String getDownloadURL() {
		return "http://www.7-zip.org/download.html";
	}

	// ***********************************************************************

	Parser createParser(File file) {
		return new Parser() {

			private Pattern pattern = Pattern.compile("7-Zip (?:\\[\\d+\\] )?(\\d+\\.\\d+).*");

			private Version version;

			private boolean found;

			@Override
			public void readLine(String line) {
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()) {
					found = true;
					version = VersionParserManager.parse(matcher.group(1));
				}
			}

			@Override
			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				return found ? SoftFound.found(new VersionSoftInfo(file, getName(), version)) : null;
			}

		};
	}
}
