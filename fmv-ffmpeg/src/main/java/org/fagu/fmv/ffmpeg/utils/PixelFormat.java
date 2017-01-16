package org.fagu.fmv.ffmpeg.utils;

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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;


/**
 * ffmpeg -v quiet -pix_fmts<br>
 * {@link http://ffmpeg.org/ffmpeg-filters.html#format-1}
 * 
 * @author f.agu
 */
public class PixelFormat {

	/**
	 * 
	 */
	private static final HelpCache<PixelFormat, PixelFormatHelp> HELP_CACHE = new HelpCache<>(runnable(), PixelFormat::new);

	public static final PixelFormat _0BGR = new PixelFormat("0bgr");

	public static final PixelFormat _0RGB = new PixelFormat("0rgb");

	public static final PixelFormat ABGR = new PixelFormat("abgr");

	public static final PixelFormat ARGB = new PixelFormat("argb");

	public static final PixelFormat BAYER_BGGR16BE = new PixelFormat("bayer_bggr16be");

	public static final PixelFormat BAYER_BGGR16LE = new PixelFormat("bayer_bggr16le");

	public static final PixelFormat BAYER_BGGR8 = new PixelFormat("bayer_bggr8");

	public static final PixelFormat BAYER_GBRG16BE = new PixelFormat("bayer_gbrg16be");

	public static final PixelFormat BAYER_GBRG16LE = new PixelFormat("bayer_gbrg16le");

	public static final PixelFormat BAYER_GBRG8 = new PixelFormat("bayer_gbrg8");

	public static final PixelFormat BAYER_GRBG16BE = new PixelFormat("bayer_grbg16be");

	public static final PixelFormat BAYER_GRBG16LE = new PixelFormat("bayer_grbg16le");

	public static final PixelFormat BAYER_GRBG8 = new PixelFormat("bayer_grbg8");

	public static final PixelFormat BAYER_RGGB16BE = new PixelFormat("bayer_rggb16be");

	public static final PixelFormat BAYER_RGGB16LE = new PixelFormat("bayer_rggb16le");

	public static final PixelFormat BAYER_RGGB8 = new PixelFormat("bayer_rggb8");

	public static final PixelFormat BGR0 = new PixelFormat("bgr0");

	public static final PixelFormat BGR24 = new PixelFormat("bgr24");

	public static final PixelFormat BGR4 = new PixelFormat("bgr4");

	public static final PixelFormat BGR444BE = new PixelFormat("bgr444be");

	public static final PixelFormat BGR444LE = new PixelFormat("bgr444le");

	public static final PixelFormat BGR48BE = new PixelFormat("bgr48be");

	public static final PixelFormat BGR48LE = new PixelFormat("bgr48le");

	public static final PixelFormat BGR4_BYTE = new PixelFormat("bgr4_byte");

	public static final PixelFormat BGR555BE = new PixelFormat("bgr555be");

	public static final PixelFormat BGR555LE = new PixelFormat("bgr555le");

	public static final PixelFormat BGR565BE = new PixelFormat("bgr565be");

	public static final PixelFormat BGR565LE = new PixelFormat("bgr565le");

	public static final PixelFormat BGR8 = new PixelFormat("bgr8");

	public static final PixelFormat BGRA = new PixelFormat("bgra");

	public static final PixelFormat BGRA64BE = new PixelFormat("bgra64be");

	public static final PixelFormat BGRA64LE = new PixelFormat("bgra64le");

	public static final PixelFormat DXVA2_VLD = new PixelFormat("dxva2_vld");

	public static final PixelFormat GBRAP = new PixelFormat("gbrap");

	public static final PixelFormat GBRAP16BE = new PixelFormat("gbrap16be");

	public static final PixelFormat GBRAP16LE = new PixelFormat("gbrap16le");

	public static final PixelFormat GBRP = new PixelFormat("gbrp");

	public static final PixelFormat GBRP10BE = new PixelFormat("gbrp10be");

	public static final PixelFormat GBRP10LE = new PixelFormat("gbrp10le");

	public static final PixelFormat GBRP12BE = new PixelFormat("gbrp12be");

	public static final PixelFormat GBRP12LE = new PixelFormat("gbrp12le");

	public static final PixelFormat GBRP14BE = new PixelFormat("gbrp14be");

	public static final PixelFormat GBRP14LE = new PixelFormat("gbrp14le");

	public static final PixelFormat GBRP16BE = new PixelFormat("gbrp16be");

	public static final PixelFormat GBRP16LE = new PixelFormat("gbrp16le");

	public static final PixelFormat GBRP9BE = new PixelFormat("gbrp9be");

	public static final PixelFormat GBRP9LE = new PixelFormat("gbrp9le");

	public static final PixelFormat GRAY = new PixelFormat("gray");

	public static final PixelFormat GRAY16BE = new PixelFormat("gray16be");

	public static final PixelFormat GRAY16LE = new PixelFormat("gray16le");

	public static final PixelFormat MONOB = new PixelFormat("monob");

	public static final PixelFormat MONOW = new PixelFormat("monow");

	public static final PixelFormat NV12 = new PixelFormat("nv12");

	public static final PixelFormat NV16 = new PixelFormat("nv16");

	public static final PixelFormat NV20BE = new PixelFormat("nv20be");

	public static final PixelFormat NV20LE = new PixelFormat("nv20le");

	public static final PixelFormat NV21 = new PixelFormat("nv21");

	public static final PixelFormat PAL8 = new PixelFormat("pal8");

	public static final PixelFormat RGB0 = new PixelFormat("rgb0");

	public static final PixelFormat RGB24 = new PixelFormat("rgb24");

	public static final PixelFormat RGB4 = new PixelFormat("rgb4");

	public static final PixelFormat RGB444BE = new PixelFormat("rgb444be");

	public static final PixelFormat RGB444LE = new PixelFormat("rgb444le");

	public static final PixelFormat RGB48BE = new PixelFormat("rgb48be");

	public static final PixelFormat RGB48LE = new PixelFormat("rgb48le");

	public static final PixelFormat RGB4_BYTE = new PixelFormat("rgb4_byte");

	public static final PixelFormat RGB555BE = new PixelFormat("rgb555be");

	public static final PixelFormat RGB555LE = new PixelFormat("rgb555le");

	public static final PixelFormat RGB565BE = new PixelFormat("rgb565be");

	public static final PixelFormat RGB565LE = new PixelFormat("rgb565le");

	public static final PixelFormat RGB8 = new PixelFormat("rgb8");

	public static final PixelFormat RGBA = new PixelFormat("rgba");

	public static final PixelFormat RGBA64BE = new PixelFormat("rgba64be");

	public static final PixelFormat RGBA64LE = new PixelFormat("rgba64le");

	public static final PixelFormat UYVY422 = new PixelFormat("uyvy422");

	public static final PixelFormat UYYVYY411 = new PixelFormat("uyyvyy411");

	public static final PixelFormat VAAPI_IDCT = new PixelFormat("vaapi_idct");

	public static final PixelFormat VAAPI_MOCO = new PixelFormat("vaapi_moco");

	public static final PixelFormat VAAPI_VLD = new PixelFormat("vaapi_vld");

	public static final PixelFormat VDA = new PixelFormat("vda");

	public static final PixelFormat VDA_VLD = new PixelFormat("vda_vld");

	public static final PixelFormat VDPAU = new PixelFormat("vdpau");

	public static final PixelFormat VDPAU_H264 = new PixelFormat("vdpau_h264");

	public static final PixelFormat VDPAU_MPEG1 = new PixelFormat("vdpau_mpeg1");

	public static final PixelFormat VDPAU_MPEG2 = new PixelFormat("vdpau_mpeg2");

	public static final PixelFormat VDPAU_MPEG4 = new PixelFormat("vdpau_mpeg4");

	public static final PixelFormat VDPAU_VC1 = new PixelFormat("vdpau_vc1");

	public static final PixelFormat VDPAU_WMV3 = new PixelFormat("vdpau_wmv3");

	public static final PixelFormat XVMCIDCT = new PixelFormat("xvmcidct");

	public static final PixelFormat XVMCMC = new PixelFormat("xvmcmc");

	public static final PixelFormat XYZ12BE = new PixelFormat("xyz12be");

	public static final PixelFormat XYZ12LE = new PixelFormat("xyz12le");

	public static final PixelFormat YA16BE = new PixelFormat("ya16be");

	public static final PixelFormat YA16LE = new PixelFormat("ya16le");

	public static final PixelFormat YA8 = new PixelFormat("ya8");

	public static final PixelFormat YUV410P = new PixelFormat("yuv410p");

	public static final PixelFormat YUV411P = new PixelFormat("yuv411p");

	public static final PixelFormat YUV420P = new PixelFormat("yuv420p");

	public static final PixelFormat YUV420P10BE = new PixelFormat("yuv420p10be");

	public static final PixelFormat YUV420P10LE = new PixelFormat("yuv420p10le");

	public static final PixelFormat YUV420P12BE = new PixelFormat("yuv420p12be");

	public static final PixelFormat YUV420P12LE = new PixelFormat("yuv420p12le");

	public static final PixelFormat YUV420P14BE = new PixelFormat("yuv420p14be");

	public static final PixelFormat YUV420P14LE = new PixelFormat("yuv420p14le");

	public static final PixelFormat YUV420P16BE = new PixelFormat("yuv420p16be");

	public static final PixelFormat YUV420P16LE = new PixelFormat("yuv420p16le");

	public static final PixelFormat YUV420P9BE = new PixelFormat("yuv420p9be");

	public static final PixelFormat YUV420P9LE = new PixelFormat("yuv420p9le");

	public static final PixelFormat YUV422P = new PixelFormat("yuv422p");

	public static final PixelFormat YUV422P10BE = new PixelFormat("yuv422p10be");

	public static final PixelFormat YUV422P10LE = new PixelFormat("yuv422p10le");

	public static final PixelFormat YUV422P12BE = new PixelFormat("yuv422p12be");

	public static final PixelFormat YUV422P12LE = new PixelFormat("yuv422p12le");

	public static final PixelFormat YUV422P14BE = new PixelFormat("yuv422p14be");

	public static final PixelFormat YUV422P14LE = new PixelFormat("yuv422p14le");

	public static final PixelFormat YUV422P16BE = new PixelFormat("yuv422p16be");

	public static final PixelFormat YUV422P16LE = new PixelFormat("yuv422p16le");

	public static final PixelFormat YUV422P9BE = new PixelFormat("yuv422p9be");

	public static final PixelFormat YUV422P9LE = new PixelFormat("yuv422p9le");

	public static final PixelFormat YUV440P = new PixelFormat("yuv440p");

	public static final PixelFormat YUV444P = new PixelFormat("yuv444p");

	public static final PixelFormat YUV444P10BE = new PixelFormat("yuv444p10be");

	public static final PixelFormat YUV444P10LE = new PixelFormat("yuv444p10le");

	public static final PixelFormat YUV444P12BE = new PixelFormat("yuv444p12be");

	public static final PixelFormat YUV444P12LE = new PixelFormat("yuv444p12le");

	public static final PixelFormat YUV444P14BE = new PixelFormat("yuv444p14be");

	public static final PixelFormat YUV444P14LE = new PixelFormat("yuv444p14le");

	public static final PixelFormat YUV444P16BE = new PixelFormat("yuv444p16be");

	public static final PixelFormat YUV444P16LE = new PixelFormat("yuv444p16le");

	public static final PixelFormat YUV444P9BE = new PixelFormat("yuv444p9be");

	public static final PixelFormat YUV444P9LE = new PixelFormat("yuv444p9le");

	public static final PixelFormat YUVA420P = new PixelFormat("yuva420p");

	public static final PixelFormat YUVA420P10BE = new PixelFormat("yuva420p10be");

	public static final PixelFormat YUVA420P10LE = new PixelFormat("yuva420p10le");

	public static final PixelFormat YUVA420P16BE = new PixelFormat("yuva420p16be");

	public static final PixelFormat YUVA420P16LE = new PixelFormat("yuva420p16le");

	public static final PixelFormat YUVA420P9BE = new PixelFormat("yuva420p9be");

	public static final PixelFormat YUVA420P9LE = new PixelFormat("yuva420p9le");

	public static final PixelFormat YUVA422P = new PixelFormat("yuva422p");

	public static final PixelFormat YUVA422P10BE = new PixelFormat("yuva422p10be");

	public static final PixelFormat YUVA422P10LE = new PixelFormat("yuva422p10le");

	public static final PixelFormat YUVA422P16BE = new PixelFormat("yuva422p16be");

	public static final PixelFormat YUVA422P16LE = new PixelFormat("yuva422p16le");

	public static final PixelFormat YUVA422P9BE = new PixelFormat("yuva422p9be");

	public static final PixelFormat YUVA422P9LE = new PixelFormat("yuva422p9le");

	public static final PixelFormat YUVA444P = new PixelFormat("yuva444p");

	public static final PixelFormat YUVA444P10BE = new PixelFormat("yuva444p10be");

	public static final PixelFormat YUVA444P10LE = new PixelFormat("yuva444p10le");

	public static final PixelFormat YUVA444P16BE = new PixelFormat("yuva444p16be");

	public static final PixelFormat YUVA444P16LE = new PixelFormat("yuva444p16le");

	public static final PixelFormat YUVA444P9BE = new PixelFormat("yuva444p9be");

	public static final PixelFormat YUVA444P9LE = new PixelFormat("yuva444p9le");

	public static final PixelFormat YUVJ411P = new PixelFormat("yuvj411p");

	public static final PixelFormat YUVJ420P = new PixelFormat("yuvj420p");

	public static final PixelFormat YUVJ422P = new PixelFormat("yuvj422p");

	public static final PixelFormat YUVJ440P = new PixelFormat("yuvj440p");

	public static final PixelFormat YUVJ444P = new PixelFormat("yuvj444p");

	public static final PixelFormat YUYV422 = new PixelFormat("yuyv422");

	public static final PixelFormat YVYU422 = new PixelFormat("yvyu422");

	/**
	 * 
	 */
	private final String name;

	/**
	 * @param name
	 */
	private PixelFormat(String name) {
		this.name = name;
		HELP_CACHE.add(name, this, null);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public boolean isSupportedInput() {
		return cache().contains('I');
	}

	/**
	 * @return
	 */
	public boolean isSupportedOutput() {
		return cache().contains('O');
	}

	/**
	 * @return
	 */
	public boolean isHardwareAccelerated() {
		return cache().contains('H');
	}

	/**
	 * @return
	 */
	public boolean isPaletted() {
		return cache().contains('P');
	}

	/**
	 * @return
	 */
	public boolean isBitstream() {
		return cache().contains('B');
	}

	/**
	 * @return
	 */
	public int getNbComponents() {
		return cache().nbComponents;
	}

	/**
	 * @return
	 */
	public int getBitsPerPixel() {
		return cache().bitsPerPixel;
	}

	/**
	 * @return
	 */
	public boolean exists() {
		return exists(name);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	// **************************************************

	/**
	 * @return
	 */
	public static boolean exists(String name) {
		return HELP_CACHE.exists(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public static PixelFormat byName(String name) {
		return HELP_CACHE.byName(name);
	}

	/**
	 * @return
	 */
	public static Set<String> availableNames() {
		return HELP_CACHE.availableNames();
	}

	/**
	 * @return
	 */
	public static List<PixelFormat> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @return
	 */
	private PixelFormatHelp cache() {
		return HELP_CACHE.cache(name).get(0);
	}

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-pix_fmts");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<PixelFormatHelp> cacheConsumer = HELP_CACHE.consumer();
				final Pattern PATTERN = Pattern.compile("(\\d+)\\s+(\\d+)");
				Function<String, PixelFormatHelp> factory = name -> new PixelFormatHelp(name);
				Consumer<PixelFormatHelp> consumer = help -> {
					Matcher matcher = PATTERN.matcher(help.getText());
					if(matcher.matches()) {
						help.nbComponents = Integer.parseInt(matcher.group(1));
						help.bitsPerPixel = Integer.parseInt(matcher.group(2));
						cacheConsumer.accept(help);
					} else {
						throw new RuntimeException("PixelFormat description unparsable: " + help.getText());
					}
				};

				AvailableHelp<PixelFormatHelp> availableHelp = AvailableHelp.create();
				availableHelp.title().legend().unreadLines(2).values(factory, consumer).parse(executor.execute().getResult());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	// ---------------------------------------------

	/**
	 * @return
	 */
	private static class PixelFormatHelp extends Help {

		/**
		 * 
		 */
		private int nbComponents;

		/**
		 * 
		 */
		private int bitsPerPixel;

		/**
		 * @param name
		 */
		protected PixelFormatHelp(String name) {
			super(name);
		}
	}
}
