package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.List;
import java.util.Map;

import org.fagu.fmv.utils.file.FileFinder.FileFound;
import org.fagu.fmv.utils.file.FileFinder.InfosFile;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 mars 2025 14:32:43
 */
@SuppressWarnings("rawtypes")
public interface DuplicatedFiles<K> {

	public record FileInfosFile(FileFound fileFound, InfosFile infosFile) {}

	void populate(FileFound fileFound, InfosFile infosFile);

	boolean analyze();

	Map<K, List<FileInfosFile>> getMap();

	Map<K, List<FileInfosFile>> getDuplicateds();
}
