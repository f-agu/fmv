package org.fagu.fmv.mymedia.file.filter;

import java.io.File;
import java.io.FileFilter;

import org.fagu.fmv.utils.IniFile;


/**
 * @author Utilisateur
 * @created 2 juin 2018 14:54:00
 */
public class ExcludeFMVFilefilter implements FileFilter {

	public static final ExcludeFMVFilefilter INSTANCE = new ExcludeFMVFilefilter();

	private final FileFilter iniFileFilter;

	public ExcludeFMVFilefilter() {
		iniFileFilter = f -> true;
	}

	public ExcludeFMVFilefilter(IniFile iniFile) {
		iniFileFilter = f -> ! iniFile.contains("exclude", f.getName());
	}

	@Override
	public boolean accept(File pathname) {
		return ! pathname.getName().startsWith(".fmv")
				&& iniFileFilter.accept(pathname);
	}

}
