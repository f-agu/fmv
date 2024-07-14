package org.fagu.fmv.ffmpeg.soft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.version.Version;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class FFInfoTestCase {

	@Test
	// @Disabled
	void testFindFFMpeg() throws Exception {
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
	@Disabled
	void testFindFFProbe() throws Exception {
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
	@Disabled
	void testLoop() throws Exception {
		Soft soft = FFProbe.search();
		while(soft.isFound()) {
			SoftFound softFound = soft.getFounds().getFirstFound();
			if(soft.isFound()) {
				// recheck soft
				softFound = soft.reFind();
			}
			System.out.println(softFound);
			Thread.sleep(1_000);
		}
	}

	@Test
	void testGetVersion_OK() throws Exception {
		String line = "1.0";
		assertEquals(new Version(1), FFSoftProvider.getVersion(line));
	}

	@Test
	void testGetConfiguration_Mark() throws Exception {
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
	void testFFMPEGFull_N70767() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version N-70767-gd24af70 Copyright (c) 2000-2015 the FFmpeg developers");
		lines.addOut("built with gcc 4.9.2 (GCC)");
		lines.addOut(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libbs2b --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-lzma --enable-decklink --enable-zlib");
		lines.addOut("libavutil      54. 20.100 / 54. 20.100");
		lines.addOut("libavcodec     56. 28.100 / 56. 28.100");
		lines.addOut("libavformat    56. 25.101 / 56. 25.101");
		lines.addOut("libavdevice    56.  4.100 / 56.  4.100");
		lines.addOut("libavfilter     5. 12.100 /  5. 12.100");
		lines.addOut("libswscale      3.  1.101 /  3.  1.101");
		lines.addOut("libswresample   1.  1.100 /  1.  1.100");
		lines.addOut("libpostproc    53.  3.100 / 53.  3.100");

		assertFull(newParserFFMpeg(lines), lines, null, LocalDate.of(2015, 1, 2), 70767);
	}

	@Test
	void testFFProbeFull_N70767() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffprobe version N-70767-gd24af70 Copyright (c) 2000-2015 the FFmpeg developers");
		lines.addOut("built with gcc 4.9.2 (GCC)");
		lines.addOut(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libbs2b --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-lzma --enable-decklink --enable-zlib");
		lines.addOut("libavutil      54. 20.100 / 54. 20.100");
		lines.addOut("libavcodec     56. 28.100 / 56. 28.100");
		lines.addOut("libavformat    56. 25.101 / 56. 25.101");
		lines.addOut("libavdevice    56.  4.100 / 56.  4.100");
		lines.addOut("libavfilter     5. 12.100 /  5. 12.100");
		lines.addOut("libswscale      3.  1.101 /  3.  1.101");
		lines.addOut("libswresample   1.  1.100 /  1.  1.100");
		lines.addOut("libpostproc    53.  3.100 / 53.  3.100");

		assertFull(newParserProbe(lines), lines, null, LocalDate.of(2015, 1, 2), 70767);
	}

	/**
	 * Sur estprod03 en mars 2015
	 *
	 * @throws Exception
	 */
	@Test
	void testFFMPEGFull_N2210() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version n2.2.10-2-g418e9a6");
		lines.addOut("built on Nov 14 2014 14:59:05 with gcc 4.4.7 (GCC) 20120313 (Red Hat 4.4.7-4)");
		lines.addOut(
				"configuration: --prefix=/root/ffmpeg_build --extra-cflags=-I/root/ffmpeg_build/include --extra-ldflags=-L/root/ffmpeg_build/lib --bindir=/root/bin --extra-libs=-ldl --enable-version3 --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libvpx --enable-libfaac --enable-libmp3lame --enable-libtheora --enable-libvorbis --enable-libx264 --enable-libvo-aacenc --enable-libxvid --disable-ffplay --enable-gpl --enable-postproc --enable-nonfree --enable-avfilter --enable-pthreads --arch=x86_64");
		lines.addOut("libavutil      52. 66.100 / 52. 66.100");
		lines.addOut("libavcodec     55. 52.102 / 55. 52.102");
		lines.addOut("libavformat    55. 33.100 / 55. 33.100");
		lines.addOut("libavdevice    55. 10.100 / 55. 10.100");
		lines.addOut("libavfilter     4.  2.100 /  4.  2.100");
		lines.addOut("libswscale      2.  5.102 /  2.  5.102");
		lines.addOut("libswresample   0. 18.100 /  0. 18.100");
		lines.addOut("libpostproc    52.  3.100 / 52.  3.100");

		assertFull(newParserFFMpeg(lines), lines, new Version(2, 2, 10, 2), LocalDate.of(2014, 11, 14), null);
	}

	@Test
	void testFFMPEGFull_N63696() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version N-63696-g2c23f87");
		lines.addOut("built on Jun  1 2014 22:09:10 with gcc 4.8.2 (GCC)");
		lines.addOut(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-decklink --enable-zlib");
		lines.addOut("libavutil      52. 89.100 / 52. 89.100");
		lines.addOut("libavcodec     55. 66.100 / 55. 66.100");
		lines.addOut("libavformat    55. 42.100 / 55. 42.100");
		lines.addOut("libavdevice    55. 13.101 / 55. 13.101");
		lines.addOut("libavfilter     4.  5.100 /  4.  5.100");
		lines.addOut("libswscale      2.  6.100 /  2.  6.100");
		lines.addOut("libswresample   0. 19.100 /  0. 19.100");
		lines.addOut("libpostproc    52.  3.100 / 52.  3.100");

		assertFull(newParserFFMpeg(lines), lines, null, LocalDate.of(2014, 6, 1), 63696);
	}

	@Test
	void testFFMPEGFull_N65107() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version N-65107-ga507623 Copyright (c) 2000-2014 the FFmpeg developers");
		lines.addOut("built on Jul 29 2014 22:10:17 with gcc 4.8.3 (GCC)");
		lines.addOut(
				"configuration: --enable-gpl --enable-version3 --disable-w32threads --enable-avisynth --enable-bzlib --enable-fontconfig --enable-frei0r --enable-gnutls --enable-iconv --enable-libass --enable-libbluray --enable-libbs2b --enable-libcaca --enable-libfreetype --enable-libgme --enable-libgsm --enable-libilbc --enable-libmodplug --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-librtmp --enable-libschroedinger --enable-libsoxr --enable-libspeex --enable-libtheora --enable-libtwolame --enable-libvidstab --enable-libvo-aacenc --enable-libvo-amrwbenc --enable-libvorbis --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs --enable-libxvid --enable-decklink --enable-zlib");
		lines.addOut("libavutil      52. 93.100 / 52. 93.100");
		lines.addOut("libavcodec     55. 71.100 / 55. 71.100");
		lines.addOut("libavformat    55. 49.100 / 55. 49.100");
		lines.addOut("libavdevice    55. 13.102 / 55. 13.102");
		lines.addOut("libavfilter     4. 11.102 /  4. 11.102");
		lines.addOut("libswscale      2.  6.100 /  2.  6.100");
		lines.addOut("libswresample   0. 19.100 /  0. 19.100");
		lines.addOut("libpostproc    52.  3.100 / 52.  3.100");

		assertFull(newParserFFMpeg(lines), lines, null, LocalDate.of(2014, 07, 29), 65107);
	}

	@Test
	void testFFMPEGFull_v2_6_1() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version 2.6.1 Copyright (c) 2000-2015 the FFmpeg developers");
		lines.addOut("built with gcc 4.8 (Ubuntu 4.8.2-19ubuntu1)");
		lines.addOut(
				"configuration: --extra-libs=-ldl --prefix=/opt/ffmpeg --enable-avresample --disable-debug --enable-nonfree --enable-gpl --enable-version3 --enable-libopencore-amrnb --enable-libopencore-amrwb --disable-decoder=amrnb --disable-decoder=amrwb --enable-libpulse --enable-libx264 --enable-libx265 --enable-libfdk-aac --enable-libvorbis --enable-libmp3lame --enable-libopus --enable-libvpx --enable-libspeex --enable-libass --enable-avisynth --enable-libsoxr --enable-libxvid --enable-libvo-aacenc --enable-libvidstab");
		lines.addOut("libavutil      54. 20.100 / 54. 20.100");
		lines.addOut("libavcodec     56. 26.100 / 56. 26.100");
		lines.addOut("libavformat    56. 25.101 / 56. 25.101");
		lines.addOut("libavdevice    56.  4.100 / 56.  4.100");
		lines.addOut("libavfilter     5. 11.102 /  5. 11.102");
		lines.addOut("libavresample   2.  1.  0 /  2.  1.  0");
		lines.addOut("libswscale      3.  1.101 /  3.  1.101");
		lines.addOut("libswresample   1.  1.100 /  1.  1.100");

		assertFull(newParserFFMpeg(lines), lines, new Version(2, 6, 1), LocalDate.of(2015, 3, 7), null);
	}

	@Test
	void testFFProbeFull_v2_6_1() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffprobe version 2.6.1 Copyright (c) 2007-2015 the FFmpeg developers");
		lines.addOut("built with gcc 4.8 (Ubuntu 4.8.2-19ubuntu1)");
		lines.addOut(
				"configuration: --extra-libs=-ldl --prefix=/opt/ffmpeg --enable-avresample --disable-debug --enable-nonfree --enable-gpl --enable-version3 --enable-libopencore-amrnb --enable-libopencore-amrwb --disable-decoder=amrnb --disable-decoder=amrwb --enable-libpulse --enable-libx264 --enable-libx265 --enable-libfdk-aac --enable-libvorbis --enable-libmp3lame --enable-libopus --enable-libvpx --enable-libspeex --enable-libass --enable-avisynth --enable-libsoxr --enable-libxvid --enable-libvo-aacenc --enable-libvidstab");
		lines.addOut("libavutil      54. 20.100 / 54. 20.100");
		lines.addOut("libavcodec     56. 26.100 / 56. 26.100");
		lines.addOut("libavformat    56. 25.101 / 56. 25.101");
		lines.addOut("libavdevice    56.  4.100 / 56.  4.100");
		lines.addOut("libavfilter     5. 11.102 /  5. 11.102");
		lines.addOut("libavresample   2.  1.  0 /  2.  1.  0");
		lines.addOut("libswscale      3.  1.101 /  3.  1.101");
		lines.addOut("libswresample   1.  1.100 /  1.  1.100");
		lines.addOut("libpostproc    53.  3.100 / 53.  3.100");

		assertFull(newParserProbe(lines), lines, new Version(2, 6, 1), LocalDate.of(2015, 3, 7), null);
	}

	@Test
	void testFFProbe_2020_06_01() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffprobe version git-2020-06-01-dd76226 Copyright (c) 2007-2020 the FFmpeg developers");
		lines.addOut("built with gcc 9.3.1 (GCC) 20200523");
		lines.addOut(
				"configuration: --enable-gpl --enable-version3 --enable-sdl2 --enable-fontconfig --enable-gnutls --enable-iconv --enable-libass --enable-libdav1d --enable-libbluray --enable-libfreetype --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-libopus --enable-libshine --enable-libsnappy --enable-libsoxr --enable-libsrt --enable-libtheora --enable-libtwolame --enable-libvpx --enable-libwavpack --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxml2 --enable-libzimg --enable-lzma --enable-zlib --enable-gmp --enable-libvidstab --enable-libvmaf --enable-libvorbis --enable-libvo-amrwbenc --enable-libmysofa --enable-libspeex --enable-libxvid --enable-libaom --disable-w32threads --enable-libmfx --enable-ffnvcodec --enable-cuda-llvm --enable-cuvid --enable-d3d11va --enable-nvenc --enable-nvdec --enable-dxva2 --enable-avisynth --enable-libopenmpt --enable-amf");
		lines.addOut("libavutil      56. 49.100 / 56. 49.100");
		lines.addOut("libavcodec     58. 90.100 / 58. 90.100");
		lines.addOut("libavformat    58. 44.100 / 58. 44.100");
		lines.addOut("libavdevice    58.  9.103 / 58.  9.103");
		lines.addOut("libavfilter     7. 84.100 /  7. 84.100");
		lines.addOut("libswscale      5.  6.101 /  5.  6.101");
		lines.addOut("libswresample   3.  6.100 /  3.  6.100");
		lines.addOut("libpostproc    55.  6.100 / 55.  6.100");

		assertFull(newParserProbe(lines), lines, null, LocalDate.of(2020, 6, 1), null);
	}

	@Test
	void testFFMpeg_2021_07_22() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version N-103035-g73b847e136-20210722 Copyright (c) 2000-2021 the FFmpeg developers");
		lines.addOut("  built with gcc 10-win32 (GCC) 20210408");
		lines.addOut(
				"  configuration: --prefix=/ffbuild/prefix --pkg-config-flags=--static --pkg-config=pkg-config --cross-prefix=x86_64-w64-mingw32- --arch=x86_64 --target-os=mingw32 --enable-gpl --enable-version3 --disable-debug --disable-w32threads --enable-pthreads --enable-iconv --enable-libxml2 --enable-zlib --enable-libfreetype --enable-libfribidi --enable-gmp --enable-lzma --enable-fontconfig --enable-libvorbis --enable-opencl --enable-libvmaf --enable-vulkan --disable-libxcb --disable-xlib --enable-amf --enable-libaom --enable-avisynth --enable-libdav1d --enable-libdavs2 --disable-libfdk-aac --enable-ffnvcodec --enable-cuda-llvm --enable-libglslang --enable-libgme --enable-libass --enable-libbluray --enable-libmp3lame --enable-libopus --enable-libtheora --enable-libvpx --enable-libwebp --enable-lv2 --enable-libmfx --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-librav1e --enable-librubberband --enable-schannel --enable-sdl2 --enable-libsoxr --enable-libsrt --enable-libsvtav1 --enable-libtwolame --enable-libuavs3d --disable-libdrm --disable-vaapi --enable-libvidstab --enable-libx264 --enable-libx265 --enable-libxavs2 --enable-libxvid --enable-libzimg --extra-cflags=-DLIBTWOLAME_STATIC --extra-cxxflags= --extra-ldflags=-pthread --extra-ldexeflags= --extra-libs=-lgomp --extra-version=20210722");
		lines.addOut("  libavutil      57.  1.100 / 57.  1.100");
		lines.addOut("  libavcodec     59.  3.102 / 59.  3.102");
		lines.addOut("  libavformat    59.  4.101 / 59.  4.101");
		lines.addOut("  libavdevice    59.  0.100 / 59.  0.100");
		lines.addOut("  libavfilter     8.  0.103 /  8.  0.103");
		lines.addOut("  libswscale      6.  0.100 /  6.  0.100");
		lines.addOut("  libswresample   4.  0.100 /  4.  0.100");
		lines.addOut("  libpostproc    56.  0.100 / 56.  0.100");
		lines.addOut("Hyper fast Audio and Video encoder");
		lines.addOut("usage: ffmpeg [options] [[infile options] -i infile]... {[outfile options] outfile}...");

		assertFull(newParserFFMpeg(lines), lines, null, LocalDate.of(2021, 7, 22), 103035);
	}

	@Test
	void testFFMpeg_2022_01_24() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version 2022-01-24-git-0a83ecbf48-full_build-www.gyan.dev Copyright (c) 2000-2022 the FFmpeg developers");
		lines.addOut("built with gcc 11.2.0 (Rev7, Built by MSYS2 project)");
		lines.addOut(
				"configuration: --enable-gpl --enable-version3 --enable-static --disable-w32threads --disable-autodetect --enable-fontconfig --enable-iconv --enable-gnutls --enable-libxml2 --enable-gmp --enable-bzlib --enable-lzma --enable-libsnappy --enable-zlib --enable-librist --enable-libsrt --enable-libssh --enable-libzmq --enable-avisynth --enable-libbluray --enable-libcaca --enable-sdl2 --enable-libdav1d --enable-libdavs2 --enable-libuavs3d --enable-libzvbi --enable-librav1e --enable-libsvtav1 --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs2 --enable-libxvid --enable-libaom --enable-libopenjpeg --enable-libvpx --enable-mediafoundation --enable-libass --enable-frei0r --enable-libfreetype --enable-libfribidi --enable-libvidstab --enable-libvmaf --enable-libzimg --enable-amf --enable-cuda-llvm --enable-cuvid --enable-ffnvcodec --enable-nvdec --enable-nvenc --enable-d3d11va --enable-dxva2 --enable-libmfx --enable-libshaderc --enable-vulkan --enable-libplacebo --enable-opencl --enable-libcdio --enable-libgme --enable-libmodplug --enable-libopenmpt --enable-libopencore-amrwb --enable-libmp3lame --enable-libshine --enable-libtheora --enable-libtwolame --enable-libvo-amrwbenc --enable-libilbc --enable-libgsm --enable-libopencore-amrnb --enable-libopus --enable-libspeex --enable-libvorbis --enable-ladspa --enable-libbs2b --enable-libflite --enable-libmysofa --enable-librubberband --enable-libsoxr --enable-chromaprint");
		lines.addOut("libavutil      57. 18.100 / 57. 18.100");
		lines.addOut("libavcodec     59. 20.100 / 59. 20.100");
		lines.addOut("libavformat    59. 17.101 / 59. 17.101");
		lines.addOut("libavdevice    59.  5.100 / 59.  5.100");
		lines.addOut("libavfilter     8. 26.101 /  8. 26.101");
		lines.addOut("libswscale      6.  5.100 /  6.  5.100");
		lines.addOut("libswresample   4.  4.100 /  4.  4.100");
		lines.addOut("libpostproc    56.  4.100 / 56.  4.100");

		assertFull(newParserFFMpeg(lines), lines, null, LocalDate.of(2022, 1, 24), null);
	}

	@Test
	void testFFMpeg_7_0_1_full_windows() throws Exception {
		Lines lines = new Lines();

		lines.addOut("ffmpeg version 7.0.1-full_build-www.gyan.dev Copyright (c) 2000-2024 the FFmpeg developers");
		lines.addOut("built with gcc 13.2.0 (Rev5, Built by MSYS2 project)");
		lines.addOut(
				"configuration: --enable-gpl --enable-version3 --enable-static --disable-w32threads --disable-autodetect --enable-fontconfig --enable-iconv --enable-gnutls --enable-libxml2 --enable-gmp --enable-bzlib --enable-lzma --enable-libsnappy --enable-zlib --enable-librist --enable-libsrt --enable-libssh --enable-libzmq --enable-avisynth --enable-libbluray --enable-libcaca --enable-sdl2 --enable-libaribb24 --enable-libaribcaption --enable-libdav1d --enable-libdavs2 --enable-libuavs3d --enable-libxevd --enable-libzvbi --enable-librav1e --enable-libsvtav1 --enable-libwebp --enable-libx264 --enable-libx265 --enable-libxavs2 --enable-libxeve --enable-libxvid --enable-libaom --enable-libjxl --enable-libopenjpeg --enable-libvpx --enable-mediafoundation --enable-libass --enable-frei0r --enable-libfreetype --enable-libfribidi --enable-libharfbuzz --enable-liblensfun --enable-libvidstab --enable-libvmaf --enable-libzimg --enable-amf --enable-cuda-llvm --enable-cuvid --enable-dxva2 --enable-d3d11va --enable-d3d12va --enable-ffnvcodec --enable-libvpl --enable-nvdec --enable-nvenc --enable-vaapi --enable-libshaderc --enable-vulkan --enable-libplacebo --enable-opencl --enable-libcdio --enable-libgme --enable-libmodplug --enable-libopenmpt --enable-libopencore-amrwb --enable-libmp3lame --enable-libshine --enable-libtheora --enable-libtwolame --enable-libvo-amrwbenc --enable-libcodec2 --enable-libilbc --enable-libgsm --enable-libopencore-amrnb --enable-libopus --enable-libspeex --enable-libvorbis --enable-ladspa --enable-libbs2b --enable-libflite --enable-libmysofa --enable-librubberband --enable-libsoxr --enable-chromaprint");
		lines.addOut("libavutil      59.  8.100 / 59.  8.100");
		lines.addOut("libavcodec     61.  3.100 / 61.  3.100");
		lines.addOut("libavformat    61.  1.100 / 61.  1.100");
		lines.addOut("libavdevice    61.  1.100 / 61.  1.100");
		lines.addOut("libavfilter    10.  1.100 / 10.  1.100");
		lines.addOut("libswscale      8.  1.100 /  8.  1.100");
		lines.addOut("libswresample   5.  1.100 /  5.  1.100");
		lines.addOut("libpostproc    58.  1.100 / 58.  1.100");

		assertFull(newParserFFMpeg(lines), lines, new Version(7, 0, 1), LocalDate.of(2024, 4, 5), null);
	}

	// ********************************************************

	private Parser newParserProbe(Lines lines) {
		FFProbeSoftProvider softProvider = new FFProbeSoftProvider();
		Parser parser = softProvider.createParser(new File("."));
		parser.read(lines);
		return parser;
	}

	private Parser newParserFFMpeg(Lines lines) {
		FFMpegSoftProvider softProvider = new FFMpegSoftProvider();
		Parser parser = softProvider.createParser(new File("."));
		parser.read(lines);
		return parser;
	}

	private void assertFull(Parser parser, Lines lines, Version version, LocalDate builtDate, Integer builtVersion) throws IOException,
			ParseException {
		SoftFound softFound = parser.closeAndParse("", 0, lines);
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
			assertEquals(builtDate, ffInfo.getBuiltDate());
		}
	}

}
