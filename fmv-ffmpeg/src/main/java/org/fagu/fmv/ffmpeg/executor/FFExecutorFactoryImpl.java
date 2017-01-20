package org.fagu.fmv.ffmpeg.executor;

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

import org.fagu.fmv.ffmpeg.operation.Operation;


/**
 * @author f.agu
 */
public class FFExecutorFactoryImpl implements FFExecutorFactory {

	/**
	 * 
	 */
	public FFExecutorFactoryImpl() {}

	/**
	 * @see org.fagu.fmv.ffmpeg.executor.FFExecutorFactory#create(org.fagu.fmv.ffmpeg.operation.Operation,
	 *      org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder)
	 */
	@Override
	public <R> FFExecutor<R> create(Operation<R, ?> operation, FFMPEGExecutorBuilder ffmpegExecutorBuilder) {
		return new FFExecutor<R>(operation, ffmpegExecutorBuilder);
	}
}