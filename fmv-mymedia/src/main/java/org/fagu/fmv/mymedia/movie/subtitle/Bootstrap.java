package org.fagu.fmv.mymedia.movie.subtitle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.SubtitleStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.FileTypeUtils;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.LoggerFFExecListener;
import org.fagu.fmv.mymedia.utils.AppVersion;
import org.fagu.fmv.utils.IniFile;


/**
 * @author Utilisateur
 * @created 3 juil. 2018 22:38:34
 */
public class Bootstrap {

	private static final String LOG_FILE_PROPERTY = "fmv.movie.subtitle.logfile";

	public void extract(File rootFile) throws IOException {
		try (Logger logger = openLogger(rootFile)) {

			AppVersion.logMyVersion(logger::log);
			logger.log("#################### Root: " + rootFile.getAbsolutePath());
			logger.log("");

			Files.walk(rootFile.toPath())
					.filter(p -> accept(p.toFile().getParentFile()))
					.filter(FileTypeUtils.with(FileType.VIDEO)::verify)
					.filter(p -> ! getSubtitleOutputFile(p.toFile(), null).exists())
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

	private boolean accept(File folder) {
		File file = new File(folder, ".fmv-moviesubtitle");
		if( ! file.exists()) {
			return true;
		}
		try {
			IniFile iniFile = IniFile.load(file);
			return iniFile.contains("exclude", folder.getName());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void extractSubtitle(Logger logger, File movieFile, MovieMetadatas metadatas) throws IOException {
		List<SubtitleStream> subtitleStreams = metadatas.getSubtitleStreams().stream()
				.filter(ss -> {
					Optional<String> lngOpt = ss.language();
					if( ! lngOpt.isPresent() || ! lngOpt.get().toLowerCase().contains("fr")) {
						return false;
					}
					Optional<String> titleOpt = ss.title();
					return titleOpt.isPresent() && titleOpt.get().toLowerCase().contains("forc");
				})
				.collect(Collectors.toList());
		if(subtitleStreams.isEmpty()) {
			return;
		}
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(movieFile);
		logger.log("Extract for " + movieFile.getPath());
		List<File> files = new ArrayList<>();
		if(subtitleStreams.size() == 1) {
			File subFile = getSubtitleOutputFile(movieFile, null);
			files.add(subFile);
			builder.addMediaOutputFile(subFile)
					.map().streams(subtitleStreams.get(0)).input(inputProcessor);
		} else {
			subtitleStreams.forEach(ss -> {
				File subFile = getSubtitleOutputFile(movieFile, ss);
				files.add(subFile);
				builder.addMediaOutputFile(getSubtitleOutputFile(movieFile, ss))
						.map().streams(ss).input(inputProcessor);
			});
		}

		FFExecutor<Object> executor = builder.build();
		executor.addListener(new LoggerFFExecListener(logger));
		try {
			executor.execute();
		} catch(IOException e) {
			files.forEach(File::deleteOnExit);
			throw e;
		}
	}

	private File getSubtitleOutputFile(File srcFile, Stream subtitleStream) {
		StringBuilder joiner = new StringBuilder("")
				.append(FilenameUtils.getBaseName(srcFile.getName()));
		if(subtitleStream != null) {
			subtitleStream.language().ifPresent(lg -> joiner.append('-').append(lg));
			subtitleStream.title().ifPresent(t -> joiner.append('-').append(t));
			joiner.append('-').append(subtitleStream.index());
		}
		joiner.append(".srt");
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
