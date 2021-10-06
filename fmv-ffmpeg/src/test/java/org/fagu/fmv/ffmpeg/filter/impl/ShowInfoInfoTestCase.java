package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
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

import org.fagu.fmv.ffmpeg.filter.impl.ShowInfo.Info;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ShowInfoInfoTestCase {

	@Test
	void test1() {
		Info.parse(
				"n:59 pts:60059 pts_time:2.00197 pos:644184 fmt:yuv420p sar:1/1 s:1280x720 i:P iskey:0 type:B checksum:87FC55E8 plane_checksum:[23C81CB9 EF6EBC1E 108E2375]");
	}

	@Test
	void test2() {
		Info.parse(
				"n:   0 pts:   1000 pts_time:0.0333333 pos:       48 fmt:yuv420p sar:1/1 s:1280x720 i:P iskey:1 type:I checksum:22541A59 plane_checksum:[7B3D5B3F B7E59A62 062C24A9] mean:[109 123 133 ] stdev:[42.5 5.6 6.3 ]");
	}

}
