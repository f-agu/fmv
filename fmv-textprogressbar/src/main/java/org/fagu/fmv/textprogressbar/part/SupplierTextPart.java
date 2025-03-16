package org.fagu.fmv.textprogressbar.part;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class SupplierTextPart implements Part {

	private String text;

	public SupplierTextPart() {
		setText(null);
	}

	public SupplierTextPart(String text) {
		setText(text);
	}

	@Override
	public String getWith(ProgressStatus status) {
		return text;
	}

	public void setText(String text) {
		this.text = StringUtils.defaultString(text);
	}

}
