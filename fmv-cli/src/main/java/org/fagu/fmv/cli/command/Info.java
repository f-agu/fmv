package org.fagu.fmv.cli.command;

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

import java.io.File;

import org.apache.commons.lang.math.NumberUtils;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.ffmpeg.metadatas.Format;
import org.fagu.fmv.ffmpeg.metadatas.InfoBase;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.media.MetadataProperties;


/**
 * @author f.agu
 */
@Command("info")
public class Info extends AbstractCommand {

	private static final String DEFAULT_PADDING = "  ";

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length == 0) {
			help();
			return;
		}
		for(String arg : args) {
			int num = NumberUtils.toInt(arg, - 1);
			if(num < 0) {
				println("Source error: " + arg);
			}
			FileSource source = project.getSource(num);
			File file = source.getFile();
			println(file.getPath());
			if(source.isImage()) {
				display(source.getImageMetadatas(), DEFAULT_PADDING);
			} else if(source.isAudioOrVideo()) {
				display(source.getVideoMetadatas());
			} else {
				println("  undefined...");
			}
		}
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Display informations of a source";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "info <source num>";
	}

	// ********************************************************

	/**
	 * @param metadatas
	 */
	private void display(MovieMetadatas metadatas) {
		Format format = metadatas.getFormat();
		display(format, DEFAULT_PADDING);
		for(InfoBase infoBase : metadatas.getInfoBaseList()) {
			if(infoBase == format) {
				continue;
			}
			if(infoBase instanceof Stream) {
				Stream stream = (Stream)infoBase;
				println(DEFAULT_PADDING + "Stream " + stream.index() + " (" + stream.type().name().toLowerCase() + ')');
			} else {
				println(DEFAULT_PADDING + "?");
			}
			display(infoBase, DEFAULT_PADDING + DEFAULT_PADDING);
		}
	}

	/**
	 * @param metadataProperties
	 * @param padding
	 */
	private void display(MetadataProperties metadataProperties, String padding) {
		for(String name : metadataProperties.getNames()) {
			Object object = metadataProperties.get(name);
			if(object instanceof MetadataProperties) {
				println(padding + name + ": ");
				display((MetadataProperties)object, padding + DEFAULT_PADDING);
			} else {
				println(padding + name + ": " + object);
			}
		}
	}

}
