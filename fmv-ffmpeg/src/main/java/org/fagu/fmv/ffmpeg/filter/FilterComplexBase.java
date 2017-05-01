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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Duration;


/**
 * @author f.agu
 */
public abstract class FilterComplexBase extends AbstractFilter implements FilterInput {

	protected final Map<IOKey, In> inputMap;

	protected final Map<Label, OutputKey> outputList;

	protected final List<String> mapList;

	// -------------------------------------------------

	/**
	 * @author f.agu
	 */
	public class In {

		private FilterInput filterInput;

		private Type type;

		/**
		 * 
		 */
		private In() {}

		/**
		 * @param filterInput
		 */
		private In(FilterInput filterInput) {
			this(filterInput, null);
		}

		/**
		 * @param filterInput
		 * @param type
		 */
		private In(FilterInput filterInput, Type type) {
			this.filterInput = filterInput;
			this.type = type;
		}

		// ******************************

		/**
		 * @return the filterInput
		 */
		public FilterInput getFilterInput() {
			return filterInput;
		}

		/**
		 * @return the type
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("In(").append(filterInput);
			if(type != null) {
				buf.append('/').append(type);
			}
			buf.append(')');
			return buf.toString();
		}

		// ******************************

		/**
		 * @return
		 */
		protected List<MediaInput> getInputs() {
			return filterInput.getInputs();
		}

		/**
		 * @param type
		 * @return
		 */
		protected boolean contains(Type type) {
			if(this.type != null) {
				return this.type == type;
			}
			return filterInput.contains(type);
		}

	}

	// -------------------------------------------------

	/**
	 * @param name
	 */
	public FilterComplexBase(String name) {
		super(name);
		inputMap = new LinkedHashMap<>();
		outputList = new LinkedHashMap<>();
		mapList = new ArrayList<>();
	}

	/**
	 * @return
	 */
	public FilterNaming getFilterNaming() {
		return filterNaming;
	}

	/**
	 * @param filterNaming the filterNaming to set
	 */
	public void setFilterNaming(FilterNaming filterNaming) {
		this.filterNaming = filterNaming;
	}

	/**
	 * @param filterInput
	 * @param types
	 * @return
	 */
	public FilterComplexBase addInput(FilterInput filterInput, Type... types) {
		if(filterInput == null) {
			return this;
		}
		boolean inserted = false;
		for(OutputKey outputKey : filterInput.getOutputKeys()) {
			if(types.length == 0) {
				inputMap.put(IOKey.of(outputKey), new In(filterInput));
				inserted = true;
			} else {
				for(Type type : types) {
					if(getTypes().contains(type) && outputKey.contains(type)) {
						// System.out.println(filterInput + "/" + type + " --> " + this);
						inputMap.put(IOKey.of(outputKey, type, hasExplicitType()), new In(filterInput, type));
						inserted = true;
					}
				}
			}
		}
		if(inserted && filterInput instanceof AbstractFilter) {
			AbstractFilter filter = (AbstractFilter)filterInput;
			InjectBuilder.inject(this, filter.operation);
		}
		return this;
	}

	/**
	 * @param filterInput
	 * @return
	 */
	public boolean contains(FilterInput filterInput) {
		for(In in : inputMap.values()) {
			if(in.filterInput == filterInput) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param type
	 * @return
	 */
	public int countInput(Type type) {
		int count = 0;
		for(In input : inputMap.values()) {
			if(input.contains(type)) {
				++count;
			}
		}
		return count;
	}

	/**
	 * @param type
	 * @return
	 */
	@Override
	public boolean contains(Type type) {
		return getTypes().contains(type);
	}

	/**
	 * @param type
	 * @return
	 */
	public boolean containsInput(Type type) {
		for(In in : inputMap.values()) {
			if(in.contains(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	@Override
	public List<MediaInput> getInputs() {
		List<MediaInput> inputs = new ArrayList<MediaInput>();
		for(In in : inputMap.values()) {
			inputs.addAll(in.getInputs());
		}
		if(this instanceof MediaInput) {
			inputs.add((MediaInput)this);
		}
		return inputs;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getInputKeys()
	 */
	@Override
	public Set<IOKey> getInputKeys() {
		return inputMap.keySet();
	}

	/**
	 * 
	 */
	public void clearInput() {
		inputMap.clear();
	}

	/**
	 * @return the inputMap
	 */
	public Map<IOKey, In> getInputMap() {
		return Collections.unmodifiableMap(inputMap);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.utils.Durable#getDuration()
	 */
	@Override
	public Optional<Duration> getDuration() {
		return inputMap.values()
				.stream()
				.map(i -> i.filterInput.getDuration().orElse(null))
				.filter(Objects::nonNull)
				.max((d1, d2) -> d1.compareTo(d2));
	}

	/**
	 * 
	 */
	public OutputKey addOutput() {
		return addOutput(Label.intermediate(StringUtils.substring(name(), 0, 3)));
	}

	/**
	 * @param outputLabel
	 * @return
	 */
	public OutputKey addOutput(Label outputLabel) {
		if(outputList.containsKey(outputLabel)) {
			throw new IllegalArgumentException("Already used: " + outputLabel);
		}
		OutputKey outputKey = new OutputKey(this, outputLabel);
		outputList.put(outputLabel, outputKey);
		return outputKey;
	}

	/**
	 * @return
	 */
	@Override
	public List<OutputKey> getOutputKeys() {
		return new ArrayList<OutputKey>(outputList.values());
	}

	/**
	 * 
	 */
	public void clearOutput() {
		outputList.clear();
	}

	/**
	 * @return
	 */
	public int countVariousInput() {
		Set<MediaInput> inputs = new HashSet<MediaInput>();
		int countSrcGen = 0;
		for(MediaInput input : getInputs()) {
			inputs.add(input);
			if(input instanceof GeneratedSource) {
				++countSrcGen;
			}
		}
		return inputs.size() - (countSrcGen >= 1 ? 1 : 0);
	}

	/**
	 * @param mapLabel
	 * @return
	 */
	public FilterComplexBase addMap(String mapLabel) {
		mapList.add(mapLabel);
		return this;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(super.toString());
	}

	/**
	 * @param middle
	 * @return
	 */
	public String toString(String middle) {
		StringBuilder buf = new StringBuilder();
		if( ! inputMap.isEmpty()) {
			org.fagu.fmv.utils.StringJoin.append(buf, ioKeyToString(inputMap.keySet().iterator()), StringUtils.EMPTY);
			buf.append(' ');
		}
		buf.append(middle);
		if( ! outputList.isEmpty()) {
			buf.append(' ');
			org.fagu.fmv.utils.StringJoin.append(buf, labelToString(outputList.keySet().iterator()), StringUtils.EMPTY, "[", "]");
		}
		return buf.toString();
	}

	// ******************************************************

	/**
	 * @return
	 */
	protected boolean hasExplicitType() {
		return true;
	}

	// ******************************************************

	/**
	 * @param labels
	 * @return
	 */
	private Iterator<String> ioKeyToString(final Iterator<IOKey> ioKey) {
		return new Iterator<String>() {

			/**
			 * @see java.util.Iterator#remove()
			 */
			@Override
			public void remove() {}

			/**
			 * @see java.util.Iterator#next()
			 */
			@Override
			public String next() {
				return ioKey.next().toString(filterNaming);
			}

			/**
			 * @see java.util.Iterator#hasNext()
			 */
			@Override
			public boolean hasNext() {
				return ioKey.hasNext();
			}
		};
	}

	/**
	 * @param labels
	 * @return
	 */
	private Iterator<String> labelToString(final Iterator<Label> labels) {
		return new Iterator<String>() {

			/**
			 * @see java.util.Iterator#next()
			 */
			@Override
			public String next() {
				return filterNaming.generate(labels.next());
			}

			/**
			 * @see java.util.Iterator#hasNext()
			 */
			@Override
			public boolean hasNext() {
				return labels.hasNext();
			}
		};
	}

}
