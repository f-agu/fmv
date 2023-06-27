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

	void beforeAdd(Operation<?, ?> operation);

	String name();

	void setMainParameter(String value);

	void upgrade(Operation<?, ?> operation);

	void upgradeInputProcessor(InputProcessor inputProcessor);

	void upgradeOutputProcessor(OutputProcessor outputProcessor);

	Filter parameter(String name, String value);

	boolean containsParameter(String name);

	String removeParameter(String name);

	void clearParameters();

	Set<Type> getTypes();

	Collection<Type> getInputTypes();

	Collection<Type> getOutputTypes();

	Filters getFilterType();

}
