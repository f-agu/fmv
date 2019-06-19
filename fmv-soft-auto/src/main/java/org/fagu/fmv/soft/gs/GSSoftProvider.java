package org.fagu.fmv.soft.gs;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.VersionDate;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.gs.exception.GSExceptionKnownAnalyzer;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper.SearchMatching;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class GSSoftProvider extends SoftProvider {

	private static final String PROP_VERSION_PATTERN = "soft.gs.search.versionPattern";

	private static final String PROP_DATE_PATTERN = "soft.gs.search.datePattern";

	private static final String DEFAULT_PATTERN_VERSION = "GPL Ghostscript ([0-9\\.\\-]+) (.*)";

	private static final String DEFAULT_PATTERN_DATE = "\\(([0-9\\-]+)\\)";

	public static final String NAME = "gs";

	public GSSoftProvider() {
		this(null);
	}

	public GSSoftProvider(SoftPolicy softPolicy) {
		super(NAME, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(9, 15))));
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		SearchMatching searchMatching = new SearchPropertiesHelper(searchProperties, getName())
				.forMatching(DEFAULT_PATTERN_VERSION, PROP_VERSION_PATTERN);
		return prepareSoftFoundFactory()
				.withParameters("-version", "-q")
				.parseVersionDate(line -> searchMatching.ifMatches(line, matcher -> {
					Version version = VersionParserManager.parse(matcher.group(1));
					Date date = null;
					if(matcher.groupCount() > 1) {
						date = parseDate(searchProperties, matcher.group(2));
					}
					return Optional.of(new VersionDate(version, date));
				})
						.orElse(null))
				.build();
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.find(programFile -> {
						File folder = new File(programFile, "gs");
						if( ! folder.exists()) {
							return Collections.emptyList();
						}
						File[] listFiles = folder.listFiles(f -> f.getName().startsWith("gs"));
						return listFiles != null && listFiles.length > 0 ? Arrays.asList(listFiles).stream()
								.map(f -> new File(f, "bin"))
								.collect(Collectors.toList())
								: Collections.emptyList();
					})
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;
	}

	@Override
	public FileFilter getFileFilter() {
		if(SystemUtils.IS_OS_WINDOWS) {
			return f -> {
				String name = f.getName();
				String baseName = FilenameUtils.getBaseName(name);
				return ("gswin32c".equals(baseName) || "gswin64c".equals(baseName)) && "exe".equalsIgnoreCase(FilenameUtils.getExtension(name));
			};
		}
		return super.getFileFilter();
	}

	@Override
	public String getDownloadURL() {
		return "http://ghostscript.com/download/";
	}

	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return GSExceptionKnownAnalyzer.class;
	}

	// *****************************************************

	private Date parseDate(Properties searchProperties, String remainLine) {
		return new SearchPropertiesHelper(searchProperties, getName())
				.forMatching(DEFAULT_PATTERN_DATE, PROP_DATE_PATTERN)
				.ifMatches(remainLine, matcher -> {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						return Optional.ofNullable(dateFormat.parse(matcher.group(1)));
					} catch(Exception e) {
						// ignore
					}
					return Optional.empty();
				})
				.orElse(null);
	}

}
