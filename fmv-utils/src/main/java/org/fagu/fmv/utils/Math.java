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

/**
 * @author f.agu
 */
public class Math {

	private Math() {}

	public static long greatestCommonDivisor(long a, long b) {
		if(a <= 0) {
			throw new IllegalArgumentException("a is zero or negative");
		}
		if(b <= 0) {
			throw new IllegalArgumentException("b is zero or negative");
		}
		long r = 0;
		if(a < b) {
			long temp = a;
			a = b;
			b = temp;
		}
		return (r = a % b) == 0 ? b : greatestCommonDivisor(b, r);
	}
}
