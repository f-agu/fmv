package org.fagu.fmv.ffmpeg;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.fagu.fmv.ffmpeg.flags.Flags;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.Parameter;
import org.fagu.fmv.ffmpeg.operation.Parameter.Way;
import org.fagu.fmv.ffmpeg.operation.Processor;


/**
 * @author f.agu
 */
public abstract class Element<M> {

	// ------------------------------------------

	/**
	 * @author f.agu
	 */
	protected class PseudoParam {

		private final Parameter parameter;

		private final String name;

		private String value;

		/**
		 * @param parameter
		 */
		PseudoParam(Parameter parameter) {
			name = null;
			value = null;
			this.parameter = parameter;
		}

		/**
		 * @param name
		 * @param value
		 */
		private PseudoParam(String name, String value) {
			this.name = name;
			this.value = value;
			parameter = null;
		}

		/**
		 * @return
		 */
		public String value() {
			return value;
		}

		/**
		 * @param ioEntity
		 * @return
		 */
		private Parameter getParameter(IOEntity ioEntity) {
			if(parameter != null) {
				if(parameter.getIOEntity() != ioEntity) {
					throw new RuntimeException("Not same IOEntity !: " + parameter.getIOEntity() + " != " + ioEntity);
				}
				return parameter;
			}
			if(value == null) {
				return Parameter.create(defaultWay, ioEntity, name);
			}
			return Parameter.create(defaultWay, ioEntity, name, value);
		}
	}

	// ------------------------------------------

	protected final String name;

	private Way defaultWay = Way.BEFORE;

	protected final Map<String, PseudoParam> parameterMap;

	/**
	 * @param name
	 */
	public Element(String name) {
		this.name = name;
		parameterMap = new LinkedHashMap<>();
	}

	/**
	 * @return
	 */
	public String name() {
		return name;
	}

	/**
	 * @param name
	 * @param value
	 */
	public M parameter(String name, String value) {
		parameterMap.put(name, new PseudoParam(name, value));
		return getMThis();
	}

	// ****************************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected M getMThis() {
		return (M)this;
	}

	// ****************************************************

	/**
	 * @param processor
	 * @param ioEntity
	 */
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		parameterMap.values().stream().forEach(p -> processor.add(p.getParameter(ioEntity)));
	}

	// ****************************************************

	/**
	 * @param processor
	 * @param ioEntity
	 * @param name
	 * @param value
	 */
	protected void parameter(Processor<?> processor, IOEntity ioEntity, String name, String value) {
		processor.add(Parameter.before(ioEntity, name, value));
	}

	/**
	 * @param processor
	 * @param ioEntity
	 * @param name
	 * @param flags
	 */
	protected void parameter(Processor<?> processor, IOEntity ioEntity, String name, @SuppressWarnings("rawtypes") Collection<? extends Flags> flags) {
		parameter(processor, ioEntity, name, flagsToString(flags));
	}

	/**
	 * @param flags
	 * @return
	 */
	protected String flagsToString(@SuppressWarnings("rawtypes") Collection<? extends Flags> flags) {
		return flags.stream().map(Flags::toString).collect(Collectors.joining());
	}
}
