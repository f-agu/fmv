package org.fagu.fmv.mymedia.img2pdf;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.soft.ExecListener;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.gs.GS;
import org.fagu.fmv.utils.ByteSize;


/**
 * @author f.agu
 */
public class Bootstrap {

	private static final int DEFAULT_RESIZE_PERCENT = 60;

	private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "tif", "tiff", "png", "bmp", "psd", "tga"));

	private final Soft convertSoft;

	private final Soft gsSoft;

	/**
	 * 
	 */
	public Bootstrap() {
		convertSoft = Convert.search();
		gsSoft = GS.search();
	}

	/**
	 * @param files
	 * @throws IOException
	 */
	private void mergeJpegToPDF(List<File> files) throws IOException {
		List<File> jpgFiles = findImages(files);
		long totalSize = jpgFiles.stream().mapToLong(File::length).sum();
		System.out.println("Found " + jpgFiles.size() + " for " + ByteSize.formatSize(totalSize));
		List<File> tmpFile = null;
		try {
			tmpFile = resizeToJpeg(jpgFiles, getResizePercent());
			mergeJpeg(tmpFile, getOutputFile(tmpFile.get(0).getParentFile()));
		} finally {
			if(tmpFile != null) {
				tmpFile.forEach(File::delete);
			}
		}
	}

	/**
	 * @return
	 */
	private int getResizePercent() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.print("Resize in % [" + DEFAULT_RESIZE_PERCENT + "%]? ");
			String line = scanner.nextLine().trim();
			if("".equals(line)) {
				return DEFAULT_RESIZE_PERCENT;
			}
			if(line.matches("\\d+")) {
				return Integer.parseInt(line);
			}
		}
	}

	/**
	 * @param parentFile
	 * @return
	 * @throws IOException
	 */
	private File getOutputFile(File parentFile) throws IOException {
		File pdfFile = new File(parentFile, parentFile.getName() + ".pdf");
		if( ! pdfFile.exists()) {
			return pdfFile;
		}
		return createTmpJpegFile(pdfFile);
	}

	/**
	 * @param files
	 * @return
	 */
	private List<File> findImages(List<File> files) {
		List<File> fs = new ArrayList<>(files.size());
		List<File> folders = new ArrayList<>(2);

		FileFilter fileFilter = f -> f.isFile() && EXTENSIONS.contains(FilenameUtils.getExtension(f.getName()));
		for(File f : files) {
			if(fileFilter.accept(f)) {
				fs.add(f);
			} else {
				folders.add(f);
				if(folders.size() > 2 || (folders.size() > 1 && ! fs.isEmpty())) {
					throw new RuntimeException("Too many folders or various inputs (folder & files)");
				}
			}
		}
		if(folders.isEmpty() && fs.isEmpty()) {
			throw new RuntimeException("File not found");
		}
		if(folders.size() > 1 && ! fs.isEmpty()) {
			throw new RuntimeException("Too many folders or various inputs (folder & files)");
		}
		if(fs.isEmpty()) {
			File[] listFiles = folders.get(0).listFiles(fileFilter);
			return findImages(listFiles != null ? Arrays.asList(listFiles) : Collections.emptyList());
		}
		return fs;
	}

	/**
	 * @param files
	 * @param resizePercent
	 * @return
	 * @throws IOException
	 */
	private List<File> resizeToJpeg(List<File> files, int resizePercent) throws IOException {
		List<File> outFiles = new ArrayList<>(files.size());
		int length = files.size();
		for(int i = 0; i < length; ++i) {
			File imageFile = files.get(i);
			File tmpFile = createTmpJpegFile(imageFile);
			System.out.println((i + 1) + "/" + length + "  Resizing " + imageFile.getName() + "...");
			convertSoft.withParameters(imageFile.getAbsolutePath(), "-resize", resizePercent + "%", tmpFile.getAbsolutePath()) //
					.execute();
			outFiles.add(tmpFile);
		}
		return outFiles;
	}

	/**
	 * @param files
	 * @throws IOException
	 */
	private void mergeJpeg(List<File> files, File outputPdf) throws IOException {
		File viewJpegPSFile = new File(new File(gsSoft.getFile().getParentFile().getParentFile(), "lib"), "viewjpeg.ps");

		List<String> parameters = new ArrayList<>();
		parameters.add("-sDEVICE=pdfwrite");
		parameters.add("-dPDFSETTINGS=/prepress");
		parameters.add("-o");
		parameters.add(outputPdf.getAbsolutePath());
		parameters.add(viewJpegPSFile.getAbsolutePath());
		parameters.add("-c");

		StringJoiner joiner = new StringJoiner(" ");
		for(File file : files) {
			joiner.add("(" + file.getName() + ") viewJPEG showpage");
		}

		parameters.add(joiner.toString());

		System.out.println("Generate PDF: " + outputPdf.getName());
		gsSoft.withParameters(parameters) //
				.addCommonReadLine(System.out::println)
				.workingDirectory(files.get(0).getParentFile())
				.addListener(new ExecListener() {

					@Override
					public void eventPrepare(CommandLine commandLine) {
						System.out.println(CommandLineUtils.toLine(commandLine));
					}
				})
				.execute();
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private File createTmpJpegFile(File file) throws IOException {
		String name = file.getName();
		return File.createTempFile(FilenameUtils.getBaseName(name), ".jpg", file.getParentFile());
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.mergeJpegToPDF(Arrays.stream(args).map(File::new).collect(Collectors.toList()));
	}

}
