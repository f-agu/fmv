package org.fagu.fmv.ffmpeg.ioe;

import java.util.Optional;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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

import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public class IntMediaInput extends AbstractIOEntity<IntMediaInput> implements MediaInput {

	private final int i;

	public IntMediaInput(int i) {
		super(Integer.toString(i));
		this.i = i;
	}

	@Override
	public Optional<Duration> getDuration() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return Integer.toString(i);
	}
}
