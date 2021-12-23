package org.fagu.fmv.soft.utils;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;


/**
 * @author Oodrive
 * @author f.agu
 * @created 23 déc. 2021 14:37:47
 */
public class FileUtils {

	private FileUtils() {}

	public static boolean exists(File file) {
		if(SystemUtils.IS_OS_WINDOWS) {
			if(file == null) {
				return false;
			}
			return file.exists() || file.getPath().contains("Local\\Microsoft\\WindowsApps");
		}
		return file != null && file.exists();
	}

}
