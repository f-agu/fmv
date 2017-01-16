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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class VolumeDetectedTestCase {

	/**
	 *
	 */
	public VolumeDetectedTestCase() {}

	/**
	 *
	 */
	@Test
	public void testToString() {
		SortedMap<Integer, Long> histogram = new TreeMap<>();
		histogram.put(0, 1L);
		histogram.put(2, 3L);
		VolumeDetected volumeDetected = new VolumeDetected(4, 2, 3, histogram);
		assertEquals("count:4,mean:2.0,max:3.0,histogram:[0=1;2=3]", volumeDetected.toString());
	}

	/**
	 *
	 */
	@Test
	public void testParse() {
		VolumeDetected volumeDetected = VolumeDetected.parse("count:4,mean:2.0,max:3.0,histogram:[0=1;2=3]");
		assertEquals(4, volumeDetected.countSample());
		assertEquals(2, volumeDetected.getMean(), 0.00001D);
		assertEquals(3, volumeDetected.getMax(), 0.00001D);

		SortedMap<Integer, Long> histogram = new TreeMap<>();
		histogram.put(0, 1L);
		histogram.put(2, 3L);
		assertEquals(histogram, volumeDetected.getHistogram());
	}

	/**
	 *
	 */
	@Test
	@Ignore
	public void testx() {
		List<String> v = new ArrayList<>();
		v.add("count:3872768,mean:-39.1,max:-0.0,histogram:[0=156;1=136;2=22;3=22;4=20;5=18;6=16;7=16;8=8;9=14;10=14;11=10;12=14;13=232;14=169;15=398;16=407;17=688;18=440;19=829;20=977]");
		v.add("count:1548288,mean:-38.2,max:-4.4,histogram:[4=2;5=10;6=8;7=14;8=56;9=82;10=107;11=87;12=83;13=444;14=393;15=357]");
		v.add("count:3883008,mean:-44.4,max:-7.4,histogram:[7=20;8=94;9=118;10=100;11=92;12=102;13=112;14=126;15=222;16=186;17=202;18=888;19=655;20=701;21=645]");
		v.add("count:1345536,mean:-51.4,max:-27.5,histogram:[27=6;28=2;29=24;30=46;31=36;32=709;33=954]");
		v.add("count:1884160,mean:-53.0,max:-9.1,histogram:[9=8;10=3;11=1;12=4;13=0;14=10;15=5;16=9;17=20;18=12;19=22;20=28;21=44;22=50;23=36;24=37;25=87;26=149;27=145;28=164;29=161;30=165;31=332;32=620]");
		v.add("count:2037760,mean:-58.2,max:-25.4,histogram:[25=10;26=6;27=4;28=10;29=4;30=14;31=8;32=135;33=101;34=273;35=130;36=112;37=98;38=198;39=174;40=462;41=1448]");
		v.add("count:1527808,mean:-67.8,max:-41.1,histogram:[41=19;42=13;43=29;44=66;45=95;46=170;47=331;48=676;49=786]");
		v.add("count:1316864,mean:-50.3,max:-26.8,histogram:[26=16;27=106;28=68;29=130;30=424;31=907]");
		v.add("count:3489792,mean:-32.4,max:0.0,histogram:[0=541;1=189;2=102;3=148;4=136;5=134;6=192;7=561;8=725;9=619;10=744]");
		v.add("count:5449728,mean:-42.3,max:-0.0,histogram:[0=41;1=52;2=60;3=15;4=14;5=8;6=12;7=16;8=18;9=9;10=15;11=11;12=34;13=97;14=182;15=348;16=298;17=779;18=1033;19=774;20=1351;21=1712]");
		v.add("count:1865728,mean:-31.8,max:0.0,histogram:[0=276;1=34;2=20;3=18;4=49;5=49;6=48;7=50;8=67;9=303;10=180;11=185;12=268;13=384]");
		v.add("count:2201600,mean:-53.6,max:-24.8,histogram:[24=20;25=114;26=230;27=278;28=472;29=640;30=574]");
		v.add("count:2365440,mean:-41.3,max:-16.3,histogram:[16=226;17=222;18=338;19=1115;20=1260]");
		v.add("count:2740224,mean:-37.9,max:-0.2,histogram:[0=6;1=10;2=16;3=14;4=14;5=40;6=28;7=29;8=41;9=31;10=42;11=50;12=120;13=94;14=160;15=431;16=316;17=418;18=817;19=1192]");
		v.add("count:3833856,mean:-46.0,max:0.0,histogram:[0=10;1=18;2=20;3=20;4=10;5=14;6=16;7=14;8=20;9=10;10=11;11=21;12=6;13=10;14=10;15=12;16=18;17=68;18=88;19=61;20=102;21=150;22=199;23=242;24=340;25=387;26=418;27=531;28=586;29=679]");
		v.add("count:1384448,mean:-59.0,max:-29.2,histogram:[29=10;30=56;31=78;32=155;33=167;34=182;35=291;36=564]");
		v.add("count:1806336,mean:-52.0,max:-15.7,histogram:[15=2;16=4;17=4;18=4;19=12;20=8;21=14;22=18;23=36;24=46;25=300;26=206;27=338;28=682;29=712]");
		v.add("count:2037760,mean:-47.6,max:-14.9,histogram:[14=2;15=14;16=14;17=70;18=126;19=102;20=84;21=136;22=232;23=202;24=236;25=252;26=361;27=646]");
		v.add("count:2152448,mean:-56.9,max:-21.1,histogram:[21=4;22=4;23=6;24=18;25=26;26=34;27=46;28=53;29=61;30=88;31=120;32=228;33=359;34=572;35=734]");
		v.add("count:5257216,mean:-62.5,max:-36.6,histogram:[36=59;37=73;38=113;39=326;40=451;41=420;42=805;43=748;44=582;45=1188;46=1991]");
		v.add("count:2480128,mean:-29.5,max:0.0,histogram:[0=363;1=131;2=242;3=395;4=558;5=484;6=652]");
		v.add("count:952320,mean:-39.1,max:-2.6,histogram:[2=8;3=19;4=29;5=44;6=53;7=52;8=62;9=49;10=50;11=46;12=55;13=35;14=41;15=33;16=135;17=215;18=295]");
		v.add("count:1566720,mean:-40.9,max:-12.8,histogram:[12=41;13=96;14=159;15=477;16=447;17=804]");
		v.add("count:2209792,mean:-31.0,max:-0.0,histogram:[0=80;1=46;2=41;3=31;4=110;5=131;6=106;7=130;8=113;9=160;10=386;11=827;12=1144]");
		v.add("count:4018176,mean:-30.0,max:0.0,histogram:[0=1092;1=298;2=385;3=442;4=482;5=563;6=475;7=528]");
		v.add("count:1576960,mean:-36.7,max:-3.8,histogram:[3=5;4=5;5=21;6=23;7=24;8=70;9=78;10=62;11=57;12=114;13=254;14=266;15=291;16=856]");
		v.add("count:9283584,mean:-36.5,max:0.0,histogram:[0=643;1=114;2=150;3=122;4=169;5=134;6=159;7=201;8=272;9=459;10=910;11=1139;12=998;13=1077;14=1556;15=2073]");
		v.add("count:7698432,mean:-36.3,max:0.0,histogram:[0=482;1=140;2=93;3=91;4=89;5=151;6=187;7=234;8=348;9=344;10=421;11=725;12=976;13=1208;14=1553;15=2111]");
		v.add("count:3729408,mean:-42.7,max:-12.7,histogram:[12=62;13=264;14=107;15=105;16=92;17=677;18=1112;19=1198;20=1330]");
		v.add("count:999424,mean:-44.5,max:-4.8,histogram:[4=4;5=0;6=6;7=4;8=16;9=12;10=20;11=28;12=44;13=58;14=56;15=62;16=82;17=94;18=96;19=100;20=111;21=99;22=126]");
		v.add("count:3008512,mean:-45.2,max:-7.8,histogram:[7=40;8=108;9=103;10=116;11=68;12=102;13=54;14=53;15=67;16=63;17=92;18=52;19=66;20=76;21=81;22=462;23=368;24=434;25=504;26=687]");
		v.add("count:2537472,mean:-33.7,max:0.0,histogram:[0=87;1=51;2=62;3=82;4=138;5=147;6=255;7=302;8=372;9=444;10=571;11=760]");
		v.add("count:2220032,mean:-39.3,max:-3.2,histogram:[3=22;4=31;5=59;6=54;7=124;8=118;9=172;10=183;11=224;12=291;13=279;14=351;15=397]");

		for(String vstr : v) {
			VolumeDetected volumeDetected = VolumeDetected.parse(vstr);
			System.out.println(vstr);
			long countTotal = volumeDetected.countSample();
			for(Map.Entry<Integer, Long> entry : volumeDetected.getHistogram().entrySet()) {
				long count = entry.getValue();
				System.out.println(entry.getKey() + "dB: " + count + "  (" + count / countTotal + ")");
			}
		}
	}

}
