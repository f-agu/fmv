package org.fagu.fmv.mymedia.classify.movie;

/*
 * #%L
 * fmv-mymedia
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
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.FFMpegUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.AutoRotate;
import org.fagu.fmv.ffmpeg.filter.impl.ResampleAudio;
import org.fagu.fmv.ffmpeg.filter.impl.Rotate;
import org.fagu.fmv.ffmpeg.filter.impl.Transpose;
import org.fagu.fmv.ffmpeg.filter.impl.Volume;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetected;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.mymedia.classify.Converter;
import org.fagu.fmv.mymedia.classify.ConverterListener;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.reduce.FFReducer;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class MovieScriptConverter extends Converter<Movie> {

	private static final Size MAX_SIZE = Size.HD1080;

	private static final int DEFAULT_AUDIO_SAMPLE_RATE = 44100;

	private PrintStream script;

	private Map<String, Rotation> rotateMap;

	private int currentVideo;

	public MovieScriptConverter(File destFolder) {
		super(destFolder);
	}

	@Override
	public String getTitle() {
		return "Ecrire un script";
	}

	@Override
	public String getFormat(String defaultValue) {
		return "mp4";
	}

	public void rotate(String fileName, Rotation rotate) {
		rotateMap.put(fileName, rotate);
	}

	@Override
	public void convert(Movie srcMedia, FileFinder<Movie>.InfosFile infosFile, File destFile, ConverterListener<Movie> listener) throws IOException {
		openScript();
		File srcFile = srcMedia.getFile();
		MovieMetadatas infos = srcMedia.getMetadatas();

		int audioFrequency = FFMpegUtils.minAudioSampleRate(infos, DEFAULT_AUDIO_SAMPLE_RATE);

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.hideBanner();

		builder.addMediaInputFile(srcFile)
				.setMovieMetadatas(infos);

		Rotation rotation = rotateMap.get(srcFile.getName());
		if(rotation != null) {
			if(rotation != Rotation.R_0) {
				builder.filter(Rotate.create(rotation));
			}
		} else {
			builder.filter(AutoRotate.create(infos));
		}
		Size newSize = FFReducer.applyScaleIfNecessary(builder, infos, getMaxSize(), getScaleLogger(), rotation);
		writeLabel();
		script.println("rem " + (newSize.isLandscape() ? "landscape" : newSize.isPortrait() ? "portrait" : "square"));

		builder.filter(ResampleAudio.build().frequency(audioFrequency));

		Optional<VolumeDetected> findFirst = infosFile.getInfos()
				.stream()
				.filter(o -> o instanceof VolumeDetected)
				.map(o -> (VolumeDetected)o)
				.findFirst();
		if(findFirst.isPresent()) {
			VolumeDetected volumeDetected = findFirst.get();
			builder.filter(Volume.build().increaseToMax(volumeDetected));
		}

		File dest = new File(destFile.getParentFile(), FilenameUtils.getBaseName(destFile.getName()) + ".mp4");

		OutputProcessor outputProcessor = builder.addMediaOutputFile(dest);
		outputProcessor.qualityScale(0);

		Transpose.addMetadataRotate(outputProcessor, Rotation.R_0);

		outputProcessor.format("mp4");
		// outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		try {
			script.println("if exist \"" + dest.getPath() + "\" goto :movie_" + currentVideo);
			script.println("echo.");
			script.println("echo Frame: " + infos.getVideoStream().countEstimateFrames().get());
			script.println(executor.getCommandLineString());
			script.println();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if(script != null) {
			writeLabel();
			script.close();
		}
	}

	// **************************************************

	private void openScript() throws IOException {
		if(script == null) {
			script = new PrintStream(new File(destFolder, "script.bat"));
			rotateMap = new HashMap<>();
			script.println("@echo off");
			script.println();
			script.println("rem Rotate 90: ... -filter:v \"transpose=dir=clock\" ...");
			script.println("rem Rotate 180: ... -filter:v \"transpose=dir=clock,transpose=dir=clock\" ...");
			script.println("rem Rotate 270: ... -filter:v \"transpose=dir=cclock\" ...");
			script.println();
		}
	}

	private void writeLabel() {
		script.println(":movie_" + currentVideo++);
	}

	private Logger getScaleLogger() {
		return new Logger() {

			@Override
			public void close() throws IOException {}

			@Override
			public void log(Throwable throwable) {}

			@Override
			public void log(String message) {
				script.println("rem " + message);
			}
		};
	}

	private Size getMaxSize() {
		String property = System.getProperty("fmv.reduce.video.maxsize");
		if(property == null) {
			return MAX_SIZE;
		}
		return Size.parse(property);
	}
}
