package org.fagu.fmv.mymedia.rip.dvd;

/*-
 * #%L
 * fmv-mymedia
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
import java.util.concurrent.atomic.AtomicInteger;

import org.fagu.fmv.ffmpeg.operation.FFMPEGProgressReadLine;


/**
 * @author Oodrive
 * @author f.agu
 * @created 5 juin 2017 16:38:40
 */
public class FFMpegProgress extends FFMPEGProgressReadLine {

	private final AtomicInteger progressEncode;

	private final int numberOfFrames;

	/**
	 * @param progressEncode
	 * @param numberOfFrames
	 */
	public FFMpegProgress(AtomicInteger progressEncode, int numberOfFrames) {
		this.progressEncode = Objects.requireNonNull(progressEncode);
		this.numberOfFrames = numberOfFrames;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.FFMPEGProgressReadLine#read(java.lang.String)
	 */
	@Override
	public void read(String line) {
		super.read(line);
		progressEncode.set(100 * getFrame() / numberOfFrames);
	}

}
