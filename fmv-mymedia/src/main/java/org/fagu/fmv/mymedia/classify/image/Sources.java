package org.fagu.fmv.mymedia.classify.image;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.fagu.fmv.mymedia.file.ImageFinder;


/**
 * @author f.agu
 */
public class Sources {

	/**
	 * 
	 */
	public Sources() {}

	/**
	 * @param sourceFinder
	 * @param file
	 * @throws IOException
	 */
	public static void save(ImageFinder sourceFinder, File file) throws IOException {
		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
			outputStream.writeObject(sourceFinder);
		}
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ImageFinder load(File file) throws IOException {
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
			return (ImageFinder)inputStream.readObject();
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
