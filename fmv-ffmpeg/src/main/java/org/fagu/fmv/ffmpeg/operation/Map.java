package org.fagu.fmv.ffmpeg.operation;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.filter.Label;
import org.fagu.fmv.ffmpeg.filter.OutputKey;
import org.fagu.fmv.ffmpeg.metadatas.Stream;


/**
 * @author f.agu
 */
public class Map {

	// ---------------------------------------------

	/**
	 * @author f.agu
	 */
	public class On {

		private Type[] types;

		private Stream[] streams;

		/**
		 * 
		 */
		private On() {}

		/**
		 * @param types
		 */
		private On(Type[] types) {
			this.types = types;
		}

		/**
		 * @param types
		 */
		private On(Stream[] streams) {
			this.streams = streams;
		}

		/**
		 * @param label
		 */
		public On label(String label) {
			return label(label, true);
		}

		/**
		 * @param label
		 */
		public On label(Label label) {
			return label(label, true);
		}

		/**
		 * @param outputKey
		 * @return
		 */
		public On outputKey(OutputKey outputKey) {
			return outputKey(outputKey, true);
		}

		/**
		 * @param outputKeys
		 * @return
		 */
		public On outputKeys(Collection<OutputKey> outputKeys) {
			return outputKeys(outputKeys, true);
		}

		/**
		 * @param filterInput
		 * @return
		 */
		public On input(FilterInput filterInput) {
			return outputKeys(filterInput.getOutputKeys(), ! (filterInput instanceof InputProcessor));
		}

		/**
		 * @return
		 */
		public Map map() {
			return Map.this;
		}

		// **********************************************

		/**
		 * 
		 */
		private Iterator<String> createIterator() {
			if(types != null) {
				return Arrays.stream(types).map(t -> Character.toString(t.code())).iterator();
			}
			if(streams != null) {
				return Arrays.stream(streams).map(s -> Integer.toString(s.index())).iterator();
			}
			return null;
		}

		/**
		 * @param label
		 * @param isLabel
		 * @return
		 */
		private On label(String label, boolean isLabel) {
			with(label, createIterator(), isLabel);
			return this;
		}

		/**
		 * @param label
		 * @param isLabel
		 * @return
		 */
		private On label(Label label, boolean isLabel) {
			with(filterNaming.generate(label), createIterator(), isLabel);
			return this;
		}

		/**
		 * @param outputKey
		 * @param isLabel
		 * @return
		 */
		private On outputKey(OutputKey outputKey, boolean isLabel) {
			return label(outputKey.getLabel(), isLabel);
		}

		/**
		 * @param outputKeys
		 * @param isLabel
		 * @return
		 */
		private On outputKeys(Collection<OutputKey> outputKeys, boolean isLabel) {
			outputKeys.stream().forEach(ok -> outputKey(ok, isLabel));
			return this;
		}

		/**
		 * @param map
		 * @param iterator
		 * @param isLabel
		 * @return
		 */
		private Map with(String map, Iterator<String> iterator, boolean isLabel) {
			if(iterator == null || ! iterator.hasNext()) {
				return map(map, isLabel);
			}
			while(iterator.hasNext()) {
				map(map + ':' + iterator.next(), isLabel);
			}
			return Map.this;
		}

		/**
		 * @param name
		 * @param isLabel
		 * @return
		 */
		private Map map(String name, boolean isLabel) {
			String map = isLabel ? '[' + name + ']' : name;
			outputProcessor.add(Parameter.before(outputProcessor.getMediaOutput(), "-map", map));
			return Map.this;
		}
	}

	// ---------------------------------------------

	private OutputProcessor outputProcessor;

	private FilterNaming filterNaming;

	/**
	 * @param outputProcessor
	 * @param filterNaming
	 */
	Map(OutputProcessor outputProcessor, FilterNaming filterNaming) {
		this.outputProcessor = outputProcessor;
		this.filterNaming = filterNaming;
	}

	/**
	 * @return
	 */
	public On allStreams() {
		return new On();
	}

	/**
	 * @param types
	 * @return
	 */
	public On types(Type... types) {
		return new On(types);
	}

	/**
	 * @param streams
	 * @return
	 */
	public On streams(Stream... streams) {
		return new On(streams);
	}

}
