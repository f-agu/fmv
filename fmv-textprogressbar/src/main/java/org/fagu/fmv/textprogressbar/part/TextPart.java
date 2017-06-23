package org.fagu.fmv.textprogressbar.part;

import java.util.Objects;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class TextPart implements Part {

	private final String text;

	/**
	 * @param text
	 */
	public TextPart(String text) {
		this.text = Objects.requireNonNull(text);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		return text;
	}

}
