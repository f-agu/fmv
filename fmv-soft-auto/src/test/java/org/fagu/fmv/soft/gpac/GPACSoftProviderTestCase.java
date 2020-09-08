package org.fagu.fmv.soft.gpac;

/*-
 * #%L
 * fmv-soft-auto
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

import org.fagu.fmv.soft.Soft;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 19 juin 2019 17:19:23
 */
public class GPACSoftProviderTestCase {

	@Test
	@Ignore
	public void testSearchMP4Box() {
		Soft soft = MP4Box.search();
		System.out.println(soft.isFound());
		System.out.println(soft.getFile());
	}

	@Test
	@Ignore
	public void testSearchMP42TS() {
		Soft soft = MP42TS.search();
		System.out.println(soft.isFound());
		System.out.println(soft.getFile());
	}

}
