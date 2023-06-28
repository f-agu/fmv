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

import org.fagu.fmv.ffmpeg.format.IO;


/**
 * Work around not autodetected encoder bugs (default 1)
 * 
 * @author f.agu
 */
public class Bug extends Flags<Bug> {

	public static final Bug AUTODETECT = new Bug(0, "autodetect", IO.INPUT);

	/**
	 * Some old lavc-generated MSMPEG4v3 files (no autodetection)
	 */
	public static final Bug OLD_MSMPEG4 = new Bug(1, "old_msmpeg4", IO.INPUT);

	/**
	 * Xvid interlacing bug (autodetected if FOURCC == XVIX)
	 */
	public static final Bug XVID_ILACE = new Bug(2, "xvid_ilace", IO.INPUT);

	/**
	 * (autodetected if FOURCC == UMP4)
	 */
	public static final Bug UMP4 = new Bug(3, "ump4", IO.INPUT);

	/**
	 * Padding bug (autodetected)
	 */
	public static final Bug NO_PADDING = new Bug(4, "no_padding", IO.INPUT);

	public static final Bug AMV = new Bug(5, "amv", IO.INPUT);

	/**
	 * Illegal VLC bug (autodetected per FOURCC)
	 */
	public static final Bug AC_VLC = new Bug(6, "ac_vlc", IO.INPUT);

	public static final Bug QPEL_CHROMA = new Bug(7, "qpel_chroma", IO.INPUT);

	/**
	 * Old standard qpel (autodetected per FOURCC/version)
	 */
	public static final Bug STD_QPEL = new Bug(8, "std_qpel", IO.INPUT);

	/**
	 * 
	 */
	public static final Bug QPEL_CHROMA2 = new Bug(9, "qpel_chroma2", IO.INPUT);

	/**
	 * Direct-qpel-blocksize bug (autodetected per FOURCC/version)
	 */
	public static final Bug DIRECT_BLOCKSIZE = new Bug(10, "direct_blocksize", IO.INPUT);

	/**
	 * Edge padding bug (autodetected per FOURCC/version)
	 */
	public static final Bug EDGE = new Bug(11, "edge", IO.INPUT);

	public static final Bug HPEL_CHROMA = new Bug(12, "hpel_chroma", IO.INPUT);

	public static final Bug DC_CLIP = new Bug(13, "dc_clip", IO.INPUT);

	/**
	 * Work around various bugs in Microsoft's broken decoders
	 */
	public static final Bug MS = new Bug(14, "ms", IO.INPUT);

	/**
	 * Truncated frames
	 */
	public static final Bug TRUNC = new Bug(15, "trunc", IO.INPUT);

	protected Bug(int index, String flag, IO io) {
		super(Bug.class, index, flag, io);
	}
}
