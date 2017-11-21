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
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.impl.Speed;


/**
 * @author f.agu
 */
public class SpeedExecutable extends GenericExecutable {

	private float multiply;

	/**
	 *
	 */
	public SpeedExecutable() {}

	/**
	 * @param project
	 */
	public SpeedExecutable(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getCode()
	 */
	@Override
	public String getCode() {
		return "speed";
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#getHash()
	 */
	@Override
	public Hash getHash() {
		return super.getHash().append(multiply);
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#load(org.fagu.fmv.core.project.Project,
	 *      org.dom4j.Element, org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);
		multiply = LoadUtils.attributeRequireFloat(fromElement, "multiply");
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);
		toElement.addAttribute("multiply", Float.toString(multiply));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "speed (x" + multiply + ")";
	}

	// ************************************

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#ignoreAttributes()
	 */
	@Override
	protected Set<String> ignoreAttributes() {
		Set<String> ignoreAttributes = super.ignoreAttributes();
		ignoreAttributes.add("multiply");
		return ignoreAttributes;
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#populateWithIdentifiables(java.io.File,
	 *      org.fagu.fmv.core.exec.FileCache.Cache, org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder)
	 */
	@Override
	protected List<FilterInput> populateWithIdentifiables(File toFile, Cache cache, FFMPEGExecutorBuilder builder) {
		List<FilterInput> filterInputs = super.populateWithIdentifiables(toFile, cache, builder);
		if(filterInputs.size() != 1) {
			throw new RuntimeException("For speed, only one input: " + filterInputs);
		}

		Speed speed = Speed.multiply(multiply);
		speed.addInput(filterInputs.get(0));
		builder.filter(speed);

		filterInputs.add(speed);

		return filterInputs;
	}

}
