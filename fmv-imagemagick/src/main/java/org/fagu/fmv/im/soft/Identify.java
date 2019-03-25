package org.fagu.fmv.im.soft;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftSearch;


/**
 * The identify program describes the format and characteristics of one or more image files. It also reports if an image
 * is incomplete or corrupt. The information returned includes the image number, the file name, the width and height of
 * the image, whether the image is colormapped or not, the number of colors in the image, the number of bytes in the
 * image, the format of the image (JPEG, PNM, etc.), and finally the number of seconds it took to read and process the
 * image. Many more attributes are available with the verbose option.
 * 
 * @see {@link https://www.imagemagick.org/script/identify.php}
 * @author f.agu
 */
public class Identify {

	private Identify() {
		throw new AssertionError("No instances for you!");
	}

	public static Soft search() {
		return Soft.search(new IdentifySoftProvider());
	}

	public static SoftSearch searchWith() {
		return Soft.with(IdentifySoftProvider::new);
	}

}
