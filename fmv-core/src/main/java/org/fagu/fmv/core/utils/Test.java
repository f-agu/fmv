package org.fagu.fmv.core.utils;

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
import java.io.IOException;

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.impl.AudioGenerator;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix.MixAudioDuration;
import org.fagu.fmv.ffmpeg.filter.impl.Blend;
import org.fagu.fmv.ffmpeg.filter.impl.Blend.Mode;
import org.fagu.fmv.ffmpeg.filter.impl.Concat;
import org.fagu.fmv.ffmpeg.filter.impl.FadeAudio;
import org.fagu.fmv.ffmpeg.filter.impl.Format;
import org.fagu.fmv.ffmpeg.filter.impl.NullSourceVideo;
import org.fagu.fmv.ffmpeg.filter.impl.SetSAR;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 *
 */
public class Test {

	/**
	 *
	 */
	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void concatFade1(File in1VideoFile, File in2VideoFile, Duration fadeDuration, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor video1InputProcessor = builder.addMediaInputFile(in1VideoFile);
		InputProcessor video2InputProcessor = builder.addMediaInputFile(in2VideoFile);

		VideoStream videoStream1 = video1InputProcessor.getMovieMetadatas().getVideoStream();
		VideoStream videoStream2 = video2InputProcessor.getMovieMetadatas().getVideoStream();

		Time startTime_T1 = Time.valueOf(videoStream1.duration().get().toSeconds() - fadeDuration.toSeconds());
		Duration duration_0_T1 = Duration.valueOf(startTime_T1.toSeconds());
		Time startTime_T2 = Time.valueOf(videoStream2.duration().get().toSeconds() - fadeDuration.toSeconds());
		Duration duration_T2_END = Duration.valueOf(startTime_T2.toSeconds());

		// source 1
		NullSourceVideo nullSourceVideo1 = NullSourceVideo.build().size(videoStream1.size()).duration(duration_T2_END);
		AudioGenerator audioGenerator1 = AudioGenerator.build().silence().duration(duration_T2_END);
		Concat concat1 = Concat.create(builder, video1InputProcessor, FilterComplex.create(nullSourceVideo1), FilterComplex.create(audioGenerator1));
		FilterComplex fadeAudio1 = FilterComplex.create(FadeAudio.out().startTime(startTime_T1).duration(fadeDuration)).addInput(concat1);

		// source 2
		NullSourceVideo nullSourceVideo2 = NullSourceVideo.build().size(videoStream2.size()).duration(duration_0_T1);
		AudioGenerator audioGenerator2 = AudioGenerator.build().silence().duration(duration_0_T1);
		Concat concat2 = Concat.create(builder, FilterComplex.create(nullSourceVideo2), FilterComplex.create(audioGenerator2), video2InputProcessor);
		FilterComplex fadeAudio2 = FilterComplex.create(FadeAudio.in().startTime(startTime_T1).duration(fadeDuration)).addInput(concat2);

		// blend for fade / merge
		// video
		SetSAR setSAR = SetSAR.toRatio("1");
		Format formatRGBA = Format.with(PixelFormat.RGBA);
		FilterComplex vfc1 = FilterComplex.create(setSAR, formatRGBA).addInput(concat1);
		FilterComplex vfc2 = FilterComplex.create(setSAR, formatRGBA).addInput(concat2);
		Blend blend = Blend.build().mode(Mode.ADDITION).repeatLast(true).opacity(1).exprFade(startTime_T1, fadeDuration);
		Format formatYUV = Format.with(PixelFormat.YUVA422P10LE);
		FilterComplex vfcBlend = FilterComplex.create(blend, formatYUV).addInput(vfc1).addInput(vfc2);
		builder.filter(vfcBlend);
		// audio
		FilterComplex audioMix = AudioMix.build().duration(MixAudioDuration.SHORTEST).addInput(fadeAudio1).addInput(fadeAudio2);
		builder.filter(audioMix);

		// out
		OutputProcessor outputProcessor = builder.addMediaOutputFile(outFile);
		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLine());

		FilterGraphUI.show(builder.getFFMPEGOperation());
		// executor.execute();
	}

	public static void concatFade2(File in1VideoFile, File in2VideoFile, Duration fadeDuration, File outFile) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		InputProcessor video1InputProcessor = builder.addMediaInputFile(in1VideoFile);
		InputProcessor video2InputProcessor = builder.addMediaInputFile(in2VideoFile);

		VideoStream videoStream1 = video1InputProcessor.getMovieMetadatas().getVideoStream();
		VideoStream videoStream2 = video2InputProcessor.getMovieMetadatas().getVideoStream();

		Time startTime_T1 = Time.valueOf(videoStream1.duration().get().toSeconds() - fadeDuration.toSeconds());
		Duration duration_0_T1 = Duration.valueOf(startTime_T1.toSeconds());
		Time startTime_T2 = Time.valueOf(videoStream2.duration().get().toSeconds() - fadeDuration.toSeconds());
		Duration duration_T2_END = Duration.valueOf(startTime_T2.toSeconds());

		// source 1: video
		NullSourceVideo nullSourceVideo1 = NullSourceVideo.build().size(videoStream1.size()).duration(duration_T2_END);
		Concat concat1V = Concat.create(builder, video1InputProcessor, FilterComplex.create(nullSourceVideo1))
				.countVideo(1)
				.countAudio(0)
				.countInputs(2);
		// source 1: audio
		AudioGenerator audioGenerator1 = AudioGenerator.build().silence().duration(duration_T2_END);
		Concat concat1A = Concat.create(builder, video1InputProcessor, FilterComplex.create(audioGenerator1))
				.countVideo(0)
				.countAudio(1)
				.countInputs(2);
		FilterComplex fadeAudio1 = FilterComplex.create(FadeAudio.out().startTime(startTime_T1).duration(fadeDuration)).addInput(concat1A);

		// source 2: video
		NullSourceVideo nullSourceVideo2 = NullSourceVideo.build().size(videoStream2.size()).duration(duration_0_T1);
		Concat concat2V = Concat.create(builder, FilterComplex.create(nullSourceVideo2), video2InputProcessor)
				.countVideo(1)
				.countAudio(0)
				.countInputs(2);
		// source 2: audio
		AudioGenerator audioGenerator2 = AudioGenerator.build().silence().duration(duration_0_T1);
		Concat concat2A = Concat.create(builder, FilterComplex.create(audioGenerator2), video2InputProcessor)
				.countVideo(0)
				.countAudio(1)
				.countInputs(2);
		FilterComplex fadeAudio2 = FilterComplex.create(FadeAudio.in().startTime(startTime_T1).duration(fadeDuration)).addInput(concat2A);

		// blend / merge video
		SetSAR setSAR = SetSAR.toRatio("1");
		Format formatRGBA = Format.with(PixelFormat.RGBA);
		FilterComplex vfc1 = FilterComplex.create(setSAR, formatRGBA).addInput(concat1V);
		FilterComplex vfc2 = FilterComplex.create(setSAR, formatRGBA).addInput(concat2V);
		Blend blend = Blend.build().mode(Mode.ADDITION).repeatLast(true).opacity(1).exprFade(startTime_T1, fadeDuration);
		Format formatYUV = Format.with(PixelFormat.YUVA422P10LE);
		FilterComplex vfcBlend = FilterComplex.create(blend, formatYUV).addInput(vfc1).addInput(vfc2);
		builder.filter(vfcBlend);

		// merge audio
		FilterComplex audioMix = AudioMix.build().duration(MixAudioDuration.SHORTEST).addInput(fadeAudio1).addInput(fadeAudio2);
		builder.filter(audioMix);

		// out
		OutputProcessor outputProcessor = builder.addMediaOutputFile(outFile);
		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLine());

		FilterGraphUI.show(builder.getFFMPEGOperation());
		// executor.execute();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File file1 = new File("D:\\tmp\\corse\\in1.mp4");
		File file2 = new File("D:\\tmp\\corse\\in2.mp4");
		concatFade2(file1, file2, Duration.valueOf(2), new File("out.mp4"));
	}

}
