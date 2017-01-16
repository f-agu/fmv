package org.fagu.fmv.core.exec.filter;

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
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.impl.AudioMix;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public class AudioMixFilterExec extends GenericFilterExec {

	private Time audioStart;

	/**
	 *
	 */
	public AudioMixFilterExec() {}

	/**
	 * @param code
	 */
	public AudioMixFilterExec(Project project) {
		super(project, "amix");
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#getCode()
	 */
	@Override
	public String getCode() {
		return "audiomix";
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#load(org.fagu.fmv.core.project.Project,
	 *      org.dom4j.Element, org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);

		audioStart = LoadUtils.attributeTime(fromElement, "audiostart");
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);
		if(audioStart != null) {
			toElement.addAttribute("audiostart", audioStart.toString());
		}
	}

	/**
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(50);
		buf.append(getCode()).append(' ').append(super.toString());
		return buf.toString();
	}

	// ************************************************************

	/**
	 * @see org.fagu.fmv.core.exec.filter.GenericFilterExec#getFilter()
	 */
	@Override
	protected Filter getFilter() {
		return AudioMix.build();
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#ignoreAttributes()
	 */
	@Override
	protected Set<String> ignoreAttributes() {
		Set<String> ignoreAttributes = super.ignoreAttributes();
		ignoreAttributes.add("audiostart");
		return ignoreAttributes;
	}

	/**
	 * @see org.fagu.fmv.core.exec.filter.GenericFilterExec#addInputIntoFilter(org.fagu.fmv.ffmpeg.filter.FilterComplexBase,
	 *      org.fagu.fmv.ffmpeg.filter.FilterInput, java.util.Set)
	 */
	@Override
	protected void addInputIntoFilter(FilterComplexBase filterComplexBase, FilterInput filterInput, Set<Type> undeclaredTypes) {
		if(filterInput.contains(Type.AUDIO) && ! filterInput.contains(Type.VIDEO)) {
			// only audio
			AudioMix audioMix = (AudioMix)filterComplexBase;
			audioMix.addInput(filterInput, audioStart);
			removeUndeclaredTypes(undeclaredTypes, filterInput);

		} else {
			super.addInputIntoFilter(filterComplexBase, filterInput, undeclaredTypes);
		}
	}

}
