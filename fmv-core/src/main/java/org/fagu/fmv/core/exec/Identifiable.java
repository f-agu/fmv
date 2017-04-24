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

import java.util.List;
import java.util.function.Predicate;

import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.ffmpeg.utils.Duration;


/**
 * @author f.agu
 */
public interface Identifiable extends IODom {

	/**
	 * @return
	 */
	String getId();

	/**
	 * @return
	 */
	String getCode();

	/**
	 * @return the project
	 */
	Project getProject();

	/**
	 * @return
	 */
	Identifiable getParent();

	/**
	 * @return
	 */
	boolean isRoot();

	/**
	 * @return
	 */
	int getDepth();

	/**
	 * @param filter
	 * @return
	 */
	int getDepth(Predicate<Identifiable> filter);

	/**
	 * @return
	 */
	boolean isFilterExec();

	/**
	 * @return
	 */
	boolean isExecutable();

	/**
	 * @return
	 */
	boolean isSource();

	/**
	 * @param identifiable
	 * @return
	 */
	Identifiable add(Identifiable identifiable);

	/**
	 * @param filterExec
	 * @return
	 */
	Identifiable add(FilterExec filterExec);

	/**
	 * @param executable
	 * @return
	 */
	Identifiable add(Executable executable);

	/**
	 * @param source
	 * @return
	 */
	Identifiable add(Source source);

	/**
	 * @return
	 */
	List<Identifiable> getIdentifiableChildren();

	/**
	 * @return
	 */
	List<FilterExec> getFilters();

	/**
	 * @return
	 */
	List<Executable> getExecutables();

	/**
	 * @param identifiable
	 * @return
	 */
	boolean remove(Identifiable identifiable);

	/**
	 * @return
	 */
	List<Source> getSources();

	/**
	 * 
	 */
	void modified();

	/**
	 * @return
	 */
	Hash getHash();

	/**
	 * @return
	 */
	Duration getDuration();

	/**
	 * 
	 */
	void resetDuration();

}
