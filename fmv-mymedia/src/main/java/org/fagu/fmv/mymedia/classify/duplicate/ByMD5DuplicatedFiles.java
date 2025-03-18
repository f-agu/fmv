package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.utils.file.FileFinder.FileFound;
import org.fagu.fmv.utils.file.FileFinder.InfosFile;
import org.fagu.fmv.utils.file.MD5Sum;


/**
 * @author f.agu
 */
public class ByMD5DuplicatedFiles extends AbstractDuplicatedFiles<String> {

	private final Map<String, List<FileInfosFile>> byMD5s = new HashMap<>();

	public ByMD5DuplicatedFiles(Logger logger) {
		super(logger,
				"content (MD5)",
				(md5, infosFiles) -> md5 + ": " + infosFiles.size() + " files",
				(md5, infosFiles) -> infosFiles.stream().map(inff -> inff.file().getName()).collect(Collectors.joining(", ")));
	}

	@Override
	public void populate(FileFound fileFound, @SuppressWarnings("rawtypes") InfosFile infosFile) {
		@SuppressWarnings("unchecked")
		Optional<MD5Sum> opt = infosFile.getInfo(MD5Sum.class);
		opt.ifPresent(md5 -> byMD5s.computeIfAbsent(md5.value(), k -> new ArrayList<>())
				.add(new FileInfosFile(fileFound.getFileFound(), infosFile)));
	}

	@Override
	public Map<String, List<FileInfosFile>> getMap() {
		return byMD5s;
	}

}
