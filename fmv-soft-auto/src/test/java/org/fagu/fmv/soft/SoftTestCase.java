package org.fagu.fmv.soft;

import org.fagu.fmv.soft.find.SoftFindListener;
import org.fagu.fmv.soft.find.SoftLocator;
import org.junit.Ignore;

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

import org.junit.Test;


/**
 * @author f.agu
 */
public class SoftTestCase {

	/**
	 * 
	 */
	public SoftTestCase() {}

	@Test
	@Ignore
	public void testFindAll() throws Exception {
		Soft.searchAll(ss -> ss.withListener(new SoftFindListener() {

			@Override
			public void eventFound(SoftLocator softLocator, Soft soft) {
				soft.getFounds().forEach(System.out::println);
			}
		})).forEach(s -> System.out.println("==>  " + s.getName() + ": " + s));
	}

}
