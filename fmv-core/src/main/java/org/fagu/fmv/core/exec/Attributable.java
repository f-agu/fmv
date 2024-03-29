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
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.dom4j.Element;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public abstract class Attributable extends BaseIdentifiable {

	private static final String ATTRIBUTE_RAWDURATION = "rawduration";

	protected Map<String, String> attributeMap = new TreeMap<>();

	private Duration duration;

	protected Attributable() {}

	protected Attributable(Project project) {
		super(project);
	}

	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);
		duration = LoadUtils.attributeDuration(fromElement, ATTRIBUTE_RAWDURATION);
		if(Duration.valueOf(0).equals(duration)) {
			duration = null;
		}

		Set<String> ignoreAttributes = ignoreAttributes();

		LoadUtils.attributes(fromElement)
				.stream()
				.filter(attr -> ! ignoreAttributes.contains(attr.getName()))
				.forEach(attr -> attributeMap.put(attr.getName(), attr.getValue()));
	}

	@Override
	public void save(Element toElement) {
		super.save(toElement);
		attributeMap.forEach(toElement::addAttribute);
		getDuration().ifPresent(d -> toElement.addAttribute(ATTRIBUTE_RAWDURATION, d.toString()));
	}

	@Override
	public Hash getHash() {
		Hash hash = super.getHash();
		attributeMap.entrySet().stream().forEach(e -> hash.append(e.getKey() + '=' + e.getValue()));
		return hash;
	}

	@Override
	public Optional<Duration> getDuration() {
		if(duration != null) {
			return Optional.of(duration);
		}
		return setDuration(getSpecificDuration().orElse(null));
	}

	@Override
	public Set<Type> getTypes() {
		Set<Type> types = new HashSet<>(8);
		filterExecs.forEach(fe -> types.addAll(fe.getTypes()));
		executables.forEach(fe -> types.addAll(fe.getTypes()));
		sources.forEach(fe -> types.addAll(fe.getTypes()));
		return types;
	}

	@Override
	public void resetDuration() {
		duration = null;
		getProject().modified();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(50);
		Iterator<Entry<String, String>> iterator = attributeMap.entrySet().iterator();
		if(iterator.hasNext()) {
			buf.append('[');
			for(;;) {
				Entry<String, String> entry = iterator.next();
				buf.append(entry.getKey()).append(':').append(entry.getValue());
				if( ! iterator.hasNext()) {
					break;
				}
				buf.append(',').append(' ');
			}
			buf.append(']');
		}
		return buf.toString();
	}

	// ****************************************************

	protected Set<String> ignoreAttributes() {
		Set<String> set = new HashSet<>(4);
		set.add("id");
		set.add("hash");
		set.add("code");
		set.add(ATTRIBUTE_RAWDURATION);
		return set;
	}

	protected Optional<Duration> setDuration(Duration duration) {
		getProject().modified();
		this.duration = duration;
		return Optional.ofNullable(duration);
	}

	protected Optional<Duration> getSpecificDuration() {
		return getGlobalDuration();
	}

}
