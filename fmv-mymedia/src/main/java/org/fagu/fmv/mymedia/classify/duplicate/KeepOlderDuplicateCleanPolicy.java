package org.fagu.fmv.mymedia.classify.duplicate;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedFiles.FileInfosFile;
import org.fagu.fmv.mymedia.utils.ScannerHelper.Answer;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 mars 2025 14:57:59
 */
public class KeepOlderDuplicateCleanPolicy implements DuplicateCleanPolicy {

	private final AskDelete askDelete;

	private final DeletePolicy deletePolicy;

	private final FileFinder<?> fileFinder;

	public KeepOlderDuplicateCleanPolicy(AskDelete askDelete, DeletePolicy deletePolicy, FileFinder<?> fileFinder) {
		this.askDelete = Objects.requireNonNull(askDelete);
		this.deletePolicy = Objects.requireNonNull(deletePolicy);
		this.fileFinder = Objects.requireNonNull(fileFinder);
	}

	@Override
	public void clean(DuplicatedFiles<?> duplicatedFiles, List<FileInfosFile> list) {
		if(list.isEmpty()) {
			return;
		}
		List<File> tmpFolders = new ArrayList<>();
		Runnable cleanTmpFolders = () -> tmpFolders.forEach(t -> {
			try {
				FileUtils.deleteDirectory(t);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
		try {
			NavigableSet<FileInfosFile> set = new TreeSet<>(new MyComparator());
			set.addAll(list);
			set.stream()
					.skip(1)
					.filter(fif -> fif.fileFound().getFileFound().exists())
					.forEach(fif -> {
						Answer answer = null;
						try {
							for(;;) {
								answer = askDelete.ask(duplicatedFiles, fif);
								if(answer == YesNoAlways.COMPARE) {
									tmpFolders.add(compare(list));
								} else if(YesNoAlways.YES.equals(answer)) {
									if(deletePolicy.delete(fif.fileFound().getFileFound())) {
										fileFinder.remove(fif.fileFound());
									}
									return;
								} else {
									return;
								}
							}
						} finally {
							cleanTmpFolders.run();
						}
					});
		} finally {
			cleanTmpFolders.run();
		}
	}

	// **************************************************************

	private File compare(List<FileInfosFile> list) {
		File rootFolder = list.get(0).fileFound().getRootFolder();
		try {
			File tmpFolder = Files.createTempDirectory(rootFolder.toPath(), "compare-").toFile();
			for(FileInfosFile fif : list) {
				File src = fif.fileFound().getFileFound();
				String name = src.getName();
				File dest = new File(tmpFolder, name);
				if(dest.exists()) {
					dest = File.createTempFile(FilenameUtils.getBaseName(name), '.' + FilenameUtils.getExtension(name), tmpFolder);
				}
				FileUtils.copyFile(src, dest);
			}

			try {
				ProcessBuilder processBuilder = new ProcessBuilder(List.of("explorer.exe", tmpFolder.toString()));
				processBuilder.start();
			} catch(IOException e) {
				e.printStackTrace();
			}

			return tmpFolder;
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
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
				if(ct1 != null && ct2 != null) {
					if(ct1 < ct2) {
						return - 1;
					}
					if(ct1 > ct2) {
						return 1;
					}
				}
				int c = Long.compare(fileLastModifiedTime(o1), fileLastModifiedTime(o2));
				if(c == 0) {
					return o1.fileFound().getFileFound().getName().compareTo(o2.fileFound().getFileFound().getName());
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
				FileTime creationTime = (FileTime)Files.getAttribute(fif.fileFound().getFileFound().toPath(), "creationTime");
				return creationTime.toMillis();
			} catch(IOException e) {
				// ignore
			}
			return null;
		}

		private long fileLastModifiedTime(FileInfosFile fif) {
			return fif.fileFound().getFileFound().lastModified();
		}
	}

}
