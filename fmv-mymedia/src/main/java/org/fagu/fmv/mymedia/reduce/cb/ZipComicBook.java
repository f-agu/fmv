package org.fagu.fmv.mymedia.reduce.cb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;


/**
 * @author f.agu
 * @created 23 nov. 2021 18:22:48
 */
public class ZipComicBook implements ComicBook {

	private final File srcFile;

	public ZipComicBook(File srcFile) {
		this.srcFile = Objects.requireNonNull(srcFile);
	}

	@Override
	public String getType() {
		return "CBZ";
	}

	@Override
	public int countEntry() throws IOException {
		int count = 0;
		try (ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(new FileInputStream(srcFile))) {
			ZipArchiveEntry inEntry = null;
			while((inEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
				if(inEntry.isDirectory()) {
					continue;
				}
				++count;
			}
		}
		return count;
	}

	@Override
	public void reduce(Appender appender) throws IOException {
		try (ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(new FileInputStream(srcFile))) {
			ZipArchiveEntry inEntry = null;
			while((inEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
				if(inEntry.isDirectory()) {
					continue;
				}
				ZipArchiveEntry outEntry = new ZipArchiveEntry(inEntry);
				appender.append(outEntry, zipArchiveInputStream);
			}
		}
	}

	@Override
	public void close() throws IOException {}

}
