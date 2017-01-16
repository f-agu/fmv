package org.fagu.fmv.ffmpeg.flags;

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


import org.fagu.fmv.ffmpeg.flags.Flags;
import org.fagu.fmv.ffmpeg.format.IO;

/**
 * RTP muxer flags (default 0)
 * 
 * @author f.agu
 */
public class Rtpflags extends Flags<Rtpflags> {
	/**
	 * Use MP4A-LATM packetization instead of MPEG4-GENERIC for AAC
	 */
	public static final Rtpflags LATM = new Rtpflags(0, "latm", IO.OUTPUT);
	/**
	 * Use RFC 2190 packetization instead of RFC 4629 for H.263
	 */
	public static final Rtpflags RFC2190 = new Rtpflags(1, "rfc2190", IO.OUTPUT);
	/**
	 * Don't send RTCP sender reports
	 */
	public static final Rtpflags SKIP_RTCP = new Rtpflags(2, "skip_rtcp", IO.OUTPUT);
	/**
	 * Use mode 0 for H264 in RTP
	 */
	public static final Rtpflags H264_MODE0 = new Rtpflags(3, "h264_mode0", IO.OUTPUT);
	/**
	 * Send RTCP BYE packets when finishing
	 */
	public static final Rtpflags SEND_BYE = new Rtpflags(4, "send_bye", IO.OUTPUT);

	/**
	 * @param index
	 * @param flag
	 * @param io
	 */
	protected Rtpflags(int index, String flag, IO io) {
		super(Rtpflags.class, index, flag, io);
	}
}
