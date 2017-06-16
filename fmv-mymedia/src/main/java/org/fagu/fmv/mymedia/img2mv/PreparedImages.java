package org.fagu.fmv.mymedia.img2mv;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.ffmpeg.coder.Libx264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.impl.Format;
import org.fagu.fmv.ffmpeg.format.Image2Demuxer;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.utils.FPS;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.mymedia.utils.FFMpegTextProgressBar;


/**
 * @author f.agu
 */
public class PreparedImages implements Closeable {

	private String extension;

	private File parentFolder;

	private SortedSet<File> files;

	private FFMpegTextProgressBar ffMpegTextProgressBar;

	/**
	 * @param files
	 */
	public PreparedImages(List<File> files) {
		if(files.isEmpty()) {
			throw new IllegalArgumentException("no file !");
		}
		Iterator<File> iterator = files.iterator();
		File next = iterator.next();
		extension = FilenameUtils.getExtension(next.getName());
		parentFolder = next.getParentFile();
		while(iterator.hasNext()) {
			next = iterator.next();
			if( ! parentFolder.equals(next.getParentFile())) {
				throw new IllegalArgumentException("No same parent: " + parentFolder + " != " + next.getParentFile());
			}
			String name = next.getName();
			if( ! name.startsWith("img_")) {
				throw new IllegalArgumentException("File name not start by 'img_': " + next);
			}
			String curExtension = FilenameUtils.getExtension(next.getName());
			if( ! extension.equals(curExtension)) {
				throw new IllegalArgumentException("No same extension: " + extension + " != " + curExtension);
			}
		}

		this.files = new TreeSet<>(files);
	}

	/**
	 * @param imageFrameRate
	 * @param destVideo
	 * @param videoFrameRate
	 * @throws IOException
	 */
	public void makeVideo(FrameRate imageFrameRate, File destVideo, FPS videoFrameRate) throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		// input
		File srcFiles = new File(parentFolder, "img_%0" + countLengthNumber() + "d." + extension);
		builder.addMediaInput(Image2Demuxer.from(srcFiles).frameRate(imageFrameRate));

		FilterComplex filter = FilterComplex.create(org.fagu.fmv.ffmpeg.filter.impl.FrameRate.to(videoFrameRate), Format.with(PixelFormat.YUV420P));
		builder.filter(filter);

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destVideo);
		outputProcessor.codec(Libx264.build());
		outputProcessor.overwrite();
		outputProcessor.map().allStreams().input(filter);

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLine());
		int countEstimateFrames = (int)(files.size() * videoFrameRate.countFrameBySeconds() * imageFrameRate.invert().doubleValue());
		ffMpegTextProgressBar = FFMpegTextProgressBar.with(executor, "").progressByFrame(countEstimateFrames);
		executor.execute();
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if(ffMpegTextProgressBar != null) {
			ffMpegTextProgressBar.close();
		}
	}

	// ****************************************************

	/**
	 * @return
	 */
	private int countLengthNumber() {
		String name = files.iterator().next().getName();
		Pattern pattern = Pattern.compile("img_([0-9]+)\\..*");
		Matcher matcher = pattern.matcher(name);
		if(matcher.matches()) {
			return matcher.group(1).length();
		}
		throw new IllegalArgumentException("Unreadable file name: " + name);
	}
}
