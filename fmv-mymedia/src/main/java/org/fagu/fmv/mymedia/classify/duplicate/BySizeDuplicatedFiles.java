package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.utils.ByteSize;
import org.fagu.fmv.utils.file.FileFinder.FileFound;
import org.fagu.fmv.utils.file.FileFinder.InfosFile;


/**
 * @author f.agu
 */
public class BySizeDuplicatedFiles extends AbstractDuplicatedFiles<Long> {

	private final NavigableMap<Long, List<FileInfosFile>> bySizes = new TreeMap<>();

	public BySizeDuplicatedFiles(Logger logger) {
		super(logger,
				"size",
				(size, infosFiles) -> ByteSize.formatSize(size) + " (" + size + "): " + infosFiles.size() + " files",
				(size, infosFiles) -> infosFiles.stream().map(inff -> inff.file().getName()).collect(Collectors.joining(", ")));
	}

	@Override
	public void populate(FileFound fileFound, @SuppressWarnings("rawtypes") InfosFile infosFile) {
		bySizes.computeIfAbsent(
				fileFound.getFileFound().length(),
				k -> new ArrayList<>())
				.add(new FileInfosFile(fileFound.getFileFound(), infosFile));
	}

	@Override
	public Map<Long, List<FileInfosFile>> getMap() {
		return bySizes;
	}

}
