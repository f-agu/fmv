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

	default void clean(Map<Object, List<FileInfosFile>> map) {
		for(Entry<Object, List<FileInfosFile>> entry : map.entrySet()) {
			System.out.println("Cleaning for " + entry.getKey() + "...");
			clean(entry.getValue());
		}
	}

	void clean(List<FileInfosFile> list);

}
