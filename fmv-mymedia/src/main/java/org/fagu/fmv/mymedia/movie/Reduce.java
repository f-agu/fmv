package org.fagu.fmv.mymedia.movie;

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
import java.nio.file.Files;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.SubtitleStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.FileTypeUtils;
import org.fagu.fmv.media.FileTypeUtils.FileIs;


/**
 * @author f.agu
 */
public class Reduce {

	// private static final long LIMIT_SIZE = 1800L * 1024L * 1024L; // 800 Mo

	// private static final long BEFORE_TIME = 1449010800000L; // 2015-12-02
	// 1449244477913

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		File folder = new File("c:\\tmp\\movies\\TODO");

		AtomicLong totalSize = new AtomicLong();
		FileIs verifyType = FileTypeUtils.with(FileType.VIDEO);
		Files.walk(folder.toPath())
				.filter(verifyType::verify)
				.forEach(p -> {
					try {
						long fileSize = Files.size(p);
						// if(fileSize < LIMIT_SIZE) {
						// return;
						// }
						// if(Files.getLastModifiedTime(p).toMillis() > BEFORE_TIME) {
						// return;
						// }

						System.out.println("rem " + fileSize);
						totalSize.addAndGet(fileSize);

						doIt(p.toFile());
					} catch(Exception e) {
						e.printStackTrace();
					}
				});

		System.out.println(totalSize);
	}

	/**
	 * @param sourceFile
	 * @throws IOException
	 */
	private static void doIt(File sourceFile) throws IOException {
		String extension = FilenameUtils.getExtension(sourceFile.getName()).toLowerCase();
		if( ! "mkv".equals(extension)) {
			extension = "mp4";
		}
		File destinationFile = new File(sourceFile.getParentFile(),
				FilenameUtils.getBaseName(sourceFile.getName()) + "-new." + extension);
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();

		InputProcessor inputProcessor = builder.addMediaInputFile(sourceFile);
		MovieMetadatas videoMetadatas = inputProcessor.getMovieMetadatas();
		boolean doVideo = ! videoMetadatas.getVideoStream().isTreatedByFMV();

		boolean doAudio = doVideo;
		Collection<AudioStream> audioStreams = StreamOrder.sort(videoMetadatas.getAudioStreams());

		for(AudioStream audioStream : audioStreams) {
			if("vorbis".equals(audioStream.codecName().get())) {
				doAudio |= true;
				break;
			}
			if("aac".equals(audioStream.codecName().get())) {
				doAudio = false;
				break;
			}
		}
		if( ! doVideo && ! doAudio) {
			return;
		}

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destinationFile);
		outputProcessor.qualityScale(0);

		// ------------------------ map ------------------------
		// video
		for(Stream stream : videoMetadatas.getVideoStreams()) {
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// audio
		for(Stream stream : audioStreams) {
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// subtitle
		Collection<SubtitleStream> subtitleStreams = StreamOrder.sort(videoMetadatas.getSubtitleStreams());
		for(Stream stream : subtitleStreams) {
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// other stream
		for(Stream stream : videoMetadatas.getStreams()) {
			Type type = stream.type();
			if(type != Type.AUDIO && type != Type.VIDEO && type != Type.SUBTITLE) {
				outputProcessor.map().streams(stream).input(inputProcessor);
			}
		}

		// ------------------------ disposition default ------------------------
		//
		int count = 0;
		for(Stream stream : audioStreams) {
			boolean beDefault = count == 1;
			if(stream.isDefaultStream() != beDefault) {
				outputProcessor.metadataStream(Type.AUDIO, count, "disposition:default", beDefault ? "1" : "0");
			}
			++count;
		}
		count = 0;
		for(Stream stream : subtitleStreams) {
			boolean beDefault = count == 1;
			if(stream.isDefaultStream() != beDefault) {
				outputProcessor.metadataStream(Type.SUBTITLE, count, "disposition:default", beDefault ? "1" : "0");
			}
			++count;
		}

		// -------------------------- codec -------------------------

		// video
		if(doVideo) {
			outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(23));
		} else {
			outputProcessor.codecCopy(Type.VIDEO);
		}

		// audio
		if(doAudio) {
			outputProcessor.codecAutoSelectAAC();
		} else {
			outputProcessor.codecCopy(Type.AUDIO);
		}

		// subtitle
		if(videoMetadatas.contains(Type.SUBTITLE)) {
			outputProcessor.codecCopy(Type.SUBTITLE);
		}

		// outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLine());
	}
}
