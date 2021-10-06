package org.fagu.fmv.utils;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 fagu
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

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
@Disabled
class ClassResolverTestCase {

	private ClassResolver resourceResolver;

	@BeforeEach
	void setUp() {
		resourceResolver = new ClassResolver();
	}

	@Test
	void testJar() throws IOException {
		for(Class<?> cls : resourceResolver.find("org.apache.commons.lang3", cls -> {
			return Exception.class.isAssignableFrom(cls);
		})) {
			System.out.println(cls);
		}
	}

}
