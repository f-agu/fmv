package org.fagu.fmv.mymedia.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.io.MessageDigestInputStream;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;
import org.fagu.fmv.utils.file.MD5Sum;


/**
 * @author f.agu
 */
public class MD5InfoFile implements InfoFile {

	@Override
	public List<Character> getCodes() {
		return List.of('5');
	}

	@Override
	public boolean isMine(Object object) {
		return false;
	}

	@Override
	public List<Line> toLines(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException {
		File file = fileFound.getFileFound();
		if(file != null) {
			MessageDigest messageDigest = getMessageDigest();
			try (MessageDigestInputStream messageDigestInputStream = new MessageDigestInputStream(new FileInputStream(file), messageDigest)) {
				IOUtils.copyLarge(messageDigestInputStream, OutputStream.nullOutputStream());
				return List.of(new Line('5', messageDigestInputStream.getStringDigest()));
			}
		}
		return null;
	}

	@Override
	public Object parse(File file, String line) throws IOException {
		return new MD5Sum(line);
	}

	// ***********************************************

	private MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
