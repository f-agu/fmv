package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedFiles.FileInfosFile;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 mars 2025 14:57:04
 */
public interface DuplicateCleanPolicy {

	default void clean(DuplicatedFiles<?> duplicatedFiles, Map<Object, List<FileInfosFile>> map) {
		for(Entry<Object, List<FileInfosFile>> entry : map.entrySet()) {
			System.out.println("Cleaning with " + duplicatedFiles.getClass().getSimpleName() + " for " + entry.getKey() + "...");
			clean(duplicatedFiles, entry.getValue());
		}
	}

	void clean(DuplicatedFiles<?> duplicatedFiles, List<FileInfosFile> list);

}
