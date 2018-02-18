package org.fagu.fmv.core.exec.executable;

import java.io.File;

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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.dom4j.Element;
import org.fagu.fmv.core.exec.Attributable;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.ExecutableOption;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.OutputInfos;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.core.project.Properties;
import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.format.BasicStreamMuxer;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;

/**
 * @author f.agu
 */
public abstract class AbstractExecutable extends Attributable implements Executable {

	protected Set<ExecutableOption> options = new HashSet<>(2);

	/**
	 *
	 */
	public AbstractExecutable() {
	}

	/**
	 * @param project
	 * @param parent
	 */
	public AbstractExecutable(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#has(org.fagu.fmv.core.exec.ExecutableOption)
	 */
	@Override
	public boolean has(ExecutableOption option) {
		return getOptions().contains(option);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getOptions()
	 */
	@Override
	public Set<ExecutableOption> getOptions() {
		return options;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#load(org.fagu.fmv.core.project.Project,
	 *      org.dom4j.Element, org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);

		String optionsStr = fromElement.attributeValue("options");
		if (optionsStr != null) {
			for (String optionStr : optionsStr.split(",")) {
				try {
					options.add(ExecutableOption.valueOf(optionStr));
				} catch (IllegalArgumentException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);

		if (!options.isEmpty()) {
			String optionsStr = options.stream().map(opt -> opt.name().toLowerCase()).collect(Collectors.joining(","));
			toElement.addAttribute("options", optionsStr);
		}
	}

	// ****************************************************

	/**
	 * @return
	 */
	@Override
	protected Set<String> ignoreAttributes() {
		Set<String> set = super.ignoreAttributes();
		set.add("options");
		return set;
	}

	/**
	 * @param builder
	 * @param outfile
	 * @param cache
	 * @return
	 */
	protected OutputProcessor outputProcessor(FFMPEGExecutorBuilder builder, File outfile, Cache cache) {
		OutputInfos outputInfos = getProject().getOutputInfos();
		BasicStreamMuxer muxer = BasicStreamMuxer.to(outfile, outputInfos.getFormat());
		return outputProcessor(builder.mux(muxer), cache);
	}

	/**
	 * @param outputProcessor
	 * @param cache
	 * @return
	 */
	protected OutputProcessor outputProcessor(OutputProcessor outputProcessor, Cache cache) {
		int crf = getH264Quality(cache);
		outputProcessor.codec(H264.findRecommanded().mostCompatible().quality(crf));
		outputProcessor.qualityScaleAudio(0);
		outputProcessor.qualityScaleVideo(0);
		outputProcessor.overwrite();
		return outputProcessor;
	}

	// ************************************

	/**
	 * @param cache
	 * @return
	 */
	private int getH264Quality(Cache cache) {
		if (cache == Cache.PREVIEW) {
			return getProject().getProperty(Properties.PREVIEW_H264_CRF);
		}
		if (cache != Cache.MAKE) {
			throw new RuntimeException("Not implemented");
		}
		return getProject().getProperty(isRoot() ? Properties.MAKE_H264_CRF : Properties.PREPARE_MAKE_H264_CRF);
	}

}
