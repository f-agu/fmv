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
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.impl.Fade;
import org.fagu.fmv.ffmpeg.filter.impl.FadeType;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public class FadeAudioVideoFilterExec extends GenericFilterExec {

	private FadeType fadeType;

	private Time startTime;

	private Duration duration;

	/**
	 *
	 */
	public FadeAudioVideoFilterExec() {}

	/**
	 * @param project
	 * @param fadeType
	 * @param startTime
	 * @param duration
	 */
	public FadeAudioVideoFilterExec(Project project, FadeType fadeType, Time startTime, Duration duration) {
		super(project, "fade-audio/video");
		this.fadeType = fadeType;
		this.startTime = startTime;
		this.duration = duration;
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#getCode()
	 */
	@Override
	public String getCode() {
		return "fade-audio/video";
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#getHash()
	 */
	@Override
	public Hash getHash() {
		return super.getHash().append(fadeType).append(startTime).append(duration);
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#load(org.fagu.fmv.core.project.Project,
	 *      org.dom4j.Element, org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);

		fadeType = FadeType.valueOf(LoadUtils.attributeRequire(fromElement, "fade").toUpperCase());
		startTime = LoadUtils.attributeRequireTime(fromElement, "start");
		duration = LoadUtils.attributeRequireDuration(fromElement, "duration");
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);
		toElement.addAttribute("fade", fadeType.name().toLowerCase());
		toElement.addAttribute("start", startTime.toString());
		toElement.addAttribute("duration", duration.toString());
	}

	/**
	 * @see org.fagu.fmv.core.exec.filter.GenericFilterExec#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(50);
		buf.append(super.toString()).append(' ').append(fadeType).append(" ; starts at ").append(startTime);
		buf.append(" on ").append(duration);
		return buf.toString();
	}

	// ************************************************************

	/**
	 * @see org.fagu.fmv.core.exec.filter.GenericFilterExec#getFilter()
	 */
	@Override
	protected Filter getFilter() {
		return Fade.create(fadeType, startTime, duration);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#ignoreAttributes()
	 */
	@Override
	protected Set<String> ignoreAttributes() {
		Set<String> ignoreAttributes = super.ignoreAttributes();
		ignoreAttributes.add("fade");
		ignoreAttributes.add("start");
		ignoreAttributes.add("duration");
		return ignoreAttributes;
	}

}
