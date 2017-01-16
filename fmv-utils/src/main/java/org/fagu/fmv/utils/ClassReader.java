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
public class ClassReader {

	/**
	 * 
	 */
	private final byte[] b;

	/**
	 * The start index of each constant pool item in {@link #b b}, plus one. The one byte offset skips the constant pool
	 * item tag that indicates its type.
	 */
	private final int[] items;

	/**
	 * The String objects corresponding to the CONSTANT_Utf8 items. This cache avoids multiple parsing of a given
	 * CONSTANT_Utf8 constant pool item, which GREATLY improves performances (by a factor 2 to 3). This caching strategy
	 * could be extended to all constant pool items, but its benefit would not be so great for these items (because they
	 * are much less expensive to parse than CONSTANT_Utf8 items).
	 */
	private final String[] strings;

	/**
	 * Maximum length of the strings contained in the constant pool of the class.
	 */
	private final int maxStringLength;

	/**
	 * Start index of the class header information (access, name...) in {@link #b b}.
	 */
	private final int header;

	/**
	 * @param b
	 */
	public ClassReader(byte[] b) {
		this.b = b;

		int off = 0;

		items = new int[readUnsignedShort(off + 8)];
		int n = items.length;
		strings = new String[n];
		int max = 0;
		int index = off + 10;
		for(int i = 1; i < n; ++i) {
			items[i] = index + 1;
			int size;
			switch(b[index]) {
				case 9: // FIELD
				case 10:// METH
				case 11: // IMETH
				case 3: // INT
				case 4: // FLOAT
				case 12: // NAME_TYPE
				case 18: // INDY
					size = 5;
					break;
				case 5: // LONG
				case 6: // DOUBLE
					size = 9;
					++i;
					break;
				case 1: // UTF8
					size = 3 + readUnsignedShort(index + 1);
					if(size > max) {
						max = size;
					}
					break;
				case 15: // HANDLE
					size = 4;
					break;
				// case : //CLASS:
				// case : //STR:
				// case : //MTYPE
				default:
					size = 3;
					break;
			}
			index += size;
		}
		maxStringLength = max;
		// the class header information starts just after the constant pool
		header = index;
	}

	/**
	 * @return
	 */
	public String getClassName() {
		return readClass(header + 2, new char[maxStringLength]);
	}

	// ****************************************

	/**
	 * @param index
	 * @return
	 */
	private int readUnsignedShort(final int index) {
		return ((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF);
	}

	/**
	 * @param index
	 * @param buf
	 * @return
	 */
	private String readClass(final int index, final char[] buf) {
		// computes the start index of the CONSTANT_Class item in b
		// and reads the CONSTANT_Utf8 item designated by
		// the first two bytes of this CONSTANT_Class item
		return readUTF8(items[readUnsignedShort(index)], buf);
	}

	/**
	 * @param index
	 * @param buf
	 * @return
	 */
	public String readUTF8(int index, final char[] buf) {
		int item = readUnsignedShort(index);
		if(index == 0 || item == 0) {
			return null;
		}
		String s = strings[item];
		if(s != null) {
			return s;
		}
		index = items[item];
		return strings[item] = readUTF(index + 2, readUnsignedShort(index), buf);
	}

	/**
	 * @param index
	 * @param utfLen
	 * @param buf
	 * @return
	 */
	private String readUTF(int index, final int utfLen, final char[] buf) {
		int endIndex = index + utfLen;
		byte[] b = this.b;
		int strLen = 0;
		int c;
		int st = 0;
		char cc = 0;
		while(index < endIndex) {
			c = b[index++];
			switch(st) {
				case 0:
					c = c & 0xFF;
					if(c < 0x80) { // 0xxxxxxx
						buf[strLen++] = (char)c;
					} else if(c < 0xE0 && c > 0xBF) { // 110x xxxx 10xx xxxx
						cc = (char)(c & 0x1F);
						st = 1;
					} else { // 1110 xxxx 10xx xxxx 10xx xxxx
						cc = (char)(c & 0x0F);
						st = 2;
					}
					break;

				case 1: // byte 2 of 2-byte char or byte 3 of 3-byte char
					buf[strLen++] = (char)((cc << 6) | (c & 0x3F));
					st = 0;
					break;

				case 2: // byte 2 of 3-byte char
					cc = (char)((cc << 6) | (c & 0x3F));
					st = 1;
					break;
			}
		}
		return new String(buf, 0, strLen);
	}

}
