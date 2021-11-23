package org.fagu.fmv.mymedia.rip.dvd;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntConsumer;

import org.fagu.fmv.ffmpeg.coder.Coder;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.soft.mplayer.MPlayerDump;
import org.fagu.fmv.soft.mplayer.MPlayerDump.MPlayerDumpBuilder;
import org.fagu.fmv.soft.mplayer.MPlayerTitle;
import org.fagu.fmv.soft.mplayer.MPlayerTitles;
import org.fagu.fmv.utils.time.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * @author fagu
 */
@Disabled
class RipperTestCase {

	@Test
	void test() throws IOException {
		Ripper ripper = Ripper.fromDVDDrive(new File(""))
				.dvdName((dvdDrive, logger) -> "my-dvd")
				.titlesExtractor((dvdDrive, logger) -> mPlayerTitles_1())
				.mPlayerDumperBuilder((dvdDrive, logger) -> mockMPlayerDumpBuilder())
				.ffMPEGExecutorBuilderSupplier(() -> {
					try {
						return ffMPEGExecutorBuilder();
					} catch(IOException e) {
						throw new UncheckedIOException(e);
					}
				})
				.build();

		ripper.rip();
	}

	// ************************************

	private MPlayerDumpBuilder mockMPlayerDumpBuilder() throws IOException {
		MPlayerDump dump = mock(MPlayerDump.class);

		MPlayerDumpBuilder builder = mock(MPlayerDumpBuilder.class);
		AtomicReference<IntConsumer> progress = new AtomicReference<>();
		doAnswer(invocation -> {
			progress.set((IntConsumer)invocation.getArguments()[0]);
			return builder;
		}).when(builder).progress(any(IntConsumer.class));

		doAnswer(invocation -> {
			for(int i = 0; i < 100; ++i) {
				Thread.sleep(30);
				progress.get().accept(i);
			}
			return dump;
		}).when(builder).dump(anyInt(), any(File.class));
		return builder;
	}

	private FFMPEGExecutorBuilder ffMPEGExecutorBuilder() throws IOException {
		FFMPEGExecutorBuilder builder = mock(FFMPEGExecutorBuilder.class);
		doReturn(builder).when(builder).hideBanner();
		doReturn(builder).when(builder).noStats();

		InputProcessor inputProcessor = mock(InputProcessor.class);
		doReturn(inputProcessor).when(builder).addMediaInputFile(any(File.class));

		MovieMetadatas movieMetadatas = mock(MovieMetadatas.class);
		doReturn(movieMetadatas).when(inputProcessor).getMovieMetadatas();

		OutputProcessor outputProcessor = mock(OutputProcessor.class);
		doReturn(outputProcessor).when(builder).addMediaOutputFile(any(File.class));
		doReturn(outputProcessor).when(outputProcessor).codec(any(Coder.class));

		return builder;
	}

	private MPlayerTitles mPlayerTitles_1() {
		Map<String, String> properties = Collections.emptyMap();
		NavigableMap<Integer, MPlayerTitle> mPlayerTitleMap = new TreeMap<>();

		NavigableSet<Duration> chapters = new TreeSet<>();
		chapters.add(Duration.valueOf(10 * 60));
		chapters.add(Duration.valueOf(20 * 60));
		chapters.add(Duration.valueOf(30 * 60));

		MPlayerTitle mPlayerTitle = new MPlayerTitle(1, Duration.valueOf(84 * 60), chapters);
		mPlayerTitleMap.put(4, mPlayerTitle);
		return new MPlayerTitles(properties, mPlayerTitleMap);
	}
}
