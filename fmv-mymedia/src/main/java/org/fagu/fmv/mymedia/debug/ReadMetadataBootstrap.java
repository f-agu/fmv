package org.fagu.fmv.mymedia.debug;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.io.IOException;
import java.util.Optional;

import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.media.MetadatasFactory;
import org.fagu.fmv.soft.exec.exception.ExceptionKnown;


/**
 * @author f.agu
 * @created 19 janv. 2017 16:52:08
 */
public class ReadMetadataBootstrap {

	private static final MetadatasFactory IMAGE_METADATAS_FACTORY = MetadatasFactory.createFactory(FileType.IMAGE);

	private static final MetadatasFactory VIDEO_METADATAS_FACTORY = MetadatasFactory.createFactory(FileType.VIDEO);

	/**
	 * 
	 */
	public ReadMetadataBootstrap() {}

	/**
	 * @param file
	 * @throws IOException
	 */
	private static void recurse(File file) throws IOException {
		if( ! file.exists()) {
			return;
		}
		if(file.isFile()) {
			onFile(file);
			return;
		}
		File[] childs = file.listFiles();
		if(childs == null || childs.length == 0) {
			return;
		}
		for(File child : childs) {
			recurse(child);
		}
	}

	/**
	 * @param file
	 */
	private static void onFile(File file) {
		System.out.println(file);
		// onFile(file, IMAGE_METADATAS_FACTORY);
		onFile(file, VIDEO_METADATAS_FACTORY);
	}

	/**
	 * @param file
	 * @param metadatasFactory
	 */
	private static void onFile(File file, MetadatasFactory metadatasFactory) {
		Metadatas metadatas;
		try {
			metadatas = metadatasFactory.extract(file);
			System.out.println(metadatas);
		} catch(IOException e) {
			Optional<ExceptionKnown> exceptionKnown = metadatasFactory.getExceptionKnown(e);
			if(exceptionKnown.isPresent()) {
				System.out.println(exceptionKnown.toString());
			} else {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String... args) throws IOException {
		for(String arg : args) {
			recurse(new File(arg));
		}
	}

}
