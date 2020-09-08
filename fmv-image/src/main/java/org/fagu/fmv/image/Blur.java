package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
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
