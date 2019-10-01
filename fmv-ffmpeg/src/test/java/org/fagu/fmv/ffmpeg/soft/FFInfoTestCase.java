package org.fagu.fmv.ffmpeg.soft;

/*
 * #%L
 * fmv-ffmpeg
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.version.Version;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class FFInfoTestCase {

	@Test
	@Ignore
	public void testFindFFMpeg() throws Exception {
		Founds founds = FFMpeg.search().getFounds();
		founds.forEach(softFound -> {
			FFInfo ffInfo = (FFInfo)softFound.getSoftInfo();
			System.out.println(softFound);
			System.out.println(softFound.getFile() + "  " + ffInfo);
			System.out.println("    " + softFound.getLocalizedBy());
			System.out.println();
		});
		System.out.println(founds.getFirstFound());
	}

	@Test
	@Ignore
	public void testFindFFProbe() throws Exception {
		Founds founds = FFProbe.search().getFounds();
		founds.forEach(softFound -> {
			FFInfo ffInfo = (FFInfo)softFound.getSoftInfo();
			System.out.println(softFound);
			System.out.println(softFound.getFile() + "  " + ffInfo);
			System.out.println("    " + softFound.getLocalizedBy());
			System.out.println();
		});
		System.out.println(founds.getFirstFound());
	}

	@Test
	public void testGetVersion_OK() throws Exception {
		String line = "1.0";
		assertEquals(new Version(1), FFSoftProvider.getVersion(line));
	}

	@Test
	public void testGetConfiguration_Mark() throws Exception {
		String s = "--prefix=/usr --libdir=/usr/lib64 --shlibdir=/usr/lib64 --mandir=/usr/share/man --incdir=/usr/include --disable-avisynth --extra-cflags='-O2 -g -pipe -Wall -Wp,-D_FORTIFY_SOURCE=2 -fexceptions -fstack-protector --param=ssp-buffer-size=4 -m64 -mtune=generic -fPIC' --enable-avfilter --enable-libdc1394 --enable-libfaac --enable-libgsm --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-librtmp --enable-libschroedinger --enable-libtheora --enable-libx264 --enable-gpl --enable-nonfree --enable-postproc --enable-pthreads --enable-shared --enable-swscale --enable-vdpau --enable-version3 --enable-x11grab";
		Set<String> configuration = FFSoftProvider.getConfiguration(s);
		Iterator<String> it = configuration.iterator();
		assertEquals("--prefix=/usr", it.next());
		assertEquals("--libdir=/usr/lib64", it.next());
		assertEquals("--shlibdir=/usr/lib64", it.next());
		assertEquals("--mandir=/usr/share/man", it.next());
		assertEquals("--incdir=/usr/include", it.next());
		assertEquals("--disable-avisynth", it.next());
		assertEquals(
				"--extra-cflags=-O2 -g -pipe -Wall -Wp,-D_FORTIFY_SOURCE=2 -fexceptions -fstack-protector --param=ssp-buffer-size=4 -m64 -mtune=generic -fPIC",
				it
						.next());
		assertEquals("--enable-avfilter", it.next());
		assertEquals("--enable-libdc1394", it.next());
		assertEquals("--enable-libfaac", it.next());
		assertEquals("--enable-libgsm", it.next());
		assertEquals("--enable-libmp3lame", it.next());
		assertEquals("--enable-libopencore-amrnb", it.next());
		assertEquals("--enable-libopencore-amrwb", it.next());
		assertEquals("--enable-librtmp", it.next());
		assertEquals("--enable-libschroedinger", it.next());
		assertEquals("--enable-libtheora", it.next());
		assertEquals("--enable-libx264", it.next());
		assertEquals("--enable-gpl", it.next());
		assertEquals("--enable-nonfree", it.next());
		assertEquals("--enable-postproc", it.next());
		assertEquals("--enable-pthreads", it.next());
		assertEquals("--enable-shared", it.next());
		assertEquals("--enable-swscale", it.next());
		assertEquals("--enable-vdpau", it.next());
		assertEquals("--enable-version3", it.next());
		assertFalse(it.hasNext());
	}

	@Test
	public void testFFMPEGFull_N70767() throws Exception {
		Parser parser = newParserFFMpeg();

		parser.readLine("ffmpeg version N-70767-gd24af70 Copyright (c) 2000-2015 the FFmpeg developers");
		parser.readLine("built with gcc 4.9.2 (GCC)");
		parser.readLine(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libbs2b --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-lzma --enable-decklink --enable-zlib");
		parser.readLine("libavutil      54. 20.100 / 54. 20.100");
		parser.readLine("libavcodec     56. 28.100 / 56. 28.100");
		parser.readLine("libavformat    56. 25.101 / 56. 25.101");
		parser.readLine("libavdevice    56.  4.100 / 56.  4.100");
		parser.readLine("libavfilter     5. 12.100 /  5. 12.100");
		parser.readLine("libswscale      3.  1.101 /  3.  1.101");
		parser.readLine("libswresample   1.  1.100 /  1.  1.100");
		parser.readLine("libpostproc    53.  3.100 / 53.  3.100");

		assertFull(parser, null, date(2015, 1, 2), 70767);
	}

	@Test
	public void testFFProbeFull_N70767() throws Exception {
		Parser parser = newParserProbe();

		parser.readLine("ffprobe version N-70767-gd24af70 Copyright (c) 2000-2015 the FFmpeg developers");
		parser.readLine("built with gcc 4.9.2 (GCC)");
		parser.readLine(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libbs2b --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-lzma --enable-decklink --enable-zlib");
		parser.readLine("libavutil      54. 20.100 / 54. 20.100");
		parser.readLine("libavcodec     56. 28.100 / 56. 28.100");
		parser.readLine("libavformat    56. 25.101 / 56. 25.101");
		parser.readLine("libavdevice    56.  4.100 / 56.  4.100");
		parser.readLine("libavfilter     5. 12.100 /  5. 12.100");
		parser.readLine("libswscale      3.  1.101 /  3.  1.101");
		parser.readLine("libswresample   1.  1.100 /  1.  1.100");
		parser.readLine("libpostproc    53.  3.100 / 53.  3.100");

		assertFull(parser, null, date(2015, 1, 2), 70767);
	}

	/**
	 * Sur estprod03 en mars 2015
	 *
	 * @throws Exception
	 */
	@Test
	public void testFFMPEGFull_N2210() throws Exception {
		Parser parser = newParserFFMpeg();

		parser.readLine("ffmpeg version n2.2.10-2-g418e9a6");
		parser.readLine("built on Nov 14 2014 14:59:05 with gcc 4.4.7 (GCC) 20120313 (Red Hat 4.4.7-4)");
		parser.readLine(
				"configuration: --prefix=/root/ffmpeg_build --extra-cflags=-I/root/ffmpeg_build/include --extra-ldflags=-L/root/ffmpeg_build/lib --bindir=/root/bin --extra-libs=-ldl --enable-version3 --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libvpx --enable-libfaac --enable-libmp3lame --enable-libtheora --enable-libvorbis --enable-libx264 --enable-libvo-aacenc --enable-libxvid --disable-ffplay --enable-gpl --enable-postproc --enable-nonfree --enable-avfilter --enable-pthreads --arch=x86_64");
		parser.readLine("libavutil      52. 66.100 / 52. 66.100");
		parser.readLine("libavcodec     55. 52.102 / 55. 52.102");
		parser.readLine("libavformat    55. 33.100 / 55. 33.100");
		parser.readLine("libavdevice    55. 10.100 / 55. 10.100");
		parser.readLine("libavfilter     4.  2.100 /  4.  2.100");
		parser.readLine("libswscale      2.  5.102 /  2.  5.102");
		parser.readLine("libswresample   0. 18.100 /  0. 18.100");
		parser.readLine("libpostproc    52.  3.100 / 52.  3.100");

		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, Calendar.NOVEMBER, 14, 14, 59, 05);
		assertFull(parser, new Version(2, 2, 10, 2), calendar.getTime(), null);
	}

	@Test
	public void testFFMPEGFull_N63696() throws Exception {
		Parser parser = newParserFFMpeg();

		parser.readLine("ffmpeg version N-63696-g2c23f87");
		parser.readLine("built on Jun  1 2014 22:09:10 with gcc 4.8.2 (GCC)");
		parser.readLine(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-decklink --enable-zlib");
		parser.readLine("libavutil      52. 89.100 / 52. 89.100");
		parser.readLine("libavcodec     55. 66.100 / 55. 66.100");
		parser.readLine("libavformat    55. 42.100 / 55. 42.100");
		parser.readLine("libavdevice    55. 13.101 / 55. 13.101");
		parser.readLine("libavfilter     4.  5.100 /  4.  5.100");
		parser.readLine("libswscale      2.  6.100 /  2.  6.100");
		parser.readLine("libswresample   0. 19.100 /  0. 19.100");
		parser.readLine("libpostproc    52.  3.100 / 52.  3.100");

		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, Calendar.JUNE, 1, 22, 9, 10);
		assertFull(parser, null, calendar.getTime(), 63696);
	}

	@Test
	public void testFFMPEGFull_N65107() throws Exception {
		Parser parser = newParserFFMpeg();

		parser.readLine("ffmpeg version N-65107-ga507623 Copyright (c) 2000-2014 the FFmpeg developers");
		parser.readLine("built on Jul 29 2014 22:10:17 with gcc 4.8.3 (GCC)");
		parser.readLine(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libbs2b --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-decklink --enable-zlib");
		parser.readLine("libavutil      52. 93.100 / 52. 93.100");
		parser.readLine("libavcodec     55. 71.100 / 55. 71.100");
		parser.readLine("libavformat    55. 49.100 / 55. 49.100");
		parser.readLine("libavdevice    55. 13.102 / 55. 13.102");
		parser.readLine("libavfilter     4. 11.102 /  4. 11.102");
		parser.readLine("libswscale      2.  6.100 /  2.  6.100");
		parser.readLine("libswresample   0. 19.100 /  0. 19.100");
		parser.readLine("libpostproc    52.  3.100 / 52.  3.100");

		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, Calendar.JULY, 29, 22, 10, 17);
		assertFull(parser, null, calendar.getTime(), 65107);
	}

	@Test
	public void testFFMPEGFull_v2_6_1() throws Exception {
		Parser parser = newParserFFMpeg();

		parser.readLine("ffmpeg version 2.6.1 Copyright (c) 2000-2015 the FFmpeg developers");
		parser.readLine("built with gcc 4.8 (Ubuntu 4.8.2-19ubuntu1)");
		parser.readLine(
				"configuration: --extra-libs=-ldl --prefix=/opt/ffmpeg --enable-avresample --disable-debug --enable-nonfree --enable-gpl --enable-version3 --enable-libopencore-amrnb --enable-libopencore-amrwb --disable-decoder=amrnb --disable-decoder=amrwb --enable-libpulse --enable-libx264 --enable-libx265 --enable-libfdk-aac --enable-libvorbis --enable-libmp3lame --enable-libopus --enable-libvpx --enable-libspeex --enable-libass --enable-avisynth --enable-libsoxr --enable-libxvid --enable-libvo-aacenc --enable-libvidstab");
		parser.readLine("libavutil      54. 20.100 / 54. 20.100");
		parser.readLine("libavcodec     56. 26.100 / 56. 26.100");
		parser.readLine("libavformat    56. 25.101 / 56. 25.101");
		parser.readLine("libavdevice    56.  4.100 / 56.  4.100");
		parser.readLine("libavfilter     5. 11.102 /  5. 11.102");
		parser.readLine("libavresample   2.  1.  0 /  2.  1.  0");
		parser.readLine("libswscale      3.  1.101 /  3.  1.101");
		parser.readLine("libswresample   1.  1.100 /  1.  1.100");

		assertFull(parser, new Version(2, 6, 1), date(2015, 3, 7), null);
	}

	@Test
	public void testFFProbeFull_v2_6_1() throws Exception {
		Parser parser = newParserProbe();

		parser.readLine("ffprobe version 2.6.1 Copyright (c) 2007-2015 the FFmpeg developers");
		parser.readLine("built with gcc 4.8 (Ubuntu 4.8.2-19ubuntu1)");
		parser.readLine(
				"configuration: --extra-libs=-ldl --prefix=/opt/ffmpeg --enable-avresample --disable-debug --enable-nonfree --enable-gpl --enable-version3 --enable-libopencore-amrnb --enable-libopencore-amrwb --disable-decoder=amrnb --disable-decoder=amrwb --enable-libpulse --enable-libx264 --enable-libx265 --enable-libfdk-aac --enable-libvorbis --enable-libmp3lame --enable-libopus --enable-libvpx --enable-libspeex --enable-libass --enable-avisynth --enable-libsoxr --enable-libxvid --enable-libvo-aacenc --enable-libvidstab");
		parser.readLine("libavutil      54. 20.100 / 54. 20.100");
		parser.readLine("libavcodec     56. 26.100 / 56. 26.100");
		parser.readLine("libavformat    56. 25.101 / 56. 25.101");
		parser.readLine("libavdevice    56.  4.100 / 56.  4.100");
		parser.readLine("libavfilter     5. 11.102 /  5. 11.102");
		parser.readLine("libavresample   2.  1.  0 /  2.  1.  0");
		parser.readLine("libswscale      3.  1.101 /  3.  1.101");
		parser.readLine("libswresample   1.  1.100 /  1.  1.100");
		parser.readLine("libpostproc    53.  3.100 / 53.  3.100");

		assertFull(parser, new Version(2, 6, 1), date(2015, 3, 7), null);
	}

	// ********************************************************

	private Date date(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, 0, 0, 0);
		return calendar.getTime();
	}

	private Parser newParserProbe() {
		FFProbeSoftProvider softProvider = new FFProbeSoftProvider();
		return softProvider.createParser(new File("."));
	}

	private Parser newParserFFMpeg() {
		FFMpegSoftProvider softProvider = new FFMpegSoftProvider();
		return softProvider.createParser(new File("."));
	}

	private void assertFull(Parser parser, Version version, Date builtDate, Integer builtVersion) throws IOException, ParseException {
		SoftFound softFound = parser.closeAndParse("", 0);
		FFInfo ffInfo = (FFInfo)softFound.getSoftInfo();
		if(version == null) {
			assertFalse(ffInfo.getVersion().isPresent());
		} else {
			assertTrue(Objects.equals(version, ffInfo.getVersion().orElse(null)));
		}

		if(builtVersion == null) {
			assertNull(ffInfo.getBuiltVersion());
		} else {
			assertTrue(Objects.equals(builtVersion, ffInfo.getBuiltVersion()));
		}

		if(builtDate == null) {
			assertNull(ffInfo.getBuiltDate());
		} else {
			assertNotNull(ffInfo.getBuiltDate());
			assertEquals(builtDate.getTime() / 1000, ffInfo.getBuiltDate().getTime() / 1000);
		}
	}

}
