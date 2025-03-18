package org.fagu.fmv.mymedia.classify.duplicate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedFiles.FileInfosFile;
import org.fagu.fmv.mymedia.utils.ScannerHelper;
import org.fagu.fmv.mymedia.utils.ScannerHelper.YesNo;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 mars 2025 14:57:59
 */
public class KeepOlderDuplicateCleanPolicy implements DuplicateCleanPolicy {

	public KeepOlderDuplicateCleanPolicy() {}

	@Override
	public void clean(List<FileInfosFile> list) {
		if(list.isEmpty()) {
			return;
		}
		NavigableSet<FileInfosFile> set = new TreeSet<>(new MyComparator());
		set.addAll(list);
		set.stream()
				.skip(1)
				.forEach(fif -> {
					if(YesNo.YES.equals(ScannerHelper.yesNo("Delete " + fif.file(), YesNo.YES))) {
						System.out.println(fif.file());
					}
				});
	}

	// ---------------------------------------------------------------

	private static class MyComparator implements Comparator<FileInfosFile> {

		@Override
		public int compare(FileInfosFile o1, FileInfosFile o2) {
			Long t1 = metadataTime(o1);
			Long t2 = metadataTime(o2);
			if(t1 != null && t2 != null) {
				if(t1 < t2) {
					return - 1;
				}
				if(t1 > t2) {
					return 1;
				}
				Long ct1 = fileCreationTime(o1);
				Long ct2 = fileCreationTime(o2);
				if(ct1 < ct2) {
					return - 1;
				}
				if(ct1 > ct2) {
					return 1;
				}
				int c = Long.compare(fileLastModifiedTime(o1), fileLastModifiedTime(o2));
				if(c == 0) {
					return o1.file().getName().compareTo(o2.file().getName());
				}
				return c;
			}
			return 0;
		}

		// ***********************************************

		private Long metadataTime(FileInfosFile fif) {
			Object main = fif.infosFile().getMain();
			return main instanceof Media media ? media.getTime() : null;
		}

		private Long fileCreationTime(FileInfosFile fif) {
			try {
				FileTime creationTime = (FileTime)Files.getAttribute(fif.file().toPath(), "creationTime");
				return creationTime.toMillis();
			} catch(IOException e) {
				// ignore
			}
			return null;
		}

		private long fileLastModifiedTime(FileInfosFile fif) {
			return fif.file().lastModified();
		}
	}

}
