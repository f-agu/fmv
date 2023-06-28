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

	public Require() {}

	public Require(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void device(String name) {
		if( ! enable) {
			return;
		}
		check("device", name, Devices.exists(name));
	}

	public void encoder(String name) {
		if( ! enable) {
			return;
		}
		if("copy".equals(name)) {
			return;
		}
		check("encoder", name, Encoders.exists(name));
	}

	public void decoder(String name) {
		if( ! enable) {
			return;
		}
		if("copy".equals(name)) {
			return;
		}
		check("decoder", name, Decoders.exists(name));
	}

	public void filter(String name) {
		if( ! enable) {
			return;
		}
		check("filter", name, Filters.exists(name));
	}

	// *********************************************************

	private void check(String category, String name, boolean exists) {
		if( ! exists) {
			throw new RequiredException(category + " '" + name + '\'');
		}
	}

}
