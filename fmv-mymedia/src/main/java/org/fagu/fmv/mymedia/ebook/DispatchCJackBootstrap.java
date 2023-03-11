package org.fagu.fmv.mymedia.ebook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Utilisateur
 * @created 7 janv. 2023 11:54:29
 */
public class DispatchCJackBootstrap {

	private static final File SRC = new File(
			"a path");

	public static void main(String... args) throws IOException {
		for(File file : SRC.listFiles()) {
			String name = file.getName();
			if( ! name.endsWith(" - Christian Jacq.epub")) {
				System.out.println(name);
				continue;
			}
			name = "Christian Jacq - " + StringUtils.substringBefore(name, " - Christian Jacq.epub") + ".epub";
			File d = new File(file.getParent(), name);
			System.out.println(file + " -> " + d);
			Files.move(file.toPath(), d.toPath());
		}
	}

}
