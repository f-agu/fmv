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


/**
 * Use the mogrify program to resize an image, blur, crop, despeckle, dither, draw on, flip, join, re-sample, and much
 * more. This tool is similar to convert except that the original image file is overwritten (unless you change the file
 * suffix with the -format option) with any changes you request.
 * 
 * @see {@link https://www.imagemagick.org/script/mogrify.php}
 * @author f.agu
 */
public class Mogrify {

	/**
	 * 
	 */
	private Mogrify() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @return
	 */
	public static Soft search() {
		return Soft.search(new MogrifySoftProvider());
	}

}
