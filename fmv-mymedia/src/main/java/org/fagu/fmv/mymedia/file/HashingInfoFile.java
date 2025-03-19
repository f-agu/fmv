package org.fagu.fmv.mymedia.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;

import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.AverageHash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;


/**
 * @author f.agu
 */
public class HashingInfoFile implements InfoFile {

	private final char code;

	private final HashingAlgorithm hashingAlgorithm;

	public HashingInfoFile(char code, HashingAlgorithm hashingAlgorithm) {
		this.code = code;
		this.hashingAlgorithm = Objects.requireNonNull(hashingAlgorithm);
	}

	public static HashingInfoFile perceptionHash() {
		return new HashingInfoFile('A', new PerceptiveHash(128));
	}

	public static HashingInfoFile averageHash() {
		return new HashingInfoFile('g', new AverageHash(64));
	}

	@Override
	public List<Character> getCodes() {
		return List.of(code);
	}

	@Override
	public boolean isMine(Object object) {
		return false;
	}

	@Override
	public Optional<Info> toInfo(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException {
		try (InputStream inputStream = new FileInputStream(fileFound.getFileFound())) {
			BufferedImage image = ImageIO.read(inputStream);
			Hash hash = hashingAlgorithm.hash(image);
			return Optional.of(
					new Info(
							hash,
							new Line(
									code,
									String.join(";",
											hash.getHashValue().toString(),
											Integer.toString(hash.getBitResolution()),
											Integer.toString(hash.getAlgorithmId())))));
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
