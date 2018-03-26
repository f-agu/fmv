package org.fagu.fmv.mymedia.reduce.to720p;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.utils.AppVersion;
import org.fagu.fmv.utils.media.Size;


/**
 * @author Utilisateur
 * @created 26 mars 2018 09:11:14
 */
public class Bootstrap {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final String PROPERTY_LOG_FILE = "fmv.reduce.log.file";

	private static final String PROPERTY_LOG_FILE_DEFAULT_NAME = "fmv-reduce.log";

	/**
	 * @param rootFile
	 * @throws IOException
	 */
	public void reduce(File rootFile) throws IOException {
		Logger logger = LoggerFactory.openLogger(LoggerFactory.getLogFile(rootFile, PROPERTY_LOG_FILE, PROPERTY_LOG_FILE_DEFAULT_NAME));

		AppVersion.logMyVersion(logger::log);
		logger.log("#################### Root: " + rootFile.getAbsolutePath());

		Files.walk(rootFile.toPath()) //
				.filter(p -> p.toFile().isFile()) //
				.forEach(p -> {
					logger.log("Reduce to 720p" + p);
					String extension = FilenameUtils.getExtension(p.getName(p.getNameCount() - 1).toString()).toLowerCase();
					if( ! "mkv".equals(extension) && ! "mp4".equals(extension)) {
						return;
					}
					File srcFile = p.toFile();
					try {
						MovieMetadatas metadatas = MovieMetadatas.with(srcFile).extract();
						if( ! isUpperThan720p(metadatas, logger)) {
							return;
						}

						File destFile = new File(srcFile.getParentFile(), FilenameUtils.getBaseName(srcFile.getName()) + "-720p." + extension);
						try (org.fagu.fmv.mymedia.reduce.to720p.FFReducer reducer = new org.fagu.fmv.mymedia.reduce.to720p.FFReducer()) {
							String msg = LocalDateTime.now().format(DATE_TIME_FORMATTER) + ' ' + srcFile.getPath();
							System.out.print(msg);
							reducer.reduceVideo(metadatas, srcFile, metadatas, destFile, msg, logger);
						}
					} catch(IOException e) {
						logger.log(e);
					}
					System.out.println();
				});
		System.out.println();
	}

	/**
	 * @param metadatas
	 * @param logger
	 * @return
	 */
	private boolean isUpperThan720p(MovieMetadatas metadatas, Logger logger) {
		if( ! metadatas.contains(Type.VIDEO)) {
			logger.log("It's not a video");
			return false;
		}
		VideoStream videoStream = metadatas.getVideoStream();
		Size size = videoStream.size();
		if(size.getHeight() > Size.HD720.getHeight()) {
			logger.log("It's upper than 720p: " + size);
			return true;
		}
		logger.log("It's not upper than 720p: " + size);
		return false;

	}

	// ************************************************

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			System.out.println("Usage: java -cp . " + Bootstrap.class + " <folder|file> [<folder|file> ...]");
			return;
		}
		Bootstrap reduce720pBootstrap = new Bootstrap();
		Arrays.asList(args)
				.stream()
				.map(File::new)
				.filter(File::exists)
				.forEach(f -> {
					try {
						reduce720pBootstrap.reduce(f);
					} catch(IOException e) {
						e.printStackTrace();
					}
				});
	}

}
