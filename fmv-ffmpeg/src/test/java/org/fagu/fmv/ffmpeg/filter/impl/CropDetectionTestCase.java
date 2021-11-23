package org.fagu.fmv.ffmpeg.filter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.util.List;

import org.fagu.fmv.ffmpeg.filter.impl.CropDetection.CropSize;
import org.fagu.fmv.ffmpeg.operation.LibLogReadLine;
import org.fagu.fmv.utils.time.Duration;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 14 nov. 2016 16:32:21
 */
class CropDetectionTestCase {

	public void test1() {
		CropDetect cropDetect = new CropDetect();
		LibLogReadLine r = new LibLogReadLine();
		r.add(cropDetect.getLibLogFilter(), cropDetect);

		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:251 t:0.251000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:293 t:0.293000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:334 t:0.334000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:376 t:0.376000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:418 t:0.418000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:460 t:0.460000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:501 t:0.501000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:543 t:0.543000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:585 t:0.585000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:626 t:0.626000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:668 t:0.668000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:710 t:0.710000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:752 t:0.752000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:793 t:0.793000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:835 t:0.835000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:877 t:0.877000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:918 t:0.918000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:960 t:0.960000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1002 t:1.002000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1044 t:1.044000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1085 t:1.085000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1127 t:1.127000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1169 t:1.169000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1210 t:1.210000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1252 t:1.252000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1294 t:1.294000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1335 t:1.335000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1377 t:1.377000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1419 t:1.419000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1461 t:1.461000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1502 t:1.502000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1544 t:1.544000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1586 t:1.586000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1627 t:1.627000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1669 t:1.669000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1711 t:1.711000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1753 t:1.753000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1794 t:1.794000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1836 t:1.836000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1878 t:1.878000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1919 t:1.919000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:1961 t:1.961000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2003 t:2.003000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2045 t:2.045000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2086 t:2.086000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2128 t:2.128000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2170 t:2.170000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2211 t:2.211000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2253 t:2.253000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2295 t:2.295000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2336 t:2.336000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2378 t:2.378000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2420 t:2.420000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2462 t:2.462000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2503 t:2.503000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2545 t:2.545000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2587 t:2.587000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2628 t:2.628000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2670 t:2.670000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2712 t:2.712000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2754 t:2.754000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2795 t:2.795000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2837 t:2.837000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2879 t:2.879000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2920 t:2.920000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:2962 t:2.962000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3004 t:3.004000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3046 t:3.046000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3087 t:3.087000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3129 t:3.129000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3171 t:3.171000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3212 t:3.212000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3254 t:3.254000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3296 t:3.296000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3337 t:3.337000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:799 y2:0 w:-1904 h:-784 x:1914 y:794 pts:3379 t:3.379000 crop=-1904:-784:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:795 y2:799 w:-1904 h:0 x:1914 y:798 pts:3421 t:3.421000 crop=-1904:0:1914:798");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:785 y2:799 w:-1904 h:0 x:1914 y:794 pts:3463 t:3.463000 crop=-1904:0:1914:794");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:775 y2:799 w:-1904 h:16 x:1914 y:780 pts:3504 t:3.504000 crop=-1904:16:1914:780");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:766 y2:799 w:-1904 h:32 x:1914 y:768 pts:3546 t:3.546000 crop=-1904:32:1914:768");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:757 y2:799 w:-1904 h:32 x:1914 y:764 pts:3588 t:3.588000 crop=-1904:32:1914:764");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:747 y2:799 w:-1904 h:48 x:1914 y:750 pts:3629 t:3.629000 crop=-1904:48:1914:750");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:738 y2:799 w:-1904 h:48 x:1914 y:746 pts:3671 t:3.671000 crop=-1904:48:1914:746");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:729 y2:799 w:-1904 h:64 x:1914 y:734 pts:3713 t:3.713000 crop=-1904:64:1914:734");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:719 y2:799 w:-1904 h:80 x:1914 y:720 pts:3755 t:3.755000 crop=-1904:80:1914:720");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:711 y2:799 w:-1904 h:80 x:1914 y:716 pts:3796 t:3.796000 crop=-1904:80:1914:716");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:699 y2:799 w:-1904 h:96 x:1914 y:702 pts:3838 t:3.838000 crop=-1904:96:1914:702");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:690 y2:799 w:-1904 h:96 x:1914 y:698 pts:3880 t:3.880000 crop=-1904:96:1914:698");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:681 y2:799 w:-1904 h:112 x:1914 y:686 pts:3921 t:3.921000 crop=-1904:112:1914:686");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:674 y2:799 w:-1904 h:112 x:1914 y:682 pts:3963 t:3.963000 crop=-1904:112:1914:682");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:664 y2:799 w:-1904 h:128 x:1914 y:668 pts:4005 t:4.005000 crop=-1904:128:1914:668");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:655 y2:799 w:-1904 h:144 x:1914 y:656 pts:4047 t:4.047000 crop=-1904:144:1914:656");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:647 y2:799 w:-1904 h:144 x:1914 y:652 pts:4088 t:4.088000 crop=-1904:144:1914:652");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:638 y2:799 w:-1904 h:160 x:1914 y:640 pts:4130 t:4.130000 crop=-1904:160:1914:640");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:631 y2:799 w:-1904 h:160 x:1914 y:636 pts:4172 t:4.172000 crop=-1904:160:1914:636");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:623 y2:799 w:-1904 h:176 x:1914 y:624 pts:4213 t:4.213000 crop=-1904:176:1914:624");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:616 y2:799 w:-1904 h:176 x:1914 y:620 pts:4255 t:4.255000 crop=-1904:176:1914:620");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:608 y2:799 w:-1904 h:192 x:1914 y:608 pts:4297 t:4.297000 crop=-1904:192:1914:608");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:601 y2:799 w:-1904 h:192 x:1914 y:606 pts:4338 t:4.338000 crop=-1904:192:1914:606");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1919 x2:0 y1:594 y2:799 w:-1904 h:192 x:1914 y:602 pts:4380 t:4.380000 crop=-1904:192:1914:602");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1851 x2:1856 y1:588 y2:799 w:0 h:208 x:1854 y:590 pts:4422 t:4.422000 crop=0:208:1854:590");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1845 x2:1858 y1:581 y2:799 w:0 h:208 x:1852 y:588 pts:4464 t:4.464000 crop=0:208:1852:588");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1841 x2:1860 y1:575 y2:799 w:16 h:224 x:1844 y:576 pts:4505 t:4.505000 crop=16:224:1844:576");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1838 x2:1860 y1:569 y2:799 w:16 h:224 x:1842 y:574 pts:4547 t:4.547000 crop=16:224:1842:574");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1832 x2:1862 y1:563 y2:799 w:16 h:224 x:1840 y:570 pts:4589 t:4.589000 crop=16:224:1840:570");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1829 x2:1919 y1:558 y2:799 w:80 h:240 x:1836 y:560 pts:4630 t:4.630000 crop=80:240:1836:560");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1823 x2:1919 y1:553 y2:799 w:96 h:240 x:1824 y:558 pts:4672 t:4.672000 crop=96:240:1824:558");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1677 x2:1919 y1:548 y2:799 w:240 h:240 x:1680 y:554 pts:4714 t:4.714000 crop=240:240:1680:554");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1668 x2:1919 y1:543 y2:799 w:240 h:256 x:1674 y:544 pts:4756 t:4.756000 crop=240:256:1674:544");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1651 x2:1919 y1:538 y2:799 w:256 h:256 x:1658 y:542 pts:4797 t:4.797000 crop=256:256:1658:542");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1646 x2:1919 y1:534 y2:799 w:272 h:256 x:1648 y:540 pts:4839 t:4.839000 crop=272:256:1648:540");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1640 x2:1919 y1:529 y2:799 w:272 h:256 x:1644 y:538 pts:4881 t:4.881000 crop=272:256:1644:538");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1632 x2:1919 y1:524 y2:799 w:288 h:272 x:1632 y:526 pts:4922 t:4.922000 crop=288:272:1632:526");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:1628 x2:1919 y1:520 y2:799 w:288 h:272 x:1630 y:524 pts:4964 t:4.964000 crop=288:272:1630:524");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:428 x2:1919 y1:515 y2:799 w:1488 h:272 x:430 y:522 pts:5006 t:5.006000 crop=1488:272:430:522");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:425 x2:1919 y1:511 y2:799 w:1488 h:288 x:430 y:512 pts:5048 t:5.048000 crop=1488:288:430:512");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:423 x2:1919 y1:506 y2:799 w:1488 h:288 x:428 y:510 pts:5089 t:5.089000 crop=1488:288:428:510");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:420 x2:1919 y1:502 y2:799 w:1488 h:288 x:426 y:508 pts:5131 t:5.131000 crop=1488:288:426:508");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:417 x2:1919 y1:498 y2:799 w:1488 h:288 x:426 y:506 pts:5173 t:5.173000 crop=1488:288:426:506");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:415 x2:1919 y1:494 y2:799 w:1504 h:304 x:416 y:496 pts:5214 t:5.214000 crop=1504:304:416:496");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:412 x2:1919 y1:490 y2:799 w:1504 h:304 x:414 y:494 pts:5256 t:5.256000 crop=1504:304:414:494");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:409 x2:1919 y1:486 y2:799 w:1504 h:304 x:414 y:492 pts:5298 t:5.298000 crop=1504:304:414:492");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:406 x2:1919 y1:483 y2:799 w:1504 h:304 x:412 y:490 pts:5339 t:5.339000 crop=1504:304:412:490");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:404 x2:1919 y1:479 y2:799 w:1504 h:320 x:410 y:480 pts:5381 t:5.381000 crop=1504:320:410:480");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:193 x2:1919 y1:476 y2:799 w:1712 h:320 x:202 y:478 pts:5423 t:5.423000 crop=1712:320:202:478");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:184 x2:1919 y1:472 y2:799 w:1728 h:320 x:188 y:476 pts:5465 t:5.465000 crop=1728:320:188:476");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:178 x2:1919 y1:468 y2:799 w:1728 h:320 x:186 y:474 pts:5506 t:5.506000 crop=1728:320:186:474");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:168 x2:1919 y1:465 y2:799 w:1744 h:320 x:172 y:474 pts:5548 t:5.548000 crop=1744:320:172:474");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:164 x2:1919 y1:462 y2:799 w:1744 h:336 x:170 y:464 pts:5590 t:5.590000 crop=1744:336:170:464");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:159 x2:1919 y1:458 y2:799 w:1760 h:336 x:160 y:462 pts:5631 t:5.631000 crop=1760:336:160:462");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:150 x2:1919 y1:455 y2:799 w:1760 h:336 x:156 y:460 pts:5673 t:5.673000 crop=1760:336:156:460");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:92 x2:1919 y1:452 y2:799 w:1824 h:336 x:94 y:458 pts:5715 t:5.715000 crop=1824:336:94:458");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:83 x2:1919 y1:450 y2:799 w:1824 h:336 x:90 y:458 pts:5757 t:5.757000 crop=1824:336:90:458");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:72 x2:1919 y1:447 y2:799 w:1840 h:352 x:76 y:448 pts:5798 t:5.798000 crop=1840:352:76:448");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:444 y2:799 w:1920 h:352 x:0 y:446 pts:5840 t:5.840000 crop=1920:352:0:446");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:441 y2:799 w:1920 h:352 x:0 y:446 pts:5882 t:5.882000 crop=1920:352:0:446");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:439 y2:799 w:1920 h:352 x:0 y:444 pts:5923 t:5.923000 crop=1920:352:0:444");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:436 y2:799 w:1920 h:352 x:0 y:442 pts:5965 t:5.965000 crop=1920:352:0:442");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:433 y2:799 w:1920 h:352 x:0 y:442 pts:6007 t:6.007000 crop=1920:352:0:442");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:430 y2:799 w:1920 h:368 x:0 y:432 pts:6049 t:6.049000 crop=1920:368:0:432");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:428 y2:799 w:1920 h:368 x:0 y:430 pts:6090 t:6.090000 crop=1920:368:0:430");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:425 y2:799 w:1920 h:368 x:0 y:430 pts:6132 t:6.132000 crop=1920:368:0:430");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:422 y2:799 w:1920 h:368 x:0 y:428 pts:6174 t:6.174000 crop=1920:368:0:428");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:418 y2:799 w:1920 h:368 x:0 y:426 pts:6215 t:6.215000 crop=1920:368:0:426");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:415 y2:799 w:1920 h:384 x:0 y:416 pts:6257 t:6.257000 crop=1920:384:0:416");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:411 y2:799 w:1920 h:384 x:0 y:414 pts:6299 t:6.299000 crop=1920:384:0:414");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:410 y2:799 w:1920 h:384 x:0 y:414 pts:6340 t:6.340000 crop=1920:384:0:414");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:409 y2:799 w:1920 h:384 x:0 y:414 pts:6382 t:6.382000 crop=1920:384:0:414");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:408 y2:799 w:1920 h:384 x:0 y:412 pts:6424 t:6.424000 crop=1920:384:0:412");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:408 y2:799 w:1920 h:384 x:0 y:412 pts:6466 t:6.466000 crop=1920:384:0:412");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:407 y2:799 w:1920 h:384 x:0 y:412 pts:6507 t:6.507000 crop=1920:384:0:412");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:251 y2:799 w:1920 h:544 x:0 y:254 pts:6549 t:6.549000 crop=1920:544:0:254");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:250 y2:799 w:1920 h:544 x:0 y:254 pts:6591 t:6.591000 crop=1920:544:0:254");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:250 y2:799 w:1920 h:544 x:0 y:254 pts:6632 t:6.632000 crop=1920:544:0:254");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6674 t:6.674000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6716 t:6.716000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6758 t:6.758000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6799 t:6.799000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6841 t:6.841000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6883 t:6.883000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6924 t:6.924000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:6966 t:6.966000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7008 t:7.008000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7050 t:7.050000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7091 t:7.091000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7133 t:7.133000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7175 t:7.175000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7216 t:7.216000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7258 t:7.258000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7300 t:7.300000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7341 t:7.341000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7383 t:7.383000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7425 t:7.425000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7467 t:7.467000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7508 t:7.508000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7550 t:7.550000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7592 t:7.592000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7633 t:7.633000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7675 t:7.675000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7717 t:7.717000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7759 t:7.759000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7800 t:7.800000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7842 t:7.842000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7884 t:7.884000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7925 t:7.925000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:7967 t:7.967000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8009 t:8.009000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8051 t:8.051000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8092 t:8.092000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8134 t:8.134000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8176 t:8.176000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8217 t:8.217000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8259 t:8.259000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8301 t:8.301000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8342 t:8.342000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8384 t:8.384000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:246 y2:799 w:1920 h:544 x:0 y:252 pts:8426 t:8.426000 crop=1920:544:0:252");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:236 y2:799 w:1920 h:560 x:0 y:238 pts:8468 t:8.468000 crop=1920:560:0:238");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:180 y2:799 w:1920 h:608 x:0 y:186 pts:8509 t:8.509000 crop=1920:608:0:186");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:155 y2:799 w:1920 h:640 x:0 y:158 pts:8551 t:8.551000 crop=1920:640:0:158");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:129 y2:799 w:1920 h:656 x:0 y:138 pts:8593 t:8.593000 crop=1920:656:0:138");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:117 y2:799 w:1920 h:672 x:0 y:124 pts:8634 t:8.634000 crop=1920:672:0:124");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:106 y2:799 w:1920 h:688 x:0 y:110 pts:8676 t:8.676000 crop=1920:688:0:110");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:98 y2:799 w:1920 h:688 x:0 y:106 pts:8718 t:8.718000 crop=1920:688:0:106");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:87 y2:799 w:1920 h:704 x:0 y:92 pts:8760 t:8.760000 crop=1920:704:0:92");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:8801 t:8.801000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:8843 t:8.843000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:8885 t:8.885000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:8926 t:8.926000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:8968 t:8.968000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9010 t:9.010000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9052 t:9.052000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9093 t:9.093000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9135 t:9.135000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9177 t:9.177000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9218 t:9.218000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9260 t:9.260000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9302 t:9.302000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9343 t:9.343000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9385 t:9.385000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9427 t:9.427000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9469 t:9.469000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9510 t:9.510000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9552 t:9.552000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9594 t:9.594000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9635 t:9.635000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9677 t:9.677000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9719 t:9.719000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9761 t:9.761000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9802 t:9.802000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9844 t:9.844000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9886 t:9.886000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9927 t:9.927000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:9969 t:9.969000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10011 t:10.011000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10053 t:10.053000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10094 t:10.094000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10136 t:10.136000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10178 t:10.178000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10219 t:10.219000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10261 t:10.261000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10303 t:10.303000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10344 t:10.344000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10386 t:10.386000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10428 t:10.428000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10470 t:10.470000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10511 t:10.511000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10553 t:10.553000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10595 t:10.595000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10636 t:10.636000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10678 t:10.678000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10720 t:10.720000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10762 t:10.762000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10803 t:10.803000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10845 t:10.845000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10887 t:10.887000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10928 t:10.928000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:10970 t:10.970000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11012 t:11.012000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11054 t:11.054000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11095 t:11.095000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11137 t:11.137000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11179 t:11.179000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11220 t:11.220000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11262 t:11.262000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11304 t:11.304000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11345 t:11.345000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11387 t:11.387000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11429 t:11.429000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11471 t:11.471000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11512 t:11.512000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11554 t:11.554000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11596 t:11.596000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11637 t:11.637000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11679 t:11.679000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11721 t:11.721000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11763 t:11.763000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11804 t:11.804000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11846 t:11.846000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11888 t:11.888000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11929 t:11.929000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:11971 t:11.971000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12013 t:12.013000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12055 t:12.055000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12096 t:12.096000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12138 t:12.138000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12180 t:12.180000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12221 t:12.221000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12263 t:12.263000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12305 t:12.305000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12346 t:12.346000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12388 t:12.388000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12430 t:12.430000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12472 t:12.472000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12513 t:12.513000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12555 t:12.555000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12597 t:12.597000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12638 t:12.638000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12680 t:12.680000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12722 t:12.722000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12764 t:12.764000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12805 t:12.805000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12847 t:12.847000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12889 t:12.889000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12930 t:12.930000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:12972 t:12.972000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13014 t:13.014000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13056 t:13.056000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13097 t:13.097000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13139 t:13.139000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13181 t:13.181000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13222 t:13.222000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13264 t:13.264000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13306 t:13.306000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13347 t:13.347000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13389 t:13.389000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13431 t:13.431000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13473 t:13.473000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13514 t:13.514000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13556 t:13.556000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13598 t:13.598000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13639 t:13.639000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13681 t:13.681000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13723 t:13.723000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13765 t:13.765000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000002220f00] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:13806 t:13.806000 crop=1920:800:0:0");

		CropDetection cropDetection = cropDetect.getCropSizeDetected();
		List<CropSize> cropSizes = cropDetection.getCropSizes();
		assertEquals(62, cropSizes.size());

		// for(CropSize cropSize : cropSizes) {
		// System.out.println(cropSize + " : " + cropSize.toCrop());
		// }
	}

	@Test
	void test2() {
		CropDetect cropDetect = new CropDetect();
		LibLogReadLine r = new LibLogReadLine();
		r.add(cropDetect.getLibLogFilter(), cropDetect);

		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:122 t:0.122000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:164 t:0.164000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:206 t:0.206000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:247 t:0.247000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:289 t:0.289000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:331 t:0.331000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:372 t:0.372000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:414 t:0.414000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:456 t:0.456000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:497 t:0.497000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:539 t:0.539000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:581 t:0.581000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:623 t:0.623000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:664 t:0.664000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:706 t:0.706000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:748 t:0.748000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:789 t:0.789000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:831 t:0.831000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:873 t:0.873000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:915 t:0.915000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:956 t:0.956000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:998 t:0.998000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1040 t:1.040000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1081 t:1.081000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1123 t:1.123000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1165 t:1.165000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1207 t:1.207000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1248 t:1.248000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1290 t:1.290000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1332 t:1.332000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1373 t:1.373000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1415 t:1.415000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1457 t:1.457000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1498 t:1.498000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1540 t:1.540000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1582 t:1.582000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1624 t:1.624000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1665 t:1.665000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1707 t:1.707000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1749 t:1.749000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1790 t:1.790000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1832 t:1.832000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1874 t:1.874000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1916 t:1.916000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1957 t:1.957000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:1999 t:1.999000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2041 t:2.041000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2082 t:2.082000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2124 t:2.124000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2166 t:2.166000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2208 t:2.208000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2249 t:2.249000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2291 t:2.291000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2333 t:2.333000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2374 t:2.374000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2416 t:2.416000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2458 t:2.458000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2499 t:2.499000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2541 t:2.541000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2583 t:2.583000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2625 t:2.625000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2666 t:2.666000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2708 t:2.708000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2750 t:2.750000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2791 t:2.791000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2833 t:2.833000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2875 t:2.875000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2917 t:2.917000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:2958 t:2.958000 crop=1920:800:0:0");
		r.read("[Parsed_cropdetect_0 @ 0000000001da2ae0] x1:0 x2:1919 y1:0 y2:799 w:1920 h:800 x:0 y:0 pts:3000 t:3.000000 crop=1920:800:0:0");

		CropDetection cropDetection = cropDetect.getCropSizeDetected();
		List<CropSize> cropSizes = cropDetection.getCropSizes();

		assertEquals(1, cropSizes.size());
		CropSize cropSize = cropSizes.get(0);
		assertEquals(800, cropSize.getH());
		assertEquals(1920, cropSize.getW());
		assertEquals(0, cropSize.getX());
		assertEquals(0, cropSize.getY());
		assertEquals(0, cropSize.getX1());
		assertEquals(1919, cropSize.getX2());
		assertEquals(0, cropSize.getY1());
		assertEquals(799, cropSize.getY2());
		assertEquals(70, cropSize.getCount());

		assertEquals(Duration.parse("00:00:02.878"), cropSize.duration());
	}

}
