package org.fagu.fmv.soft.mediainfo;

import org.fagu.fmv.soft.Soft;


/**
 * @author f.agu
 */
public class MediaInfoSearch {

	public static void main(String[] args) throws Exception {
		Soft soft = MediaInfo.search();
		System.out.println(soft.getFile());
	}

}
