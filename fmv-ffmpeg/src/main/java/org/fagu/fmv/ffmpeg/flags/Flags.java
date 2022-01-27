package org.fagu.fmv.ffmpeg.flags;

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

import java.util.HashMap;
import java.util.TreeMap;

import org.fagu.fmv.ffmpeg.format.IO;
import org.fagu.fmv.utils.collection.MapMap;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public abstract class Flags<F> {

	@SuppressWarnings("rawtypes")
	private static final MapMap<Class<? extends Flags>, Integer, Flags<?>> MAP = MultiValueMaps.map(new HashMap<>(), TreeMap::new);

	private final int index;

	private final String name;

	private final IO io;

	public Flags(@SuppressWarnings("rawtypes") Class<? extends Flags> cls, int index, String name, IO io) {
		this.index = index;
		this.name = name;
		this.io = io;
		if(cls != null) {
			MAP.add(cls, index, this);
		}
	}

	public int index() {
		return index;
	}

	public String name() {
		return name;
	}

	public IO io() {
		return io;
	}

	public Flags<F> inverse() {
		return invert(this);
	}

	@Override
	public String toString() {
		return new StringBuilder(name.length() + 1).append('+').append(name).toString();
	}

	public static <F> Flags<F> invert(Flags<F> flags) {
		if(flags instanceof InvertFlags) {
			return ((InvertFlags<F>)flags).origin;
		}
		return new InvertFlags<>(flags);
	}

	// -------------------------------------------------------

	/**
	 * @author f.agu
	 *
	 * @param <F>
	 */
	private static class InvertFlags<F> extends Flags<F> {

		private Flags<F> origin;

		private InvertFlags(Flags<F> flags) {
			super(null, flags.index, flags.name, flags.io);
			origin = flags;
		}

		@Override
		public String toString() {
			return new StringBuilder(name().length() + 1).append('-').append(name()).toString();
		}

	}

}
