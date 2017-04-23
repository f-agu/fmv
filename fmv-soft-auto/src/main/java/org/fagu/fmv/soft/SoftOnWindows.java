package org.fagu.fmv.soft;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;


/**
 * @author fagu
 */
public class SoftOnWindows {

	/**
	 * @param softPolicySupplier
	 * @param softNameSupplier
	 * @return
	 */
	public static SoftFoundFactory createSoftFoundFactory(Supplier<SoftPolicy<?, ?, ?>> softPolicySupplier, Supplier<SoftName> softNameSupplier) {
		return file -> {
			String versionStr = SoftOnWindows.getExeVersion(file);
			Version version = Version.parse(versionStr);
			SoftPolicy<?, ?, ?> softPolicy = softPolicySupplier.get();
			SoftName softName = softNameSupplier.get();
			return softPolicy.toSoftFound(new VersionSoftInfo(file, softName, version));
		};
	}

	/**
	 * @param softLocator
	 * @param folderNames
	 */
	public static void addProgramFilesLocator(SoftLocator softLocator, FileFilter fileFilter, Collection<String> folderNames) {
		if( ! SystemUtils.IS_OS_WINDOWS || folderNames.isEmpty()) {
			return;
		}
		Set<String> paths = new HashSet<>(4);
		addIfNotNullAndExists(paths, System.getenv("ProgramFiles"));
		addIfNotNullAndExists(paths, System.getenv("ProgramFiles(x86)"));
		addIfNotNullAndExists(paths, System.getenv("ProgramW6432"));

		for(String programFilePath : paths) {
			for(String folderName : folderNames) {
				Locators locators = new Locators(fileFilter);
				softLocator.addLocator(locators.byPath(programFilePath + File.separator + folderName));
			}
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public static String getExeVersion(File file) {
		return WindowsFileVersionInfo.getVersion(file.getAbsolutePath());
	}

	// **********************************************

	/**
	 * @param paths
	 * @param strToAdd
	 */
	private static void addIfNotNullAndExists(Set<String> paths, String strToAdd) {
		if(strToAdd != null && Files.exists(Paths.get(strToAdd))) {
			paths.add(strToAdd);
		}
	}
}
