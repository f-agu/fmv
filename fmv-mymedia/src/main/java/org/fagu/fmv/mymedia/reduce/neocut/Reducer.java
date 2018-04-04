package org.fagu.fmv.mymedia.reduce.neocut;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.Delogo;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;


/**
 * @author Utilisateur
 * @created 4 avr. 2018 18:32:36
 */
public class Reducer implements Closeable {

	/**
	 * 
	 */
	public Reducer() {}

	public void reduce(File srcFile, File destFile, Logo logo) {
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
		outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(26));

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
	}

	@Override
	public void close() throws IOException {

	}

}
