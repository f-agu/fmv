package org.fagu.fmv.soft.mediainfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.fagu.fmv.soft.mediainfo.json.JsonMediaInfoExtractor;
import org.fagu.fmv.soft.mediainfo.raw.RawMediaInfoExtractor;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Utilisateur
 * @created 7 avr. 2018 14:44:47
 */
public class MediaInfoExtractorTestCase {

	@Test
	@Ignore
	public void testRaw() throws IOException {
		RawMediaInfoExtractor extractor = new RawMediaInfoExtractor();
		Map<File, Info> map = extractor.extractAll(
				new File("C:\\Projects\\fmv\\fmv-soft-auto\\src\\test\\resources\\org\\fagu\\fmv\\soft\\mp4.mp4"),
				new File("C:\\Oodrive\\video\\mp4-2\\a.mp4"));

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
	@Ignore
	public void testJson() throws IOException {
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
