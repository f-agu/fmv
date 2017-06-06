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

import java.util.Objects;

import org.fagu.fmv.ffmpeg.flags.AvoidNegativeTs;
import org.fagu.fmv.ffmpeg.operation.MediaOutput;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public abstract class Muxer<M> extends Format<M> implements MediaOutput {

	private final MediaOutput mediaOutput;

	/**
	 * @param name
	 * @param mediaOutput
	 */
	public Muxer(String name, MediaOutput mediaOutput) {
		super(name);
		this.mediaOutput = Objects.requireNonNull(mediaOutput);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.format.Format#getIO()
	 */
	@Override
	public IO getIO() {
		return IO.OUTPUT;
	}

	/**
	 * @return
	 */
	public MediaOutput getMediaOutput() {
		return mediaOutput;
	}

	/**
	 * Set packet size (from 0 to INT_MAX) (default 0)
	 * 
	 * @param packetsize
	 * @return
	 */
	public M packetsize(int packetsize) {
		if(packetsize < 0) {
			throw new IllegalArgumentException("packetsize must be at least 0: " + packetsize);
		}
		parameter("-packetsize", Integer.toString(packetsize));
		return getMThis();
	}

	/**
	 * Wall-clock time when stream begins (PTS==0) (from I64_MIN to I64_MAX) (default I64_MIN)
	 * 
	 * @param startTimeRealtime
	 * @return
	 */
	public M startTimeRealtime(long startTimeRealtime) {
		parameter("-start_time_realtime", Long.toString(startTimeRealtime));
		return getMThis();
	}

	/**
	 * Microseconds by which audio packets should be interleaved earlier (from 0 to 2.14748e+009) (default 0)
	 * 
	 * @param audioPreload
	 * @return
	 */
	public M audioPreload(int audioPreload) {
		if(audioPreload < 0) {
			throw new IllegalArgumentException("audioPreload must be at least 0: " + audioPreload);
		}
		parameter("-audio_preload", Integer.toString(audioPreload));
		return getMThis();
	}

	/**
	 * Microseconds for each chunk (from 0 to 2.14748e+009) (default 0)
	 * 
	 * @param chunkDuration
	 * @return
	 */
	public M chunkDuration(int chunkDuration) {
		if(chunkDuration < 0) {
			throw new IllegalArgumentException("chunkDuration must be at least 0: " + chunkDuration);
		}
		parameter("-chunk_duration", Integer.toString(chunkDuration));
		return getMThis();
	}

	/**
	 * Size in bytes for each chunk (from 0 to 2.14748e+009) (default 0)
	 * 
	 * @param chunkSize
	 * @return
	 */
	public M chunkSize(int chunkSize) {
		if(chunkSize < 0) {
			throw new IllegalArgumentException("chunkSize must be at least 0: " + chunkSize);
		}
		parameter("-chunk_size", Integer.toString(chunkSize));
		return getMThis();
	}

	/**
	 * Shift timestamps so they start at 0 (from -1 to 2) (default -1)
	 * 
	 * @param avoidNegativeTs
	 * @return
	 */
	public M avoidNegativeTs(AvoidNegativeTs avoidNegativeTs) {
		if( ! avoidNegativeTs.io().isOutput()) {
			throw new IllegalArgumentException("IO is wrong: " + avoidNegativeTs.io());
		}
		parameter("-avoid_negative_ts", avoidNegativeTs.flag());
		return getMThis();
	}

	/**
	 * Enable flushing of the I/O context after each packet (from 0 to 1) (default 1)
	 * 
	 * @param flushPackets
	 * @return
	 */
	public M flushPackets(boolean flushPackets) {
		parameter("-flush_packets", Integer.toString(flushPackets ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set number of bytes to be written as padding in a metadata header (from -1 to INT_MAX) (default -1)
	 * 
	 * @param metadataHeaderPadding
	 * @return
	 */
	public M metadataHeaderPadding(int metadataHeaderPadding) {
		if(metadataHeaderPadding < - 1) {
			throw new IllegalArgumentException("metadataHeaderPadding must be at least -1: " + metadataHeaderPadding);
		}
		parameter("-metadata_header_padding", Integer.toString(metadataHeaderPadding));
		return getMThis();
	}

	/**
	 * Set output timestamp offset (default 0)
	 * 
	 * @param outputTsOffset
	 * @return
	 */
	public M outputTsOffset(Duration outputTsOffset) {
		parameter("-output_ts_offset", outputTsOffset.toString());
		return getMThis();
	}

	/**
	 * Maximum buffering duration for interleaving (from 0 to I64_MAX) (default 1e+007)
	 * 
	 * @param maxInterleaveDelta
	 * @return
	 */
	public M maxInterleaveDelta(long maxInterleaveDelta) {
		if(maxInterleaveDelta < 0) {
			throw new IllegalArgumentException("maxInterleaveDelta must be at least 0: " + maxInterleaveDelta);
		}
		parameter("-max_interleave_delta", Long.toString(maxInterleaveDelta));
		return getMThis();
	}

}
