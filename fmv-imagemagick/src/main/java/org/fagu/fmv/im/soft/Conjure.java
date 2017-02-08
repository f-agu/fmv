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
 * The conjure program gives you the ability to perform custom image processing tasks from a script written in the
 * Magick Scripting Language (MSL). MSL is XML-based and consists of action statements with attributes. Actions include
 * reading an image, processing an image, getting attributes from an image, writing an image, and more. An attribute is
 * a key/value pair that modifies the behavior of an action.
 * 
 * @see {@link https://www.imagemagick.org/script/conjure.php}
 * @author f.agu
 */
public class Conjure {

	/**
	 * 
	 */
	private Conjure() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @return
	 */
	public static Soft search() {
		return Soft.search(ConjureSoftProvider.NAME);
	}

}
