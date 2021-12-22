package org.fagu.fmv.ffmpeg.utils;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * Example: ffmpeg -v quiet -pix_fmts<br>
 * ffmpeg -v quiet -formats<br>
 * ffmpeg -v quiet -devices<br>
 * ffmpeg -v quiet -codecs<br>
 * ...
 * 
 * 
 * @author f.agu
 */
public class AvailableHelp<H extends Help> {

	// ------------------------------------

	@FunctionalInterface
	public interface Reader {

		boolean read(String line);
	}

	// ------------------------------------

	protected static class UnlimitedReader implements Reader {

		@Override
		public boolean read(String line) {
			return true;
		}
	}

	// ------------------------------------

	protected static class OneLineReader implements Reader {

		protected String line;

		@Override
		public boolean read(String line) {
			if(this.line != null) {
				return false;
			}
			this.line = line;
			return true;
		}

		@Override
		public String toString() {
			return "OneLineReader(" + (line == null ? "un" : "") + "read)";
		}
	}

	// ------------------------------------

	protected static class LinesReader implements Reader {

		protected final int max;

		protected int count;

		protected LinesReader(int max) {
			if(max <= 0) {
				throw new IllegalArgumentException("Max must be positiv: " + max);
			}
			this.max = max;
		}

		@Override
		public boolean read(String line) {
			if(count >= max) {
				return false;
			}
			++count;
			return true;
		}

		@Override
		public String toString() {
			return "LinesReader(" + count + "/" + max + ")";
		}
	}

	// ------------------------------------

	protected static class TitleReader extends OneLineReader {

		public String getTitle() {
			return line;
		}

		@Override
		public String toString() {
			return "TitleReader(" + (line == null ? "un" : "") + "read)";
		}
	}

	// ------------------------------------

	protected static class LegendReader implements Reader {

		private static final Pattern LEGEND_PATTERN = Pattern.compile("(\\.*([A-Za-z0-9\\|])\\.*)\\s+=\\s+(\\w+.*)");

		private final Map<Character, String> legendMap;

		private int countPossibilties;

		private boolean checkDot;

		public LegendReader(boolean checkDot) {
			legendMap = new HashMap<>();
			this.checkDot = checkDot;
		}

		public LegendReader() {
			this(false);
		}

		@Override
		public boolean read(String line) {
			Matcher matcher = LEGEND_PATTERN.matcher(line);
			if( ! matcher.matches()) {
				return false;
			}
			String all = matcher.group(1);
			if(countPossibilties == 0) {
				if(checkDot) {
					if(all.contains(".")) {
						countPossibilties = all.length();
					}
				} else {
					countPossibilties = all.length();
				}
			}
			char charAt = matcher.group(2).charAt(0);
			legendMap.put(charAt, matcher.group(3));
			return true;
		}

		public Map<Character, String> getLegends() {
			return legendMap;
		}

		@Override
		public String toString() {
			return "LegendReader(" + legendMap.size() + ")";
		}
	}

	// ------------------------------------

	protected static class ValuesReader<H extends Help> implements Reader {

		private static final Pattern VALUES_PATTERN = Pattern.compile("([\\w\\-,]+)(?:[\\s\\.]+(.*))?");

		private LegendReader legendReader;

		private Function<String, H> factory;

		private Consumer<H> consumer;

		private int count = 0;

		public ValuesReader(LegendReader legendReader, Function<String, H> factory, Consumer<H> consumer) {
			this.legendReader = legendReader;
			this.factory = factory;
			this.consumer = consumer;
		}

		@Override
		public boolean read(String line) {
			String sline = StringUtils.substring(line, legendReader.countPossibilties).trim();
			Matcher matcher = VALUES_PATTERN.matcher(sline);
			if( ! matcher.matches()) {
				return false;
			}
			String startline = StringUtils.substring(line, 0, legendReader.countPossibilties).trim();
			char[] chars = startline.replaceAll("[\\.\\s]", "").toCharArray();
			String[] names = matcher.group(1).split(",");
			String text = StringUtils.defaultString(matcher.group(2));
			for(String name : names) {
				H h = factory.apply(name);
				Help help = h;
				help.chars = chars;
				help.text = text;
				if(consumer != null) {
					consumer.accept(h);
				}
			}
			++count;
			return true;
		}

		@Override
		public String toString() {
			return "ValuesReader(" + count + ")";
		}
	}

	// ------------------------------------

	private List<Reader> readers;

	private LegendReader legendReader;

	private AvailableHelp() {
		readers = new ArrayList<>();
	}

	public static <H extends Help> AvailableHelp<H> create() {
		return new AvailableHelp<>();
	}

	public AvailableHelp<H> reader(Reader reader) {
		if(reader instanceof LegendReader) {
			legendReader = (LegendReader)reader;
		}
		readers.add(reader);
		return this;
	}

	public AvailableHelp<H> title() {
		return reader(new TitleReader());
	}

	public AvailableHelp<H> legend() {
		return reader(new LegendReader());
	}

	public AvailableHelp<H> legend(boolean checkDot) {
		return reader(new LegendReader(checkDot));
	}

	public AvailableHelp<H> unreadLine() {
		return reader(new OneLineReader());
	}

	public AvailableHelp<H> unreadLines(int count) {
		return reader(new LinesReader(count));
	}

	public AvailableHelp<H> unlimited() {
		return reader(new UnlimitedReader());
	}

	public AvailableHelp<H> values(Function<String, H> factory) {
		return values(factory, null);
	}

	public AvailableHelp<H> values(Function<String, H> factory, Consumer<H> consumer) {
		if(legendReader == null) {
			throw new RuntimeException("Before, define a LegendReader");
		}
		return reader(new ValuesReader<H>(legendReader, factory, consumer));
	}

	public void parse(List<String> help) {
		Iterator<String> lineIterator = help.iterator();
		Iterator<Reader> readerIterator = readers.iterator();
		if( ! readerIterator.hasNext()) {
			throw new IllegalArgumentException("No reader found");
		}

		int number = 0;
		Reader currentReader = readerIterator.next();
		while(lineIterator.hasNext()) {
			String line = lineIterator.next().trim();
			++number;
			while( ! currentReader.read(line)) {
				if( ! readerIterator.hasNext()) {
					for(int i = Math.max(0, number - 8); i < Math.min(help.size(), number + 8); ++i) {
						System.err.println((i + 1) + ": " + help.get(i));
					}

					throw new IllegalArgumentException("No enough reader (line " + number + ") : " + line);
				}
				currentReader = readerIterator.next();
			}
		}
	}

}
