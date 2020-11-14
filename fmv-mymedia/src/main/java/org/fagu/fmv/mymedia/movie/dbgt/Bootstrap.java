package org.fagu.fmv.mymedia.movie.dbgt;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.ByteSize;


/**
 * @author Utilisateur
 * @created 11 nov. 2020 09:58:22
 */
public class Bootstrap {

	private static final String PREFIX = "Dragon Ball GT - S";

	public static void main(String... args) throws Exception {
		File folder = new File(args[0]);

		File[] files = folder.listFiles(f -> f.isFile() && f.getName().startsWith(PREFIX));
		if(files == null) {
			return;
		}
		for(File file : files) {
			File outFile = getOutFile(file);
			if(outFile.exists() && outFile.length() > 100_000_000) {
				continue;
			}
			System.out.print(outFile.getName());
			rewriteWithoutAudioStream(file, outFile);
			System.out.println("     " + ByteSize.toStringDiffSize(file.length(), outFile.length()));
		}
	}

	private static File getOutFile(File srcFile) {
		final String suffix = ") 1080p vf-vost [HoddenAnime]";
		File parentFolder = srcFile.getParentFile();
		File outFolder = new File(parentFolder.getParentFile(), parentFolder.getName() + "-out");
		outFolder.mkdirs();
		String fileName = FilenameUtils.getBaseName(srcFile.getName());
		if(fileName.endsWith(suffix)) {
			fileName = fileName.substring(0, fileName.length() - suffix.length() - 6);
		}
		fileName = StringUtils.substringAfter(fileName, PREFIX);
		fileName = StringUtils.substringAfter(fileName, "E");
		fileName += "." + FilenameUtils.getExtension(srcFile.getName());
		return new File(outFolder, fileName);
	}

	private static void rewriteWithoutAudioStream(File srcFile, File outFile) throws Exception {
		// 1'52
		MovieMetadatas metadatas = MovieMetadatas.with(srcFile).extract();

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		OutputProcessor outputProcessor = builder.addMediaOutputFile(outFile);

		metadatas.getVideoStreams()
				.forEach(videoStream -> outputProcessor.map().streams(videoStream).input(inputProcessor));
		metadatas.getAudioStreams().stream()
				.filter(audioStream -> audioStream.language().orElse("").toLowerCase().startsWith("fr"))
				.forEach(audioStream -> outputProcessor.map().streams(audioStream).input(inputProcessor));
		outputProcessor.codecCopy(Type.AUDIO);
		outputProcessor.codecCopy(Type.VIDEO);

		outputProcessor.overwrite();
		builder.build().execute();
	}

}
