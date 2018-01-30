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

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.fagu.fmv.core.exec.Attributable;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.core.exec.FilterExec;
import org.fagu.fmv.core.exec.FilterFactory;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.exec.ObjectInvoker;
import org.fagu.fmv.core.exec.Source;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class GenericFilterExec extends Attributable implements FilterExec {

	/**
	 *
	 */
	public GenericFilterExec() {}

	/**
	 * @param code
	 */
	public GenericFilterExec(String code) {
		setCode(code);
	}

	/**
	 * @param project
	 * @param code
	 */
	public GenericFilterExec(Project project, String code) {
		super(project);
		setCode(code);
	}

	/**
	 * @see org.fagu.fmv.core.exec.FilterExec#makeFilter(org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder, Cache)
	 */
	@Override
	public Filter makeFilter(FFMPEGExecutorBuilder builder, Cache cache) {
		Filter filter = getFilter();

		// parameters
		ObjectInvoker.invoke(filter, attributeMap);

		FilterComplexBase filterComplex = filter instanceof FilterComplexBase ? (FilterComplexBase)filter : FilterComplex.create(filter);
		builder.filter(filterComplex);

		Set<Type> undeclaredTypes = populateWithIdentifiables(filterComplex, cache, builder);

		if( ! undeclaredTypes.isEmpty()) {
			undeclaredTypes(undeclaredTypes, filterComplex, cache, builder);
		}

		return filterComplex;
	}

	/**
	 * @see org.fagu.fmv.core.exec.Identifiable#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return getFilter().getTypes();
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(50);
		buf.append(getCode()).append(' ').append(super.toString());
		return buf.toString();
	}

	// *************************************************************

	/**
	 * @param filterComplex
	 * @param cache
	 * @param builder
	 * @return
	 */
	protected Set<Type> populateWithIdentifiables(FilterComplexBase filterComplex, Cache cache, FFMPEGExecutorBuilder builder) {
		Set<Type> undeclaredTypes = new HashSet<>(filterComplex.getTypes());
		for(Identifiable identifiable : getIdentifiableChildren()) {
			if(identifiable.isExecutable()) {
				File file = getProject().getFileCache().getFile((Executable)identifiable, cache);
				InputProcessor inputProcessor = builder.addMediaInputFile(file);
				addInputIntoFilter(filterComplex, inputProcessor, undeclaredTypes);

			} else if(identifiable.isSource()) {
				FilterInput filterInput = ((Source)identifiable).createAndAdd(builder);
				addInputIntoFilter(filterComplex, filterInput, undeclaredTypes);

			} else if(identifiable.isFilterExec()) {
				Filter inFilter = ((FilterExec)identifiable).makeFilter(builder, cache);
				addInputIntoFilter(filterComplex, (FilterComplexBase)inFilter, undeclaredTypes);
			}
		}
		return undeclaredTypes;
	}

	/**
	 * @param undeclaredTypes
	 * @param filterComplex
	 * @param cache
	 * @param builder
	 */
	protected void undeclaredTypes(Set<Type> undeclaredTypes, FilterComplexBase filterComplex, Cache cache, FFMPEGExecutorBuilder builder) {
		for(Type type : undeclaredTypes) {
			builder.getFFMPEGOperation()
					.getInputProcessorStream()
					.filter(ip -> ip.contains(type))
					.forEach(ip -> filterComplex.addInput(ip, type));
		}
	}

	/**
	 * @return
	 */
	protected Filter getFilter() {
		return FilterFactory.getSingleton().get(getCode());
	}

	/**
	 * @param filterComplexBase
	 * @param filterInput
	 * @param undeclaredTypes
	 */
	protected void addInputIntoFilter(FilterComplexBase filterComplexBase, FilterInput filterInput, Set<Type> undeclaredTypes) {
		filterComplexBase.addInput(filterInput);
		removeUndeclaredTypes(undeclaredTypes, filterInput);
	}

	/**
	 * @param undeclaredTypes
	 * @param withFilterInput
	 */
	protected void removeUndeclaredTypes(Set<Type> undeclaredTypes, FilterInput withFilterInput) {
		Iterator<Type> iterator = undeclaredTypes.iterator();
		while(iterator.hasNext()) {
			if(withFilterInput.contains(iterator.next())) {
				iterator.remove();
			}
		}
	}

}
