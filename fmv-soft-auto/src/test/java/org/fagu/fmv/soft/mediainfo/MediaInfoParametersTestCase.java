package org.fagu.fmv.soft.mediainfo;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 3 juil. 2019 15:31:39
 */
public class MediaInfoParametersTestCase {

	@Test
	@Ignore
	public void testParameters() throws IOException {
		Info info = new MediaInfoParameters().getAllParameters();
		for(InfoBase infoBase : info.getInfos()) {
			System.out.println();
			System.out.println("****************************");
			System.out.println(infoBase.getType() + " #" + infoBase.getIndexByType());
			infoBase.getDataMap().forEach((k, v) -> System.out.println(k + " : " + v));
			System.out.println();
		}
	}

}
