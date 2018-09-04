package org.fagu.fmv.soft.gs;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

	/**
	 * 
	 */
	public GSSoftProvider() {
		this(null);
	}

	/**
	 * @param softPolicy
	 */
	public GSSoftProvider(SoftPolicy softPolicy) {
		super(NAME, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(9, 15))));
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory(java.util.Properties)
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		final Pattern versionPattern = Pattern.compile(getPattern(searchProperties, PROP_VERSION_PATTERN, DEFAULT_PATTERN_VERSION));
		return prepareSoftFoundFactory()
				.withParameters("-version", "-q")
				.parseVersionDate(line -> {
					Matcher matcher = versionPattern.matcher(line);
					Version version = null;
					if(matcher.matches()) {
						version = VersionParserManager.parse(matcher.group(1));
						Date date = null;
						System.out.println(matcher.groupCount());
						for(int i = 0; i <= matcher.groupCount(); i++) {
							System.out.println(i + " : " + matcher.group(i));
						}
						if(matcher.groupCount() > 1) {
							date = parseDate(searchProperties, matcher.group(2));
						}
						return new VersionDate(version, date);
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

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getFileFilter()
	 */
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

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "http://ghostscript.com/download/";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getExceptionKnownAnalyzerClass()
	 */
	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return GSExceptionKnownAnalyzer.class;
	}

	// *****************************************************

	private String getPattern(Properties searchProperties, String code, String defaultValue) {
		if(searchProperties == null) {
			return defaultValue;
		}
		return (String)searchProperties.getOrDefault(code, defaultValue);
	}

	private Date parseDate(Properties searchProperties, String remainLine) {
		Pattern pattern = Pattern.compile(getPattern(searchProperties, PROP_DATE_PATTERN, DEFAULT_PATTERN_DATE));
		Matcher matcher = pattern.matcher(remainLine);
		if(matcher.matches()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return dateFormat.parse(matcher.group(1));
			} catch(Exception e) {
				// ignore
			}
		}
		return null;
	}

}
