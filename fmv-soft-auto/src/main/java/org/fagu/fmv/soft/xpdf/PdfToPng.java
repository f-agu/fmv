package org.fagu.fmv.soft.xpdf;

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
import org.fagu.fmv.soft.find.SoftPolicy;


/**
 * @author f.agu
 */
public class PdfToPng {

	/**
	 * 
	 */
	private PdfToPng() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @return
	 */
	public static Soft search() {
		return Soft.search(new PdfToPngSoftProvider());
	}

	/**
	 * @param softPolicy
	 * @return
	 */
	public static Soft search(SoftPolicy softPolicy) {
		return Soft.search(new PdfToPngSoftProvider(softPolicy));
	}

}
