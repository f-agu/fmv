package org.fagu.fmv.ffmpeg;

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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.coder.Encoders;
import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.coder.LibFDK_AAC;
import org.fagu.fmv.ffmpeg.executor.Executed;
import org.fagu.fmv.ffmpeg.executor.FFExecFallback;
import org.fagu.fmv.ffmpeg.executor.FFExecListener;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.OutputKey;
import org.fagu.fmv.ffmpeg.filter.impl.AudioGenerator;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix.MixAudioDuration;
import org.fagu.fmv.ffmpeg.filter.impl.AudioModifier;
import org.fagu.fmv.ffmpeg.filter.impl.AudioToPictureShowWaves;
import org.fagu.fmv.ffmpeg.filter.impl.AutoRotate;
import org.fagu.fmv.ffmpeg.filter.impl.Blend;
import org.fagu.fmv.ffmpeg.filter.impl.Blend.Mode;
import org.fagu.fmv.ffmpeg.filter.impl.Boxblur;
import org.fagu.fmv.ffmpeg.filter.impl.Concat;
import org.fagu.fmv.ffmpeg.filter.impl.Crop;
import org.fagu.fmv.ffmpeg.filter.impl.CropDetect;
import org.fagu.fmv.ffmpeg.filter.impl.CropDetection;
import org.fagu.fmv.ffmpeg.filter.impl.Fade;
import org.fagu.fmv.ffmpeg.filter.impl.FadeAudio;
import org.fagu.fmv.ffmpeg.filter.impl.FadeType;
import org.fagu.fmv.ffmpeg.filter.impl.FadeVideo;
import org.fagu.fmv.ffmpeg.filter.impl.Format;
import org.fagu.fmv.ffmpeg.filter.impl.FrameRate;
import org.fagu.fmv.ffmpeg.filter.impl.Hue;
import org.fagu.fmv.ffmpeg.filter.impl.NullSourceVideo;
import org.fagu.fmv.ffmpeg.filter.impl.Overlay;
import org.fagu.fmv.ffmpeg.filter.impl.ResampleAudio;
import org.fagu.fmv.ffmpeg.filter.impl.Scale;
import org.fagu.fmv.ffmpeg.filter.impl.ScaleMode;
import org.fagu.fmv.ffmpeg.filter.impl.SelectVideo;
import org.fagu.fmv.ffmpeg.filter.impl.SetPTSVideo;
import org.fagu.fmv.ffmpeg.filter.impl.SetSAR;
import org.fagu.fmv.ffmpeg.filter.impl.ShowInfo;
import org.fagu.fmv.ffmpeg.filter.impl.Speed;
import org.fagu.fmv.ffmpeg.filter.impl.SplitAudio;
import org.fagu.fmv.ffmpeg.filter.impl.SplitVideo;
import org.fagu.fmv.ffmpeg.filter.impl.Volume;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetect;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetected;
import org.fagu.fmv.ffmpeg.flags.AvoidNegativeTs;
import org.fagu.fmv.ffmpeg.flags.Movflags;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.format.BasicStreamMuxer;
import org.fagu.fmv.ffmpeg.format.Image2Muxer;
import org.fagu.fmv.ffmpeg.format.MP4Muxer;
import org.fagu.fmv.ffmpeg.format.NullMuxer;
import org.fagu.fmv.ffmpeg.ioe.FileMediaInput;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.AutoMaps;
import org.fagu.fmv.ffmpeg.operation.ExtractThumbnail;
import org.fagu.fmv.ffmpeg.operation.InfoOperation;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;
import org.fagu.fmv.ffmpeg.utils.FPS;
import org.fagu.fmv.ffmpeg.utils.LogLevel;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.ffmpeg.utils.VSync;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class FFHelper {

	/**
	 *
	 */
	private FFHelper() {}

	/**
	 * @param inFile
	 * @return
	 * @throws IOException
	 */
	public static MovieMetadatas videoMetadatas(File inFile) throws IOException {
		MediaInput input = new FileMediaInput(inFile);
		InfoOperation infoOperation = new InfoOperation(input);
		FFExecutor<MovieMetadatas> executor = new FFExecutor<>(infoOperation);
		Executed<MovieMetadatas> execute = executor.execute();
		return execute.getResult();
	}

	/**
	 * @param inFile
	 * @throws IOException
	 */
	public static void reencodeToH264(File inFile, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.addMediaInputFile(inFile);

		builder.mux(MP4Muxer.to(outFile)) //
				.codec(H264.findRecommanded().mostCompatible()) //
				// .codec(Libx264.build().mostCompatible()) //
				.overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLineString());
		long startTime = System.currentTimeMillis();
		executor.execute();
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	/**
	 * @param inFile
	 * @throws IOException
	 */
	public static void showInfoVideoFrames(File inFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.addMediaInputFile(inFile);

		ShowInfo showInfo = ShowInfo.build().addListener(System.out::println); // print
		builder.filter(showInfo);

		builder.addMediaOutput(NullMuxer.build()).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param outFile
	 * @param duration
	 * @throws IOException
	 */
	public static void captureWebCam(File outFile, Duration duration) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.addMediaInputWebCam().duration(duration);

		builder.addMediaOutputFile(outFile);

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void audioConvert2AAC(File inFile, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.addMediaInputFile(inFile);

		builder.mux(BasicStreamMuxer.to(outFile).movflags(Movflags.FASTSTART)).codec(LibFDK_AAC.build()).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param outFile
	 * @throws IOException
	 */
	public static void audioGenerator(File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		AudioGenerator ag = AudioGenerator.build().expr("sin(440*2*PI*t)").sampleRate(44100)
				.duration(Duration.valueOf(5));
		builder.addMediaInput(ag.forInput());

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void audioHalfVolume(File inFile, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);

		AudioModifier audioModifier = AudioModifier.build().halfVolume().channelLayout(ChannelLayout.SAME);

		FilterComplex filtercomplex = FilterComplex.create(audioModifier);
		filtercomplex.addInput(inputProcessor);
		builder.filter(filtercomplex);

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static VolumeDetected audioVolumeDetect(File inFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInputFile(inFile);

		VolumeDetect volumeDetect = VolumeDetect.build();
		builder.filter(volumeDetect);

		builder.addMediaOutput(NullMuxer.build()).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();

		return volumeDetect.getDetected();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void audioVolumeAdjustToMax(File inFile, File outFile) throws IOException {
		VolumeDetected volumeDetected = audioVolumeDetect(inFile);

		System.out.println(volumeDetected);

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.addMediaInputFile(inFile);

		Volume volume = Volume.build().increaseToMax(volumeDetected);

		builder.filter(volume);

		builder.addMediaOutputFile(outFile).videoCodecCopy().overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param videoFile
	 * @param frenchAudioToAddFile
	 * @param outFile
	 * @throws Exception
	 */
	public static void addFrenchAudioStream(File videoFile, File frenchAudioToAddFile, File outFile) throws Exception {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		InputProcessor videoInput = builder.addMediaInputFile(videoFile);
		int countAudioStreams = videoInput.getMovieMetadatas().getAudioStreams().size();
		InputProcessor audioInput = builder.addMediaInputFile(frenchAudioToAddFile);

		OutputProcessor outputProcessor = builder.addMediaOutputFile(outFile);
		outputProcessor.map().allStreams().input(videoInput).input(audioInput);
		outputProcessor.codecCopy(Type.VIDEO).shortest().overwrite().metadataStream(Type.AUDIO, countAudioStreams,
				"language", "fra");

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outImageFile
	 * @param time
	 * @return
	 * @throws IOException
	 */
	public static void extractOneThumbnail(File inFile, File outImageFile, Time time) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.addMediaInputFile(inFile);

		builder.mux(Image2Muxer.to(outImageFile))
				.timeSeek(time)
				.numberOfVideoFrameToRecord(1)// only one thumbnail
				.overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFolder
	 * @param fps
	 * @return
	 * @throws IOException
	 */
	public static ExtractThumbnail extractThumbnails(File inFile, File outFolder, FPS fps) throws IOException {
		if( ! outFolder.exists()) {
			outFolder.mkdirs();
		}
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.addMediaInputFile(inFile);
		builder.filter(FrameRate.to(fps));

		builder.mux(Image2Muxer.to(new File(outFolder, "out%05d.jpg")));

		FFExecutor<Object> executor = builder.build();
		executor.execute();

		return ExtractThumbnail.find(outFolder, "out\\d+\\.jpg");
	}

	/**
	 * @param inFile
	 * @param outFolder
	 * @param countFrame
	 * @return
	 * @throws IOException
	 */
	public static ExtractThumbnail extractThumbnails2JPEGS(File inFile, File outFolder, int countFrame)
			throws IOException {

		// extract images
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		// global
		builder.hideBanner();
		builder.logLevel(LogLevel.INFO);
		// input
		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);

		MovieMetadatas videoMetadatas = inputProcessor.getMovieMetadatas();
		VideoStream videoStream = videoMetadatas.getVideoStreams().get(0);
		if( ! outFolder.exists()) {
			outFolder.mkdirs();
		}

		// filter
		SelectVideo selectVideo = SelectVideo.build().countFrame(videoStream, countFrame);
		builder.filter(selectVideo);

		// output
		builder.mux(Image2Muxer.to(new File(outFolder, "out%05d.jpg"))).videoSync(VSync.PASSTHROUGH).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();

		return ExtractThumbnail.find(outFolder, "out\\d+\\.jpg");
	}

	/**
	 * similar extractThumbnails2JPEGS(..) without 'format("image2")'
	 *
	 * @param inFile
	 * @param outFolder
	 * @param countFrame
	 * @return
	 * @throws IOException
	 */
	public static void extractThumbnails2GIF(File inFile, File outFile, int countFrame) throws IOException {
		if( ! "gif".equalsIgnoreCase(FilenameUtils.getExtension(outFile.getName()))) {
			throw new IllegalArgumentException("Not a gif: " + outFile.getPath());
		}

		// extract images
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		// global
		builder.hideBanner();
		builder.logLevel(LogLevel.INFO);
		// input
		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);

		// infos
		MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();
		VideoStream videoStream = movieMetadatas.getVideoStream();

		// filter
		SelectVideo selectVideo = SelectVideo.build().countFrame(videoStream, countFrame);
		builder.filter(selectVideo);

		// necessary ?
		builder.filter(Format.with(PixelFormat.RGB8));
		builder.filter(Format.with(PixelFormat.RGB24));

		// output
		builder.addMediaOutputFile(outFile).videoSync(VSync.PASSTHROUGH).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFolder
	 * @param startTime
	 * @param duration
	 * @throws IOException
	 */
	public static void extractPart(File inFile, File outFile, Time startTime, Duration duration) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		// input
		builder.addMediaInputFile(inFile).timeSeek(startTime);

		// ouput
		builder.mux(MP4Muxer.to(outFile).avoidNegativeTs(AvoidNegativeTs.MAKE_NON_NEGATIVE)).duration(duration)
				.qualityScale(0).codec(H264.findRecommanded().mostCompatible()).overwrite();

		// execute
		builder.build().execute();
	}

	/**
	 * @param inFile
	 * @param size
	 * @param outFile
	 * @throws IOException
	 */
	public static void resize(File inFile, Size size, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);

		builder.filter(Scale.to(size, ScaleMode.fitToBoxKeepAspectRatio()));

		builder.mux(MP4Muxer.to(outFile)).qualityScaleVideo(0).codecCopy(Type.AUDIO).codecCopy(Type.SUBTITLE)
				.mapAllStreams(inputProcessor).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.addListener(new FFExecListener() {

			@Override
			public void eventExecFailed(IOException e, CommandLine command) {
				System.out.println("FAILED: " + CommandLineUtils.toLine(command));
			}

			@Override
			public void eventPreExecFallbacks(CommandLine command, Collection<FFExecFallback> fallbacks) {
				System.out.println("With fallback: " + fallbacks);
				System.out.println(" -->: " + CommandLineUtils.toLine(command));
			}
		});
		executor.execute();
	}

	/**
	 * @param inFiles
	 * @param outFile
	 * @throws IOException
	 */
	public static void concat(List<File> inFiles, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		Concat concat = Concat.create(builder, inFiles);

		builder.filter(concat);

		builder.mux(MP4Muxer.to(outFile).movflags(Movflags.FASTSTART)).qualityScale(0)
				.codec(H264.findRecommanded().mostCompatible()).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFiles
	 * @param outFile
	 * @throws IOException
	 */
	public static void cutConcat(List<File> inFiles, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		Concat concat = Concat.create(builder, inFiles);
		// for(File inFile : inFiles) {
		// InputProcessor inputProcessor = builder.inputFile(inFile);
		//
		// // les 2 SetPTS permettent de commencer la video au debut car une video ne
		// commence pas toujours a 0 !
		//
		// SetPTS setPTS = new SetPTSVideo();
		// setPTS.setStartAtFirstFrame();
		// FilterComplex setPTSComplex = FilterComplex.createWith(setPTS);
		// setPTSComplex.addInput(inputProcessor, Type.VIDEO);
		// builder.add(setPTSComplex);
		// concat.addInput(setPTSComplex);
		//
		// setPTS = new SetPTSAudio();
		// setPTS.setStartAtFirstFrame();
		// setPTSComplex = FilterComplex.createWith(setPTS);
		// setPTSComplex.addInput(inputProcessor, Type.AUDIO);
		// builder.add(setPTSComplex);
		// concat.addInput(setPTSComplex);
		// }

		builder.filter(concat);

		builder.addMediaOutputFile(outFile).qualityScaleAudio(0).qualityScaleVideo(0)
				.codec(H264.findRecommanded().mostCompatible()).format("mp4").overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @param multiplyBy
	 * @throws IOException
	 */
	public static void speed(File inFile, File outFile, float multiplyBy) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);

		Speed speed = Speed.multiply(multiplyBy);
		speed.addInput(inputProcessor);
		builder.filter(speed);

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @param fadeType
	 * @param duration
	 * @throws IOException
	 */
	public static void fade1(File inFile, File outFile, FadeType fadeType, Duration duration) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInputFile(inFile);
		FadeVideo fadeVideo = FadeVideo.build().type(fadeType).startTime(Time.valueOf(0)).duration(duration);
		FadeAudio fadeAudio = FadeAudio.build().type(fadeType).startTime(Time.valueOf(0)).duration(duration);

		builder.filter(fadeVideo).filter(fadeAudio);

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @param fadeType
	 * @param duration
	 * @throws IOException
	 */
	public static void fade2(File inFile, File outFile, FadeType fadeType, Duration duration) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);
		Fade fade = Fade.create(fadeType, Time.valueOf(0), duration);
		fade.addInput(inputProcessor);

		builder.filter(fade);

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @throws IOException
	 */
	public static CropDetection cropDetect(File inFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInputFile(inFile).timeSeek(Time.parse("36:05")).duration(Duration.valueOf(3));

		CropDetect cropDetect = CropDetect.build();
		builder.filter(cropDetect);

		builder.addMediaOutput(NullMuxer.build()).overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLineString());
		executor.execute();

		return cropDetect.getCropSizeDetected();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @param size
	 * @throws IOException
	 */
	public static void crop(File inFile, File outFile, Size size) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInputFile(inFile);
		Crop crop = Crop.build().centralArea(size);
		builder.filter(crop);

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile1
	 * @param outFile2
	 * @throws IOException
	 */
	public static void splitTo3(File inFile, File outFile1, File outFile2, File outFile3) throws IOException {
		// outFile1 : no fade
		// outFile2 : fade in
		// outFile3 : fade out
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);

		SplitVideo splitVideo = SplitVideo.build();
		splitVideo.addInput(inputProcessor);
		OutputKey outv1 = splitVideo.addOutput();
		OutputKey outv2 = splitVideo.addOutput();
		OutputKey outv3 = splitVideo.addOutput();

		SplitAudio splitAudio = SplitAudio.build();
		splitAudio.addInput(inputProcessor);
		OutputKey outa1 = splitAudio.addOutput();
		OutputKey outa2 = splitAudio.addOutput();
		OutputKey outa3 = splitAudio.addOutput();

		Fade fade2 = Fade.create(FadeType.IN, Time.valueOf(0), Duration.valueOf(1));
		Fade fade3 = Fade.create(FadeType.OUT, Time.valueOf(0), Duration.valueOf(1));

		fade2.addInput(outv2).addInput(outa2);
		fade3.addInput(outv3).addInput(outa3);

		builder.filter(splitVideo);
		builder.filter(splitAudio);
		builder.filter(fade2);
		builder.filter(fade3);

		OutputProcessor outputProcessor1 = builder.addMediaOutputFile(outFile1);
		outputProcessor1.map().allStreams().label(outv1.getLabel()).label(outa1.getLabel());
		outputProcessor1.overwrite();

		OutputProcessor outputProcessor2 = builder.addMediaOutputFile(outFile2);
		outputProcessor2.map().allStreams().input(fade2);
		outputProcessor2.overwrite();

		OutputProcessor outputProcessor3 = builder.addMediaOutputFile(outFile3);
		outputProcessor3.map().allStreams().input(fade3);
		outputProcessor3.overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @param size
	 * @throws IOException
	 */
	public static void autoRotate(File inFile, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);
		builder.filter(AutoRotate.create(inputProcessor.getMovieMetadatas()));

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inVideoFile
	 * @param inAudioFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void mixAudio(File inVideoFile, File inAudioFile, File outFile, Time audioStart) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor videoInputProcessor = builder.addMediaInputFile(inVideoFile);
		InputProcessor audioInputProcessor = builder.addMediaInputFile(inAudioFile);
		// audioInputProcessor.timeSeek(audioStart);

		AudioMix audioMix = AudioMix.build();
		audioMix.addInput(audioInputProcessor, audioStart);
		audioMix.addInput(videoInputProcessor);
		audioMix.duration(MixAudioDuration.SHORTEST);

		builder.filter(audioMix);

		builder.addMediaOutputFile(outFile).videoCodecCopy().overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param in1VideoFile
	 * @param in2VideoFile
	 * @param in3VideoFile
	 * @param in4VideoFile
	 * @param outFile
	 * @param size
	 * @throws IOException
	 */
	public static void overlay4(File in1VideoFile, File in2VideoFile, File in3VideoFile, File in4VideoFile,
			File outFile, Size size) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor video1InputProcessor = builder.addMediaInputFile(in1VideoFile);
		InputProcessor video2InputProcessor = builder.addMediaInputFile(in2VideoFile);
		InputProcessor video3InputProcessor = builder.addMediaInputFile(in3VideoFile);
		InputProcessor video4InputProcessor = builder.addMediaInputFile(in4VideoFile);

		Size quartSize = size.getRatio().getSizeByWidth(size.getWidth() / 2);
		NullSourceVideo nullSource = NullSourceVideo.build().size(size);
		FilterComplex nullSourceFC = FilterComplex.create(nullSource);

		SetPTSVideo setPTS = SetPTSVideo.createStartAtFirstFrame();

		Scale scale = Scale.to(quartSize, ScaleMode.fitToBox());
		FilterComplex vfc1 = FilterComplex.create(setPTS, scale).addInput(video1InputProcessor);
		FilterComplex vfc2 = FilterComplex.create(setPTS, scale).addInput(video2InputProcessor);
		FilterComplex vfc3 = FilterComplex.create(setPTS, scale).addInput(video3InputProcessor);
		FilterComplex vfc4 = FilterComplex.create(setPTS, scale).addInput(video4InputProcessor);

		builder.filter(nullSourceFC);
		builder.filter(vfc1);
		builder.filter(vfc2);
		builder.filter(vfc3);
		builder.filter(vfc4);

		Overlay overlay1 = Overlay.with(nullSourceFC, vfc1).shortest(true);
		Overlay overlay2 = Overlay.with(overlay1, vfc2).shortest(true).x(quartSize.getWidth());
		Overlay overlay3 = Overlay.with(overlay2, vfc3).shortest(true).y(quartSize.getHeight());
		Overlay.with(overlay3, vfc4).shortest(true).x(quartSize.getWidth()).y(quartSize.getHeight());

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param in1VideoFile
	 * @param in2VideoFile
	 * @param fadeDuration
	 * @param outFile
	 * @throws IOException
	 */
	public static void concatFade(File in1VideoFile, File in2VideoFile, Duration fadeDuration, File outFile)
			throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor video1InputProcessor = builder.addMediaInputFile(in1VideoFile);
		InputProcessor video2InputProcessor = builder.addMediaInputFile(in2VideoFile);

		VideoStream videoStream1 = video1InputProcessor.getMovieMetadatas().getVideoStream();
		VideoStream videoStream2 = video2InputProcessor.getMovieMetadatas().getVideoStream();

		Optional<Duration> duration1 = videoStream1.duration();
		Time startTimeT1 = Time.valueOf(duration1.get().toSeconds() - fadeDuration.toSeconds());
		Duration duration0T1 = Duration.valueOf(startTimeT1.toSeconds());
		Optional<Duration> duration2 = videoStream2.duration();
		Time startTimeT2 = Time.valueOf(duration2.get().toSeconds() - fadeDuration.toSeconds());
		Duration durationT2END = Duration.valueOf(startTimeT2.toSeconds());

		// source 1: video
		NullSourceVideo nullSourceVideo1 = NullSourceVideo.build().size(videoStream1.size()).duration(durationT2END);
		Concat concat1V = Concat.create(builder, video1InputProcessor, FilterComplex.create(nullSourceVideo1))
				.countVideo(1).countAudio(0).countInputs(2);
		// source 1: audio
		AudioGenerator audioGenerator1 = AudioGenerator.build().silence().duration(durationT2END);
		Concat concat1A = Concat.create(builder, video1InputProcessor, FilterComplex.create(audioGenerator1))
				.countVideo(0).countAudio(1).countInputs(2);
		FilterComplex fadeAudio1 = FilterComplex.create(FadeAudio.out().startTime(startTimeT1).duration(fadeDuration))
				.addInput(concat1A);

		// source 2: video
		NullSourceVideo nullSourceVideo2 = NullSourceVideo.build().size(videoStream2.size()).duration(duration0T1);
		Concat concat2V = Concat.create(builder, FilterComplex.create(nullSourceVideo2), video2InputProcessor)
				.countVideo(1).countAudio(0).countInputs(2);
		// source 2: audio
		AudioGenerator audioGenerator2 = AudioGenerator.build().silence().duration(duration0T1);
		Concat concat2A = Concat.create(builder, FilterComplex.create(audioGenerator2), video2InputProcessor)
				.countVideo(0).countAudio(1).countInputs(2);
		FilterComplex fadeAudio2 = FilterComplex.create(FadeAudio.in().startTime(startTimeT1).duration(fadeDuration))
				.addInput(concat2A);

		// blend / merge video
		SetSAR setSAR = SetSAR.toRatio("1");
		Format formatRGBA = Format.with(PixelFormat.RGBA);
		FilterComplex vfc1 = FilterComplex.create(setSAR, formatRGBA).addInput(concat1V);
		FilterComplex vfc2 = FilterComplex.create(setSAR, formatRGBA).addInput(concat2V);
		Blend blend = Blend.build().mode(Mode.ADDITION).repeatLast(true).opacity(1).exprFade(startTimeT1, fadeDuration);
		Format formatYUV = Format.with(PixelFormat.YUVA422P10LE);
		FilterComplex vfcBlend = FilterComplex.create(blend, formatYUV).addInput(vfc1).addInput(vfc2);
		builder.filter(vfcBlend);

		// merge audio
		FilterComplex audioMix = AudioMix.build().duration(MixAudioDuration.SHORTEST).addInput(fadeAudio1)
				.addInput(fadeAudio2);
		builder.filter(audioMix);

		// out
		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void backgroundBlurOverlayScale(File inFile, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor inputProcessor = builder.addMediaInputFile(inFile); // inFile is in HD720

		// builder.filter(FilterComplex.create(AudioGenerator.build().silence()));

		Boxblur boxblur = Boxblur.build().lumaRadius("8").lumaPower(8);
		Hue hue = Hue.build().blackAndWhite();
		FilterComplex blurAndBW = FilterComplex.create(boxblur, hue).addInput(inputProcessor);

		FilterComplex scale = FilterComplex.create(Scale.build().size(Size.HD480)).addInput(inputProcessor);

		Overlay overlay = Overlay.with(blurAndBW, scale).shortest(true).positionMiddle();

		FilterComplex scale2 = FilterComplex.create(Scale.build().size(Size.HD480)).addInput(overlay);

		builder.filter(scale2);

		builder.addMediaOutputFile(outFile).overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();

	}

	/**
	 * @param inMkvFile
	 * @param outMp4File
	 * @param locales
	 * @throws IOException
	 */
	public static void encodeTox264_KeepChaptersAndSubtitles(File inFile, File outFile, Collection<Locale> locales)
			throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();

		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);
		MovieMetadatas videoMetadatas = inputProcessor.getMovieMetadatas();

		OutputProcessor outputProcessor = builder.addMediaOutputFile(outFile);

		for(Stream stream : videoMetadatas.getStreams()) {
			// exclude some subtitles
			// if(stream.is(Type.SUBTITLE) && ! locales.contains(stream.locale())) {
			// continue;
			// }
			// if(stream.is(Type.AUDIO) && ! locales.contains(stream.locale())) {
			// continue;
			// }
			outputProcessor.map().streams(stream).input(inputProcessor);
		}

		outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(23))
				// .codecCopy(Type.AUDIO)
				.codecAutoSelectAAC().codecCopy(Type.SUBTITLE).overwrite();

		FFExecutor<Object> executor = builder.build();

		// System.out.println(executor.getCommandLine());

		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void oo(File srcFile, File outFile) throws IOException {
		final int DEFAULT_AUDIO_SAMPLE_RATE = 44100;
		final int DEFAULT_AUDIO_BIT_RATE = 128000;
		final int DEFAULT_AUDIO_CHANNEL = 2;

		Size size = Size.valueOf(930, 500);

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();

		// input
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		// infos
		MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();

		int audioFrequency = FFMpegUtils.minAudioSampleRate(movieMetadatas, DEFAULT_AUDIO_SAMPLE_RATE);
		int audioBitRate = FFMpegUtils.minAudioBitRate(movieMetadatas, DEFAULT_AUDIO_BIT_RATE);
		int audioChannel = FFMpegUtils.minAudioChannel(movieMetadatas, DEFAULT_AUDIO_CHANNEL);

		// filters (garder l'ordre des filters)
		builder.filter(AutoRotate.create(movieMetadatas));
		builder.filter(Scale.to(size, ScaleMode.fitToBoxKeepAspectRatio()));
		builder.filter(Format.with(PixelFormat.YUV420P));
		builder.filter(ResampleAudio.build().frequency(audioFrequency));

		// output
		builder.mux(MP4Muxer.to(outFile) //
				.movflags(Movflags.FASTSTART)) // , Movflags.FRAG_KEYFRAME, Movflags.EMPTY_MOOV
				.codec(H264.findRecommanded().mostCompatible()) //
				.pixelFormat(PixelFormat.YUV420P) // pour quicktime/safari
				.codecAutoSelectAAC() //
				.audioChannel(audioChannel) //
				.audioBitRate(audioBitRate) //
				.overwrite();

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void m4aToMp3(File inFile, File outFile) throws IOException {
		final int SAMPLE_RATE = 44100;
		final String bitRate = "128k";

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(inFile);
		MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();

		int sampleRate = Math.min(movieMetadatas.getAudioStream().sampleRate().orElse(SAMPLE_RATE), SAMPLE_RATE);

		FilterComplex filter = FilterComplex.create(ResampleAudio.build().frequency(sampleRate));
		builder.filter(filter);

		OutputProcessor outputProcessor = builder.addMediaOutputFile(outFile).audioChannel(2).audioBitRate(bitRate)
				.format("mp3").overwrite();
		outputProcessor.map().allStreams().input(filter);

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLineString());
		// Integer numberOfFrames =
		// movieMetadatas.getAudioStream().countEstimateFrames();
		// prepareProgressBar(numberOfFrames, executor, consolePrefixMessage);
		// executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void audioToGraph(File inAudio, File outImage) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		builder.getFFMPEGOperation().setAutoMap(AutoMaps.disable());
		builder.addMediaInputFile(inAudio);

		FilterComplex filter = FilterComplex.create(AudioToPictureShowWaves.build().size(Size.valueOf(1024, 200)));
		builder.filter(filter);

		builder.addMediaOutputFile(outImage).numberOfVideoFrameToRecord(1);
		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLineString());
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void videoToGraph(File inAudio, File outImage) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		builder.getFFMPEGOperation().setAutoMap(AutoMaps.disable());
		builder.addMediaInputFile(inAudio);

		FilterComplex filter = FilterComplex.create(AudioToPictureShowWaves.build().size(Size.valueOf(1024, 200)));
		builder.filter(filter);

		builder.addMediaOutputFile(outImage).numberOfVideoFrameToRecord(1);
		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLineString());
		executor.execute();
	}

	/**
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void encoderList() throws IOException {
		Encoders.availableNames().stream().filter(s -> s.contains("264")).sorted().forEach(System.out::println);
	}

	public static void main0(String[] args) throws Exception {
		captureWebCam(new File("D:\\tmp\\capture.mp4"), Duration.valueOf(10));
	}

	public static void main(String[] args) throws Exception {
		// audioGenerator(new File("D:\\tmp\\test.mp3"));
		// audioToGraph(new File("D:\\tmp\\test.mp3"), new
		// File("D:\\tmp\\test.mp3.png"));
		// encoderList();
		// reencodeToH264(new File("D:\\tmp\\GOPR3967.MP4"), new
		// File("D:\\tmp\\GOPR3967-libx264.MP4"));
		reencodeToH264(new File("D:\\tmp\\GOPR3967.MP4"), new File("D:\\tmp\\GOPR3967-h264_nvenc.MP4"));
	}
}
