package org.fagu.fmv.ffmpeg.filter;

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
import java.util.Set;

import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public interface Filter {

	/**
	 * @param operation
	 */
	void beforeAdd(Operation<?, ?> operation);

	/**
	 * @return the name
	 */
	String name();

	/**
	 * @param value
	 */
	void setMainParameter(String value);

	/**
	 * @param operation
	 */
	void upgrade(Operation<?, ?> operation);

	/**
	 * @param inputProcessor
	 */
	void upgradeInputProcessor(InputProcessor inputProcessor);

	/**
	 * @param outputProcessor
	 */
	void upgradeOutputProcessor(OutputProcessor outputProcessor);

	/**
	 * @param name
	 * @param value
	 */
	Filter parameter(String name, String value);

	/**
	 * @param name
	 * @return
	 */
	boolean containsParameter(String name);

	/**
	 * @param name
	 */
	String removeParameter(String name);

	/**
	 *
	 */
	void clearParameters();

	/**
	 * @return
	 */
	Set<Type> getTypes();

	/**
	 * @return
	 */
	Collection<Type> getInputTypes();

	/**
	 * @return
	 */
	Collection<Type> getOutputTypes();

	/**
	 * @return
	 */
	Filters getFilterType();

}
