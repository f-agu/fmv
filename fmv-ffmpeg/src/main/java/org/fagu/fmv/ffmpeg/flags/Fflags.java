package org.fagu.fmv.ffmpeg.flags;

import org.fagu.fmv.ffmpeg.format.IO;


/**
 * @author f.agu
 */
public class Fflags extends Flags<Fflags> {

	/**
	 * Reduce the latency by flushing out packets immediately
	 */
	public static final Fflags FLUSH_PACKETS = new Fflags(0, "flush_packets", IO.OUTPUT);

	/**
	 * Ignore index
	 */
	public static final Fflags IGNIDX = new Fflags(1, "ignidx", IO.INPUT);

	/**
	 * Generate pts
	 */
	public static final Fflags GENPTS = new Fflags(2, "genpts", IO.INPUT);

	/**
	 * Do not fill in missing values that can be exactly calculated
	 */
	public static final Fflags NOFILLIN = new Fflags(3, "nofillin", IO.INPUT);

	/**
	 * Disable AVParsers, this needs nofillin too
	 */
	public static final Fflags NOPARSE = new Fflags(4, "noparse", IO.INPUT);

	/**
	 * Ignore dts
	 */
	public static final Fflags IGNDTS = new Fflags(5, "igndts", IO.INPUT);

	/**
	 * Discard corrupted frames
	 */
	public static final Fflags DISCARDCORRUPT = new Fflags(6, "discardcorrupt", IO.INPUT);

	/**
	 * Try to interleave outputted packets by dts
	 */
	public static final Fflags SORTDTS = new Fflags(7, "sortdts", IO.INPUT);

	/**
	 * Don't merge side data
	 */
	public static final Fflags KEEPSIDE = new Fflags(8, "keepside", IO.INPUT);

	/**
	 * Enable RTP MP4A-LATM payload
	 */
	public static final Fflags LATM = new Fflags(9, "latm", IO.OUTPUT);

	/**
	 * Reduce the latency introduced by optional buffering
	 */
	public static final Fflags NOBUFFER = new Fflags(10, "nobuffer", IO.INPUT);

	/**
	 * Do not write random/volatile data
	 */
	public static final Fflags BITEXACT = new Fflags(11, "bitexact", IO.OUTPUT);

	protected Fflags(int index, String flag, IO io) {
		super(Fflags.class, index, flag, io);
	}
}
