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

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.filter.graph.FilterGraph;
import org.fagu.fmv.soft.exec.ReadLine;


/**
 * @author f.agu
 */
public interface Operation<R, O> {

	/**
	 * @param entities
	 */
	O add(IOEntity... entities);

	/**
	 * @param parameter
	 */
	O add(Parameter parameter);

	/**
	 * @param filter
	 */
	boolean add(Filter filter);

	/**
	 * @param parameters
	 */
	void addAll(Collection<Parameter> parameters);

	/**
	 * @param name
	 */
	void removeParameter(String name);

	/**
	 * @return
	 */
	GlobalParameters getGlobalParameters();

	/**
	 * @return
	 */
	InputParameters getInputParameters();

	/**
	 * @return
	 */
	OutputParameters getOutputParameters();

	/**
	 * @param parameter
	 * @return
	 */
	boolean containsGlobalParameter(String parameter);

	/**
	 * @return
	 */
	Stream<InputProcessor> getInputProcessorStream();

	/**
	 * @return
	 */
	Stream<OutputProcessor> getOutputProcessorStream();

	/**
	 * @return
	 */
	<T extends Processor<?>> Stream<T> getProcessorStream(Class<T> cls);

	/**
	 * @param ioEntity
	 * @return
	 */
	Processor<?> getProcessor(IOEntity ioEntity);

	/**
	 * @param ioEntity
	 * @return
	 */
	Collection<Processor<?>> getProcessors();

	/**
	 * @return
	 */
	FilterNaming getFilterNaming();

	/**
	 * @return
	 */
	FilterGraph getFilterGraph();

	/**
	 * @return
	 */
	String getFFName();

	/**
	 * @return
	 */
	List<LibLog> getLibLogs();

	/**
	 * @param libLog
	 */
	void add(LibLog libLog);

	/**
	 * @return
	 */
	ReadLine getOutReadLine();

	/**
	 * @return
	 */
	ReadLine getErrReadLine();

	/**
	 * @return
	 */
	R getResult();

	/**
	 * @return
	 */
	List<String> toArguments();

	/**
	 * @return
	 */
	boolean containsFilterComplexs();

	/**
	 * @return
	 */
	List<FilterComplex> getFilterComplexs();

	/**
	 * @return
	 */
	AutoMap getAutoMap();

	/**
	 * @param autoMap
	 */
	void setAutoMap(AutoMap autoMap);

	/**
	 * @param operationListener
	 */
	void addListener(OperationListener operationListener);

	/**
	 * @return
	 */
	List<OperationListener> getListeners();
}
