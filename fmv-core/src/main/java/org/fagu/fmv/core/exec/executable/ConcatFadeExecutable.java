package org.fagu.fmv.core.exec.executable;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.FFUtils;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.exec.Source;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
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
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public class ConcatFadeExecutable extends GenericExecutable {

	private Duration duration;

	private Time startTime;

	/**
	 *
	 */
	public ConcatFadeExecutable() {}

	/**
	 * @param project
	 */
	public ConcatFadeExecutable(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getCode()
	 */
	@Override
	public String getCode() {
		return "concat-fade";
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#getHash()
	 */
	@Override
	public Hash getHash() {
		return super.getHash().append(startTime).append(duration);
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#load(org.fagu.fmv.core.project.Project,
	 *      org.dom4j.Element, org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);

		duration = LoadUtils.attributeRequireDuration(fromElement, "duration");
		startTime = LoadUtils.attributeRequireTime(fromElement, "startTime");
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);
		toElement.addAttribute("startTime", startTime.toString());
		toElement.addAttribute("duration", duration.toString());
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#execute()
	 */
	@Override
	public void execute(File toFile, Cache cache) throws IOException {
		if( ! hasChildren()) {
			return;
		}

		FFMPEGExecutorBuilder builder = FFUtils.builder(getProject());

		List<InputProcessor> inputProcessors = new ArrayList<InputProcessor>();

		// executable
		for(Executable executable : executables) {
			File file = getProject().getFileCache().getFile(executable, cache);
			InputProcessor inputProcessor = builder.addMediaInputFile(file);
			// audioMix.addInput(inputProcessor);
			inputProcessors.add(inputProcessor);
		}

		// source
		for(Source source : sources) {
			FilterInput filterInput = source.createAndAdd(builder);
			if(filterInput instanceof InputProcessor) {
				inputProcessors.add((InputProcessor)filterInput);
				// MovieMetadatas movieMetadatas = ((InputProcessor)filterInput).getMovieMetadatas();
				// if(movieMetadatas.contains(Type.AUDIO)) {
				// audioMix.addInput(filterInput, audioStart);
				// } else {
				// throw new RuntimeException("Source is not an audio stream: " + source);
				// }
			} else {
				throw new RuntimeException("Source is not a InputProcessor: " + source);
			}
		}

		applyConcatFade(builder, inputProcessors, toFile, cache);

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "generic-exec " + super.toString();
	}

	// ************************************

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#ignoreAttributes()
	 */
	@Override
	protected Set<String> ignoreAttributes() {
		Set<String> ignoreAttributes = super.ignoreAttributes();
		ignoreAttributes.add("duration");
		ignoreAttributes.add("startTime");
		return ignoreAttributes;
	}

	/**
	 * @param builder
	 * @param inputProcessors
	 * @param toFile
	 * @param cache
	 * @throws IOException
	 */
	protected void applyConcatFade(FFMPEGExecutorBuilder builder, List<InputProcessor> inputProcessors, File toFile, Cache cache) throws IOException {
		if(inputProcessors.size() != 2) {
			throw new RuntimeException("Need 2 inputs ! : " + inputProcessors);
		}

		InputProcessor video1InputProcessor = inputProcessors.get(0);
		InputProcessor video2InputProcessor = inputProcessors.get(1);

		VideoStream videoStream1 = video1InputProcessor.getMovieMetadatas().getVideoStream();
		VideoStream videoStream2 = video2InputProcessor.getMovieMetadatas().getVideoStream();

		Time startTime_T1 = Time.valueOf(videoStream1.duration().get().toSeconds() - duration.toSeconds());
		Duration duration_0_T1 = Duration.valueOf(startTime_T1.toSeconds());
		Time startTime_T2 = Time.valueOf(videoStream2.duration().get().toSeconds() - duration.toSeconds());
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
		FilterComplex fadeAudio1 = FilterComplex.create(FadeAudio.out().startTime(startTime_T1).duration(duration)).addInput(concat1A);

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
		FilterComplex fadeAudio2 = FilterComplex.create(FadeAudio.in().startTime(startTime_T1).duration(duration)).addInput(concat2A);

		// blend / merge video
		SetSAR setSAR = SetSAR.toRatio("1");
		Format formatRGBA = Format.with(PixelFormat.RGBA);
		FilterComplex vfc1 = FilterComplex.create(setSAR, formatRGBA).addInput(concat1V);
		FilterComplex vfc2 = FilterComplex.create(setSAR, formatRGBA).addInput(concat2V);
		Blend blend = Blend.build().mode(Mode.ADDITION).repeatLast(true).opacity(1).exprFade(startTime_T1, duration);
		Format formatYUV = Format.with(PixelFormat.YUVA422P10LE);
		FilterComplex vfcBlend = FilterComplex.create(blend, formatYUV).addInput(vfc1).addInput(vfc2);
		builder.filter(vfcBlend);

		// merge audio
		FilterComplex audioMix = AudioMix.build().duration(MixAudioDuration.SHORTEST).addInput(fadeAudio1).addInput(fadeAudio2);
		builder.filter(audioMix);

		outputProcessor(builder, toFile, cache);
	}

}
