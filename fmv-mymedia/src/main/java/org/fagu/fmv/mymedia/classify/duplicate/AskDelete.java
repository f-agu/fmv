package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedFiles.FileInfosFile;
import org.fagu.fmv.mymedia.utils.ScannerHelper;
import org.fagu.fmv.mymedia.utils.ScannerHelper.Answer;
import org.fagu.fmv.mymedia.utils.ScannerHelper.YesNo;


/**
 * @author Oodrive
 * @author f.agu
 * @created 19 mars 2025 09:58:51
 */
public interface AskDelete {

	Answer ask(DuplicatedFiles<?> duplicatedFiles, FileInfosFile fileInfosFile);

	static AskDelete yesNoAlways() {
		Map<DuplicatedFiles<?>, AtomicReference<Answer>> lasts = new HashMap<>();
		return (duplicatedFiles, fif) -> {
			AtomicReference<Answer> last = lasts.computeIfAbsent(duplicatedFiles, k -> new AtomicReference<>());
			if(YesNoAlways.YES_ALWAYS.equals(last.get())) {
				return YesNoAlways.YES;
			}
			if(YesNoAlways.NO_ALWAYS.equals(last.get())) {
				return YesNoAlways.NO;
			}
			List<Answer> answers = new ArrayList<>(4);
			answers.addAll(Arrays.asList(YesNo.values()));
			answers.addAll(Arrays.asList(YesNoAlways.values()));
			Answer answer = ScannerHelper.ask("Delete " + fif.fileFound().getFileFound(), Arrays.asList(YesNoAlways.values()), YesNoAlways.YES);
			last.set(answer);
			return answer;
		};
	}
}
