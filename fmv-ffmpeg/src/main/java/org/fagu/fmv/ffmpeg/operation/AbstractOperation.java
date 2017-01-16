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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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

	/**
	 *
	 */
	protected AbstractOperation() {
		filters = new ArrayList<>();
		rawFilters = new LinkedHashSet<>();
		globalParameters = new GlobalParameters(this);
		listeners = new ArrayList<>();
		processorMap = new LinkedHashMap<>();
		libLogs = new ArrayList<>();
	}

	/**
	 * @param filterNaming
	 * @param require
	 */
	protected AbstractOperation(FilterNaming filterNaming, Require require) {
		this();
		this.filterNaming = filterNaming;
		this.require = require;
		autoMap = AutoMaps.oneStreamByType(EnumSet.of(Type.VIDEO, Type.AUDIO), filterNaming);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#add(org.fagu.fmv.ffmpeg.operation.IOEntity[])
	 */
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

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#add(org.fagu.fmv.ffmpeg.operation.Parameter)
	 */
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

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#add(Filter)
	 */
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

		if(filter instanceof LibLog) {
			libLogs.add((LibLog)filter);
		}

		operationListener.eventPostAddFilter(this, filter);
		return true;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#addAll(java.util.Collection)
	 */
	@Override
	public void addAll(Collection<Parameter> parameters) {
		for(Parameter parameter : parameters) {
			add(parameter);
		}
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#removeParameter(java.lang.String)
	 */
	@Override
	public void removeParameter(String name) {
		getInputParameters().removeParameter(name);
		getOutputParameters().removeParameter(name);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getGlobalParameters()
	 */
	@Override
	public GlobalParameters getGlobalParameters() {
		return globalParameters;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#containsGlobalParameter(java.lang.String)
	 */
	@Override
	public boolean containsGlobalParameter(String parameter) {
		return globalParameters.contains(parameter);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#toArguments()
	 */
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
			for(;;) {
				FilterComplex f = it.next();
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

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#containsFilterComplexs()
	 */
	@Override
	public boolean containsFilterComplexs() {
		return containsFilterComplex;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getFilterComplexs()
	 */
	@Override
	public List<FilterComplex> getFilterComplexs() {
		List<FilterComplex> list = new ArrayList<>();
		for(Filter filter : filters) {
			list.add((FilterComplex)filter);
		}
		return list;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getAutoMap()
	 */
	@Override
	public AutoMap getAutoMap() {
		return autoMap;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#setAutoMap(org.fagu.fmv.ffmpeg.operation.AutoMap)
	 */
	@Override
	public void setAutoMap(AutoMap autoMap) {
		this.autoMap = autoMap;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#addListener(org.fagu.fmv.ffmpeg.operation.OperationListener)
	 */
	@Override
	public void addListener(OperationListener operationListener) {
		listeners.add(operationListener);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getListeners()
	 */
	@Override
	public List<OperationListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getInputProcessorStream()
	 */
	@Override
	public Stream<InputProcessor> getInputProcessorStream() {
		return getProcessorStream(InputProcessor.class);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getOutputProcessorStream()
	 */
	@Override
	public Stream<OutputProcessor> getOutputProcessorStream() {
		return getProcessorStream(OutputProcessor.class);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getProcessorStream(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Processor<?>> Stream<T> getProcessorStream(Class<T> cls) {
		return processorMap.values().stream().filter(p -> cls.isAssignableFrom(p.getClass())).map(p -> (T)p);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getProcessor(org.fagu.fmv.ffmpeg.operation.IOEntity)
	 */
	@Override
	public Processor<?> getProcessor(IOEntity ioEntity) {
		return processorMap.get(ioEntity);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getProcessors()
	 */
	@Override
	public Collection<Processor<?>> getProcessors() {
		return Collections.unmodifiableCollection(processorMap.values());
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getFilterNaming()
	 */
	@Override
	public FilterNaming getFilterNaming() {
		return filterNaming;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getFilterGraph()
	 */
	@Override
	public FilterGraph getFilterGraph() {
		return FilterGraph.of(this);
	}

	/**
	 * @return the executables
	 */
	@Override
	public List<LibLog> getLibLogs() {
		return Collections.unmodifiableList(libLogs);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#add(org.fagu.fmv.ffmpeg.operation.LibLog)
	 */
	@Override
	public void add(LibLog libLog) {
		libLogs.add(Objects.requireNonNull(libLog));
	}

	/**
	 * @return
	 */
	public Require require() {
		return require;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append("Operation[global=");
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
		buf.append(']');
		return buf.toString();
	}

	// ***************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected O getOThis() {
		return (O)this;
	}

	/**
	 * @return
	 */
	public List<Filter> getFilters() {
		return filters;
	}

	/**
	 * @param filterComplexs
	 * @return
	 */
	// protected Set<Label> findAutoMap0(List<FilterComplex> filterComplexs) {
	// return findAutoMap_ROOT(filterComplexs);
	// }

	/**
	 * @param filterComplexs
	 * @return
	 */
	// protected Set<Label> findAutoMap_TAIL(List<FilterComplex> filterComplexs) {
	// List<FilterComplex> roots = new ArrayList<>();
	// for(FilterComplex filterComplex : filterComplexs) {
	// System.out.println(filterComplex);
	// Map<IOKey, In> inputMap = filterComplex.getInputMap();
	// if(inputMap.isEmpty()) {
	// for(MediaInput mediaInput : filterComplex.getInputs()) {
	// if(mediaInput instanceof GeneratedSource) {
	// roots.add(filterComplex);
	// }
	// }
	// } else {
	// for(Entry<IOKey, In> entry : inputMap.entrySet()) {
	// FilterInput filterInput = entry.getValue().getFilterInput();
	// System.out.println(" " + filterInput.getClass());
	// if(filterInput instanceof InputProcessor) {
	// roots.add(filterComplex);
	// }
	// }
	// }
	// }
	//
	// System.out.println("ROOTS : ");
	// for(FilterComplex root : roots) {
	// System.out.println(" " + root);
	// }
	//
	// return null;
	// }

	/**
	 * @param filterComplexs
	 * @return
	 */
	// protected Set<Label> findAutoMap_ROOT(List<FilterComplex> filterComplexs) {
	// List<FilterComplex> roots = new ArrayList<>();
	//
	// // Map<in, List<filter>>
	// // MapList<Label, FilterComplex> byInMap = MultiValueMaps.hashMapArrayList();
	// // Map<out, List<filter>>
	// MapSet<Label, FilterComplex> byOutMap = MultiValueMaps.hashMapHashSet();
	// for(FilterComplex filterComplex : filterComplexs) {
	// System.out.println(filterComplex);
	// Map<IOKey, In> inputMap = filterComplex.getInputMap();
	// if(inputMap.isEmpty()) {
	// for(MediaInput mediaInput : filterComplex.getInputs()) {
	// if(mediaInput instanceof GeneratedSource) {
	// roots.add(filterComplex);
	// }
	// }
	// } else {
	// for(Entry<IOKey, In> entry : inputMap.entrySet()) {
	// FilterInput filterInput = entry.getValue().getFilterInput();
	// System.out.println(" " + filterInput.getClass());
	// if(filterInput instanceof InputProcessor) {
	// roots.add(filterComplex);
	// }
	// for(OutputKey outputKey : filterComplex.getOutputKeys()) {
	// byOutMap.add(outputKey.getLabel(), filterComplex);
	// }
	// }
	// }
	// }
	//
	// System.out.println();
	// System.out.println("ROOTS : ");
	// for(FilterComplex root : roots) {
	// System.out.println(" " + root);
	// }
	// System.out.println("WAYS : ");
	// for(Entry<Label, Set<FilterComplex>> lblEntry : byOutMap.entrySet()) {
	// for(FilterComplex filterComplex : lblEntry.getValue()) {
	// System.out.println(" " + lblEntry.getKey() + " <- " + filterComplex);
	// }
	// }
	//
	// return null;
	// }

	/**
	 * @param ioEntity
	 * @param processor
	 */
	protected Processor<?> addProcessor(IOEntity ioEntity, Processor<?> processor) {
		processorMap.put(ioEntity, processor);
		return processor;
	}

	/**
	 * @param processorClass
	 * @return
	 */
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

	/**
	 * @param filter
	 */
	private void addRawFilter(Filter filter) {
		if( ! (filter instanceof FilterComplexBase)) {
			require.filter(filter.name());
		}
		rawFilters.add(filter);
	}

	/**
	 * @param type
	 * @return
	 */
	private List<Filter> getFilterSimples(Type type) {
		return filters.stream().filter(filter -> ! (filter instanceof FilterComplexBase) && filter.getTypes().contains(type)).collect(Collectors
				.toList());
	}

	/**
	 * @return
	 */
	private boolean containsMap() {
		OutputParameters outputParameters = getOutputParameters();
		return outputParameters.contains("-map");
	}

	// private Set<String> findAutoMapSAVE(List<FilterComplex> filterComplexs) {
	// MapSet<String, Type> labelMap = MultiValueMaps.hashMapHashSet();
	//
	// Set<Label> labels = new HashSet<Label>();
	// System.out.println("#### FilterComplex");
	// for(FilterComplex filterComplex : filterComplexs) {
	// System.out.println(filterComplex);
	// for(OutputKey outputKey : filterComplex.getOutputKeys()) {
	// System.out.println(" " + outputKey.getLabel());
	// labels.add(outputKey.getLabel());
	// }
	// }
	// System.out.println("#### IOEntity");
	// InputParameters inputParameters = getInputParameters();
	// for(IOEntity ioEntity : inputParameters.getIOEntities()) {
	// System.out.println(ioEntity);
	// Processor<?> processor = inputParameters.getProcessor(ioEntity);
	// if(processor != null && processor instanceof FilterInput) {
	// System.out.println(" " + processor);
	// for(OutputKey outputKey : ((FilterInput)processor).getOutputKeys()) {
	// System.out.println(" " + outputKey.getLabel());
	// labels.add(outputKey.getLabel());
	// }
	// }
	// }
	// System.out.println("---------------");
	// for(FilterComplex filterComplex : filterComplexs) {
	// System.out.println(filterComplex);
	// for(IOKey ioKey : filterComplex.getInputKeys()) {
	// System.out.println(" " + ioKey.getLabel());
	// labels.remove(ioKey.getLabel());
	// }
	// }
	// SortedSet<String> mapSet = new TreeSet<String>();
	// for(Label label : labels) {
	// mapSet.add(filterNaming.generate(label));
	// }
	// return mapSet;
	// }

	/**
	 * @return
	 */
	private OperationListener operationListener() {
		return new Proxifier<>(OperationListener.class).addAll(listeners).proxify();
	}
}
