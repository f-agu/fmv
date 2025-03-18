package org.fagu.fmv.mymedia.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author f.agu
 */
public interface InfoFile {

	public record Line(char code, String value) {}

	List<Character> getCodes();

	boolean isMine(Object object);

	Optional<Info> toInfo(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException;

	Object parse(File file, String line) throws IOException;

	// ------------------------------------------

	public record Info(Object object, Line line) {}
}
