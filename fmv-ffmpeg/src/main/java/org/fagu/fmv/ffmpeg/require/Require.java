package org.fagu.fmv.ffmpeg.require;

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

import org.fagu.fmv.ffmpeg.coder.Decoders;
import org.fagu.fmv.ffmpeg.coder.Encoders;
import org.fagu.fmv.ffmpeg.filter.Filters;
import org.fagu.fmv.ffmpeg.utils.Devices;


/**
 * @author f.agu
 */
public class Require {

	private boolean enable;

	/**
	 *
	 */
	public Require() {}

	/**
	 * @param enable
	 */
	public Require(boolean enable) {
		this.enable = enable;
	}

	/**
	 * @return
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * @param enable
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * @param name
	 */
	public void device(String name) {
		if( ! enable) {
			return;
		}
		check("device", name, Devices.exists(name));
	}

	/**
	 * @param name
	 */
	public void encoder(String name) {
		if( ! enable) {
			return;
		}
		if("copy".equals(name)) {
			return;
		}
		check("encoder", name, Encoders.exists(name));
	}

	/**
	 * @param name
	 */
	public void decoder(String name) {
		if( ! enable) {
			return;
		}
		if("copy".equals(name)) {
			return;
		}
		check("decoder", name, Decoders.exists(name));
	}

	/**
	 * @param name
	 */
	public void filter(String name) {
		if( ! enable) {
			return;
		}
		check("filter", name, Filters.exists(name));
	}

	// *********************************************************

	/**
	 * @param category
	 * @param name
	 * @param exists
	 */
	private void check(String category, String name, boolean exists) {
		if( ! exists) {
			throw new RequiredException(category + " '" + name + '\'');
		}
	}

}
