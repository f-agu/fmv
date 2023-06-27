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

import java.util.Collection;
import java.util.Locale;


/**
 * @author f.agu
 */
public class LocaleUtils {

	private LocaleUtils() {}

	public static Locale near(final Locale locale, final Collection<Locale> inLocales) {
		return near(locale, inLocales, null);
	}

	public static Locale near(final Locale locale, final Collection<Locale> inLocales, Locale defaultLocale) {
		if(locale == null || inLocales == null || inLocales.isEmpty()) {
			return defaultLocale;
		}
		// near by parent
		Locale lplcl = locale;
		while(lplcl != null) {
			if(inLocales.contains(lplcl)) {
				return lplcl;
			}
			lplcl = getParent(lplcl);
		}
		// near in child (the first in search)
		lplcl = locale;
		while(lplcl != null) {
			String code = lplcl.toString();
			for(Locale localeInCol : inLocales) {
				String cs = localeInCol.toString();
				if(cs != null && cs.startsWith(code)) {
					return localeInCol;
				}
			}
			lplcl = getParent(lplcl);
		}
		// not found
		return defaultLocale;
	}

	public static Locale getParent(Locale locale) {
		if(locale == null) {
			return null;
		}
		String language = nullIfEmpty(locale.getLanguage());
		String country = nullIfEmpty(locale.getCountry());
		String variant = nullIfEmpty(locale.getVariant());
		if(language == null) {
			return null;
		}
		if(country != null ^ variant != null) {
			return new Locale(language);
		}
		if(country != null && variant != null) {
			return new Locale(language, country);
		}
		return null;
	}

	// **************************************************************************

	private static String nullIfEmpty(String s) {
		return s == null || "".equals(s) ? null : s;
	}

}
