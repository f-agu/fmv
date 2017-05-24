package org.fagu.fmv.soft.gs;

import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.VersionDate;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.fmv.soft.gs.exception.GSExceptionKnownAnalyzer;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class GSSoftProvider extends SoftProvider {

	public static final String NAME = "gs";

	/**
	 * 
	 */
	public GSSoftProvider() {
		super(NAME);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		final Pattern pattern = Pattern.compile("GPL Ghostscript ([0-9\\.\\-]+) \\(([0-9\\-]+)\\)");
		return prepareSoftFoundFactory()
				.withParameters("-version", "-q")
				.parseVersionDate(line -> {
					Matcher matcher = pattern.matcher(line);
					Version version = null;
					Date date = null;
					if(matcher.matches()) {
						version = VersionParserManager.parse(matcher.group(1));
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						date = dateFormat.parse(matcher.group(2));
					} catch(Exception e) {
						// ignore
					}
					return new VersionDate(version, date);
				})
				.build();
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
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftPolicy()
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy()
				.onAllPlatforms().minVersion(new Version(9, 15));
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getExceptionKnownAnalyzerClass()
	 */
	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return GSExceptionKnownAnalyzer.class;
	}

}
