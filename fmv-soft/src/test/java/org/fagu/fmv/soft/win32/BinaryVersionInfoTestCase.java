package org.fagu.fmv.soft.win32;

/*-
 * #%L
 * fmv-soft
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

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 24 sept. 2019 09:12:22
 */
class BinaryVersionInfoTestCase {

	@Test
	@Disabled
	void test() {
		if( ! SystemUtils.IS_OS_WINDOWS) {
			return;
		}
		File exeFile = new File(System.getenv("windir"), "regedit.exe");
		BinaryVersionInfo.getInfo(exeFile)
				.ifPresent(m -> m.entrySet().forEach(System.out::println));
	}

}
