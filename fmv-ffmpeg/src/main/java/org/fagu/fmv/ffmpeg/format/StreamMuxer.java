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
import java.util.Set;

import org.fagu.fmv.ffmpeg.flags.Movflags;
import org.fagu.fmv.ffmpeg.flags.Rtpflags;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.MediaOutput;
import org.fagu.fmv.ffmpeg.operation.Processor;


/**
 * @author f.agu
 */
public abstract class StreamMuxer<M> extends Muxer<M> {

	private final Set<Movflags> movflagss;

	private final Set<Rtpflags> rtpflagss;

	public StreamMuxer(String name, MediaOutput mediaOutput) {
		super(name, mediaOutput);
		movflagss = new HashSet<>();
		rtpflagss = new HashSet<>();
	}

	/**
	 * MOV muxer flags (default 0)
	 * 
	 * @param movflags
	 * @return
	 */
	public M movflags(Movflags... movflagss) {
		return movflags(Arrays.asList(movflagss));
	}

	/**
	 * MOV muxer flags (default 0)
	 * 
	 * @param movflags
	 * @return
	 */
	public M movflags(Collection<Movflags> inmovflagss) {
		inmovflagss.stream()
				.filter(f -> getIO().accept(f.io()))
				.forEach(movflagss::add);
		return getMThis();
	}

	/**
	 * Maximum moov size so it can be placed at the begin (from 0 to INT_MAX) (default 0)
	 * 
	 * @param moovSize
	 * @return
	 */
	public M moovSize(int moovSize) {
		if(moovSize < 0) {
			throw new IllegalArgumentException("moovSize must be at least 0");
		}
		parameter("-moov_size", Integer.toString(moovSize));
		return getMThis();
	}

	/**
	 * RTP muxer flags (default 0)
	 * 
	 * @param rtpflags
	 * @return
	 */
	public M rtpflags(Rtpflags... rtpflagss) {
		return rtpflags(Arrays.asList(rtpflagss));
	}

	/**
	 * RTP muxer flags (default 0)
	 * 
	 * @param rtpflags
	 * @return
	 */
	public M rtpflags(Collection<Rtpflags> inrtpflagss) {
		inrtpflagss.stream()
				.filter(f -> getIO().accept(f.io()))
				.forEach(rtpflagss::add);
		return getMThis();
	}

	/**
	 * Skip writing iods atom. (from 0 to 1) (default 1)
	 * 
	 * @param skipIods
	 * @return
	 */
	public M skipIods(boolean skipIods) {
		parameter("-skip_iods", Integer.toString(skipIods ? 1 : 0));
		return getMThis();
	}

	/**
	 * Iods audio profile atom. (from -1 to 255) (default -1)
	 * 
	 * @param iodsAudioProfile
	 * @return
	 */
	public M iodsAudioProfile(int iodsAudioProfile) {
		if( - 1 > iodsAudioProfile || iodsAudioProfile > 255) {
			throw new IllegalArgumentException("iodsAudioProfile must be between -1 and 255");
		}
		parameter("-iods_audio_profile", Integer.toString(iodsAudioProfile));
		return getMThis();
	}

	/**
	 * Iods video profile atom. (from -1 to 255) (default -1)
	 * 
	 * @param iodsVideoProfile
	 * @return
	 */
	public M iodsVideoProfile(int iodsVideoProfile) {
		if( - 1 > iodsVideoProfile || iodsVideoProfile > 255) {
			throw new IllegalArgumentException("iodsVideoProfile must be between -1 and 255");
		}
		parameter("-iods_video_profile", Integer.toString(iodsVideoProfile));
		return getMThis();
	}

	/**
	 * Maximum fragment duration (from 0 to INT_MAX) (default 0)
	 * 
	 * @param fragDuration
	 * @return
	 */
	public M fragDuration(int fragDuration) {
		if(fragDuration < 0) {
			throw new IllegalArgumentException("fragDuration must be at least 0");
		}
		parameter("-frag_duration", Integer.toString(fragDuration));
		return getMThis();
	}

	/**
	 * Minimum fragment duration (from 0 to INT_MAX) (default 0)
	 * 
	 * @param minFragDuration
	 * @return
	 */
	public M minFragDuration(int minFragDuration) {
		if(minFragDuration < 0) {
			throw new IllegalArgumentException("minFragDuration must be at least 0");
		}
		parameter("-min_frag_duration", Integer.toString(minFragDuration));
		return getMThis();
	}

	/**
	 * Maximum fragment size (from 0 to INT_MAX) (default 0)
	 * 
	 * @param fragSize
	 * @return
	 */
	public M fragSize(int fragSize) {
		if(fragSize < 0) {
			throw new IllegalArgumentException("fragSize must be at least 0");
		}
		parameter("-frag_size", Integer.toString(fragSize));
		return getMThis();
	}

	/**
	 * Number of lookahead entries for ISM files (from 0 to INT_MAX) (default 0)
	 * 
	 * @param ismLookahead
	 * @return
	 */
	public M ismLookahead(int ismLookahead) {
		if(ismLookahead < 0) {
			throw new IllegalArgumentException("ismLookahead must be at least 0");
		}
		parameter("-ism_lookahead", Integer.toString(ismLookahead));
		return getMThis();
	}

	/**
	 * Use edit list (from -1 to 1) (default -1)
	 * 
	 * @param useEditlist
	 * @return
	 */
	public M useEditlist(int useEditlist) {
		if( - 1 > useEditlist || useEditlist > 1) {
			throw new IllegalArgumentException("useEditlist must be between -1 and 1");
		}
		parameter("-use_editlist", Integer.toString(useEditlist));
		return getMThis();
	}

	/**
	 * Set timescale of all video tracks (from 0 to INT_MAX) (default 0)
	 * 
	 * @param videoTrackTimescale
	 * @return
	 */
	public M videoTrackTimescale(int videoTrackTimescale) {
		if(videoTrackTimescale < 0) {
			throw new IllegalArgumentException("videoTrackTimescale must be at least 0");
		}
		parameter("-video_track_timescale", Integer.toString(videoTrackTimescale));
		return getMThis();
	}

	/**
	 * Override major brand
	 * 
	 * @param brand
	 * @return
	 */
	public M brand(String brand) {
		parameter("-brand", brand);
		return getMThis();
	}

	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		super.eventAdded(processor, ioEntity);
		if( ! movflagss.isEmpty()) {
			parameter(processor, ioEntity, "-movflags", movflagss);
		}
		if( ! rtpflagss.isEmpty()) {
			parameter(processor, ioEntity, "-rtpflags", rtpflagss);
		}
	}
}
