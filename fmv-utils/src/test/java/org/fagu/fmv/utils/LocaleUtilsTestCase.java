package org.fagu.fmv.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.util.Locale;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class LocaleUtilsTestCase {

	@Test
	void testGetParent_null() {
		assertNull(LocaleUtils.getParent(null));
	}

	@Test
	void testGetParent_onlyLanguage() {
		assertNull(LocaleUtils.getParent(Locale.KOREAN));
	}

	@Test
	void testGetParent_LanguageCountry() {
		assertEquals(Locale.KOREAN, LocaleUtils.getParent(Locale.KOREA));
	}

	@Test
	void testGetParent_LanguageCountryVariant() {
		assertEquals(Locale.KOREA, LocaleUtils.getParent(new Locale("ko", "KR", "xxx")));
	}

	@Test
	void testGetParent_LanguageVariant() {
		assertEquals(Locale.KOREAN, LocaleUtils.getParent(new Locale("ko", "", "xxx")));
	}

}
