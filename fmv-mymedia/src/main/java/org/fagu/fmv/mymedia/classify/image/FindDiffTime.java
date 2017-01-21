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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.fagu.fmv.im.Image;
import org.fagu.fmv.mymedia.file.ImageFinder;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public class FindDiffTime {

	/**
	 * 
	 */
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ImageFinder imagesFinder = Sources.load(new File("C:\\tmp\\july\\save"));
		for(FileFinder<Image>.InfosFile infosFile : imagesFinder.getAll()) {
			Image image = infosFile.getMain();
			File file = image.getFile();
			System.out.println(file.getParentFile().getName() + "/" + file.getName() + " : " + DATE_FORMAT.format(new Date(image.getTime())));

			if("GOPR0698.JPG".equals(file.getName())) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(2014, Calendar.JULY, 10, 15, 30, 0);
				System.out.println("############### " + (image.getTime() - calendar.getTimeInMillis()));
			}

		}
	}

}
