package org.fagu.fmv.mymedia.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;


/**
 * @author Oodrive
 * @author f.agu
 * @created 17 mars 2025 18:20:07
 */
public class PerceptionHashInfoFile implements InfoFile {

	@Override
	public List<Character> getCodes() {
		return List.of('A');
	}

	@Override
	public boolean isMine(Object object) {
		return false;
	}

	@Override
	public List<Line> toLines(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException {
		PerceptiveHash perceptiveHash = new PerceptiveHash(32);
		try (InputStream inputStream = new FileInputStream(fileFound.getFileFound())) {
			BufferedImage image = ImageIO.read(inputStream);
			Hash hash = perceptiveHash.hash(image);
			return List.of(
					new Line(
							'A',
							String.join(";",
									hash.getHashValue().toString(),
									Integer.toString(hash.getBitResolution()),
									Integer.toString(hash.getAlgorithmId()))));
		}
	}

	@Override
	public Object parse(File file, String line) throws IOException {
		StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
		return new Hash(
				new BigInteger(stringTokenizer.nextToken()),
				Integer.parseInt(stringTokenizer.nextToken()),
				Integer.parseInt(stringTokenizer.nextToken()));
	}

}
