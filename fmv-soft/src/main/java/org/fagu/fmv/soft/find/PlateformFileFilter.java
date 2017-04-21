package org.fagu.fmv.soft.find;

import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;


/**
 * @author fagu
 */
public class PlateformFileFilter {

	/**
	 * 
	 */
	private PlateformFileFilter() {}

	/**
	 * @param softName
	 * @return
	 */
	public static FileFilter getFileFilter(String softName) {
		FileFilter fileFilter = f -> softName.equalsIgnoreCase(FilenameUtils.getBaseName(f.getName()));
		if(SystemUtils.IS_OS_WINDOWS) {
			Set<String> defaultExtensions = new HashSet<>(4);
			defaultExtensions.add("exe");
			defaultExtensions.add("com");
			defaultExtensions.add("cmd");
			defaultExtensions.add("bat");
			return f -> {
				if( ! fileFilter.accept(f)) {
					return false;
				}
				String extension = FilenameUtils.getExtension(f.getName());
				return extension != null ? defaultExtensions.contains(extension.toLowerCase()) : false;
			};
		}
		return fileFilter;
	}

}
