package org.fagu.fmv.core.exec;

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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.dom4j.Element;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.utils.Duration;


/**
 * @author f.agu
 */
public abstract class Attributable extends BaseIdentifiable {

	private static final String ATTRIBUTE_RAWDURATION = "rawduration";

	protected Map<String, String> attributeMap = new TreeMap<>();

	private Duration duration;

	/**
	 *
	 */
	public Attributable() {}

	/**
	 * @param project
	 */
	public Attributable(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.timeline.BaseIdentifiable#load(org.dom4j.Element)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);
		duration = LoadUtils.attributeDuration(fromElement, ATTRIBUTE_RAWDURATION);

		Set<String> ignoreAttributes = ignoreAttributes();

		LoadUtils.attributes(fromElement).stream()
				.filter(attr -> ! ignoreAttributes.contains(attr.getName()))
				.forEach(attr -> attributeMap.put(attr.getName(), attr.getValue()));
	}

	/**
	 * @see org.fagu.fmv.core.timeline.BaseIdentifiable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		super.save(toElement);
		attributeMap.forEach(toElement::addAttribute);
		if(duration != null) {
			toElement.addAttribute(ATTRIBUTE_RAWDURATION, duration.toString());
		}
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#getHash()
	 */
	@Override
	public Hash getHash() {
		Hash hash = super.getHash();
		attributeMap.entrySet().stream().forEach(e -> hash.append(e.getKey() + '=' + e.getValue()));
		return hash;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getDuration()
	 */
	@Override
	public Duration getDuration() {
		if(duration != null) {
			return duration;
		}
		return setDuration(getSpecificDuration());
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#resetDuration()
	 */
	@Override
	public void resetDuration() {
		duration = null;
		getProject().modified();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(50);
		buf.append('[');
		Iterator<Entry<String, String>> iterator = attributeMap.entrySet().iterator();
		if(iterator.hasNext()) {
			for(;;) {
				Entry<String, String> entry = iterator.next();
				buf.append(entry.getKey()).append(':').append(entry.getValue());
				if( ! iterator.hasNext()) {
					break;
				}
				buf.append(',').append(' ');
			}
		}
		buf.append(']');
		return buf.toString();
	}

	// ****************************************************

	/**
	 * @return
	 */
	protected Set<String> ignoreAttributes() {
		Set<String> set = new HashSet<>();
		set.add("id");
		set.add("hash");
		set.add("code");
		set.add(ATTRIBUTE_RAWDURATION);
		return set;
	}

	/**
	 * @param duration
	 * @return
	 */
	protected Duration setDuration(Duration duration) {
		getProject().modified();
		this.duration = duration;
		return duration;
	}

	/**
	 * @return
	 */
	protected Duration getSpecificDuration() {
		return getGlobalDuration();
	}

}
