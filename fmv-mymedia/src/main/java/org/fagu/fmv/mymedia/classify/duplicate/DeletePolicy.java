package org.fagu.fmv.mymedia.classify.duplicate;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.logger.Logger;


/**
 * @author Oodrive
 * @author f.agu
 * @created 19 mars 2025 10:21:38
 */
public interface DeletePolicy {

	boolean delete(File file);

	static DeletePolicy delete(Logger logger) {
		return file -> {
			boolean deleted = file.delete();
			logger.log("Delete " + file + " => " + deleted);
			return deleted;
		};
	}

	static DeletePolicy moveTo(Logger logger, File folder) throws IOException {
		if( ! folder.exists() && ! folder.mkdirs()) {
			throw new IOException("Fail to mkdir: " + folder);
		}
		return file -> {
			String name = file.getName();
			File dest = new File(folder, file.getName());
			if(dest.exists()) {
				try {
					dest = File.createTempFile(FilenameUtils.getBaseName(name), '.' + FilenameUtils.getExtension(name), folder);
				} catch(IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch(IOException e) {
					throw new UncheckedIOException(e);
				}
			}
			boolean renameTo = file.renameTo(dest);
			logger.log("Move " + file + " to " + dest + " => " + renameTo);
			return renameTo;
		};
	}

}
