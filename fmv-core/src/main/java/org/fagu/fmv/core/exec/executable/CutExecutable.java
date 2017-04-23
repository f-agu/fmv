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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.FFUtils;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.core.exec.FilterExec;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.exec.Source;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.OutputInfos;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.flags.AvoidNegativeTs;
import org.fagu.fmv.ffmpeg.format.BasicStreamMuxer;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public class CutExecutable extends AbstractExecutable {

	private Time startTime;

	private Duration duration;

	/**
	 *
	 */
	public CutExecutable() {}

	/**
	 * @param project
	 * @param startTime
	 * @param duration
	 */
	public CutExecutable(Project project, Time startTime, Duration duration) {
		super(project);
		this.startTime = startTime;
		this.duration = duration;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getCode()
	 */
	@Override
	public String getCode() {
		return "cut";
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getFilters()
	 */
	@Override
	public List<FilterExec> getFilters() {
		return Collections.emptyList();
	}

	/**
	 * @param executable the executable to set
	 */
	public void setExecutable(Executable executable) {
		executables.clear();
		sources.clear();
		add(executable);
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Source source) {
		executables.clear();
		sources.clear();
		add(source);
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#getHash()
	 */
	@Override
	public Hash getHash() {
		Hash hash = super.getHash();
		hash.append(startTime).append(duration);
		return hash;
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#load(org.fagu.fmv.core.project.Project, org.dom4j.Element,
	 *      Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		List<Element> elements = LoadUtils.elements(fromElement);
		if(elements.size() != 1) {
			throw new LoadException("Need only one entry ! " + elements.size());
		}
		super.load(project, fromElement, parent);

		startTime = LoadUtils.attributeRequireTime(fromElement, "timestart");
		duration = LoadUtils.attributeRequireDuration(fromElement, "duration");
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);

		toElement.addAttribute("timestart", startTime.toString());
		toElement.addAttribute("duration", duration.toString());
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#execute(java.io.File, Cache)
	 */
	@Override
	public void execute(File toFile, Cache cache) throws IOException {
		Source source = getSource();
		Executable executable = getExecutable();
		if(executable == null && source == null) {
			return;
		}

		OutputInfos outputInfos = getProject().getOutputInfos();

		FFMPEGExecutorBuilder builder = FFUtils.builder(getProject());

		InputProcessor inputProcessor = null;
		if(executable != null) {
			File childExec = getProject().getFileCache().getFile(executable, cache);
			// executable.execute(childExec, cache);
			inputProcessor = builder.addMediaInputFile(childExec);
		} else if(source != null) {
			inputProcessor = (InputProcessor)source.createAndAdd(builder);

		} else {
			throw new IllegalArgumentException("Source or executable not defined !");
		}

		inputProcessor.timeSeek(startTime);

		// SetPTS videoSetPTS = new SetPTSVideo();
		// videoSetPTS.setStartAtFirstFrame();
		// FilterComplex videoSetPTSComplex = FilterComplex.createWith(videoSetPTS);
		// videoSetPTSComplex.addInput(inputProcessor, Type.VIDEO);
		// builder.add(videoSetPTSComplex);
		//
		// SetPTS audioSetPTS = new SetPTSAudio();
		// audioSetPTS.setStartAtFirstFrame();
		// FilterComplex audioSetPTSComplex = FilterComplex.createWith(audioSetPTS);
		// audioSetPTSComplex.addInput(inputProcessor, Type.AUDIO);
		// builder.add(audioSetPTSComplex);

		BasicStreamMuxer muxer = BasicStreamMuxer.to(toFile, outputInfos.getFormat()).avoidNegativeTs(AvoidNegativeTs.MAKE_NON_NEGATIVE);
		OutputProcessor outputProcessor = builder.mux(muxer);
		outputProcessor.duration(duration);
		outputProcessor(outputProcessor, cache);

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append(getCode()).append(' ').append(startTime).append('/').append(duration);
		return buf.toString();
	}

	// **********************************************

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#ignoreAttributes()
	 */
	@Override
	protected Set<String> ignoreAttributes() {
		Set<String> set = super.ignoreAttributes();
		set.add("timestart");
		set.add("duration");
		return set;
	}

	/**
	 * @return
	 */
	private Source getSource() {
		return sources.stream().findFirst().orElse(null);
	}

	/**
	 * @return
	 */
	private Executable getExecutable() {
		return executables.stream().findFirst().orElse(null);
	}

}
