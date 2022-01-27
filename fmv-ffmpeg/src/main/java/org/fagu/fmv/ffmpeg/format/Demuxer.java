package org.fagu.fmv.ffmpeg.format;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.fagu.fmv.ffmpeg.flags.ErrDetect;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.utils.Binary;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public abstract class Demuxer<M> extends Format<M> implements MediaInput {

	private final MediaInput mediaInput;

	private final Set<ErrDetect> errDetects;

	public Demuxer(String name, MediaInput mediaInput) {
		super(name);
		this.mediaInput = mediaInput;
		errDetects = new HashSet<>();
	}

	@Override
	public IO getIO() {
		return IO.INPUT;
	}

	public MediaInput getMediaInput() {
		return mediaInput;
	}

	/**
	 * Set probing size (from 32 to INT_MAX) (default 5e+006)
	 * 
	 * @param probesize
	 * @return
	 */
	public M probesize(int probesize) {
		if(probesize < 32) {
			throw new IllegalArgumentException("probesize must be at least 32: " + probesize);
		}
		parameter("-probesize", Integer.toString(probesize));
		return getMThis();
	}

	/**
	 * Number of bytes to probe file format (from 0 to 2.14748e+009) (default 1.04858e+006)
	 * 
	 * @param formatprobesize
	 * @return
	 */
	public M formatprobesize(int formatprobesize) {
		if(formatprobesize < 0) {
			throw new IllegalArgumentException("formatprobesize must be at least 0: " + formatprobesize);
		}
		parameter("-formatprobesize", Integer.toString(formatprobesize));
		return getMThis();
	}

	/**
	 * Allow seeking to non-keyframes on demuxer level when supported (from 0 to 1) (default 0)
	 * 
	 * @param seek2any
	 * @return
	 */
	public M seek2any(boolean seek2any) {
		parameter("-seek2any", Integer.toString(seek2any ? 1 : 0));
		return getMThis();
	}

	/**
	 * Specify how many microseconds are analyzed to probe the input (from 0 to I64_MAX) (default 0)
	 * 
	 * @param analyzeduration
	 * @return
	 */
	public M analyzeduration(long analyzeduration) {
		if(analyzeduration < 0) {
			throw new IllegalArgumentException("analyzeduration must be at least 0: " + analyzeduration);
		}
		parameter("-analyzeduration", Long.toString(analyzeduration));
		return getMThis();
	}

	/**
	 * Decryption key
	 * 
	 * @param cryptokey
	 * @return
	 */
	public M cryptokey(Binary cryptokey) {
		parameter("-cryptokey", cryptokey.toString());
		return getMThis();
	}

	/**
	 * Max memory used for timestamp index (per stream) (from 0 to INT_MAX) (default 1.04858e+006)
	 * 
	 * @param indexmem
	 * @return
	 */
	public M indexmem(int indexmem) {
		if(indexmem < 0) {
			throw new IllegalArgumentException("indexmem must be at least 0: " + indexmem);
		}
		parameter("-indexmem", Integer.toString(indexmem));
		return getMThis();
	}

	/**
	 * Max memory used for buffering real-time frames (from 0 to INT_MAX) (default 3.04128e+006)
	 * 
	 * @param rtbufsize
	 * @return
	 */
	public M rtbufsize(int rtbufsize) {
		if(rtbufsize < 0) {
			throw new IllegalArgumentException("rtbufsize must be at least 0: " + rtbufsize);
		}
		parameter("-rtbufsize", Integer.toString(rtbufsize));
		return getMThis();
	}

	/**
	 * Number of frames used to probe fps (from -1 to 2.14748e+009) (default -1)
	 * 
	 * @param fpsprobesize
	 * @return
	 */
	public M fpsprobesize(int fpsprobesize) {
		if(fpsprobesize < - 1) {
			throw new IllegalArgumentException("fpsprobesize must be at least -1: " + fpsprobesize);
		}
		parameter("-fpsprobesize", Integer.toString(fpsprobesize));
		return getMThis();
	}

	/**
	 * Set error detection flags (default 1)
	 * 
	 * @param errDetect
	 * @return
	 */
	public M errDetect(ErrDetect... errDetects) {
		return errDetect(Arrays.asList(errDetects));
	}

	/**
	 * Set error detection flags (default 1)
	 * 
	 * @param errDetect
	 * @return
	 */
	public M errDetect(Collection<ErrDetect> inerrDetects) {
		inerrDetects.stream()
				.filter(f -> getIO().accept(f.io()))
				.forEach(errDetects::add);
		return getMThis();
	}

	/**
	 * Use wallclock as timestamps (from 0 to 2.14748e+009) (default 0)
	 * 
	 * @param useWallclockAsTimestamps
	 * @return
	 */
	public M useWallclockAsTimestamps(int useWallclockAsTimestamps) {
		if(useWallclockAsTimestamps < 0) {
			throw new IllegalArgumentException("useWallclockAsTimestamps must be at least 0: " + useWallclockAsTimestamps);
		}
		parameter("-use_wallclock_as_timestamps", Integer.toString(useWallclockAsTimestamps));
		return getMThis();
	}

	/**
	 * Set number of bytes to skip before reading header and frames (from 0 to I64_MAX) (default 0)
	 * 
	 * @param skipInitialBytes
	 * @return
	 */
	public M skipInitialBytes(long skipInitialBytes) {
		if(skipInitialBytes < 0) {
			throw new IllegalArgumentException("skipInitialBytes must be at least 0: " + skipInitialBytes);
		}
		parameter("-skip_initial_bytes", Long.toString(skipInitialBytes));
		return getMThis();
	}

	/**
	 * Correct single timestamp overflows (from 0 to 1) (default 1)
	 * 
	 * @param correctTsOverflow
	 * @return
	 */
	public M correctTsOverflow(boolean correctTsOverflow) {
		parameter("-correct_ts_overflow", Integer.toString(correctTsOverflow ? 1 : 0));
		return getMThis();
	}

	@Override
	public Optional<Duration> getDuration() {
		return Optional.empty();
	}

	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		super.eventAdded(processor, ioEntity);
		if( ! errDetects.isEmpty()) {
			parameter(processor, ioEntity, "-err_detect", errDetects);
		}
	}
}
