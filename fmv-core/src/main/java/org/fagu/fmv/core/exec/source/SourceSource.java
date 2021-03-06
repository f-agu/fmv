package org.fagu.fmv.core.exec.source;

import java.util.Collections;
import java.util.Optional;

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

import java.util.Set;

import org.dom4j.Element;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.exec.ObjectInvoker;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatasUtils;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public class SourceSource extends AbstractSource {

	private int number;

	/**
	 * @param number
	 */
	public SourceSource() {}

	/**
	 * @param project
	 * @param number
	 */
	public SourceSource(Project project, int number) {
		super(project);
		this.number = number;
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#getCode()
	 */
	@Override
	public String getCode() {
		return "source";
	}

	/**
	 * @see org.fagu.fmv.core.exec.source.AbstractSource#load(org.fagu.fmv.core.project.Project, org.dom4j.Element,
	 *      org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);
		number = LoadUtils.attributeRequireInt(fromElement, "n");
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);
		toElement.addAttribute("n", Integer.toString(number));
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#getHash()
	 */
	@Override
	public Hash getHash() {
		Hash hash = super.getHash();
		hash.append(number);
		return hash;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Source#createAndAdd(org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder)
	 */
	@Override
	public FilterInput createAndAdd(FFMPEGExecutorBuilder builder) {
		InputProcessor inputProcessor = builder.addMediaInputFile(getProject().getSource(number).getFile());
		ObjectInvoker.invoke(inputProcessor, attributeMap);
		return inputProcessor;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		FileSource source = getProject().getSource(number);
		if(source.getFileType() == FileType.AUDIO || source.getFileType() == FileType.VIDEO) {
			MovieMetadatas videoMetadatas = source.getVideoMetadatas();
			return MovieMetadatasUtils.getTypes(videoMetadatas);
		}
		if(source.getFileType() == FileType.IMAGE) {
			return Collections.singleton(Type.VIDEO);
		}
		return Collections.emptySet();
	}

	/**
	 * @see org.fagu.fmv.core.exec.source.AbstractSource#toString()
	 */
	@Override
	public String toString() {
		FileSource fileSource = getProject().getSource(number);
		return super.toString() + " n:" + number + " (" + fileSource.getFile().getName() + ')';
	}

	// ********************************************

	/**
	 * @see org.fagu.fmv.core.exec.source.AbstractSource#ignoreAttributes()
	 */
	@Override
	protected Set<String> ignoreAttributes() {
		Set<String> set = super.ignoreAttributes();
		set.add("n");
		return set;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#getSpecificDuration()
	 */
	@Override
	protected Optional<Duration> getSpecificDuration() {
		FileSource source = getProject().getSource(number);
		if(source.getFileType() == FileType.AUDIO || source.getFileType() == FileType.VIDEO) {
			MovieMetadatas videoMetadatas = source.getVideoMetadatas();
			return MovieMetadatasUtils.getDuration(videoMetadatas);
		}
		if(source.getFileType() == FileType.IMAGE) {
			// TODO
		}
		return Optional.empty();
	}

}
