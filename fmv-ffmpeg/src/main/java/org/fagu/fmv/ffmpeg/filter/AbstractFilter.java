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
import java.util.Set;

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

	/**
	 * @param name
	 */
	public AbstractFilter(String name) {
		super(name);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#beforeAdd(org.fagu.fmv.ffmpeg.operation.Operation)
	 */
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

	/**
	 * @param value
	 */
	@Override
	public void setMainParameter(String value) {
		clearParameters();
		parameter("", value);
	}

	/**
	 * @param operation
	 */
	@Override
	public void upgrade(Operation<?, ?> operation) {
		for(Processor<?> processor : operation.getProcessors()) {
			if(processor instanceof InputProcessor) {
				upgradeInputProcessor((InputProcessor)processor);
			} else if(processor instanceof OutputProcessor) {
				upgradeOutputProcessor((OutputProcessor)processor);
			}
		}
	}

	/**
	 * @param inputProcessor
	 */
	@Override
	public void upgradeInputProcessor(InputProcessor inputProcessor) {}

	/**
	 * @param outputProcessor
	 */
	@Override
	public void upgradeOutputProcessor(OutputProcessor outputProcessor) {}

	/**
	 * @param name
	 * @return
	 */
	@Override
	public boolean containsParameter(String name) {
		return parameterMap.containsKey(name);
	}

	/**
	 * @param name
	 */
	@Override
	public String removeParameter(String name) {
		PseudoParam removed = parameterMap.remove(name);
		return removed != null ? removed.value() : null;
	}

	/**
	 *
	 */
	@Override
	public void clearParameters() {
		parameterMap.clear();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getInputTypes()
	 */
	@Override
	public Collection<Type> getInputTypes() {
		return getTypes();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getOutputTypes()
	 */
	@Override
	public Collection<Type> getOutputTypes() {
		return getTypes();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getFilterType()
	 */
	@Override
	public Filters getFilterType() {
		return Filters.byName(name);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(32);
		buf.append(name);
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

	/**
	 * @return
	 */
	@Override
	abstract public Set<Type> getTypes();

	// **************************************************

	/**
	 * @param operation
	 * @param filterNaming
	 */
	protected void beforeAddAround(Operation<?, ?> operation, FilterNaming filterNaming) {}
}
