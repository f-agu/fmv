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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;


/**
 * ffmpeg -v quiet -devices
 * 
 * @author f.agu
 */
public class Devices {

	private static final HelpCache<Devices, DeviceHelp> HELP_CACHE = new HelpCache<>(runnable(), Devices::new);

	// caca (color ASCII art) output device
	public static final Devices CACA = new Devices("caca");

	// DirectShow capture
	public static final Devices DSHOW = new Devices("dshow");

	// Libavfilter virtual input device
	public static final Devices LAVFI = new Devices("lavfi");

	// SDL output device
	public static final Devices SDL = new Devices("sdl");

	// VfW video capture
	public static final Devices VFWCAP = new Devices("vfwcap");

	//
	public static final Devices VIDEO4LINUX2 = new Devices("video4linux2");

	/**
	 * 
	 */
	private final String name;

	/**
	 * @param name
	 */
	private Devices(String name) {
		this.name = name;
		HELP_CACHE.add(name, this, null);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public boolean isDemuxingSupported() {
		for(DeviceHelp h : HELP_CACHE.cache(name)) {
			if(h.contains('D')) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean isMuxingSupported() {
		for(DeviceHelp h : HELP_CACHE.cache(name)) {
			if(h.contains('E')) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return HELP_CACHE.cache(name).stream().map(b -> b.text).collect(Collectors.joining(", "));
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
	public static Devices webCam() {
		if(Devices.VIDEO4LINUX2.exists()) { // linux
			return Devices.VIDEO4LINUX2;
		}
		if(Devices.VFWCAP.exists()) { // windows
			return Devices.VFWCAP;
		}
		throw new RuntimeException("not managed");
	}

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
	public static Devices byName(String name) {
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
	public static List<Devices> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-devices");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<DeviceHelp> cacheConsumer = HELP_CACHE.consumer();
				Function<String, DeviceHelp> factory = DeviceHelp::new;

				AvailableHelp<DeviceHelp> availableHelp = AvailableHelp.create();
				availableHelp.title().legend().unreadLine().values(factory, cacheConsumer).parse(executor.execute().getResult());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}

		};
	}

	// ---------------------------------------------

	/**
	 * @return
	 */
	private static class DeviceHelp extends Help {

		/**
		 * @param name
		 */
		protected DeviceHelp(String name) {
			super(name);
		}
	}

}
