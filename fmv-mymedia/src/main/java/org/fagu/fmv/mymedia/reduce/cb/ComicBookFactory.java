package org.fagu.fmv.mymedia.reduce.cb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.github.junrar.exception.UnsupportedRarV5Exception;


/**
 * @author Utilisateur
 * @created 23 nov. 2021 18:24:57
 */
public class ComicBookFactory {

	private enum Type {
		CBR, CBZ
	}

	public ComicBook create(File srcFile) throws IOException {
		Type type = findType(srcFile);
		if(type == Type.CBZ) {
			return new ZipComicBook(srcFile);
		}
		if(type == Type.CBR) {
			RarComicBook cbr = new RarComicBook(srcFile);
			try {
				cbr.countEntry();
				return cbr;
			} catch(IOException e) {
				Throwable cause = e.getCause();
				if(cause != null && cause.getClass().equals(UnsupportedRarV5Exception.class)) {
					return new Rar5ComicBook(srcFile);
				}
			}
		}
		return null;
	}

	// -----------------------------------------------------

	private Type findType(File srcFile) throws IOException {
		try (InputStream inputStream = new FileInputStream(srcFile)) {
			byte[] b = new byte[4];
			inputStream.read(b);
			if(b[0] == 'R' && b[1] == 'a' && b[2] == 'r' && b[3] == '!') {
				return Type.CBR;
			}
			if(b[0] == 'P' && b[1] == 'K') {
				return Type.CBZ;
			}
		}
		return null;
	}
}
