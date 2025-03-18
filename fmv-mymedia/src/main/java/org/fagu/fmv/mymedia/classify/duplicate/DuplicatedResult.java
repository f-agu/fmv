package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.List;
import java.util.Objects;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 mars 2025 15:03:04
 */
public class DuplicatedResult {

	private final List<DuplicatedFiles<?>> list;

	public DuplicatedResult(List<DuplicatedFiles<?>> list) {
		this.list = Objects.requireNonNull(list);
	}

	public boolean haveDuplicates() {
		return list.stream().anyMatch(df -> ! df.getDuplicateds().isEmpty());
	}

	public List<DuplicatedFiles<?>> getList() {
		return list;
	}

}
