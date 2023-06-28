package org.fagu.fmv.ffmpeg.flags;

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

	protected Rtpflags(int index, String flag, IO io) {
		super(Rtpflags.class, index, flag, io);
	}
}
