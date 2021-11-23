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

import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class ETAPart implements Part {

	private final Function<ProgressStatus, Integer> etaInSecondsSupplier;

	public ETAPart(Function<ProgressStatus, Integer> etaInSecondsSupplier) {
		this.etaInSecondsSupplier = Objects.requireNonNull(etaInSecondsSupplier);
	}

	@Override
	public String getWith(ProgressStatus status) {
		Integer etaInSeconds = etaInSecondsSupplier.apply(status);
		if(etaInSeconds != null && etaInSeconds > 0) {
			int seconds = etaInSeconds;
			StringBuilder buf = new StringBuilder();
			buf.append(StringUtils.leftPad(Integer.toString(seconds / 60), 2, '0')).append(':');
			buf.append(StringUtils.leftPad(Integer.toString(seconds % 60), 2, '0')).append(" ETA");
			return buf.toString();
		}
		return StringUtils.EMPTY;
	}

}
