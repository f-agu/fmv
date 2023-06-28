package org.fagu.fmv.ffmpeg.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

		private final Collection<Type> types;

		private final Collection<Stream> streams;

		private On() {
			this.types = null;
			this.streams = null;
		}

		private On(Collection<Type> types, Collection<Stream> streams) {
			this.types = types != null ? Collections.unmodifiableList(new ArrayList<>(types)) : null;
			this.streams = streams != null ? Collections.unmodifiableList(new ArrayList<>(streams)) : null;
		}

		public On label(String label) {
			return label(label, true);
		}

		public On label(Label label) {
			return label(label, true);
		}

		public On outputKey(OutputKey outputKey) {
			return outputKey(outputKey, true);
		}

		public On outputKeys(Collection<OutputKey> outputKeys) {
			return outputKeys(outputKeys, true);
		}

		public On input(FilterInput filterInput) {
			return outputKeys(filterInput.getOutputKeys(), ! (filterInput instanceof InputProcessor));
		}

		public Map map() {
			return Map.this;
		}

		// **********************************************

		private Iterator<String> createIterator() {
			if(types != null) {
				return types.stream().map(t -> Character.toString(t.code())).iterator();
			}
			if(streams != null) {
				return streams.stream().map(s -> Integer.toString(s.index())).iterator();
			}
			return null;
		}

		private On label(String label, boolean isLabel) {
			with(label, createIterator(), isLabel);
			return this;
		}

		private On label(Label label, boolean isLabel) {
			with(filterNaming.generate(label), createIterator(), isLabel);
			return this;
		}

		private On outputKey(OutputKey outputKey, boolean isLabel) {
			return label(outputKey.getLabel(), isLabel);
		}

		private On outputKeys(Collection<OutputKey> outputKeys, boolean isLabel) {
			outputKeys.stream().forEach(ok -> outputKey(ok, isLabel));
			return this;
		}

		private Map with(String map, Iterator<String> iterator, boolean isLabel) {
			if(iterator == null || ! iterator.hasNext()) {
				return map(map, isLabel);
			}
			while(iterator.hasNext()) {
				map(map + ':' + iterator.next(), isLabel);
			}
			return Map.this;
		}

		private Map map(String name, boolean isLabel) {
			String map = isLabel ? '[' + name + ']' : name;
			outputProcessor.add(Parameter.before(outputProcessor.getMediaOutput(), "-map", map));
			return Map.this;
		}
	}

	// ---------------------------------------------

	private OutputProcessor outputProcessor;

	private FilterNaming filterNaming;

	Map(OutputProcessor outputProcessor, FilterNaming filterNaming) {
		this.outputProcessor = outputProcessor;
		this.filterNaming = filterNaming;
	}

	public On allStreams() {
		return new On();
	}

	public On types(Type... types) {
		return new On(Arrays.asList(types), null);
	}

	public On types(Collection<Type> types) {
		return new On(types, null);
	}

	public On streams(Stream... streams) {
		return new On(null, Arrays.asList(streams));
	}

	public On streams(Collection<Stream> streams) {
		return new On(null, streams);
	}

}
