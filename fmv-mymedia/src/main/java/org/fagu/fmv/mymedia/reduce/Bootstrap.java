package org.fagu.fmv.mymedia.reduce;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 fagu
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

import static org.fagu.fmv.utils.ByteSize.formatSize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;


/**
 * @author f.agu
 */
public class Bootstrap {

	private static final String PROPERTY_LOG_FILE = "fmv.reduce.log.file";

	private static final String PROPERTY_LOG_FILE_DEFAULT_NAME = "fmv-reduce.log";

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private Set<String> imageSet = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "tiff", "tif", "bmp", "psd", "tga"));

	private Set<String> soundSet = new HashSet<>(Arrays.asList("mp3", "wma", "ogg", "m4a", "flac", "wav", "aac"));

	private Set<String> movieSet = new HashSet<>(Arrays.asList("avi", "mov", "mp4", "wmv", "mpg", "3gp", "flv", "ts", "mkv"));

	private Map<String, Supplier<Reducer>> reducerMap = new HashMap<>(4);

	/**
	 *
	 */
	public Bootstrap() {
		addReducer(IMReducer::new, imageSet);
		addReducer(FFReducer::new, soundSet);
		addReducer(FFReducer::new, movieSet);
	}

	/**
	 * @param reducer
	 * @param extensions
	 */
	public void addReducer(Supplier<Reducer> reducer, Collection<String> extensions) {
		extensions.forEach(s -> reducerMap.put(s.toLowerCase(), reducer));
	}

	/**
	 * @param rootFile
	 * @throws IOException
	 */
	public void reduce(File rootFile) throws IOException {
		Logger logger = LoggerFactory.openLogger(LoggerFactory.getLogFile(rootFile, PROPERTY_LOG_FILE, PROPERTY_LOG_FILE_DEFAULT_NAME));

		logger.log("#################### Root: " + rootFile.getAbsolutePath());

		MutableLong previousSize = new MutableLong();
		MutableLong newSize = new MutableLong();

		Files.walk(rootFile.toPath()) //
				.filter(p -> p.toFile().isFile()) //
				.forEach(p -> {
					logger.log("Reduce " + p);
					Supplier<Reducer> supplier = reducerMap.get(FilenameUtils.getExtension(p.getName(p.getNameCount() - 1).toString()).toLowerCase());
					if(supplier != null) {
						try (Reducer reducer = supplier.get()) {
							logger.log("Reducer found: " + reducer.getName());
							File srcFile = p.toFile();
							try {
								String msg = LocalDateTime.now().format(DATE_TIME_FORMATTER) + ' ' + srcFile.getPath() + "...";
								System.out.print(msg);
								File destFile = reducer.reduceMedia(srcFile, msg, logger);
								if(destFile != null && destFile.exists()) {
									boolean extensionChanged = ! FilenameUtils.getExtension(srcFile.getName()).equalsIgnoreCase(FilenameUtils
											.getExtension(destFile.getName()));
									if(destFile.length() > 100 && (srcFile.length() > destFile.length() || extensionChanged)) {
										String stringDiffSize = toStringDiffSize(srcFile.length(), destFile.length());
										logger.log("Replace source by reduced: " + stringDiffSize);
										System.out.print(" OK : " + stringDiffSize);
										previousSize.add(srcFile.length());
										newSize.add(destFile.length());

										srcFile.delete();
										String srcName = srcFile.getName();
										String destExt = FilenameUtils.getExtension(destFile.getName());
										if( ! FilenameUtils.getExtension(srcName).equalsIgnoreCase(destExt)) {
											srcFile = new File(srcFile.getParentFile(), FilenameUtils.getBaseName(srcName) + "." + destExt);
										}

										if( ! destFile.renameTo(srcFile)) {
											System.err.print(" Rename failed: " + destFile);
											logger.log("Rename failed: " + srcFile + " -> " + destFile);
										}
									} else {
										logger.log("Revert");
										System.out.print(" Revert");
										if( ! destFile.delete()) {
											logger.log("Delete failed: " + destFile);
											System.err.print(" Delete failed: " + destFile);
										}
									}
								}
								System.out.println();
							} catch(Exception e) {
								System.out.println();
								e.printStackTrace();
							}
						} catch(IOException e) {
							throw new RuntimeException(e);
						}
					} else {
						logger.log("Reducer not found");
					}
				});
		System.out.println();
		System.out.println(toStringDiffSize(previousSize.longValue(), newSize.longValue()));
	}

	// ************************************************

	/**
	 * @param origSize
	 * @param convertSize
	 * @return
	 */
	private String toStringDiffSize(long origSize, long convertSize) {
		long diff = convertSize - origSize;
		int prct = origSize != 0 ? (int)(100 * diff / origSize) : 0;
		String sign = diff < 0 ? "" : "+";
		return formatSize(origSize) + " -> " + formatSize(convertSize) + " : " + sign + formatSize(diff) + ", " + sign + prct + "%";
	}

	// ************************************************

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			System.out.println("Usage: java -cp . " + Bootstrap.class + " <folder|file> [<folder|file> ...]");
			return;
		}
		Bootstrap reduceBootstrap = new Bootstrap();
		Arrays.asList(args).stream().map(File::new).filter(f -> f.exists()).forEach(f -> {
			try {
				reduceBootstrap.reduce(f);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
	}

}
