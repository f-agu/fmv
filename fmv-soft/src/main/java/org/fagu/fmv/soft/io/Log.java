package org.fagu.fmv.soft.io;

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

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 * @created 8 juil. 2019 16:32:02
 */
class Log {

	private static final int MAX_LENGTH = 50;

	private Log() {}

	static String reference(Object ref) {
		return "#" + Integer.toHexString(ref.hashCode());
	}

	static String data(int data) {
		return data(new byte[] {(byte)data}, 0, 1);
	}

	static String data(byte[] data, int off, int len) {
		int max = Math.min(len, MAX_LENGTH);
		String str = isString(data, off, len) ? new String(data, off, max) : StringUtils.EMPTY;
		StringJoiner joiner = new StringJoiner(" ", "0x", len > MAX_LENGTH ? "... " + str : StringUtils.EMPTY);
		for(int i = off; i < max; ++i) {
			joiner.add(StringUtils.leftPad(Integer.toHexString(data[i] & 0xFF), 2, '0'));
		}
		return joiner.toString();
	}

	private static boolean isString(byte[] data, int off, int len) {
		int b = 0;
		for(int i = off; i < Math.min(len, MAX_LENGTH); ++i) {
			b = data[i] & 0xFF;
			if(b < 31 && (b != '\t' || b != '\r' || b != '\n')) {
				return false;
			}
		}
		return true;
	}

}
