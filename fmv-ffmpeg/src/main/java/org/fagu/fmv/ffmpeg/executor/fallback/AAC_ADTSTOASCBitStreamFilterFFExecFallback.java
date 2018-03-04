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

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.fagu.fmv.ffmpeg.executor.FFEnv;
import org.fagu.fmv.ffmpeg.executor.FFExecFallback;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.BitStreamFilter;


/**
 * @author f.agu
 */
public class AAC_ADTSTOASCBitStreamFilterFFExecFallback implements FFExecFallback {

	/**
	 * @see org.fagu.fmv.ffmpeg.executor.FFExecFallback#prepare(java.io.IOException)
	 */
	@Override
	public boolean prepare(FFEnv ffEnv, IOException ioException) throws IOException {
		FFExecutor<Object> executor = ffEnv.getExecutor();
		MutableBoolean change = new MutableBoolean(false);
		ffEnv.getOperation().getOutputProcessorStream().forEach(outputProcessor -> {
			for(String line : executor.getOutputReadLine().getLines()) {
				if(line.contains("'aac_adtstoasc' to fix it ('-bsf:a aac_adtstoasc'")) {
					outputProcessor.bitStream(Type.AUDIO, BitStreamFilter.AAC_ADTSTOASC);
					change.setTrue();
				}
			}
		});
		return change.booleanValue();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BitStream aac_adtstoasc";
	}

}
