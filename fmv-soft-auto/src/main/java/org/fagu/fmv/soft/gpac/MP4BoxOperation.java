package org.fagu.fmv.soft.gpac;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.util.LinkedList;
import java.util.Objects;


/**
 * @author f.agu
 * @created 1 juil. 2019 14:20:15
 */
public class MP4BoxOperation<O extends MP4BoxOperation<?>> {

	// -------------------------------------------------------------------

	public static class DASHMP4BoxOperation extends MP4BoxOperation<DASHMP4BoxOperation> {

		/**
		 * enables DASH segmentation of input files with the given segment duration. For onDemand profile, where each
		 * media presentation is a single segment, this option sets the duration of a subsegment.
		 * 
		 * @return
		 */
		public DASHMP4BoxOperation dash(int duration) {
			return addArguments("-dash", Integer.toString(duration));
		}

		/**
		 * generates a live DASH session using dur segment duration, optionnally writing live context to F. MP4Box will
		 * run the live session until \’q\’ is pressed or a fatal error occurs.
		 * 
		 * @return
		 */
		public DASHMP4BoxOperation dashLive(int duration) {
			return addArguments("-dash-live", Integer.toString(duration));
		}

		/**
		 * specifies the duration of subsegments in ms. This duration is always less than the segment duration. By
		 * default (when not set), the subsegment duration is the DASH duration, i.e. there is only one subsegment per
		 * segment. For onDemand profile, where each media presentation is a single segment, this option sets the
		 * duration of a subsegment’s subsegment.
		 * 
		 * @param durationInMilli
		 * @return
		 */
		public DASHMP4BoxOperation frag(long durationInMilli) {
			return addArguments("-frag", Long.toString(durationInMilli));
		}
	}

	// -------------------------------------------------------------------

	private final LinkedList<String> arguments = new LinkedList<>();

	MP4BoxOperation() {}

	public O addArguments(String... args) {
		for(String arg : args) {
			arguments.add(arg);
		}
		return getThis();
	}

	/**
	 * specifies where the temporary file(s) used by MP4Box shall be created. This is quite useful on Windows systems
	 * where user may not has the rights to create temporary files. By default, MP4Box uses the OS temporary file
	 * handling as provided in C stdio
	 * 
	 * @param path
	 * @return
	 */
	public O tmpDir(String path) {
		Objects.requireNonNull(path);
		return addArguments("-tmp", path);
	}

	/**
	 * interleaves media data in chunks of desired duration (in seconds). This is useful to optimize the file for
	 * HTTP/FTP streaming or reducing disk access. All meta data are placed first in the file, allowing a player to
	 * start playback while downloading the content. By default MP4Box always stores files with half a second
	 * interleaving and performs drift checking between tracks while interleaving. Specifying a 0 interleaving time will
	 * result in the file being stored without interleaving, with all meta-data placed at beginning of the file.
	 * 
	 * @param durationInSeconds
	 * @return
	 */
	public O inter(int durationInSeconds) {
		if(durationInSeconds < 0) {
			throw new IllegalArgumentException("Must be positive: " + durationInSeconds);
		}
		return addArguments("-inter", Integer.toString(durationInSeconds));
	}

	// *************************************************************

	@SuppressWarnings("unchecked")
	private O getThis() {
		return (O)this;
	}
}
