package org.fagu.fmv.mymedia.reduce.neocut;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalDouble;

import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.Delogo;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.soft.mediainfo.Info;
import org.fagu.fmv.soft.mediainfo.MediaInfoExtractor;
import org.fagu.fmv.soft.mediainfo.VideoInfo;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author Utilisateur
 * @created 4 avr. 2018 18:32:36
 */
public class Reducer implements Closeable {

	public void reduce(File srcFile, File destFile, Logo logo) throws IOException {
		int crf = (int)(getCRF(srcFile).orElse(26));

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		builder.addMediaInputFile(srcFile);

		if(logo != null) {
			Delogo delogo = logo.generateFilter()
					.show(true);
			builder.filter(delogo);
		}

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);
		outputProcessor.duration(Duration.valueOf(60));
		outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(crf));

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	@Override
	public void close() throws IOException {

	}

	// *********************************************************

	private OptionalDouble getCRF(File srcFile) throws IOException {
		MediaInfoExtractor extractor = new MediaInfoExtractor();
		Info info = extractor.extract(srcFile);
		Optional<VideoInfo> firstVideo = info.getFirstVideo();
		if( ! firstVideo.isPresent()) {
			return OptionalDouble.empty();
		}
		return firstVideo.get().getEncodingSettings().getCRF();
	}

}
