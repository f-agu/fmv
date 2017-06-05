package org.fagu.fmv.soft.mplayer;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
/**
 * @author Oodrive
 * @author f.agu
 * @created 5 juin 2017 09:57:00
 */
public abstract class MSoftProvider extends SoftProvider {

	/**
	 * @param name
	 */
	public MSoftProvider(String name) {
		super(name);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		Pattern winPattern = Pattern.compile(getName() + " sherpya-r(\\d+)+.*-[\\d\\.]+ \\(C\\).*", Pattern.CASE_INSENSITIVE);
		return prepareSoftFoundFactory()
				.withoutParameter()
				.parseVersion(line -> {
					Matcher matcher = winPattern.matcher(line);
					return matcher.matches() ? VersionParserManager.parse(matcher.group(1)) : null;
				})
				.exitValue(MEncoderSoftProvider.NAME.equals(getName()) ? 1 : 0)
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
						File[] mFolders = programFile.listFiles(f -> f.getName().toLowerCase().startsWith("mplayer"));
						return mFolders != null ? Arrays.asList(mFolders) : Collections.emptyList();
					})
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
		if(SystemUtils.IS_OS_WINDOWS) {
			return "http://oss.netfarm.it/mplayer/";
		}
		return "http://www.mplayerhq.hu/design7/dload.html";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftPolicy()
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy()
				.onWindows().minVersion(new Version(37905))
				.onLinux().minVersion(new Version(1, 3));
	}

}
