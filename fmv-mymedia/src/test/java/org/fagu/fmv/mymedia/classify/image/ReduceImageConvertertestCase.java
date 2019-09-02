package org.fagu.fmv.mymedia.classify.image;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.utils.media.Size;
import org.junit.Test;


/**
 * @author f.agu
 * @created 26 août 2019 21:05:39
 */
public class ReduceImageConvertertestCase {

	@Test
	public void test() throws IOException {
		Soft convertSoft = Convert.search();
		System.out.println(convertSoft.getFirstFound().getFile());
		try (ReduceImageConverter converter = new ReduceImageConverter(new File("."))) {
			converter.setSize(Size.HD720);
			IMOperation op = new IMOperation();
			op.image("source.jpg", "[0]");
			converter.populateOperation(op);
			op.image("dest.jpg");

			System.out.println(CommandLineUtils.toLine(op.toList()));
		}
	}
}
