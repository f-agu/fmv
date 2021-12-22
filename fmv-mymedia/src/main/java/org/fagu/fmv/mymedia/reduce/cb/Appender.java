package org.fagu.fmv.mymedia.reduce.cb;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;


/**
 * @author f.agu
 * @created 23 nov. 2021 18:24:02
 */
public interface Appender {

	void append(ZipArchiveEntry entry, InputStream inputStream) throws IOException;
}
