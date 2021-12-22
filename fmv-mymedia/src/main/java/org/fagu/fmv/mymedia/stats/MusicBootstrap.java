package org.fagu.fmv.mymedia.stats;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.OptionalInt;

import org.fagu.fmv.ffmpeg.FFHelper;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.FileTypeUtils;


/**
 * @author f.agu
 * @created 25 sept. 2021 12:28:31
 */
public class MusicBootstrap implements Closeable {

	private final PrintStream printStream;

	public MusicBootstrap(PrintStream printStream) {
		this.printStream = Objects.requireNonNull(printStream);
	}

	public void extractStats(File file) {
		extractStats(file, OptionalInt.empty());
	}

	public void extractStats(File file, OptionalInt maxDepth) {
		extractStats(Collections.singletonList(file), maxDepth);
	}

	public void extractStats(Collection<File> files, OptionalInt maxDepth) {
		for(File file : files) {
			recurse(file.toPath(), file, 0, maxDepth.orElseGet(() -> Integer.MAX_VALUE));
		}
	}

	@Override
	public void close() throws IOException {
		printStream.close();
	}

	// *****************************************************

	private void recurse(Path rootPath, File currentFile, int currentDepth, int maxDepth) {
		if(currentFile.isFile()) {
			doFile(rootPath, currentFile);
			return;
		}
		System.out.println(currentFile);
		File[] files = currentFile.listFiles();
		Arrays.sort(files);
		for(File f : files) {
			recurse(rootPath, f, currentDepth + 1, maxDepth);
		}
	}

	private void doFile(Path rootPath, File file) {
		if( ! FileTypeUtils.with(FileType.AUDIO).verify(file)) {
			return;
		}
		try {
			MovieMetadatas movieMetadatas = FFHelper.videoMetadatas(file);
			AudioStream audioStream = movieMetadatas.getAudioStream();
			if(audioStream == null) {
				return;
			}
			audioStream.duration().ifPresent(d -> System.out.println(d));
			// printStream.println(values.values().stream()
			// .map(StringUtils::defaultString)
			// .collect(Collectors.joining("\t")));
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String... args) throws IOException {
		if(args.length < 1) {
			System.out.println("Usage: " + MusicBootstrap.class.getName() + " ..."); // TODO
			return;
		}
		try (MusicBootstrap bootstrap = new MusicBootstrap(System.out)) {
			bootstrap.extractStats(new File(args[0]));
		}
	}

}
