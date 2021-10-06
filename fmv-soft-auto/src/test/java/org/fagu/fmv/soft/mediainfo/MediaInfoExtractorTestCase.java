package org.fagu.fmv.soft.mediainfo;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.util.Map;

import org.fagu.fmv.soft.mediainfo.json.JsonMediaInfoExtractor;
import org.fagu.fmv.soft.mediainfo.raw.RawMediaInfoExtractor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author Utilisateur
 * @created 7 avr. 2018 14:44:47
 */
class MediaInfoExtractorTestCase {

	@Test
	@Disabled
	void testRaw() throws IOException {
		RawMediaInfoExtractor extractor = new RawMediaInfoExtractor();
		Map<File, Info> map = extractor.extractAll(
				new File("C:\\Projects\\fmv\\fmv-soft-auto\\src\\test\\resources\\org\\fagu\\fmv\\soft\\mp4.mp4"));

		map.forEach((file, info) -> {
			System.out.println();
			System.out.println("############################ " + file);
			for(InfoBase infoBase : info.getInfos()) {
				System.out.println(infoBase.getType() + " #" + infoBase.getIndexByType());
				infoBase.getData().forEach((k, v) -> System.out.println(k + " : " + v));
				System.out.println();
			}
		});
	}

	@Test
	@Disabled
	void testJson() throws IOException {
		JsonMediaInfoExtractor extractor = new JsonMediaInfoExtractor();
		Map<File, Info> map = extractor.extractAll(
				new File("C:\\tmp\\transform2\\VID_20190624_113754.mp4"),
				new File("C:\\tmp\\transform2\\gif.gif"));

		map.forEach((file, info) -> {
			System.out.println();
			System.out.println("############################ " + file);
			for(InfoBase infoBase : info.getInfos()) {
				System.out.println(infoBase.getType() + " #" + infoBase.getIndexByType());
				infoBase.getData().forEach((k, v) -> System.out.println(k + " : " + v));
				System.out.println();
			}
		});
	}

}
