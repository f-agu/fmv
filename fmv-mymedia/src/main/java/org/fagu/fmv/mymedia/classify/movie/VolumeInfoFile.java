package org.fagu.fmv.mymedia.classify.movie;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetect;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetected;
import org.fagu.fmv.ffmpeg.format.NullMuxer;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.progressbar.FFMpegProgressBar;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.file.InfoFile;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author f.agu
 */
public class VolumeInfoFile implements InfoFile {

	@Override
	public List<Character> getCodes() {
		return List.of(Character.valueOf('V'));
	}

	@Override
	public boolean isMine(Object object) {
		return object instanceof VolumeDetected;
	}

	@Override
	public Optional<Info> toInfo(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException {
		Media main = infosFile.getMain();
		MovieMetadatas metadatas = (MovieMetadatas)main.getMetadatas();
		if(metadatas == null || metadatas.getAudioStreams().isEmpty()) {
			return Optional.empty();
		}

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInputFile(fileFound.getFileFound());

		VolumeDetect volumeDetect = VolumeDetect.build();
		builder.filter(volumeDetect);

		builder.addMediaOutput(NullMuxer.build()).overwrite();

		FFExecutor<Object> executor = builder.build();
		Optional<Integer> countEstimateFrames = metadatas.getVideoStream().countEstimateFrames();
		Progress progress = executor.getProgress();
		if(countEstimateFrames.isPresent() && progress != null) {
			try (TextProgressBar bar = FFMpegProgressBar.with(progress)
					.byFrame(countEstimateFrames.get())
					.build()
					.makeBar("Detect volume")) {
				executor.execute();
			}
			System.out.println();
		}
		if(volumeDetect.isDetected()) {
			VolumeDetected volumeDetected = volumeDetect.getDetected();
			return Optional.of(
					new Info(
							volumeDetected,
							new Line('V', volumeDetected.toString())));
		}
		return Optional.empty();
	}

	@Override
	public Object parse(File file, String line) throws IOException {
		return VolumeDetected.parse(line);
	}

}
