package org.fagu.fmv.ffmpeg.progressbar;

/*-
 * #%L
 * fmv-ffmpeg
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

import java.util.function.IntSupplier;

import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.part.ETAPart;


/**
 * @author fagu
 */
public class ByFrame extends By {

	// -----------------------------------------------------

	public static class ByFrameBuilder extends ByBuilder {

		private final int numberOfFrames;

		ByFrameBuilder(Progress progress, int numberOfFrames) {
			super(progress);
			this.numberOfFrames = numberOfFrames;
		}

		public ByFrameBuilder fileSize(long size) {
			this.fileSize = size;
			return this;
		}

		public ByFrame build() {
			return new ByFrame(this);
		}

	}

	// -----------------------------------------------------

	private final int numberOfFrames;

	ByFrame(ByFrameBuilder builder) {
		super(builder);
		this.numberOfFrames = builder.numberOfFrames;
	}

	@Override
	public IntSupplier progressInPercent() {
		return () -> 100 * progress.getFrame() / numberOfFrames;
	}

	@Override
	public Part etaPart() {
		long startTime = System.currentTimeMillis();
		return new ETAPart(status -> {
			int currentFrame = progress.getFrame();
			if(currentFrame > 0) {
				long diff = System.currentTimeMillis() - startTime;
				long milliseconds = numberOfFrames * diff / currentFrame - diff;
				return (int)(milliseconds / 1000L);
			}
			return null;
		});
	}
}
