package org.fagu.fmv.core.exec;

/*
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 fagu
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.dom4j.Element;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.exec.executable.GenericExecutable;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.LoadUtils;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.utils.collection.MapMap;
import org.fagu.fmv.utils.collection.MultiValueMaps;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public abstract class BaseIdentifiable implements Identifiable {

	private static final MapMap<Project, String, Identifiable> UNIQUE_ID_MAP = MultiValueMaps.hashMapHashMap();

	protected final List<Identifiable> identifiableChildren = new ArrayList<>();

	protected final List<FilterExec> filterExecs = new ArrayList<>();

	protected final List<Executable> executables = new ArrayList<>();

	protected final List<Source> sources = new ArrayList<>();

	private String id;

	private String code;

	private Project project;

	private Identifiable parent;

	/**
	 *
	 */
	public BaseIdentifiable() {}

	/**
	 * @param project
	 */
	public BaseIdentifiable(Project project) {
		this.project = project;
		registerMe();
	}

	/**
	 * @see org.fagu.fmv.core.timeline.Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getCode()
	 */
	@Override
	public String getCode() {
		return code;
	}

	/**
	 * @see org.fagu.fmv.core.timeline.Identifiable#getProject()
	 */
	@Override
	public Project getProject() {
		return project;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getParent()
	 */
	@Override
	public Identifiable getParent() {
		return parent;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#isRoot()
	 */
	@Override
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getDepth()
	 */
	@Override
	public int getDepth() {
		return getDepth(i -> true);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getDepth(java.util.function.Predicate)
	 */
	@Override
	public int getDepth(Predicate<Identifiable> filter) {
		if(parent == null) {
			return 0;
		}
		final int SECURE_LOOP = 1000;
		Identifiable curParent = this;
		int count = 0;
		while((curParent = curParent.getParent()) != null) {
			if(filter.test(curParent)) {
				++count;
			}
			if(count > SECURE_LOOP) {
				throw new RuntimeException("Too many parent ! " + count + " : " + this);
			}
		}
		return count;
	}

	/**
	 * @see org.fagu.fmv.core.timeline.Identifiable#modified()
	 */
	@Override
	public void modified() {
		project.modified();
	}

	/**
	 * @see org.fagu.fmv.core.timeline.Identifiable#getHash()
	 */
	@Override
	public Hash getHash() {
		Hash hash = new Hash(id);
		Identifiable curParent = this;
		while((curParent = curParent.getParent()) != null) {
			hash.append(curParent.getId());
		}
		identifiableChildren.stream().forEach(i -> hash.append(i.getHash()));
		return hash;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#isExecutable()
	 */
	@Override
	public boolean isExecutable() {
		return this instanceof Executable;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#isFilterExec()
	 */
	@Override
	public boolean isFilterExec() {
		return this instanceof FilterExec;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#isSource()
	 */
	@Override
	public boolean isSource() {
		return this instanceof Source;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#add(org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public Identifiable add(Identifiable identifiable) {
		if(identifiable.isFilterExec()) {
			return add((FilterExec)identifiable);
		}
		if(identifiable.isExecutable()) {
			return add((Executable)identifiable);
		}
		if(identifiable.isSource()) {
			return add((Source)identifiable);
		}
		throw new RuntimeException("Undefined identifiable: " + identifiable);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#add(org.fagu.fmv.core.exec.Executable)
	 */
	@Override
	public Identifiable add(Executable executable) {
		Identifiable previousParentOfChild = insert(executables, executable);

		if(isFilterExec()) {
			if(previousParentOfChild == null) { // root
				return new GenericExecutable(project).add(this);
			} else if(previousParentOfChild.isFilterExec()) {
				// nothing
			} else if(previousParentOfChild.isExecutable()) {}
		}
		// TODO
		return null;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#add(org.fagu.fmv.core.exec.FilterExec)
	 */
	@Override
	public Identifiable add(FilterExec filterExec) {
		Identifiable newParent = insert(filterExecs, filterExec);
		if(isFilterExec()) {
			return newParent;
		}
		// TODO
		return null;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#add(org.fagu.fmv.core.exec.Source)
	 */
	@Override
	public Identifiable add(Source source) {
		insert(sources, source);

		// TODO
		return null;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getIdentifiableChildren()
	 */
	@Override
	public List<Identifiable> getIdentifiableChildren() {
		return Collections.unmodifiableList(identifiableChildren);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getFilters()
	 */
	@Override
	public List<FilterExec> getFilters() {
		return Collections.unmodifiableList(filterExecs);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getExecutables()
	 */
	@Override
	public List<Executable> getExecutables() {
		return Collections.unmodifiableList(executables);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getSources()
	 */
	@Override
	public List<Source> getSources() {
		return Collections.unmodifiableList(sources);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#remove(org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public boolean remove(Identifiable identifiable) {
		identifiableChildren.remove(identifiable);
		return getIdentifiableList(identifiable).remove(identifiable);
	}

	/**
	 * @see org.fagu.fmv.core.timeline.Identifiable#load(org.dom4j.Element)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		this.project = project;
		this.parent = parent;
		this.code = LoadUtils.attributeRequire(fromElement, "code");
		String loadedId = fromElement.attributeValue("id");
		if(loadedId != null) {
			unregisterMe();
			id = loadedId;
			registerMe();
		}
		if(id == null) {
			registerMe();
		}

		for(Element element : LoadUtils.elements(fromElement)) {
			String name = element.getName();
			Identifiable child = null;
			if("filter".equals(name)) {
				FilterExec filterExec = FilterExecFactory.getSingleton().get(project, element, this);
				filterExecs.add(filterExec);
				child = filterExec;
			} else if("exec".equals(name)) {
				Executable executable = ExecutableFactory.getSingleton().get(project, element, this);
				executables.add(executable);
				child = executable;
			} else if("source".equals(name)) {
				Source source = SourceFactory.getSingleton().get(project, element, this);
				sources.add(source);
				child = source;
			}
			if(child != null) {
				identifiableChildren.add(child);
			}
		}
	}

	/**
	 * @see org.fagu.fmv.core.timeline.Identifiable#save(org.dom4j.Element)
	 */
	@Override
	public void save(Element toElement) {
		toElement.addAttribute("code", getCode());
		if(id != null) {
			toElement.addAttribute("id", id);
			toElement.addAttribute("hash", getHash().toString());
		}

		for(Identifiable child : identifiableChildren) {
			String elementName = null;
			if(child.isFilterExec()) {
				elementName = "filter";
			}
			if(child.isExecutable()) {
				elementName = "exec";
			}
			if(child.isSource()) {
				elementName = "source";
			}
			Element element = toElement.addElement(elementName);
			child.save(element);
		}
	}

	// *************************************************

	/**
	 * @param project
	 * @param id
	 * @return
	 */
	public static Optional<Identifiable> findById(Project project, String id) {
		return Optional.ofNullable(UNIQUE_ID_MAP.get(project, id));
	}

	/**
	 * @param project
	 * @param id
	 * @return
	 */
	public static List<Executable> getRoots(Project project) {
		return stream(project)
				.filter(Identifiable::isExecutable)
				.map(id -> (Executable)id)
				.filter(Executable::isRoot)
				.collect(Collectors.toList());
	}

	/**
	 * @param project
	 * @return
	 */
	public static Stream<Identifiable> stream(Project project) {
		Map<String, Identifiable> map = UNIQUE_ID_MAP.get(project);
		if(map == null) {
			return Collections.<Identifiable>emptyList().stream();
		}
		return map.values().stream();
	}

	// *************************************************

	/**
	 * @param identifiable
	 * @return
	 */
	protected List<? extends Identifiable> getIdentifiableList(Identifiable identifiable) {
		if(identifiable.isFilterExec()) {
			return filterExecs;
		}
		if(identifiable.isExecutable()) {
			return executables;
		}
		if(identifiable.isSource()) {
			return sources;
		}
		throw new RuntimeException("Undefined identifiable: " + identifiable);
	}

	/**
	 * @param list
	 * @param child
	 * @return
	 */
	protected <I extends Identifiable> Identifiable insert(List<I> list, I child) {
		Identifiable previousParent = child.getParent();
		list.add(child);
		identifiableChildren.add(child);
		((BaseIdentifiable)child).parent = this;
		if(previousParent != null) {
			this.parent = previousParent;
			previousParent.remove(child);
			BaseIdentifiable baseParent = (BaseIdentifiable)previousParent;
			@SuppressWarnings("unchecked")
			List<Identifiable> identifiableList = (List<Identifiable>)baseParent.getIdentifiableList(this);
			identifiableList.add(this);
			baseParent.identifiableChildren.add(this);
		}
		return previousParent == null ? this : previousParent;
	}

	/**
	 * @return
	 */
	protected Optional<Duration> getGlobalDuration() {
		List<Duration> durations = new ArrayList<>(3);
		sumDuration(filterExecs).ifPresent(durations::add);
		sumDuration(executables).ifPresent(durations::add);
		sumDuration(sources).ifPresent(durations::add);
		if(durations.size() != 3) {
			return Optional.empty();
		}
		Duration dur = Duration.valueOf(0);
		for(Duration d : durations) {
			dur = dur.add(d);
		}
		return Optional.of(dur);
	}

	/**
	 * @param identifiables
	 * @return
	 */
	private Optional<Duration> sumDuration(Collection<? extends Identifiable> identifiables) {
		Duration duration = Duration.valueOf(0);
		for(Identifiable identifiable : identifiables) {
			Optional<Duration> dur = identifiable.getDuration();
			if( ! dur.isPresent()) {
				return Optional.empty();
			}
			duration = duration.add(dur.get());
		}
		return Optional.of(duration);
	}

	/**
	 * @param code
	 */
	protected void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return
	 */
	protected synchronized String registerMe() {
		if(project == null) {
			return null;
		}
		Map<String, Identifiable> map = UNIQUE_ID_MAP.get(project);
		if(map == null) {
			map = UNIQUE_ID_MAP.addEmpty(project);
		}
		if(id != null) {
			if(map.containsKey(id)) {
				throw new RuntimeException("id already exists: " + id + " by " + map.get(id));
			}
		} else {
			String id = null;
			do {
				id = RandomStringUtils.randomAlphanumeric(5);
			} while(map.containsKey(id));
			this.id = id;
		}
		map.put(id, this);
		return id;
	}

	/**
	 *
	 */
	protected synchronized void unregisterMe() {
		Map<String, Identifiable> map = UNIQUE_ID_MAP.get(project);
		if(map == null || id == null) {
			return;
		}
		map.remove(id);
	}
}
