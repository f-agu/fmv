package org.fagu.fmv.ffmpeg.flags;

import org.fagu.fmv.ffmpeg.format.IO;


/**
 * ... Export metadata as side data (default 0)
 * 
 * @author f.agu
 */
public class ExportSideData extends Flags<ExportSideData> {

	/**
	 * ... export motion vectors through frame side data
	 */
	public static final ExportSideData MVS = new ExportSideData(0, "mvs", IO.INPUT);

	/**
	 * ... export Producer Reference Time through packet side data
	 */
	public static final ExportSideData PRFT = new ExportSideData(1, "prft", IO.OUTPUT);

	/**
	 * ... export video encoding parameters through frame side data
	 */
	public static final ExportSideData VENC_PARAMS = new ExportSideData(2, "venc_params", IO.INPUT);

	/**
	 * ... export film grain parameters through frame side data
	 */
	public static final ExportSideData FILM_GRAIN = new ExportSideData(3, "film_grain", IO.INPUT);

	protected ExportSideData(int index, String flag, IO io) {
		super(ExportSideData.class, index, flag, io);
	}
}
