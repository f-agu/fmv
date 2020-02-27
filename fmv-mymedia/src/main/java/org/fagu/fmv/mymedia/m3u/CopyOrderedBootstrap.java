package org.fagu.fmv.mymedia.m3u;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;


public class CopyOrderedBootstrap {

	public static void main(String... args) throws Exception {
		File file = new File(args[0]);
		if(file.isDirectory()) {
			File[] files = file.listFiles(f -> "m3u".equals(FilenameUtils.getExtension(f.getName())));
			if(ArrayUtils.isEmpty(files)) {
				return;
			}
			file = files[0];
		}

		File destFolder = new File(args[1]);
		FileUtils.forceMkdir(destFolder);

		File srcFolder = file.getParentFile();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			AtomicInteger index = new AtomicInteger();
			reader.lines()
					.filter(l -> ! l.startsWith("#"))
					.map(l -> new File(srcFolder, l))
					.forEach(f -> {
						int idx = index.incrementAndGet();
						try {
							File destFile = new File(destFolder, (idx <= 9 ? "0" : "") + idx + ' ' + getName(f));
							System.out.println(destFile);
							FileUtils.copyFile(f, destFile);
						} catch(IOException e) {
							e.printStackTrace();
						}
					});
		}
	}

	private static final char[] IGNORE_CHARS = ".)-_".toCharArray();

	private static String getName(File file) {
		String name = file.getName();
		int pos = - 1;
		for(char c : name.toCharArray()) {
			++pos;
			if( ! Character.isDigit(c)
					&& ! Character.isWhitespace(c)
					&& ! ArrayUtils.contains(IGNORE_CHARS, c)) {
				break;
			}
		}
		return pos >= 0 ? name.substring(pos) : name;
	}

}
