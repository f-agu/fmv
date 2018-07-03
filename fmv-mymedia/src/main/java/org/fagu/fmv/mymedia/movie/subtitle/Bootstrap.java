package org.fagu.fmv.mymedia.movie.subtitle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.SubtitleStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.LoggerFFExecListener;
import org.fagu.fmv.mymedia.utils.AppVersion;


/**
 * @author Utilisateur
 * @created 3 juil. 2018 22:38:34
 */
public class Bootstrap {

	private static final String LOG_FILE_PROPERTY = "fmv.movie.subtitle.logfile";

	private Set<String> movieSet = new HashSet<>(Arrays.asList("avi", "mov", "mp4", "wmv", "mpg", "3gp", "flv", "ts", "mkv", "vob"));

	public void extract(File rootFile) throws IOException {
		try (Logger logger = openLogger(rootFile)) {

			AppVersion.logMyVersion(logger::log);
			logger.log("#################### Root: " + rootFile.getAbsolutePath());
			logger.log("");

			Files.walk(rootFile.toPath()) //
					.filter(p -> p.toFile().isFile()) //
					.filter(p -> movieSet.contains(FilenameUtils.getExtension(p.toFile().getName()).toLowerCase()))
					.forEach(p -> {
						try {
							File movieFile = p.toFile();
							MovieMetadatas metadatas = MovieMetadatas.with(movieFile).extract();
							extractSubtitle(logger, movieFile, metadatas);
						} catch(IOException e) {
							logger.log(e);
						}
					});
		}
	}

	// ************************************

	private void extractSubtitle(Logger logger, File movieFile, MovieMetadatas metadatas) throws IOException {
		List<SubtitleStream> subtitleStreams = metadatas.getSubtitleStreams();
		if(subtitleStreams.isEmpty()) {
			return;
		}
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(movieFile);
		logger.log("Extract for " + movieFile.getPath());
		for(Stream stream : subtitleStreams) {
			builder.addMediaOutputFile(getSubtitleOutputFile(movieFile, stream))
					.map().streams(stream).input(inputProcessor);
		}

		FFExecutor<Object> executor = builder.build();
		executor.addListener(new LoggerFFExecListener(logger));
		executor.execute();
	}

	private File getSubtitleOutputFile(File srcFile, Stream subtitleStream) {
		StringBuilder joiner = new StringBuilder("")
				.append(FilenameUtils.getBaseName(srcFile.getName()));
		subtitleStream.language().ifPresent(lg -> joiner.append('-').append(lg));
		subtitleStream.title().ifPresent(t -> joiner.append('-').append(t));
		joiner.append('-').append(subtitleStream.index()).append(".srt");
		return new File(srcFile.getParentFile(), joiner.toString());
	}

	// ************************************

	/**
	 * @return
	 * @throws IOException
	 */
	private Logger openLogger(File rootFile) throws IOException {
		String property = System.getProperty(LOG_FILE_PROPERTY);
		if(property == null) {
			property = "fmv-movie-subtitle.log";
		}
		File logFile = new File(rootFile, property);
		System.out.println("Log file: " + logFile.getAbsolutePath());
		return Loggers.fork(LoggerFactory.openLogger(logFile), Loggers.systemOut());
	}

	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.out.println("Usage: " + Bootstrap.class.getName()
					+ " <file1|folder1> [file2|folder2] ...");
			return;
		}
		Bootstrap bootstrap = new Bootstrap();
		Arrays.asList(args)
				.stream()
				.map(File::new)
				.filter(File::exists)
				.forEach(f -> {
					try {
						bootstrap.extract(f);
					} catch(IOException e) {
						e.printStackTrace();
					}
				});
	}

}
