package org.fagu.fmv.textprogressbar.part;

/*-
 * #%L
 * fmv-textprogressbar
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Locale;
import java.util.Objects;
import java.util.function.LongSupplier;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;
import org.fagu.fmv.utils.ByteSize;


/**
 * @author fagu
 */
public class ByteSizePart implements Part {

	private final String totalSize;

	private final LongSupplier currentSize;

	private final Locale locale;

	/**
	 * @param currentSize
	 * @param totalSize
	 */
	public ByteSizePart(LongSupplier currentSize, long totalSize, Locale locale) {
		this.currentSize = Objects.requireNonNull(currentSize);
		this.locale = locale;
		this.totalSize = ByteSize.formatSize(totalSize, locale);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		StringBuilder buf = new StringBuilder();
		buf.append(ByteSize.formatSize(currentSize.getAsLong(), locale)).append(" / ").append(totalSize);
		return buf.toString();
	}

}
