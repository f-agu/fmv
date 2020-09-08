/**
 * 
 */
package org.fagu.fmv.mymedia.movie;

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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.FFMpegUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.ResampleAudio;
import org.fagu.fmv.ffmpeg.filter.impl.Transpose;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.reduce.FFReducer;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author fagu
 *
 */
public class Rotate {

	private static final Size MAX_SIZE = Size.HD1080;

	private static final int DEFAULT_AUDIO_SAMPLE_RATE = 44100;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File srcFile = new File("D:\\tmp\\Auvergne\\phone-vv\\VID_20170813_183349.mp4");
		Rotation rotation = Rotation.R_90;
		File dest = new File(srcFile.getParentFile(), FilenameUtils.getBaseName(srcFile.getName()) + "-rotate-" + rotation + ".mp4");

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.hideBanner();

		MovieMetadatas infos = builder.addMediaInputFile(srcFile).getMovieMetadatas();

		int audioFrequency = FFMpegUtils.minAudioSampleRate(infos, DEFAULT_AUDIO_SAMPLE_RATE);

		if(rotation != null && rotation != Rotation.R_0) {
			builder.filter(org.fagu.fmv.ffmpeg.filter.impl.Rotate.create(rotation));
		}
		Logger logger = Loggers.systemOut();
		Size newSize = FFReducer.applyScaleIfNecessary(builder, infos, MAX_SIZE, logger, rotation);
		logger.log((newSize.isLandscape() ? "landscape" : newSize.isPortrait() ? "portrait" : "square"));

		builder.filter(ResampleAudio.build().frequency(audioFrequency));

		OutputProcessor outputProcessor = builder.addMediaOutputFile(dest);
		outputProcessor.qualityScale(0);

		Transpose.addMetadataRotate(outputProcessor, Rotation.R_0);

		outputProcessor.format("mp4");

		FFExecutor<Object> executor = builder.build();
		logger.log(executor.getCommandLineString());
	}

}
