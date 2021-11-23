package org.fagu.fmv.mymedia.reduce.cb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft._7z._7z;
import org.fagu.fmv.utils.file.FileUtils;


/**
 * @author f.agu
 * @created 23 nov. 2021 18:36:59
 */
public class Rar5ComicBook implements ComicBook {

	private final File srcFile;

	private final File tmpFolder;

	private final Soft soft7z;

	public Rar5ComicBook(File srcFile) throws IOException {
		this.srcFile = Objects.requireNonNull(srcFile);
		this.soft7z = _7z.search();
		this.tmpFolder = FileUtils.getTempFolder(srcFile.getName(), null, srcFile.getParentFile());
		extractAll();
	}

	@Override
	public String getType() {
		return "CBR5";
	}

	@Override
	public int countEntry() throws IOException {
		return (int)Files.walk(tmpFolder.toPath())
				.parallel()
				.filter(p -> ! p.toFile().isDirectory())
				.count();
	}

	@Override
	public void reduce(Appender appender) throws IOException {
		Path rootPath = tmpFolder.toPath();
		Files.walk(rootPath)
				.filter(p -> ! p.toFile().isDirectory())
				.forEach(p -> {
					Path relPath = p.subpath(rootPath.getNameCount(), p.getNameCount());
					File file = p.toFile();
					ZipArchiveEntry zipArchiveEntry = toZipArchiveEntry(file, relPath.toString());
					try (InputStream inputStream = new FileInputStream(file)) {
						appender.append(zipArchiveEntry, inputStream);
					} catch(IOException e) {
						throw new UncheckedIOException(e);
					}
				});
	}

	@Override
	public void close() throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(tmpFolder);
	}

	// ******************************************************

	private void extractAll() throws IOException {
		soft7z.withParameters("x", "-o" + tmpFolder.getAbsolutePath(), srcFile.getAbsolutePath())
				// .logCommandLine(cmdLine -> System.out.println(cmdLine))
				.execute();
	}

	private ZipArchiveEntry toZipArchiveEntry(File file, String relPath) {
		ZipArchiveEntry entry = new ZipArchiveEntry(relPath);
		entry.setSize(file.length());
		return entry;
	}
}
