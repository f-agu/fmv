package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.flags.Peak;
import org.fagu.fmv.ffmpeg.operation.ParsedLibLog;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class Ebur128 extends AbstractFilter implements ParsedLibLog {

	/**
	 *
	 */
	protected Ebur128() {
		super("ebur128");
	}

	/**
	 * @return
	 */
	public static Ebur128 build() {
		return new Ebur128();
	}

	/**
	 * Set video output (default false)
	 *
	 * @param video
	 * @return
	 */
	public Ebur128 video(boolean video) {
		parameter("video", Boolean.toString(video));
		return this;
	}

	/**
	 * Set video size (default "640x480")
	 *
	 * @param size
	 * @return
	 */
	public Ebur128 size(Size size) {
		parameter("size", size.toString());
		return this;
	}

	/**
	 * Set scale meter (+9 to +18) (from 9 to 18) (default 9)
	 *
	 * @param meter
	 * @return
	 */
	public Ebur128 meter(int meter) {
		if(9 > meter || meter > 18) {
			throw new IllegalArgumentException("meter must be between 9 and 18: " + meter);
		}
		parameter("meter", Integer.toString(meter));
		return this;
	}

	/**
	 * Force frame logging level (from INT_MIN to INT_MAX) (default -1)
	 *
	 * @param framelog
	 * @return
	 */
	public Ebur128 framelog(int level) {
		parameter("framelog", Integer.toString(level));
		return this;
	}

	/**
	 * Inject metadata in the filtergraph (default false)
	 *
	 * @param metadata
	 * @return
	 */
	public Ebur128 metadata(boolean metadata) {
		parameter("metadata", Boolean.toString(metadata));
		return this;
	}

	/**
	 * Set peak mode (default 0)
	 *
	 * @param peak
	 * @return
	 */
	public Ebur128 peak(Peak peak) {
		parameter("peak", peak.name());
		return this;
	}

	/**
	 * Treat mono input files as dual-mono (default false)
	 *
	 * @param dualmono
	 * @return
	 */
	public Ebur128 dualmono(boolean dualmono) {
		parameter("dualmono", Boolean.toString(dualmono));
		return this;
	}

	/**
	 * Set a specific pan law for dual-mono files (from -10 to 0) (default -3.0103)
	 *
	 * @param panlaw
	 * @return
	 */
	public Ebur128 panlaw(double panlaw) {
		if( - 10.0 > panlaw || panlaw > 0.0) {
			throw new IllegalArgumentException("panlaw must be between -10.0 and 0.0: " + panlaw);
		}
		parameter("panlaw", Double.toString(panlaw));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.LibLog#log(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void log(String title, String somethings, String log) {
		// NOTHING
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}
}
