package org.fagu.fmv.mymedia.rip.dvd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.fagu.fmv.ffmpeg.coder.Libx264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.utils.TextProgressBar;
import org.fagu.fmv.mymedia.utils.TextProgressBar.TextProgressBarBuilder;
import org.fagu.fmv.soft.mplayer.DefaultSelectTitlesPolicy;
import org.fagu.fmv.soft.mplayer.MPlayer;
import org.fagu.fmv.soft.mplayer.MPlayerDump;
import org.fagu.fmv.soft.mplayer.MPlayerDump.MPlayerDumpBuilder;
import org.fagu.fmv.soft.mplayer.MPlayerTitle;
import org.fagu.fmv.soft.mplayer.MPlayerTitles;
import org.fagu.fmv.soft.mplayer.SelectTitlesPolicy;


/**
 * @author f.agu
 * @created 5 juin 2017 13:08:48
 */
public class Ripper {

	private static final String PROPERTY_LOG_FILE = "fmv.dvdrip.log.file";

	private static final String PROPERTY_LOG_FILE_DEFAULT_NAME = "fmv-dvdrip.log";

	// ---------------------------------------------

	/**
	 * @author f.agu
	 * @created 5 juin 2017 13:09:47
	 */
	public static class RipperBuilder {

		private final File dvdDrive;

		private File tmpDirectory = new File("d:\\tmp\\dvd-rip");

		private SelectTitlesPolicy selectTitlesPolicy = new DefaultSelectTitlesPolicy();

		private TitlesExtractor titlesExtractor = (dvdDrive, logger) -> MPlayerTitles.fromDVDDrive(dvdDrive)
				.logger(logger::log)
				.find();

		private DVDName dvdName = Ripper::getDVDName;

		private MPlayerDumperBuilder mPlayerDumperBuilder = (dvdDrive, logger) -> MPlayerDump.fromDVDDrive(dvdDrive).logger(logger::log);

		private Supplier<FFMPEGExecutorBuilder> ffMPEGExecutorBuilderSupplier = FFMPEGExecutorBuilder::create;

		private RipperBuilder(File dvdDrive) {
			this.dvdDrive = Objects.requireNonNull(dvdDrive);
		}

		public RipperBuilder tmpDirectory(File tmpDirectory) {
			this.tmpDirectory = Objects.requireNonNull(tmpDirectory);
			return this;
		}

		public RipperBuilder selectTitlesPolicy(SelectTitlesPolicy selectTitlesPolicy) {
			this.selectTitlesPolicy = Objects.requireNonNull(selectTitlesPolicy);
			return this;
		}

		public RipperBuilder titlesExtractor(TitlesExtractor titlesExtractor) {
			this.titlesExtractor = Objects.requireNonNull(titlesExtractor);
			return this;
		}

		public RipperBuilder dvdName(DVDName dvdName) {
			this.dvdName = Objects.requireNonNull(dvdName);
			return this;
		}

		public RipperBuilder mPlayerDumperBuilder(MPlayerDumperBuilder mPlayerDumperBuilder) {
			this.mPlayerDumperBuilder = Objects.requireNonNull(mPlayerDumperBuilder);
			return this;
		}

		public RipperBuilder ffMPEGExecutorBuilderSupplier(Supplier<FFMPEGExecutorBuilder> ffMPEGExecutorBuilderSupplier) {
			this.ffMPEGExecutorBuilderSupplier = Objects.requireNonNull(ffMPEGExecutorBuilderSupplier);
			return this;
		}

		public Ripper build() {
			return new Ripper(this);
		}
	}

	// ---------------------------------------------

	/**
	 * @author fagu
	 */
	public interface TitlesExtractor {

		/**
		 * @param dvdDrive
		 * @param logger
		 * @return
		 * @throws IOException
		 */
		MPlayerTitles extract(File dvdDrive, Logger logger) throws IOException;
	}
	// ---------------------------------------------

	/**
	 * @author fagu
	 */
	public interface DVDName {

		/**
		 * @param dvdDrive
		 * @param logger
		 * @return
		 * @throws IOException
		 */
		String nameOf(File dvdDrive, Logger logger) throws IOException;
	}

	// ---------------------------------------------

	/**
	 * @author fagu
	 */
	public interface MPlayerDumperBuilder {

		/**
		 * @param dvdDrive
		 * @param logger
		 * @return
		 * @throws IOException
		 */
		MPlayerDumpBuilder prepare(File dvdDrive, Logger logger) throws IOException;
	}

	// ---------------------------------------------

	private final File dvdDrive;

	private final File tmpDirectory;

	private final ExecutorService ffmpegService;

	private final SelectTitlesPolicy selectTitlesPolicy;

	private final TitlesExtractor titlesExtractor;

	private final DVDName dvdName;

	private final MPlayerDumperBuilder mPlayerDumperBuilder;

	private final Supplier<FFMPEGExecutorBuilder> ffMPEGExecutorBuilderSupplier;

	private final Logger logger;

	private TextProgressBar textProgressBar;

	/**
	 * @param builder
	 */
	private Ripper(RipperBuilder builder) {
		this.dvdDrive = builder.dvdDrive;
		this.tmpDirectory = builder.tmpDirectory;
		this.selectTitlesPolicy = builder.selectTitlesPolicy;
		this.titlesExtractor = builder.titlesExtractor;
		this.dvdName = builder.dvdName;
		this.mPlayerDumperBuilder = builder.mPlayerDumperBuilder;
		this.ffMPEGExecutorBuilderSupplier = builder.ffMPEGExecutorBuilderSupplier;
		try {
			this.logger = LoggerFactory.openLogger(LoggerFactory.getLogFile(tmpDirectory, PROPERTY_LOG_FILE, PROPERTY_LOG_FILE_DEFAULT_NAME));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		ffmpegService = Executors.newSingleThreadExecutor();
	}

	/**
	 * @param dvdDrive
	 * @return
	 */
	public static RipperBuilder fromDVDDrive(File dvdDrive) {
		return new RipperBuilder(dvdDrive);
	}

	/**
	 * @throws IOException
	 */
	public void rip() throws IOException {
		logger.log("===========================================================");
		displayAndLog("Analyzing DVD on " + dvdDrive + "...");
		String name = dvdName.nameOf(dvdDrive, logger);
		displayAndLog("Name: " + name);

		displayAndLog("Scanning titles...");
		MPlayerTitles mPlayerTitles = titlesExtractor.extract(dvdDrive, logger);
		Collection<MPlayerTitle> titles = selectTitlesPolicy.select(mPlayerTitles.getTitles());
		displayAndLog("Found " + titles.size() + " titles");
		titles.forEach(t -> System.out.println("   " + t.getNum() + "/" + t.getLength()));

		AtomicInteger currentTitle = new AtomicInteger();
		final int prefixWidth = 40;
		textProgressBar = TextProgressBarBuilder.width(40)
				.consolePrefixMessage(() -> StringUtils.rightPad(StringUtils.abbreviate(name, prefixWidth), prefixWidth) + " " + currentTitle.get()
						+ "/" + titles.size() + "  ")
				.build();

		int nbProgresses = titles.size() * 2;
		List<AtomicInteger> progressList = new ArrayList<>(nbProgresses);
		titles.forEach(t -> {
			progressList.add(new AtomicInteger());
			progressList.add(new AtomicInteger());
		});

		textProgressBar.schedule(() -> {
			return progressList.stream()
					.mapToInt(AtomicInteger::get)
					.sum() / nbProgresses;
		}, null);

		if( ! tmpDirectory.exists() && ! tmpDirectory.mkdirs()) {
			throw new IOException("Unable to make directory: " + tmpDirectory);
		}
		Iterator<AtomicInteger> progressIterator = progressList.iterator();
		for(MPlayerTitle title : titles) {
			currentTitle.incrementAndGet();
			AtomicInteger dumpProgress = progressIterator.next();
			AtomicInteger encodeProgress = progressIterator.next();
			dumpTitle(title, name, dumpProgress, encodeProgress);
		}
	}

	// ******************************************

	/**
	 * @param msg
	 */
	private void displayAndLog(String msg) {
		logger.log(msg);
		System.out.println(msg);
	}

	/**
	 * @param title
	 * @param dvdName
	 * @param progressDump
	 * @param progressEncode
	 * @throws IOException
	 */
	private void dumpTitle(MPlayerTitle title, String dvdName, AtomicInteger progressDump, AtomicInteger progressEncode) throws IOException {
		String baseName = "dvd-" + dvdName + "-" + title.getNum() + "-";
		File vobFile = File.createTempFile(baseName, ".vob", tmpDirectory);

		MPlayerDump mPlayerDump = mPlayerDumperBuilder.prepare(dvdDrive, logger)
				.progress(progressDump::set)
				.dump(title.getNum(), vobFile);

		File mp4File = File.createTempFile(baseName, ".mp4", tmpDirectory);
		encode(vobFile, mp4File, mPlayerDump, progressEncode);
	}

	/**
	 * @param vobFile
	 * @param mp4File
	 * @param mPlayerDump
	 * @param progressEncode
	 * @throws IOException
	 */
	private void encode(File vobFile, File mp4File, MPlayerDump mPlayerDump, AtomicInteger progressEncode) throws IOException {
		FFMPEGExecutorBuilder builder = ffMPEGExecutorBuilderSupplier.get();
		builder.hideBanner()
				.noStats();

		InputProcessor inputProcessor = builder.addMediaInputFile(vobFile);
		MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();

		OutputProcessor outputProcessor = builder.addMediaOutputFile(mp4File);

		// video
		for(VideoStream stream : movieMetadatas.getVideoStreams()) {
			outputProcessor.map().streams(stream).input(inputProcessor);
		}

		// audio
		filterAndMap(inputProcessor, outputProcessor, movieMetadatas.getAudioStreams().iterator(), mPlayerDump.getAudioStreams());

		// subtitle
		filterAndMap(inputProcessor, outputProcessor, movieMetadatas.getSubtitleStreams().iterator(), mPlayerDump.getSubtitles());

		outputProcessor.codec(Libx264.build().strict(Strict.EXPERIMENTAL).crf(21))
				.overwrite();

		FFExecutor<Object> executor = builder.build();
		logger.log(executor.getCommandLine());
		ffmpegService.submit(() -> {
			try {
				int nbFrames = 0;
				OptionalInt countEstimateFrames = movieMetadatas.getVideoStream().countEstimateFrames();
				if(countEstimateFrames.isPresent()) {
					nbFrames = countEstimateFrames.getAsInt();
				} else {
					// TODO
				}

				FFMpegProgress ffMpegProgress = new FFMpegProgress(progressEncode, nbFrames);
				executor.addReadLineOnErr(ffMpegProgress);
				// System.out.println("FFMPEG : " + mp4File);
				// System.out.println(executor.getCommandLine());
				executor.execute();
				vobFile.delete();
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @param inputProcessor
	 * @param outputProcessor
	 * @param ffmpegStreams
	 * @param mplayerStreams
	 */
	private void filterAndMap(InputProcessor inputProcessor, OutputProcessor outputProcessor, Iterator<? extends Stream> ffmpegStreams,
			List<? extends org.fagu.fmv.soft.mplayer.Stream> mplayerStreams) {
		if(mplayerStreams.size() == 1) {
			outputProcessor.map().streams(ffmpegStreams.next()).input(inputProcessor);
		} else if(mplayerStreams.size() > 1) {
			Set<Locale> locales = new HashSet<>();
			for(org.fagu.fmv.soft.mplayer.Stream mplayerStream : mplayerStreams) {
				String lang = mplayerStream.getLanguage();
				org.fagu.fmv.ffmpeg.metadatas.Stream ffmpegStream = ffmpegStreams.next();
				Locale locale = null;
				if(lang.toLowerCase().startsWith("fr")) {
					locale = Locale.FRENCH;
				} else if(lang.toLowerCase().startsWith("en")) {
					locale = Locale.ENGLISH;
				}
				if(locale != null && locales.add(locale)) {
					outputProcessor.map().streams(ffmpegStream).input(inputProcessor);
				}
			}
		}
	}

	/**
	 * @param dvdDrive
	 * @param logger
	 * @return
	 * @throws IOException
	 */
	private static String getDVDName(File dvdDrive, Logger logger) throws IOException {
		List<String> params = new ArrayList<>();
		params.add("-noquiet");
		params.add("-slave");
		params.add("-identify");
		params.add("-dvd-device");
		params.add(dvdDrive.toString());
		params.add("-frames");
		params.add("0");
		params.add("dvd://");

		MutableObject<String> volumeId = new MutableObject<>();
		MPlayer.search()
				.withParameters(params)
				.logCommandLine(logger::log)
				.addOutReadLine(l -> {
					if(l.startsWith("ID_DVD_VOLUME_ID=")) {
						volumeId.setValue(StringUtils.substringAfter(l, "="));
					}
				})
				.execute();
		return volumeId.getValue();
	}

	// public static void main(String[] args) throws IOException {
	// Ripper ripper = Ripper.fromDVDDrive(new File("e:")).build();
	// File vobFile = new File("D:\\tmp\\dvd-rip\\dvd-123_DVD01_SAISON_IV-1-6694961849075169155.vob");
	// File mp4File = new File("D:\\tmp\\dvd-rip\\dvd-123_DVD01_SAISON_IV-1-6694961849075169155.mp4");
	//
	// Map<String, String> map = new HashMap<>();
	// map.put("audio stream", "0");
	// map.put("format", "ac3 (stereo)");
	// map.put("language", "fr");
	// map.put("aid", "128");
	//
	// List<org.fagu.fmv.soft.mplayer.Stream> asList = Arrays.asList(new AudioStream(0, map));
	// MPlayerDump mPlayerDump = new MPlayerDump(asList);
	// ripper.encode(vobFile, mp4File, mPlayerDump);
	// }

}
