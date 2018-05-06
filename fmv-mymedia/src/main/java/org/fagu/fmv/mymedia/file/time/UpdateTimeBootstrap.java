package org.fagu.fmv.mymedia.file.time;

import java.io.File;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;


/**
 * @author Utilisateur
 * @created 5 mai 2018 15:51:46
 */
public class UpdateTimeBootstrap {

	private static void recurse(File file, long lastModified) {
		if( ! file.exists()) {
			return;
		}
		file.setLastModified(lastModified);
		if(file.isFile()) {
			return;
		}
		File[] files = file.listFiles();
		if(files != null) {
			for(File f : files) {
				recurse(f, lastModified);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long lastModified = LocalDateTime.of(2017, 01, 01, 0, 0).toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
		for(String arg : args) {
			System.out.println(arg + "...");
			recurse(new File(arg), lastModified);
		}
	}

}
