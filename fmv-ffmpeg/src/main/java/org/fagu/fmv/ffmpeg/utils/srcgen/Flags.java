package org.fagu.fmv.ffmpeg.utils.srcgen;

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

import org.fagu.fmv.ffmpeg.format.IO;


/**
 * @author f.agu
 */
public class Flags {

	private static final Flags NOTHING = new Flags();

	private boolean encoder;

	private boolean decoder;

	private boolean filter;

	private boolean video;

	private boolean audio;

	private boolean subtitle;

	// private boolean x;

	// private boolean r;

	private Flags() {}

	/**
	 * @return
	 */
	public static Flags nothing() {
		return NOTHING;
	}

	public static Flags parse(String str) {
		if(str.length() != 8) {
			throw new RuntimeException("Flags unparsable: " + str);
		}
		char[] chars = str.toCharArray();
		Flags flags = new Flags();
		flags.encoder = is(chars, 0, 'E');
		flags.decoder = is(chars, 1, 'D');
		flags.filter = is(chars, 2, 'F');
		flags.video = is(chars, 3, 'V');
		flags.audio = is(chars, 4, 'A');
		flags.subtitle = is(chars, 5, 'S');
		// flags.x = is(chars, 6, 'X');
		// flags.r = is(chars, 7, 'R');
		return flags;
	}

	public boolean isEncoder() {
		return encoder;
	}

	public boolean isDecoder() {
		return decoder;
	}

	public boolean isFilter() {
		return filter;
	}

	public boolean isVideo() {
		return video;
	}

	public boolean isAudio() {
		return audio;
	}

	public boolean isSubtitle() {
		return subtitle;
	}

	public Flags add(Flags flags) {
		Flags f = new Flags();
		f.encoder = encoder || flags.encoder;
		f.decoder = decoder || flags.decoder;
		f.filter = filter || flags.filter;
		f.video = video || flags.video;
		f.audio = audio || flags.audio;
		f.subtitle = subtitle || flags.subtitle;
		// f.x = x || flags.x;
		// f.r = r || flags.r;
		return f;
	}

	public IO io() {
		return io(this);
	}

	public static IO io(Flags flags) {
		boolean input = flags.isDecoder();
		boolean output = flags.isEncoder();
		if(input && output) {
			return IO.INPUT_OUTPUT;
		}
		if(input) {
			return IO.INPUT;
		}
		if(output) {
			return IO.OUTPUT;
		}
		// throw new RuntimeException("encoder or decoder not found");
		return null;
	}

	// **************************************************

	private static boolean is(char[] chars, int index, char expectedIfExist) {
		char c = chars[index];
		if(c == '.') {
			return false;
		}
		if(c == expectedIfExist) {
			return true;
		}
		throw new RuntimeException("Unknown char '" + c + "' in " + new String(chars) + " at position " + index);
	}

}
