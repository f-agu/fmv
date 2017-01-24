package org.fagu.fmv.utils;

/*-
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
/**
 * @author f.agu
 */
public class StringUtils {

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	/**
	 * 
	 */
	private StringUtils() {}

	/**
	 * @param bytes
	 * @return
	 */
	public static String toHex(byte[] bytes) {
		final int count = bytes.length;
		final char[] hexChars = new char[count * 2];
		int j = 0;
		for(int i = 0; i < count; i++, j += 2) {
			final int v = bytes[i] & 0xFF;
			hexChars[j] = HEX_ARRAY[v >>> 4];
			hexChars[j + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

}
