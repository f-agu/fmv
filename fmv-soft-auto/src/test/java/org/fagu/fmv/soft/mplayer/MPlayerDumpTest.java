package org.fagu.fmv.soft.mplayer;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.mplayer.MPlayerDump.MPlayerDumpBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled
class MPlayerDumpTest {

	@Test
	void testBuilderLineParse() {
		System.out.println(MPlayerDumpBuilder.parse("audio stream: 0 format: ac3 (5.1) language: en aid: 128."));
	}

	@Test
	void testBuilderLineParse2() {
		Pattern pattern = Pattern.compile("dump: \\d+ bytes written \\(~(\\d+).\\d+%\\)");
		Matcher matcher = pattern.matcher("dump: 35966976 bytes written (~73.8%)");
		if(matcher.matches()) {
			System.out.println(matcher.group(1));
		}
	}

}
