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
 * ffmpeg -v quiet -bsfs<br>
 * 
 * @author f.agu
 */
public class BitStreamFilter {

	private static final HelpCache<BitStreamFilter, BitStreamFilterHelp> HELP_CACHE = new HelpCache<>(runnable(), BitStreamFilter::new);

	public static final BitStreamFilter AAC_ADTSTOASC = new BitStreamFilter("aac_adtstoasc");

	public static final BitStreamFilter CHOMP = new BitStreamFilter("chomp");

	public static final BitStreamFilter DUMP_EXTRA = new BitStreamFilter("dump_extra");

	public static final BitStreamFilter H264_MP4TOANNEXB = new BitStreamFilter("h264_mp4toannexb");

	public static final BitStreamFilter IMXDUMP = new BitStreamFilter("imxdump");

	public static final BitStreamFilter MJPEG2JPEG = new BitStreamFilter("mjpeg2jpeg");

	public static final BitStreamFilter MJPEGADUMP = new BitStreamFilter("mjpegadump");

	public static final BitStreamFilter MOV2TEXTSUB = new BitStreamFilter("mov2textsub");

	public static final BitStreamFilter MP3DECOMP = new BitStreamFilter("mp3decomp");

	public static final BitStreamFilter NOISE = new BitStreamFilter("noise");

	public static final BitStreamFilter REMOVE_EXTRA = new BitStreamFilter("remove_extra");

	public static final BitStreamFilter TEXT2MOVSUB = new BitStreamFilter("text2movsub");

	/**
	 * 
	 */
	private final String name;

	/**
	 * @param name
	 */
	private BitStreamFilter(String name) {
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
	public static BitStreamFilter byName(String name) {
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
	public static List<BitStreamFilter> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-bsfs");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<BitStreamFilterHelp> cacheConsumer = HELP_CACHE.consumer();
				AvailableHelp<BitStreamFilterHelp> availableHelp = AvailableHelp.create();
				availableHelp.title().legend().reader(l -> {
					if(StringUtils.isBlank(l)) {
						return false;
					}
					cacheConsumer.accept(new BitStreamFilterHelp(l));
					return true;
				}).unlimited().parse(executor.execute().getResult());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	// ---------------------------------------------

	/**
	 * @return
	 */
	private static class BitStreamFilterHelp extends Help {

		/**
		 * @param name
		 */
		protected BitStreamFilterHelp(String name) {
			super(name);
		}
	}
}
