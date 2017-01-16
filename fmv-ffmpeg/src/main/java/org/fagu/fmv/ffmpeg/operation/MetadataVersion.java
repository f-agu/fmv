package org.fagu.fmv.ffmpeg.operation;

/*
 * #%L
 * fmv-ffmpeg
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.ioe.FileMediaOutput;
import org.fagu.fmv.utils.Resources;


/**
 * @author f.agu
 */
public class MetadataVersion {

	private static final Map<String, String> FORMAT_MAP = new HashMap<>();

	static {
		FORMAT_MAP.put("mp4", "comment");
		FORMAT_MAP.put("m4a", "comment");
		FORMAT_MAP.put("mov", "comment");
		FORMAT_MAP.put("asf", "comment");
		FORMAT_MAP.put("wmv", "comment");
		FORMAT_MAP.put("wma", "comment");
		FORMAT_MAP.put("avi", "comment");
		FORMAT_MAP.put("mkv", "comment");
		FORMAT_MAP.put("mp3", "comment");
	}

	/**
	 *
	 */
	private MetadataVersion() {}

	/**
	 * @param outputProcessor
	 */
	public static void add(OutputProcessor outputProcessor) {
		String tagName = null;
		MediaOutput mediaOutput = outputProcessor.getMediaOutput();
		if(mediaOutput instanceof FileMediaOutput) {
			String extension = FilenameUtils.getExtension(((FileMediaOutput)mediaOutput).getFile().getName());
			tagName = tagForFormat(extension);
		}

		if(tagName != null) {
			outputProcessor.metadataStream(Type.VIDEO, tagName, "fmvversion:" + getVersion());
		}
	}

	// *********************************************

	/**
	 * @return
	 */
	private static String getVersion() {
		// ImplementationVersion
		String version = MetadataVersion.class.getPackage().getImplementationVersion();
		if(version != null) {
			return version;
		}

		// pom.xml... beurk !
		URL url = MetadataVersion.class.getClassLoader().getResource(Resources.getResourcePath(MetadataVersion.class));
		String urlStr = url.toString();
		if(urlStr.startsWith("file:")) {
			File pomFile = new File(StringUtils.substringBeforeLast(StringUtils.substringAfter(urlStr, "file:"), "target"), "pom.xml");
			if(pomFile.exists()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(pomFile))) {
					String line = null;
					Pattern pattern = Pattern.compile(".*<version>(.*)</version>.*");
					while((line = reader.readLine()) != null) {
						Matcher matcher = pattern.matcher(line);
						if(matcher.matches()) {
							return matcher.group(1);
						}
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}

		return "?";
	}

	/**
	 * @param extension
	 * @return
	 */
	private static String tagForFormat(String extension) {
		if(extension == null) {
			return null;
		}
		return FORMAT_MAP.get(extension.toLowerCase());
	}
}
