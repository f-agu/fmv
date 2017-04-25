package org.fagu.fmv.soft.win32;

import java.io.File;
import java.util.function.Supplier;

import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;


/**
 * @author fagu
 */
public class SoftOnWindows {

	/**
	 * 
	 */
	private SoftOnWindows() {}

	/**
	 * @param softPolicySupplier
	 * @param softNameSupplier
	 * @return
	 */
	public static SoftFoundFactory createSoftFoundFactory(Supplier<SoftPolicy<?, ?, ?>> softPolicySupplier, Supplier<SoftName> softNameSupplier) {
		return (file, locator) -> {
			String versionStr = SoftOnWindows.getExeVersion(file);
			Version version = Version.parse(versionStr);
			SoftPolicy<?, ?, ?> softPolicy = softPolicySupplier.get();
			SoftName softName = softNameSupplier.get();
			return softPolicy.toSoftFound(new VersionSoftInfo(file, softName, version));
		};
	}

	/**
	 * @param file
	 * @return
	 */
	public static String getExeVersion(File file) {
		return WindowsFileVersionInfo.getVersion(file.getAbsolutePath());
	}

}
