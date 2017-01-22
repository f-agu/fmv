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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;


/**
 * ffmpeg -v quiet -sample_fmts<br>
 * 
 * @author f.agu
 */
public class AudioSampleFormat {

	private static final HelpCache<AudioSampleFormat, AudioSampleFormatHelp> HELP_CACHE = new HelpCache<>(runnable(), AudioSampleFormat::new);

	// depth: 64
	public static final AudioSampleFormat DBL = new AudioSampleFormat("dbl");

	// depth: 64
	public static final AudioSampleFormat DBLP = new AudioSampleFormat("dblp");

	// depth: 32
	public static final AudioSampleFormat FLT = new AudioSampleFormat("flt");

	// depth: 32
	public static final AudioSampleFormat FLTP = new AudioSampleFormat("fltp");

	// depth: 16
	public static final AudioSampleFormat S16 = new AudioSampleFormat("s16");

	// depth: 16
	public static final AudioSampleFormat S16P = new AudioSampleFormat("s16p");

	// depth: 32
	public static final AudioSampleFormat S32 = new AudioSampleFormat("s32");

	// depth: 32
	public static final AudioSampleFormat S32P = new AudioSampleFormat("s32p");

	// depth: 8
	public static final AudioSampleFormat U8 = new AudioSampleFormat("u8");

	// depth: 8
	public static final AudioSampleFormat U8P = new AudioSampleFormat("u8p");

	/**
	 * 
	 */
	private final String name;

	/**
	 * @param name
	 */
	private AudioSampleFormat(String name) {
		this.name = name;
		HELP_CACHE.add(name, this, null);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getDepth() {
		return cache().depth;
	}

	/**
	 * @return
	 */
	public boolean exists() {
		return exists(name);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	// **************************************************

	/**
	 * @return
	 */
	public static boolean exists(String name) {
		return HELP_CACHE.exists(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public static AudioSampleFormat byName(String name) {
		return HELP_CACHE.byName(name);
	}

	/**
	 * @return
	 */
	public static Set<String> availableNames() {
		return HELP_CACHE.availableNames();
	}

	/**
	 * @return
	 */
	public static List<AudioSampleFormat> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @return
	 */
	private AudioSampleFormatHelp cache() {
		return HELP_CACHE.cache(name).get(0);
	}

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-sample_fmts");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<AudioSampleFormatHelp> cacheConsumer = HELP_CACHE.consumer();
				AvailableHelp<AudioSampleFormatHelp> availableHelp = AvailableHelp.create();
				availableHelp.title().legend().reader(l -> {
					if(StringUtils.isBlank(l)) {
						return false;
					}
					String name = StringUtils.substringBefore(l, " ");
					int depth = Integer.parseInt(StringUtils.substringAfter(l, " ").trim());
					cacheConsumer.accept(new AudioSampleFormatHelp(name, depth));
					return true;
				}).parse(executor.execute().getResult());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	// ---------------------------------------------

	/**
	 * @return
	 */
	private static class AudioSampleFormatHelp extends Help {

		private final int depth;

		/**
		 * @param name
		 * @param depth
		 */
		protected AudioSampleFormatHelp(String name, int depth) {
			super(name);
			this.depth = depth;
		}
	}
}
