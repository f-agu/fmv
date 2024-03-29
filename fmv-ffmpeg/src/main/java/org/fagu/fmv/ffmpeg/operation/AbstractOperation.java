package org.fagu.fmv.ffmpeg.operation;

/*
 * #%L
 * fmv-ffmpeg
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
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.filter.Label;
import org.fagu.fmv.ffmpeg.filter.graph.FilterGraph;
import org.fagu.fmv.ffmpeg.require.Require;
import org.fagu.fmv.utils.Proxifier;


/**
 * @author f.agu
 */
public abstract class AbstractOperation<R, O> implements Operation<R, O> {

	private final GlobalParameters globalParameters;

	private boolean containsFilterComplex;

	private List<Filter> filters;

	private final Set<Filter> rawFilters;

	private FilterNaming filterNaming;

	private Require require;

	private AutoMap autoMap;

	private final List<OperationListener> listeners;

	private final Map<IOEntity, Processor<?>> processorMap;

	private final List<LibLog> libLogs;

	protected AbstractOperation() {
		filters = new ArrayList<>();
		rawFilters = new LinkedHashSet<>();
		globalParameters = new GlobalParameters(this);
		listeners = new ArrayList<>();
		processorMap = new LinkedHashMap<>();
		libLogs = new ArrayList<>();
	}

	protected AbstractOperation(FilterNaming filterNaming, Require require) {
		this();
		this.filterNaming = filterNaming;
		this.require = require;
		autoMap = AutoMaps.oneStreamByType(EnumSet.of(Type.VIDEO, Type.AUDIO), filterNaming);
	}

	@Override
	public O add(IOEntity... entities) {
		OperationListener operationListener = operationListener();
		for(IOEntity ioEntity : entities) {
			operationListener.eventPreAddIOEntity(this, ioEntity);
			if(ioEntity instanceof MediaInput) {
				getInputParameters().add(ioEntity, null);
			} else if(ioEntity instanceof MediaOutput) {
				getOutputParameters().add(ioEntity, null);
			} else {
				throw new IllegalArgumentException("IOEntity unknown: " + ioEntity.getClass().getSimpleName());
			}
			operationListener.eventPostAddIOEntity(this, ioEntity);
		}
		return getOThis();
	}

	@Override
	public O add(Parameter parameter) {
		OperationListener operationListener = operationListener();
		operationListener.eventPreAddParameter(this, parameter);
		IOEntity ioEntity = parameter.getIOEntity();
		if(ioEntity instanceof MediaInput) {
			getInputParameters().add(parameter);
		} else if(ioEntity instanceof MediaOutput) {
			getOutputParameters().add(parameter);
		} else if(parameter.isGlobal()) {
			getGlobalParameters().add(parameter);
		} else {
			throw new IllegalArgumentException("IOEntity unknown: " + ioEntity.getClass().getSimpleName() + " for " + parameter);
		}
		operationListener.eventPostAddParameter(this, parameter);
		return getOThis();
	}

	@Override
	public boolean add(Filter filter) {
		if(rawFilters.contains(filter)) {
			return false;
		}
		OperationListener operationListener = operationListener();
		operationListener.eventPreAddFilter(this, filter);
		addRawFilter(filter);
		if(filter instanceof FilterComplexBase) {
			if( ! containsFilterComplex) {
				containsFilterComplex = true;
				List<Filter> newFilters = new ArrayList<>();
				for(Filter f : filters) {
					FilterComplex fc = FilterComplex.create(f);
					newFilters.add(fc);
					addRawFilter(fc);
				}
				filters = newFilters;
			}
			filters.add(filter);
		} else {
			if(containsFilterComplex) {
				FilterComplex fc = FilterComplex.create(filter);
				filters.add(fc);
				addRawFilter(fc);
			} else {
				filters.add(filter);
			}
		}

		if(filter instanceof LibLog ll) {
			libLogs.add(ll);
		}

		operationListener.eventPostAddFilter(this, filter);
		return true;
	}

	@Override
	public void addAll(Collection<Parameter> parameters) {
		for(Parameter parameter : parameters) {
			add(parameter);
		}
	}

	@Override
	public void removeParameter(String name) {
		getInputParameters().removeParameter(name);
		getOutputParameters().removeParameter(name);
	}

	@Override
	public GlobalParameters getGlobalParameters() {
		return globalParameters;
	}

	@Override
	public boolean containsGlobalParameter(String parameter) {
		return globalParameters.contains(parameter);
	}

	@Override
	public List<String> toArguments() {
		OperationListener operationListener = operationListener();
		operationListener.eventPreToArguments(this);

		List<String> arguments = new ArrayList<>();
		// global
		getGlobalParameters().toArguments(arguments);
		// input
		getInputParameters().toArguments(arguments);

		// filter_complex
		if(containsFilterComplexs()) {
			List<FilterComplex> filterComplexs = getFilterComplexs();
			arguments.add("-filter_complex");

			StringBuilder fbuf = new StringBuilder();
			Iterator<FilterComplex> it = filterComplexs.iterator();
			boolean useLabels = autoMap.useLabels();
			for(;;) {
				FilterComplex f = it.next();
				if( ! useLabels) {
					f.clearInput();
					f.clearOutput();
				}
				fbuf.append(f.toString());
				if( ! it.hasNext()) {
					break;
				}
				fbuf.append(';');
			}
			arguments.add(fbuf.toString());

			if( ! containsMap()) { // auto-map
				try {
					for(Label label : autoMap.find(this)) {
						arguments.add("-map");
						arguments.add(filterNaming.generateBrackets(label));
					}
				} catch(RuntimeException e) {
					throw new RuntimeException(arguments.toString(), e);
				}
			}
		} else {

			// filter
			for(Type type : Type.values()) {
				StringBuilder fbuf = new StringBuilder();
				List<Filter> filterSimples = getFilterSimples(type);
				if( ! filterSimples.isEmpty()) {
					Iterator<Filter> it = filterSimples.iterator();
					for(;;) {
						Filter f = it.next();
						fbuf.append(f.toString());
						if( ! it.hasNext()) {
							break;
						}
						fbuf.append(',');
					}
					String fstr = fbuf.toString();
					if(StringUtils.isNotBlank(fstr)) {
						arguments.add("-filter:" + type.code());
						arguments.add(fstr);
					}
				}
			}
		}

		// output
		getOutputParameters().toArguments(arguments);

		operationListener.eventPostToArguments(this);
		return arguments;
	}

	@Override
	public boolean containsFilterComplexs() {
		return containsFilterComplex;
	}

	@Override
	public List<FilterComplex> getFilterComplexs() {
		List<FilterComplex> list = new ArrayList<>();
		for(Filter filter : filters) {
			list.add((FilterComplex)filter);
		}
		return list;
	}

	@Override
	public AutoMap getAutoMap() {
		return autoMap;
	}

	@Override
	public void setAutoMap(AutoMap autoMap) {
		this.autoMap = Objects.requireNonNull(autoMap);
	}

	@Override
	public void addListener(OperationListener operationListener) {
		listeners.add(operationListener);
	}

	@Override
	public List<OperationListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	@Override
	public Stream<InputProcessor> getInputProcessorStream() {
		return getProcessorStream(InputProcessor.class);
	}

	@Override
	public Stream<OutputProcessor> getOutputProcessorStream() {
		return getProcessorStream(OutputProcessor.class);
	}

	@Override
	public boolean removeProcessorStream(Processor<?> processor) {
		Iterator<Entry<IOEntity, Processor<?>>> iterator = processorMap.entrySet().iterator();
		while(iterator.hasNext()) {
			if(iterator.next().getValue() == processor) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Processor<?>> Stream<T> getProcessorStream(Class<T> cls) {
		return processorMap.values()
				.stream()
				.filter(p -> p != null && cls.isAssignableFrom(p.getClass()))
				.map(p -> (T)p);
	}

	@Override
	public Processor<?> getProcessor(IOEntity ioEntity) {
		return processorMap.get(ioEntity);
	}

	@Override
	public Collection<Processor<?>> getProcessors() {
		return Collections.unmodifiableCollection(processorMap.values());
	}

	@Override
	public FilterNaming getFilterNaming() {
		return filterNaming;
	}

	@Override
	public FilterGraph getFilterGraph() {
		return FilterGraph.of(this);
	}

	@Override
	public List<LibLog> getLibLogs() {
		return Collections.unmodifiableList(libLogs);
	}

	@Override
	public void add(LibLog libLog) {
		libLogs.add(Objects.requireNonNull(libLog));
	}

	public Require require() {
		return require;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100)
				.append("Operation[global=");
		if( ! globalParameters.isEmpty()) {
			buf.append(globalParameters);
		}
		InputParameters inputParameters = getInputParameters();
		buf.append(";input=");
		if( ! inputParameters.isEmpty()) {
			buf.append(inputParameters);
		}
		OutputParameters outputParameters = getOutputParameters();
		buf.append(";output=");
		if( ! outputParameters.isEmpty()) {
			buf.append(outputParameters);
		}
		if( ! filters.isEmpty()) {
			buf.append(";filter=").append(filters);
		}
		return buf.append(']')
				.toString();
	}

	// ***************************************

	@SuppressWarnings("unchecked")
	protected O getOThis() {
		return (O)this;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	protected Processor<?> addProcessor(IOEntity ioEntity, Processor<?> processor) {
		processorMap.put(ioEntity, processor);
		return processor;
	}

	protected int countIOEntity(Class<?> processorClass) {
		int count = 0;
		for(Processor<?> processor : processorMap.values()) {
			if(processorClass.isAssignableFrom(processor.getClass())) {
				++count;
			}
		}
		return count;
	}

	// ***************************************

	private void addRawFilter(Filter filter) {
		if( ! (filter instanceof FilterComplexBase)) {
			require.filter(filter.name());
		}
		rawFilters.add(filter);
	}

	private List<Filter> getFilterSimples(Type type) {
		return filters.stream()
				.filter(filter -> ! (filter instanceof FilterComplexBase) && filter.getTypes().contains(type))
				.toList();
	}

	private boolean containsMap() {
		OutputParameters outputParameters = getOutputParameters();
		return outputParameters.contains("-map");
	}

	private OperationListener operationListener() {
		return new Proxifier<>(OperationListener.class).addAll(listeners).proxify();
	}
}
