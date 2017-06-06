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
import org.fagu.fmv.core.exec.ObjectInvoker;
import org.fagu.fmv.core.exec.Source;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix.MixAudioDuration;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Map;
import org.fagu.fmv.ffmpeg.operation.Map.On;
import org.fagu.fmv.utils.time.Time;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class AudioMixExecutable extends GenericExecutable {

	private MixAudioDuration mixAudioDuration;

	private Time audioStart;

	/**
	 *
	 */
	public AudioMixExecutable() {}

	/**
	 * @param project
	 */
	public AudioMixExecutable(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getCode()
	 */
	@Override
	public String getCode() {
		return "audiomix";
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#getHash()
	 */
	@Override
	public Hash getHash() {
		return super.getHash().append(mixAudioDuration).append(audioStart);
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#load(org.fagu.fmv.core.project.Project,
	 *      org.dom4j.Element, org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);

		mixAudioDuration = MixAudioDuration.valueOf(fromElement.attributeValue("duration").toUpperCase());
		audioStart = LoadUtils.attributeTime(fromElement, "audiostart");
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);
		toElement.addAttribute("duration", mixAudioDuration.name().toLowerCase());
		if(audioStart != null) {
			toElement.addAttribute("audiostart", audioStart.toString());
		}
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#execute()
	 */
	@Override
	public void execute(File toFile, Cache cache) throws IOException {
		if( ! hasChildren()) {
			return;
		}

		AudioMix audioMix = AudioMix.build();

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

		List<InputProcessor> videoInputProcessors = new ArrayList<InputProcessor>();
		for(InputProcessor inputProcessor : inputProcessors) {
			MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();
			if(movieMetadatas.contains(Type.AUDIO) && ! movieMetadatas.contains(Type.VIDEO)) {
				audioMix.addInput(inputProcessor, audioStart);
			} else {
				videoInputProcessors.add(inputProcessor);
				audioMix.addInput(inputProcessor);
			}
		}

		audioMix.duration(mixAudioDuration);

		OutputProcessor outputProcessor = outputProcessor(builder, toFile, cache);
		ObjectInvoker.invoke(outputProcessor, attributeMap);

		builder.filter(audioMix);

		Map map = outputProcessor.map();
		map.allStreams().input(audioMix);
		On videoStreams = map.types(Type.VIDEO);
		videoInputProcessors.stream().forEach(vip -> videoStreams.input(vip));

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
		ignoreAttributes.add("audiostart");
		return ignoreAttributes;
	}

}
