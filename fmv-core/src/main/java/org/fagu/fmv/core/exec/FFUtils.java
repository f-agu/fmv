package org.fagu.fmv.core.exec;

/*
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.io.File;

import org.fagu.fmv.core.project.OutputInfos;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.core.project.Properties;
import org.fagu.fmv.ffmpeg.coder.Libx264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.format.BasicStreamMuxer;
import org.fagu.fmv.ffmpeg.operation.OperationListener;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;


/**
 * @author f.agu
 */
public class FFUtils {

	/**
	 *
	 */
	private FFUtils() {}

	/**
	 * @param project
	 * @return
	 */
	public static FFMPEGExecutorBuilder builder(Project project) {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		if(project.getProperty(Properties.SHOW_COMMAND_LINE)) {
			builder.addListener(new OperationListener() {

				/**
				 * @see org.fagu.fmv.ffmpeg.operation.OperationListenerAdaptor#eventCreate(org.fagu.fmv.ffmpeg.executor.FFExecutor)
				 */
				@Override
				public void eventCreate(FFExecutor<?> ffExecutor) {
					try {
						System.out.println(ffExecutor.getCommandLine());
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return builder;
	}

	/**
	 * @param builder
	 * @param outfile
	 * @param outputInfos
	 * @return
	 */
	public static OutputProcessor outputProcessor(FFMPEGExecutorBuilder builder, File outfile, OutputInfos outputInfos) {
		BasicStreamMuxer muxer = BasicStreamMuxer.to(outfile, outputInfos.getFormat());
		return outputProcessor(builder.mux(muxer), outputInfos);
	}

	/**
	 * @param outputProcessor
	 * @param outputInfos
	 * @return
	 */
	public static OutputProcessor outputProcessor(OutputProcessor outputProcessor, OutputInfos outputInfos) {
		outputProcessor.codec(Libx264.build().mostCompatible());
		outputProcessor.qualityScaleAudio(0);
		outputProcessor.qualityScaleVideo(0);
		outputProcessor.overwrite();
		return outputProcessor;
	}

}
