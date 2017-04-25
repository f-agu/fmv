package org.fagu.fmv.im.soft;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.SoftFound;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 25 avr. 2017 12:45:28
 */
public class ConvertSoftProviderTestCase {

	@Test
	@Ignore
	public void test() {
		Soft soft = Convert.search();
		for(SoftFound softFound : soft.getFounds()) {
			System.out.println(softFound.getLocalizedBy());
			System.out.println(softFound + " / " + softFound.getReason());
			System.out.println();
		}
	}
}
