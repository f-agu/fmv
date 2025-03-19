package org.fagu.fmv.mymedia.file;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.fagu.fmv.im.DominantColor;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.ColorUtils;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author Utilisateur
 */
public class DominantColorInfoFile implements InfoFile {

	private final DominantColor dominantColor;

	public DominantColorInfoFile(DominantColor dominantColor) {
		this.dominantColor = Objects.requireNonNull(dominantColor);
	}

	@Override
	public List<Character> getCodes() {
		return List.of('D');
	}

	@Override
	public boolean isMine(Object object) {
		return false;
	}

	@Override
	public Optional<Info> toInfo(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException {
		Color color = dominantColor.getDominantColor(fileFound.getFileFound());
		return Optional.of(
				new Info(
						color,
						new Line(
								'D',
								ColorUtils.toHex(color))));
	}

	@Override
	public Object parse(File file, String line) throws IOException {
		return ColorUtils.parse(line);
	}

}
