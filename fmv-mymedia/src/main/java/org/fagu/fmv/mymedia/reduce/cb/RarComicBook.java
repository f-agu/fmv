package org.fagu.fmv.mymedia.reduce.cb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;


/**
 * @author f.agu
 * @created 23 nov. 2021 18:22:48
 */
public class RarComicBook implements ComicBook {

	private final File srcFile;

	public RarComicBook(File srcFile) {
		this.srcFile = Objects.requireNonNull(srcFile);
	}

	@Override
	public String getType() {
		return "CBR";
	}

	@Override
	public int countEntry() throws IOException {
		int count = 0;
		try (Archive archive = new Archive(new FileInputStream(srcFile))) {
			FileHeader fileHeader = null;
			while((fileHeader = archive.nextFileHeader()) != null) {
				if(fileHeader.isDirectory()) {
					continue;
				}
				++count;
			}
			// } catch(UnsupportedRarV5Exception e) {
			// TODO
		} catch(RarException e) {
			throw new IOException(e);
		}
		return count;
	}

	@Override
	public void reduce(Appender appender) throws IOException {
		try (Archive archive = new Archive(new FileInputStream(srcFile))) {
			FileHeader fileHeader = null;
			while((fileHeader = archive.nextFileHeader()) != null) {
				if(fileHeader.isDirectory()) {
					continue;
				}
				ZipArchiveEntry outEntry = toZipArchiveEntry(fileHeader);
				try (InputStream inputStream = archive.getInputStream(fileHeader)) {
					appender.append(outEntry, inputStream);
				}
			}
		} catch(RarException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {}

	// ******************************************************

	private ZipArchiveEntry toZipArchiveEntry(FileHeader fileHeader) {
		ZipArchiveEntry entry = new ZipArchiveEntry(fileHeader.getFileName());
		entry.setCreationTime(FileTime.from(fileHeader.getCTime().toInstant()));
		entry.setSize(fileHeader.getUnpSize());
		return entry;
	}
}
