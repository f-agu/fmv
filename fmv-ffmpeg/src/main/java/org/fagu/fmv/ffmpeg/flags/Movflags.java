package org.fagu.fmv.ffmpeg.flags;

import org.fagu.fmv.ffmpeg.format.IO;


/**
 * MOV muxer flags (default 0)
 * 
 * @author f.agu
 */
public class Movflags extends Flags<Movflags> {

	/**
	 * Add RTP hint tracks
	 */
	public static final Movflags RTPHINT = new Movflags(0, "rtphint", IO.OUTPUT);

	/**
	 * Make the initial moov atom empty (not supported by QuickTime)
	 */
	public static final Movflags EMPTY_MOOV = new Movflags(1, "empty_moov", IO.OUTPUT);

	/**
	 * Fragment at video keyframes
	 */
	public static final Movflags FRAG_KEYFRAME = new Movflags(2, "frag_keyframe", IO.OUTPUT);

	/**
	 * Write separate moof/mdat atoms for each track
	 */
	public static final Movflags SEPARATE_MOOF = new Movflags(3, "separate_moof", IO.OUTPUT);

	/**
	 * Flush fragments on caller requests
	 */
	public static final Movflags FRAG_CUSTOM = new Movflags(4, "frag_custom", IO.OUTPUT);

	/**
	 * Create a live smooth streaming feed (for pushing to a publishing point)
	 */
	public static final Movflags ISML = new Movflags(5, "isml", IO.OUTPUT);

	/**
	 * Run a second pass to put the index (moov atom) at the beginning of the file
	 */
	public static final Movflags FASTSTART = new Movflags(6, "faststart", IO.OUTPUT);

	/**
	 * Omit the base data offset in tfhd atoms
	 */
	public static final Movflags OMIT_TFHD_OFFSET = new Movflags(7, "omit_tfhd_offset", IO.OUTPUT);

	protected Movflags(int index, String flag, IO io) {
		super(Movflags.class, index, flag, io);
	}
}
