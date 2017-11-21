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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.FFUtils;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.core.exec.FilterExec;
import org.fagu.fmv.core.exec.ObjectInvoker;
import org.fagu.fmv.core.exec.Source;
import org.fagu.fmv.core.project.OutputInfos;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;


/**
 * @author f.agu
 */
public class GenericExecutable extends AbstractExecutable {

	/**
	 * 
	 */
	public GenericExecutable() {}

	/**
	 * @param project
	 */
	public GenericExecutable(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getCode()
	 */
	@Override
	public String getCode() {
		return "generic";
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#execute()
	 */
	@Override
	public void execute(File toFile, Cache cache) throws IOException {
		if( ! hasChildren()) {
			return;
		}

		FFMPEGExecutorBuilder builder = FFUtils.builder(getProject());

		populateWithIdentifiables(toFile, cache, builder);

		OutputProcessor outputProcessor = outputProcessor(builder, toFile, cache);
		ObjectInvoker.invoke(outputProcessor, attributeMap);
		fixOutput(outputProcessor);

		FFExecutor<Object> executor = builder.build();
		executor.execute();
	}

	/**
	 * @return
	 */
	public boolean hasChildren() {
		return ! filterExecs.isEmpty() || ! executables.isEmpty() || ! sources.isEmpty();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getCode() + ' ' + super.toString();
	}

	// *******************************************************

	/**
	 * @param toFile
	 * @param cache
	 * @param builder
	 */
	protected List<FilterInput> populateWithIdentifiables(File toFile, Cache cache, FFMPEGExecutorBuilder builder) {
		List<FilterInput> filterInputs = new ArrayList<>();

		// executable
		for(Executable executable : executables) {
			File file = getProject().getFileCache().getFile(executable, cache);
			filterInputs.add(builder.addMediaInputFile(file));
		}

		// source
		for(Source source : sources) {
			filterInputs.add(source.createAndAdd(builder));
		}

		// filters
		for(FilterExec filterExec : getFilters()) {
			Filter filter = filterExec.makeFilter(builder, cache);
			builder.filter(filter);
			if(filter instanceof FilterComplexBase) {
				filterInputs.add((FilterComplexBase)filter);
			}
		}

		return filterInputs;
	}

	/**
	 * @param outputProcessor
	 */
	protected void fixOutput(OutputProcessor outputProcessor) {
		if(executables.isEmpty() && getFilters().isEmpty() && ! sources.isEmpty()) {
			OutputInfos outputInfos = getProject().getOutputInfos();
			outputProcessor.videoRate(outputInfos.getFrameRate().invert().intValue());
		}
	}
}
