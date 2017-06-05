package org.fagu.fmv.mymedia.rip.dvd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.fagu.fmv.mymedia.utils.TextProgressBar;
import org.fagu.fmv.mymedia.utils.TextProgressBar.TextProgressBarBuilder;
import org.fagu.fmv.soft.mplayer.MPlayer;
import org.fagu.fmv.soft.mplayer.MPlayerDump;
import org.fagu.fmv.soft.mplayer.MPlayerTitle;
import org.fagu.fmv.soft.mplayer.MPlayerTitles;


/**
 * @author f.agu
 * @created 5 juin 2017 13:08:48
 */
public class Ripper {

	// ---------------------------------------------

	/**
	 * @author f.agu
	 * @created 5 juin 2017 13:09:47
	 */
	public static class RipperBuilder {

		private final File dvdDrive;

		private File tmpDirectory = new File("d:\\tmp\\dvd-rip");

		private RipperBuilder(File dvdDrive) {
			this.dvdDrive = Objects.requireNonNull(dvdDrive);
		}

		public RipperBuilder tmpDirectory(File tmpDirectory) {
			this.tmpDirectory = Objects.requireNonNull(tmpDirectory);
			return this;
		}

		public Ripper build() {
			return new Ripper(this);
		}
	}

	// ---------------------------------------------

	private final File dvdDrive;

	private final File tmpDirectory;

	private final ExecutorService ffmpegService;

	private TextProgressBar textProgressBar;

	/**
	 * @param builder
	 */
	private Ripper(RipperBuilder builder) {
		this.dvdDrive = builder.dvdDrive;
		this.tmpDirectory = builder.tmpDirectory;
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
		System.out.println("Analyzing DVD on " + dvdDrive + "...");
		String dvdName = getDVDName();
		System.out.println("Name: " + dvdName);

		System.out.println("Scanning titles...");
		MPlayerTitles mPlayerTitles = MPlayerTitles.fromDVDDrive(dvdDrive).find();
		List<MPlayerTitle> titles = mPlayerTitles.getTitlesLongest();
		System.out.println("Found " + titles.size() + " titles");
		titles.forEach(t -> System.out.println("   " + t.getNum() + "/" + t.getLength()));

		final int prefixWidth = 40;
		textProgressBar = TextProgressBarBuilder.width(40)
				.consolePrefixMessage(StringUtils.rightPad(StringUtils.abbreviate(dvdName, prefixWidth), prefixWidth) + "  ")
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
			AtomicInteger dumpProgress = progressIterator.next();
			AtomicInteger encodeProgress = progressIterator.next();
			dumpTitle(title, dvdName, dumpProgress, encodeProgress);
		}
	}

	// ******************************************

	/**
	 * @param title
	 * @param dvdName
	 * @param progressDump
	 * @param progressEncode
	 * @throws IOException
	 */
	private void dumpTitle(MPlayerTitle title, String dvdName, AtomicInteger progressDump, AtomicInteger progressEncode) throws IOException {
		System.out.println("DVD : " + title);
		String baseName = "dvd-" + dvdName + "-" + title.getNum() + "-";
		File vobFile = File.createTempFile(baseName, ".vob", tmpDirectory);
		File mp4File = File.createTempFile(baseName, ".mp4", tmpDirectory);

		MPlayerDump mPlayerDump = MPlayerDump.fromDVDDrive(dvdDrive)
				.progress(progressDump::set)
				.dump(title.getNum(), vobFile);

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
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
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
	 * @return
	 * @throws IOException
	 */
	private String getDVDName() throws IOException {
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
				// .logCommandLine(System.out::println)
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
