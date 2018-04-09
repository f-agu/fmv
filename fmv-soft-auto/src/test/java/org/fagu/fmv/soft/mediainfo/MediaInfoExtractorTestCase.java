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
	public void test1() throws IOException {
		MediaInfoExtractor extractor = new MediaInfoExtractor();
		Map<File, Info> map = extractor.extractAll(
				new File("D:\\tmp\\movie\\2018-04-09\\The Karate Kid (2010) MULTi-VF2 [1080p] BluRay x264-PopHD.mkv"));

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
