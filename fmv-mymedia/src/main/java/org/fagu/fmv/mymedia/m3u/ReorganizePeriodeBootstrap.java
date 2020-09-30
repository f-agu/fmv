package org.fagu.fmv.mymedia.m3u;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * @author f.agu
 * @created 27 août 2020 10:34:40
 */
public class ReorganizePeriodeBootstrap {

	private static final Map<String, File> FILE_MAP = new HashMap<>();

	public static void main(String... args) {
		if(args.length != 1) {
			System.out.println("Usage: java " + ReorganizePeriodeBootstrap.class + " <all-folder>");
			return;
		}
		File periodFolder = new File(args[0]);

	}

}
