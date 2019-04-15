package org.fagu.fmv.mymedia.rip.dvd;

import java.io.Closeable;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.fagu.fmv.ffmpeg.coder.H264;
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
import org.fagu.fmv.mymedia.utils.AppVersion;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.mplayer.CmdLineConfirmSelectTitlesPolicy;
import org.fagu.fmv.soft.mplayer.DefaultSelectTitlesPolicy;
import org.fagu.fmv.soft.mplayer.MPlayer;
import org.fagu.fmv.soft.mplayer.MPlayerDump;
import org.fagu.fmv.soft.mplayer.MPlayerDump.MPlayerDumpBuilder;
import org.fagu.fmv.soft.mplayer.MPlayerTitle;
import org.fagu.fmv.soft.mplayer.MPlayerTitles;
import org.fagu.fmv.soft.mplayer.SelectTitlesPolicy;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.TextProgressBar.TextProgressBarBuilder;
import org.fagu.fmv.textprogressbar.part.PercentPart;
import org.fagu.fmv.textprogressbar.part.ProgressPart;
import org.fagu.fmv.textprogressbar.part.SpinnerPart;


/**
 * @author f.agu
 * @created 5 juin 2017 13:08:48
 */
public class Ripper implements Closeable {

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

		private SelectTitlesPolicy selectTitlesPolicy = new CmdLineConfirmSelectTitlesPolicy(
				new DefaultSelectTitlesPolicy());

		private TitlesExtractor titlesExtractor = (dvdDrive, logger) -> MPlayerTitles.fromDVDDrive(dvdDrive)
				.logger(logger::log).find();

		private DVDName dvdName = Ripper::getDVDName;

		private MPlayerDumperBuilder mPlayerDumperBuilder = (dvdDrive, logger) -> MPlayerDump.fromDVDDrive(dvdDrive)
				.logger(logger::log);

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

		public RipperBuilder ffMPEGExecutorBuilderSupplier(
				Supplier<FFMPEGExecutorBuilder> ffMPEGExecutorBuilderSupplier) {
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
			this.logger = LoggerFactory.openLogger(
					LoggerFactory.getLogFile(tmpDirectory, PROPERTY_LOG_FILE, PROPERTY_LOG_FILE_DEFAULT_NAME));
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
		AppVersion.logMyVersion(logger::log);
		logger.log("===========================================================");
		String msg = "Analyzing DVD on " + dvdDrive + "...";
		logger.log(msg);
		String name = null;
		try (TextProgressBar bar = TextProgressBar.newBar().appendText(msg + "   ").append(new SpinnerPart())
				.buildAndSchedule()) {
			name = dvdName.nameOf(dvdDrive, logger);
		}
		displayAndLog("Name: " + name);

		msg = "Scanning titles...";
		logger.log(msg);
		MPlayerTitles mPlayerTitles = null;
		try (TextProgressBar bar = TextProgressBar.newBar().appendText(msg + "   ").append(new SpinnerPart())
				.buildAndSchedule()) {
			mPlayerTitles = titlesExtractor.extract(dvdDrive, logger);
		}
		Collection<MPlayerTitle> titles = selectTitlesPolicy.select(mPlayerTitles.getTitles());
		displayAndLog("Found " + titles.size() + " titles:");
		titles.forEach(t -> displayAndLog("   " + t.getNum() + "/" + t.getLength()));
		displayAndLog("Write to " + tmpDirectory);

		AtomicInteger currentTitle = new AtomicInteger();
		AtomicInteger currentEncoding = new AtomicInteger();
		final int prefixWidth = 30;
		int nbProgresses = titles.size() * 2;
		List<AtomicInteger> progressList = new ArrayList<>(nbProgresses);
		titles.forEach(t -> {
			progressList.add(new AtomicInteger()); // dump
			progressList.add(new AtomicInteger()); // encode
		});

		final String finalName = name;
		TextProgressBarBuilder builder = TextProgressBar.newBar()
				.fixWidth(32).withText(StringUtils.abbreviate(finalName, prefixWidth) + " ");

		if(titles.size() == 1) {
			builder.fixWidth(20).with(
					s -> currentTitle.get() == 1 ? "reading DVD..." : currentEncoding.get() == 1 ? "encoding..." : "");
		} else {
			builder.fixWidth(19).with(
					s -> currentTitle.get() > 0 ? "reading DVD: " + currentTitle.get() + "/" + titles.size() : "");
			builder.fixWidth(15).with(
					s -> currentEncoding.get() > 0 ? "encoding: " + currentEncoding.get() + "/" + titles.size() : "");
		}
		textProgressBar = builder.append(ProgressPart.width(31).build()).fixWidth(6).leftPad().with(new PercentPart())
				.buildAndSchedule(() -> progressList.stream().mapToInt(AtomicInteger::get).sum() / nbProgresses);

		if( ! tmpDirectory.exists() && ! tmpDirectory.mkdirs()) {
			throw new IOException("Unable to make directory: " + tmpDirectory);
		}
		CountDownLatch encodingLatch = new CountDownLatch(titles.size());
		Iterator<AtomicInteger> progressIterator = progressList.iterator();
		for(MPlayerTitle title : titles) {
			AtomicInteger dumpProgress = progressIterator.next();
			AtomicInteger encodeProgress = progressIterator.next();

			currentTitle.incrementAndGet();
			String baseName = name + "-" + title.getNum();
			File vobFile = File.createTempFile(baseName + '-', ".vob", tmpDirectory);
			logger.log("Dumping title " + currentTitle + "/" + titles.size() + ": " + vobFile.getAbsolutePath());

			MPlayerDump mPlayerDump = mPlayerDumperBuilder.prepare(dvdDrive, logger).progress(dumpProgress::set)
					.dump(title.getNum(), vobFile);

			File mp4File = new File(tmpDirectory, baseName + ".mp4");
			logger.log("Encoding title " + currentTitle + "/" + titles.size() + ": " + mp4File.getAbsolutePath());
			encode(vobFile, mp4File, mPlayerDump, encodeProgress, currentEncoding, encodingLatch);
		}
		currentTitle.set( - 1);
		try {
			encodingLatch.await();
		} catch(InterruptedException e) {
			throw new IOException(e);
		}
		logger.log("End");
	}

	/**
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		textProgressBar.close();
		ffmpegService.shutdown();
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
	 * @param vobFile
	 * @param mp4File
	 * @param mPlayerDump
	 * @param progressEncode
	 * @throws IOException
	 */
	private void encode(File vobFile, File mp4File, MPlayerDump mPlayerDump, AtomicInteger progressEncode,
			AtomicInteger currentEncoding, CountDownLatch encodingLatch) throws IOException {
		FFMPEGExecutorBuilder builder = ffMPEGExecutorBuilderSupplier.get();
		builder.hideBanner();

		InputProcessor inputProcessor = builder.addMediaInputFile(vobFile);
		MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();

		OutputProcessor outputProcessor = builder.addMediaOutputFile(mp4File);

		// video
		for(VideoStream stream : movieMetadatas.getVideoStreams()) {
			outputProcessor.map().streams(stream).input(inputProcessor);
		}

		// audio
		filterAndMap(inputProcessor, outputProcessor, movieMetadatas.getAudioStreams().iterator(),
				mPlayerDump.getAudioStreams());

		// subtitle
		filterAndMap(inputProcessor, outputProcessor, movieMetadatas.getSubtitleStreams().iterator(),
				mPlayerDump.getSubtitles());

		outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(21)).overwrite();

		int nbFrames = 0;
		OptionalInt countEstimateFrames = movieMetadatas.getVideoStream().countEstimateFrames();
		if(countEstimateFrames.isPresent()) {
			nbFrames = countEstimateFrames.getAsInt();
		} else {
			// TODO
		}
		builder.progressReadLine(new FFMpegProgress(progressEncode, nbFrames));

		FFExecutor<Object> executor = builder.build();
		logger.log(executor.getCommandLineString());
		ffmpegService.submit(() -> {
			try {
				currentEncoding.incrementAndGet();
				executor.execute();
			} catch(Exception e) {
				logger.log(e);
			} finally {
				encodingLatch.countDown();
				vobFile.delete();
			}
		});
	}

	/**
	 * @param inputProcessor
	 * @param outputProcessor
	 * @param ffmpegStreams
	 * @param mplayerStreams
	 */
	private void filterAndMap(InputProcessor inputProcessor, OutputProcessor outputProcessor,
			Iterator<? extends Stream> ffmpegStreams, List<? extends org.fagu.fmv.soft.mplayer.Stream> mplayerStreams) {
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
		MPlayer.search().withParameters(params)
				.logCommandLine(cl -> logger.log(CommandLineUtils.toLine(cl)))
				.addOutReadLine(l -> {
					if(l.startsWith("ID_DVD_VOLUME_ID=")) {
						volumeId.setValue(StringUtils.substringAfter(l, "="));
					}
				}).execute();
		return volumeId.getValue();
	}

	// public static void main(String[] args) throws IOException {
	// Ripper ripper = Ripper.fromDVDDrive(new File("e:")).build();
	// File vobFile = new
	// File("D:\\tmp\\dvd-rip\\dvd-123_DVD01_SAISON_IV-1-6694961849075169155.vob");
	// File mp4File = new
	// File("D:\\tmp\\dvd-rip\\dvd-123_DVD01_SAISON_IV-1-6694961849075169155.mp4");
	//
	// Map<String, String> map = new HashMap<>();
	// map.put("audio stream", "0");
	// map.put("format", "ac3 (stereo)");
	// map.put("language", "fr");
	// map.put("aid", "128");
	//
	// List<org.fagu.fmv.soft.mplayer.Stream> asList = Arrays.asList(new
	// AudioStream(0, map));
	// MPlayerDump mPlayerDump = new MPlayerDump(asList);
	// ripper.encode(vobFile, mp4File, mPlayerDump);
	// }

	// public static void main(String[] args) throws Exception {
	// File vobFile = new
	// File("D:\\tmp\\dvd-rip\\shak\\dvd-Shark_Tale-50-7679807258330259065.vob");
	// File mp4File = new
	// File("D:\\tmp\\dvd-rip\\shak\\dvd-Shark_Tale-50-7679807258330259065.mp4");
	// Ripper ripper = Ripper.fromDVDDrive(new File(".")).build();
	// AtomicInteger progressEncode = new AtomicInteger();
	// AtomicInteger currentEncoding = new AtomicInteger();
	// CountDownLatch encodingLatch = new CountDownLatch(1);
	// ripper.encode(vobFile, mp4File, null, progressEncode, currentEncoding,
	// encodingLatch);
	// }

}
