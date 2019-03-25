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
 * Use the montage program to create a composite image by combining several separate images. The images are tiled on the
 * composite image optionally adorned with a border, frame, image name, and more.
 * 
 * @see {@link https://www.imagemagick.org/script/montage.php}
 * @author f.agu
 */
public class Montage {

	private Montage() {
		throw new AssertionError("No instances for you!");
	}

	public static Soft search() {
		return Soft.search(new MontageSoftProvider());
	}

	public static SoftSearch searchWith() {
		return Soft.with(MontageSoftProvider::new);
	}

}
