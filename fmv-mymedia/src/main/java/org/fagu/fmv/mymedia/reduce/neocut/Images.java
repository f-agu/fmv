package org.fagu.fmv.mymedia.reduce.neocut;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.FFHelper;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 * @created 4 avr. 2018 09:48:32
 */
public class Images {

	private final File imageFolder;

	private final Set<Time> times;

	/**
	 * @param imageFolder
	 * @param times
	 */
	public Images(File imageFolder, Set<Time> times) {
		this.imageFolder = Objects.requireNonNull(imageFolder);
		this.times = new HashSet<>(times);
		try {
			FileUtils.forceMkdir(imageFolder);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * @param movieFile
	 * @return
	 * @throws IOException
	 */
	public Map<Time, File> getImages(File movieFile) throws IOException {
		Map<Time, File> map = new HashMap<>();
		for(Time time : times) {
			map.put(time, getOrExtractImage(imageFolder, movieFile, time));
		}
		return map;
	}

	/**
	 * @param movieFile
	 * @param time
	 * @return
	 * @throws IOException
	 */
	public static File extractImage(File movieFile, Time time) throws IOException {
		return getOrExtractImage(movieFile.getParentFile(), movieFile, time);
	}

	// ****************************************************

	/**
	 * @param parentFile
	 * @param movieFile
	 * @param time
	 * @return
	 * @throws IOException
	 */
	private static File getOrExtractImage(File parentFile, File movieFile, Time time) throws IOException {
		File imgFile = new File(parentFile,
				FilenameUtils.getBaseName(movieFile.getName()) + '-' + time.toString().replace(':', '_') + ".png");
		if(imgFile.exists() && imgFile.length() > 1) {
			return imgFile;
		}
		System.out.println("  Generating images for " + movieFile.getName() + "  [" + time + "]...");
		FFHelper.extractOneThumbnail(movieFile, imgFile, time);
		return imgFile;
	}

}
