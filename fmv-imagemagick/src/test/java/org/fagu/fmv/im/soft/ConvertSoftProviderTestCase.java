package org.fagu.fmv.im.soft;

import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.LogExecuteDelegate;
/*-
 * #%L
 * fmv-imagemagick
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
	public void testSearch() {
		ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		Convert.search();
	}

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
