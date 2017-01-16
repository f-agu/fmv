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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fagu.fmv.core.exec.ExecutableOption;
import org.fagu.fmv.core.exec.FileCache.Cache;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.impl.Concat;


/**
 * @author f.agu
 */
public class ConcatExecutable extends GenericExecutable {

	/**
	 *
	 */
	public ConcatExecutable() {}

	/**
	 * @param project
	 */
	public ConcatExecutable(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Executable#getCode()
	 */
	@Override
	public String getCode() {
		return "concat";
	}

	/**
	 * @see org.fagu.fmv.core.exec.executable.AbstractExecutable#getOptions()
	 */
	@Override
	public Set<ExecutableOption> getOptions() {
		Set<ExecutableOption> opts = super.getOptions();
		if(opts.contains(ExecutableOption.PROPAGATION_MAKE_BACKGROUND) || opts.contains(ExecutableOption.STOP_PROPAGATION_MAKE_BACKGROUND)) {
			return opts;
		}
		if(getIdentifiableChildren().size() < 3) {
			return opts;
		}
		opts = new HashSet<>(opts);
		opts.add(ExecutableOption.STOP_PROPAGATION_MAKE_BACKGROUND);
		return opts;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append(getCode()).append(' ').append(executables.size()).append(' ').append("sources");
		return buf.toString();
	}

	// *****************************************************

	/**
	 * @see org.fagu.fmv.core.exec.executable.GenericExecutable#populateWithIdentifiables(java.io.File,
	 *      org.fagu.fmv.core.exec.FileCache.Cache, org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder)
	 */
	@Override
	protected List<FilterInput> populateWithIdentifiables(File toFile, Cache cache, FFMPEGExecutorBuilder builder) {
		List<FilterInput> filterInputs = super.populateWithIdentifiables(toFile, cache, builder);

		Concat concat = Concat.create(builder, filterInputs).countInputs(filterInputs.size());
		builder.filter(concat);

		filterInputs.add(concat);

		return filterInputs;
	}
}
