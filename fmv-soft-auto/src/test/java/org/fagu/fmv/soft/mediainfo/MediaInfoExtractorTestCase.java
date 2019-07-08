package org.fagu.fmv.soft.mediainfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;


/**
 * @author Utilisateur
 * @created 7 avr. 2018 14:44:47
 */
public class MediaInfoExtractorTestCase {

	@Test
	// @Ignore
	public void test1() throws IOException {
		MediaInfoExtractor extractor = new MediaInfoExtractor();
		Map<File, Info> map = extractor.extractAll(
				new File("C:\\Projects\\fmv\\fmv-soft-auto\\src\\test\\resources\\org\\fagu\\fmv\\soft\\mp4.mp4"),
				new File("C:\\Oodrive\\video\\mp4-2\\a.mp4"));

		map.forEach((file, info) -> {
			System.out.println();
			System.out.println("############################ " + file);
			for(InfoBase infoBase : info.getInfos()) {
				System.out.println(infoBase.getType() + " #" + infoBase.getIndexByType());
				infoBase.getDataMap().forEach((k, v) -> System.out.println(k + " : " + v));
				System.out.println();
			}
		});
	}

}
