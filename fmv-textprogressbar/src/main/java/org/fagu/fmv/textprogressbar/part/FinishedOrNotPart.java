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

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class FinishedOrNotPart implements Part {

	private final Part notFinishedPart;

	private final Part finishedPart;

	public FinishedOrNotPart(Part notFinishedPart, Part finishedPart) {
		this.notFinishedPart = Objects.requireNonNull(notFinishedPart);
		this.finishedPart = Objects.requireNonNull(finishedPart);
	}

	@Override
	public String getWith(ProgressStatus status) {
		return status.isFinished() ? finishedPart.getWith(status) : notFinishedPart.getWith(status);
	}

}
