package org.fagu.fmv.ffmpeg.executor.fallback;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.IOException;

import org.fagu.fmv.ffmpeg.executor.FFEnv;
import org.fagu.fmv.ffmpeg.executor.FFExecFallback;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.impl.Scale;
import org.fagu.fmv.ffmpeg.filter.impl.ScaleMode;
import org.fagu.fmv.ffmpeg.operation.AbstractOperation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class Libx264NotDisibleBy2FFExecFallback implements FFExecFallback {

	/**
	 * @see org.fagu.fmv.ffmpeg.executor.FFExecFallback#prepare(org.fagu.fmv.ffmpeg.executor.FFEnv, java.io.IOException)
	 */
	@Override
	public boolean prepare(FFEnv ffEnv, IOException ioException) throws IOException {
		FFExecutor<Object> executor = ffEnv.getExecutor();
		AbstractOperation<?, ?> operation = (AbstractOperation<?, ?>)ffEnv.getOperation();
		for(String line : executor.getOutputReadLine().getLines()) {
			if(line.startsWith("[libx264 @") && line.contains(" not divisible by 2")) {
				for(Filter filter : operation.getFilters()) {
					if(filter instanceof Scale) {
						Scale scale = (Scale)filter;
						Size size = scale.getSize();
						ScaleMode scaleMode = scale.getScaleMode();
						if(size != null && scaleMode != null) {
							Size newSize = resize(size);
							if(newSize == null) {
								return false;
							}
							scale.set(size, scaleMode);
							return true;
						}
					}
				}
			}
		}
		return false;

	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Libx264 not divisible by 2";
	}

	/**
	 * @param size
	 * @return
	 */
	public static Size resize(final Size size) {
		Size newSize = size;
		if(size.getHeight() % 2 == 1) {
			newSize = Size.valueOf(size.getWidth(), size.getHeight() - 1);
		}
		if(size.getWidth() % 2 == 1) {
			newSize = Size.valueOf(size.getWidth() - 1, size.getHeight());
		}
		return size.fitAndKeepRatioTo(newSize);
	}
}
