package org.fagu.fmv.mymedia.m3u;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;


/**
 * @author f.agu
 * @created 27 août 2020 10:34:40
 */
public class ListPeriodeBootstrap {

	public static void main(String... args) {
		if(args.length != 1) {
			System.out.println("Usage: java " + ListPeriodeBootstrap.class + " <period-folder>");
			return;
		}
		File periodFolder = new File(args[0]);

		File[] listFolders = periodFolder.listFiles(f -> f.isDirectory());
		if(listFolders == null) {
			System.out.println("Folders not found in " + periodFolder);
			return;
		}
		for(File folder : listFolders) {
			System.out.println(folder);
			try (PrintStream printStream = new PrintStream(new File(periodFolder, folder.getName() + ".txt"))) {
				File[] listFiles = folder.listFiles(f -> f.isFile());
				if(listFiles != null) {
					Arrays.stream(listFiles).forEach(f -> printStream.println(f.getName()));
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
