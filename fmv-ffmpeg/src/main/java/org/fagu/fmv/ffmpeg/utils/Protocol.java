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
import org.apache.commons.lang3.mutable.MutableObject;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;


/**
 * ffmpeg -v quiet -protocols<br>
 * 
 * @author f.agu
 */
public class Protocol {

	// --------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum IO {
		INPUT, OUTPUT
	}

	// --------------------------------------------

	private static final HelpCache<Protocol, ProtocolHelp> HELP_CACHE = new HelpCache<>(runnable(), Protocol::new);

	// input
	public static final Protocol BLURAY = new Protocol("bluray");

	// input
	public static final Protocol CACHE = new Protocol("cache");

	// input
	public static final Protocol CONCAT = new Protocol("concat");

	// input
	public static final Protocol CRYPTO = new Protocol("crypto");

	// input
	public static final Protocol DATA = new Protocol("data");

	// input & output
	public static final Protocol FILE = new Protocol("file");

	// input & output
	public static final Protocol FTP = new Protocol("ftp");

	// input & output
	public static final Protocol GOPHER = new Protocol("gopher");

	// input
	public static final Protocol HLS = new Protocol("hls");

	// input & output
	public static final Protocol HTTP = new Protocol("http");

	// input & output
	public static final Protocol HTTPPROXY = new Protocol("httpproxy");

	// input & output
	public static final Protocol HTTPS = new Protocol("https");

	// output
	public static final Protocol ICECAST = new Protocol("icecast");

	// output
	public static final Protocol MD5 = new Protocol("md5");

	// input
	public static final Protocol MMSH = new Protocol("mmsh");

	// input
	public static final Protocol MMST = new Protocol("mmst");

	// input & output
	public static final Protocol PIPE = new Protocol("pipe");

	// input & output
	public static final Protocol RTMP = new Protocol("rtmp");

	// input & output
	public static final Protocol RTMPE = new Protocol("rtmpe");

	// input & output
	public static final Protocol RTMPS = new Protocol("rtmps");

	// input & output
	public static final Protocol RTMPT = new Protocol("rtmpt");

	// input & output
	public static final Protocol RTMPTE = new Protocol("rtmpte");

	// input & output
	public static final Protocol RTP = new Protocol("rtp");

	// input & output
	public static final Protocol SRTP = new Protocol("srtp");

	// input
	public static final Protocol SUBFILE = new Protocol("subfile");

	// input & output
	public static final Protocol TCP = new Protocol("tcp");

	// input & output
	public static final Protocol TLS = new Protocol("tls");

	// input & output
	public static final Protocol UDP = new Protocol("udp");

	/**
	 * 
	 */
	private final String name;

	/**
	 * @param name
	 */
	private Protocol(String name) {
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
	public boolean isInput() {
		return HELP_CACHE.cache(name).stream().map(h -> h.io).filter(io -> io == IO.INPUT).findFirst().isPresent();
	}

	/**
	 * @return
	 */
	public boolean isOutput() {
		return HELP_CACHE.cache(name).stream().map(h -> h.io).filter(io -> io == IO.OUTPUT).findFirst().isPresent();
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
	public static Protocol byName(String name) {
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
	public static List<Protocol> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @return
	 */
	// private ProtocolHelp cache() {
	// return HELP_CACHE.cache(name).get(0);
	// }

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-protocols");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<ProtocolHelp> cacheConsumer = HELP_CACHE.consumer();
				AvailableHelp<ProtocolHelp> availableHelp = AvailableHelp.create();
				MutableObject<IO> mutableObject = new MutableObject<>();
				availableHelp.title().legend().reader(l -> {
					if(StringUtils.isBlank(l)) {
						return false;
					}
					if(l.endsWith(":")) {
						mutableObject.setValue(IO.valueOf(StringUtils.substringBefore(l, ":").toUpperCase()));
						return true;
					}
					if(mutableObject.getValue() == null) {
						throw new RuntimeException("Impossible !");
					}
					cacheConsumer.accept(new ProtocolHelp(l, mutableObject.getValue()));
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
	private static class ProtocolHelp extends Help {

		private final IO io;

		/**
		 * @param name
		 * @param io
		 */
		protected ProtocolHelp(String name, IO io) {
			super(name);
			this.io = io;
		}
	}
}
