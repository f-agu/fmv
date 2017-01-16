package org.fagu.fmv.utils;

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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.junit.Test;


/**
 * @author f.agu
 */
public class LocaleUtilsTestCase {

	/**
	 * 
	 */
	public LocaleUtilsTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testGetParent_null() {
		assertNull(LocaleUtils.getParent(null));
	}

	/**
	 * 
	 */
	@Test
	public void testGetParent_onlyLanguage() {
		assertNull(LocaleUtils.getParent(Locale.KOREAN));
	}

	/**
	 * 
	 */
	@Test
	public void testGetParent_LanguageCountry() {
		assertEquals(Locale.KOREAN, LocaleUtils.getParent(Locale.KOREA));
	}

	/**
	 * 
	 */
	@Test
	public void testGetParent_LanguageCountryVariant() {
		assertEquals(Locale.KOREA, LocaleUtils.getParent(new Locale("ko", "KR", "xxx")));
	}

	/**
	 * 
	 */
	@Test
	public void testGetParent_LanguageVariant() {
		assertEquals(Locale.KOREAN, LocaleUtils.getParent(new Locale("ko", "", "xxx")));
	}

}
