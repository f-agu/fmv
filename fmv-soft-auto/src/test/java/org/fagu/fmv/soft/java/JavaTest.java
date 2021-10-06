package org.fagu.fmv.soft.java;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.fagu.fmv.soft.Soft;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 5 sept. 2019 12:02:47
 */
class JavaTest {

	@Test
	@Disabled
	void testAll() {
		Java.search().getFounds()
				.forEach(sf -> System.out.println(sf.getFile()));
	}

	@Test
	@Disabled
	void testCurrent() {
		System.out.println(Java.current().getFile());
	}

	@Test
	void testInput() throws Exception {
		try (InputStream inputStream = new ByteArrayInputStream(new byte[1_000_000]);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			Soft.withExecFile("ping")
					.withParameters("-n", "1", "localhost")
					.output(byteArrayOutputStream)
					.err(byteArrayOutputStream)
					.execute();
			assertTrue(byteArrayOutputStream.size() > 0);
			System.out.println(new String(byteArrayOutputStream.toByteArray()));
		}
	}

}
