package org.fagu.fmv.image;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;


/**
 * @author f.agu
 * @created 9 avr. 2018 15:32:32
 */
public class Blur {

	private Blur() {}

	public static BufferedImage blur(BufferedImage src, float distor) {
		float[] matrix = {
				distor, distor, distor,
				distor, distor, distor,
				distor, distor, distor,
		};
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
		return op.filter(src, null);
	}

}
