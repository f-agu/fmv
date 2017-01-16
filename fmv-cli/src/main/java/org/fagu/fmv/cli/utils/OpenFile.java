package org.fagu.fmv.cli.utils;

/*
 * #%L
 * fmv-cli
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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.cli.FMVCLIConfig;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.media.FileType;


/**
 * @author f.agu
 */
public class OpenFile {

	private final FMVCLIConfig fmvCliConfig;

	private final Printer printer;

	/**
	 * @param fmvCliConfig
	 * @param printer
	 */
	public OpenFile(FMVCLIConfig fmvCliConfig, Printer printer) {
		this.fmvCliConfig = fmvCliConfig;
		this.printer = printer;
	}

	/**
	 * @param fileSource
	 */
	public void open(FileSource fileSource) {
		File file = fileSource.getFile();
		FileType fileType = fileSource.getFileType();
		open(file, fileType);
	}

	/**
	 * @param file
	 * @param fileType
	 */
	public void open(File file, FileType fileType) {
		String propKey = "view." + fileType.name().toLowerCase();
		String property = fmvCliConfig.getProperty(propKey);
		if(StringUtils.isNotBlank(property)) {
			ProcessBuilder pb = new ProcessBuilder(property, file.getPath());
			try {
				pb.start();
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Desktop.getDesktop().open(file);
			} catch(IOException e) {
				printer.println("Cannot open the file: " + file.getPath());
				printer.println("Add in your properties file the key '" + propKey + "' with the path to your software");
			}
		}
	}

}
