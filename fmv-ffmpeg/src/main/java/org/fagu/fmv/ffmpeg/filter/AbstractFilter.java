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
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.Element;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public abstract class AbstractFilter extends Element<Filter> implements Filter {

	protected Operation<?, ?> operation;

	protected FilterNaming filterNaming;

	protected AbstractFilter(String name) {
		super(name);
	}

	@Override
	public final void beforeAdd(Operation<?, ?> operation) {
		if(operation == null) {
			return;
		}
		this.operation = operation;
		this.filterNaming = operation.getFilterNaming();

		beforeAddAround(operation, filterNaming);

		if( ! (this instanceof Combined)) {
			operation.add(this);
		}
		InjectBuilder.inject(this, operation);
	}

	@Override
	public void setMainParameter(String value) {
		clearParameters();
		parameter("", value);
	}

	@Override
	public void upgrade(Operation<?, ?> operation) {
		for(Processor<?> processor : operation.getProcessors()) {
			if(processor instanceof InputProcessor ip) {
				upgradeInputProcessor(ip);
			} else if(processor instanceof OutputProcessor op) {
				upgradeOutputProcessor(op);
			}
		}
	}

	@Override
	public void upgradeInputProcessor(InputProcessor inputProcessor) {}

	@Override
	public void upgradeOutputProcessor(OutputProcessor outputProcessor) {}

	@Override
	public boolean containsParameter(String name) {
		return parameterMap.containsKey(name);
	}

	@Override
	public String removeParameter(String name) {
		PseudoParam removed = parameterMap.remove(name);
		return removed != null ? removed.value() : null;
	}

	@Override
	public void clearParameters() {
		parameterMap.clear();
	}

	@Override
	public Collection<Type> getInputTypes() {
		return getTypes();
	}

	@Override
	public Collection<Type> getOutputTypes() {
		return getTypes();
	}

	@Override
	public Filters getFilterType() {
		return Filters.byName(name);
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(32)
				.append(name);
		if( ! parameterMap.isEmpty()) {
			buf.append('=');
			Iterator<Entry<String, PseudoParam>> it = parameterMap.entrySet().iterator();
			for(;;) {
				Entry<String, PseudoParam> entry = it.next();
				if(StringUtils.isNotEmpty(entry.getKey())) {
					buf.append(entry.getKey()).append('=');
				}
				buf.append(entry.getValue().value());
				if( ! it.hasNext()) {
					break;
				}
				buf.append(':');
			}
		}
		return buf.toString();
	}

	// **************************************************

	protected void beforeAddAround(Operation<?, ?> operation, FilterNaming filterNaming) {}
}
