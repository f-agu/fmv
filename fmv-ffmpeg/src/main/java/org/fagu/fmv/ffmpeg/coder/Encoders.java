package org.fagu.fmv.ffmpeg.coder;

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

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;
import org.fagu.fmv.ffmpeg.utils.AvailableHelp;
import org.fagu.fmv.ffmpeg.utils.HelpCache;


/**
 * @author f.agu
 */
public class Encoders extends Coders {

	/**
	 * 
	 */
	private static final HelpCache<Encoders, EncoderHelp> HELP_CACHE = new HelpCache<>(runnable(), Encoders::new);

	/**
	 * Multicolor charset for Commodore 64 (codec a64_multi)
	 */
	public static final Encoders A64MULTI = new Encoders("a64multi");

	/**
	 * Multicolor charset for Commodore 64, extended with 5th color (colram) (codec a64_multi5)
	 */
	public static final Encoders A64MULTI5 = new Encoders("a64multi5");

	/**
	 * AAC (Advanced Audio Coding)
	 */
	public static final Encoders AAC = new Encoders("aac");

	/**
	 * ATSC A/52A (AC-3)
	 */
	public static final Encoders AC3 = new Encoders("ac3");

	/**
	 * ATSC A/52A (AC-3) (codec ac3)
	 */
	public static final Encoders AC3_FIXED = new Encoders("ac3_fixed");

	/**
	 * SEGA CRI ADX ADPCM
	 */
	public static final Encoders ADPCM_ADX = new Encoders("adpcm_adx");

	/**
	 * ADPCM IMA QuickTime
	 */
	public static final Encoders ADPCM_IMA_QT = new Encoders("adpcm_ima_qt");

	/**
	 * ADPCM IMA WAV
	 */
	public static final Encoders ADPCM_IMA_WAV = new Encoders("adpcm_ima_wav");

	/**
	 * ADPCM Microsoft
	 */
	public static final Encoders ADPCM_MS = new Encoders("adpcm_ms");

	/**
	 * ADPCM Shockwave Flash
	 */
	public static final Encoders ADPCM_SWF = new Encoders("adpcm_swf");

	/**
	 * ADPCM Yamaha
	 */
	public static final Encoders ADPCM_YAMAHA = new Encoders("adpcm_yamaha");

	/**
	 * ALAC (Apple Lossless Audio Codec)
	 */
	public static final Encoders ALAC = new Encoders("alac");

	/**
	 * Alias/Wavefront PIX image
	 */
	public static final Encoders ALIAS_PIX = new Encoders("alias_pix");

	/**
	 * AMV Video
	 */
	public static final Encoders AMV = new Encoders("amv");

	/**
	 * ASS (Advanced SubStation Alpha) subtitle
	 */
	public static final Encoders ASS = new Encoders("ass");

	/**
	 * ASUS V1
	 */
	public static final Encoders ASV1 = new Encoders("asv1");

	/**
	 * ASUS V2
	 */
	public static final Encoders ASV2 = new Encoders("asv2");

	/**
	 * Avid 1:1 10-bit RGB Packer
	 */
	public static final Encoders AVRP = new Encoders("avrp");

	/**
	 * Avid Meridien Uncompressed
	 */
	public static final Encoders AVUI = new Encoders("avui");

	/**
	 * Uncompressed packed MS 4:4:4:4
	 */
	public static final Encoders AYUV = new Encoders("ayuv");

	/**
	 * BMP (Windows and OS/2 bitmap)
	 */
	public static final Encoders BMP = new Encoders("bmp");

	/**
	 * Cinepak / CVID
	 */
	public static final Encoders CINEPAK = new Encoders("cinepak");

	/**
	 * Cirrus Logic AccuPak
	 */
	public static final Encoders CLJR = new Encoders("cljr");

	/**
	 * RFC 3389 comfort noise generator
	 */
	public static final Encoders COMFORTNOISE = new Encoders("comfortnoise");

	/**
	 * DCA (DTS Coherent Acoustics) (codec dts)
	 */
	public static final Encoders DCA = new Encoders("dca");

	/**
	 * VC3/DNxHD
	 */
	public static final Encoders DNXHD = new Encoders("dnxhd");

	/**
	 * DPX (Digital Picture Exchange) image
	 */
	public static final Encoders DPX = new Encoders("dpx");

	/**
	 * DVB subtitles (codec dvb_subtitle)
	 */
	public static final Encoders DVBSUB = new Encoders("dvbsub");

	/**
	 * DVD subtitles (codec dvd_subtitle)
	 */
	public static final Encoders DVDSUB = new Encoders("dvdsub");

	/**
	 * DV (Digital Video)
	 */
	public static final Encoders DVVIDEO = new Encoders("dvvideo");

	/**
	 * ATSC A/52 E-AC-3
	 */
	public static final Encoders EAC3 = new Encoders("eac3");

	/**
	 * FFmpeg video codec #1
	 */
	public static final Encoders FFV1 = new Encoders("ffv1");

	/**
	 * Huffyuv FFmpeg variant
	 */
	public static final Encoders FFVHUFF = new Encoders("ffvhuff");

	/**
	 * FLAC (Free Lossless Audio Codec)
	 */
	public static final Encoders FLAC = new Encoders("flac");

	/**
	 * Flash Screen Video
	 */
	public static final Encoders FLASHSV = new Encoders("flashsv");

	/**
	 * Flash Screen Video Version 2
	 */
	public static final Encoders FLASHSV2 = new Encoders("flashsv2");

	/**
	 * FLV / Sorenson Spark / Sorenson H.263 (Flash Video) (codec flv1)
	 */
	public static final Encoders FLV = new Encoders("flv");

	/**
	 * G.722 ADPCM (codec adpcm_g722)
	 */
	public static final Encoders G722 = new Encoders("g722");

	/**
	 * G.723.1
	 */
	public static final Encoders G723_1 = new Encoders("g723_1");

	/**
	 * G.726 ADPCM (codec adpcm_g726)
	 */
	public static final Encoders G726 = new Encoders("g726");

	/**
	 * GIF (Graphics Interchange Format)
	 */
	public static final Encoders GIF = new Encoders("gif");

	/**
	 * H.261
	 */
	public static final Encoders H261 = new Encoders("h261");

	/**
	 * H.263 / H.263-1996
	 */
	public static final Encoders H263 = new Encoders("h263");

	/**
	 * H.263+ / H.263-1998 / H.263 version 2
	 */
	public static final Encoders H263P = new Encoders("h263p");

	/**
	 * Huffyuv / HuffYUV
	 */
	public static final Encoders HUFFYUV = new Encoders("huffyuv");

	/**
	 * JPEG 2000
	 */
	public static final Encoders JPEG2000 = new Encoders("jpeg2000");

	/**
	 * JPEG-LS
	 */
	public static final Encoders JPEGLS = new Encoders("jpegls");

	/**
	 * 
	 */
	public static final Encoders LIBFAAC = new Encoders("libfaac");

	/**
	 * 
	 */
	public static final Encoders LIBFDK_AAC = new Encoders("libfdk_aac");

	/**
	 * libgsm GSM (codec gsm)
	 */
	public static final Encoders LIBGSM = new Encoders("libgsm");

	/**
	 * libgsm GSM Microsoft variant (codec gsm_ms)
	 */
	public static final Encoders LIBGSM_MS = new Encoders("libgsm_ms");

	/**
	 * iLBC (Internet Low Bitrate Codec) (codec ilbc)
	 */
	public static final Encoders LIBILBC = new Encoders("libilbc");

	/**
	 * libmp3lame MP3 (MPEG audio layer 3) (codec mp3)
	 */
	public static final Encoders LIBMP3LAME = new Encoders("libmp3lame");

	/**
	 * OpenCORE AMR-NB (Adaptive Multi-Rate Narrow-Band) (codec amr_nb)
	 */
	public static final Encoders LIBOPENCORE_AMRNB = new Encoders("libopencore_amrnb");

	/**
	 * OpenJPEG JPEG 2000 (codec jpeg2000)
	 */
	public static final Encoders LIBOPENJPEG = new Encoders("libopenjpeg");

	/**
	 * libopus Opus (codec opus)
	 */
	public static final Encoders LIBOPUS = new Encoders("libopus");

	/**
	 * libschroedinger Dirac 2.2 (codec dirac)
	 */
	public static final Encoders LIBSCHROEDINGER = new Encoders("libschroedinger");

	/**
	 * libspeex Speex (codec speex)
	 */
	public static final Encoders LIBSPEEX = new Encoders("libspeex");

	/**
	 * libtheora Theora (codec theora)
	 */
	public static final Encoders LIBTHEORA = new Encoders("libtheora");

	/**
	 * libtwolame MP2 (MPEG audio layer 2) (codec mp2)
	 */
	public static final Encoders LIBTWOLAME = new Encoders("libtwolame");

	/**
	 * Android VisualOn AAC (Advanced Audio Coding) (codec aac)
	 */
	public static final Encoders LIBVO_AACENC = new Encoders("libvo_aacenc");

	/**
	 * Android VisualOn AMR-WB (Adaptive Multi-Rate Wide-Band) (codec amr_wb)
	 */
	public static final Encoders LIBVO_AMRWBENC = new Encoders("libvo_amrwbenc");

	/**
	 * libvorbis (codec vorbis)
	 */
	public static final Encoders LIBVORBIS = new Encoders("libvorbis");

	/**
	 * libvpx VP8 (codec vp8)
	 */
	public static final Encoders LIBVPX = new Encoders("libvpx");

	/**
	 * libvpx VP9 (codec vp9)
	 */
	public static final Encoders LIBVPX_VP9 = new Encoders("libvpx-vp9");

	/**
	 * (codec wavpack)
	 */
	public static final Encoders LIBWAVPACK = new Encoders("libwavpack");

	/**
	 * libwebp WebP image (codec webp)
	 */
	public static final Encoders LIBWEBP = new Encoders("libwebp");

	/**
	 * libx264 H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10 (codec h264)
	 */
	public static final Encoders LIBX264 = new Encoders("libx264");

	/**
	 * libx264 H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10 RGB (codec h264)
	 */
	public static final Encoders LIBX264RGB = new Encoders("libx264rgb");

	/**
	 * libx265 H.265 / HEVC (codec hevc)
	 */
	public static final Encoders LIBX265 = new Encoders("libx265");

	/**
	 * libxavs Chinese AVS (Audio Video Standard) (codec cavs)
	 */
	public static final Encoders LIBXAVS = new Encoders("libxavs");

	/**
	 * libxvidcore MPEG-4 part 2 (codec mpeg4)
	 */
	public static final Encoders LIBXVID = new Encoders("libxvid");

	/**
	 * Lossless JPEG
	 */
	public static final Encoders LJPEG = new Encoders("ljpeg");

	/**
	 * MJPEG (Motion JPEG)
	 */
	public static final Encoders MJPEG = new Encoders("mjpeg");

	/**
	 * 3GPP Timed Text subtitle
	 */
	public static final Encoders MOV_TEXT = new Encoders("mov_text");

	/**
	 * MP2 (MPEG audio layer 2)
	 */
	public static final Encoders MP2 = new Encoders("mp2");

	/**
	 * MP2 fixed point (MPEG audio layer 2) (codec mp2)
	 */
	public static final Encoders MP2FIXED = new Encoders("mp2fixed");

	/**
	 * MPEG-1 video
	 */
	public static final Encoders MPEG1VIDEO = new Encoders("mpeg1video");

	/**
	 * MPEG-2 video
	 */
	public static final Encoders MPEG2VIDEO = new Encoders("mpeg2video");

	/**
	 * MPEG-4 part 2
	 */
	public static final Encoders MPEG4 = new Encoders("mpeg4");

	/**
	 * MPEG-4 part 2 Microsoft variant version 3 (codec msmpeg4v3)
	 */
	public static final Encoders MSMPEG4 = new Encoders("msmpeg4");

	/**
	 * MPEG-4 part 2 Microsoft variant version 2
	 */
	public static final Encoders MSMPEG4V2 = new Encoders("msmpeg4v2");

	/**
	 * Microsoft Video-1
	 */
	public static final Encoders MSVIDEO1 = new Encoders("msvideo1");

	/**
	 * Nellymoser Asao
	 */
	public static final Encoders NELLYMOSER = new Encoders("nellymoser");

	/**
	 * PAM (Portable AnyMap) image
	 */
	public static final Encoders PAM = new Encoders("pam");

	/**
	 * PBM (Portable BitMap) image
	 */
	public static final Encoders PBM = new Encoders("pbm");

	/**
	 * PCM A-law / G.711 A-law
	 */
	public static final Encoders PCM_ALAW = new Encoders("pcm_alaw");

	/**
	 * PCM 32-bit floating point big-endian
	 */
	public static final Encoders PCM_F32BE = new Encoders("pcm_f32be");

	/**
	 * PCM 32-bit floating point little-endian
	 */
	public static final Encoders PCM_F32LE = new Encoders("pcm_f32le");

	/**
	 * PCM 64-bit floating point big-endian
	 */
	public static final Encoders PCM_F64BE = new Encoders("pcm_f64be");

	/**
	 * PCM 64-bit floating point little-endian
	 */
	public static final Encoders PCM_F64LE = new Encoders("pcm_f64le");

	/**
	 * PCM mu-law / G.711 mu-law
	 */
	public static final Encoders PCM_MULAW = new Encoders("pcm_mulaw");

	/**
	 * PCM signed 16-bit big-endian
	 */
	public static final Encoders PCM_S16BE = new Encoders("pcm_s16be");

	/**
	 * PCM signed 16-bit big-endian planar
	 */
	public static final Encoders PCM_S16BE_PLANAR = new Encoders("pcm_s16be_planar");

	/**
	 * PCM signed 16-bit little-endian
	 */
	public static final Encoders PCM_S16LE = new Encoders("pcm_s16le");

	/**
	 * PCM signed 16-bit little-endian planar
	 */
	public static final Encoders PCM_S16LE_PLANAR = new Encoders("pcm_s16le_planar");

	/**
	 * PCM signed 24-bit big-endian
	 */
	public static final Encoders PCM_S24BE = new Encoders("pcm_s24be");

	/**
	 * PCM D-Cinema audio signed 24-bit
	 */
	public static final Encoders PCM_S24DAUD = new Encoders("pcm_s24daud");

	/**
	 * PCM signed 24-bit little-endian
	 */
	public static final Encoders PCM_S24LE = new Encoders("pcm_s24le");

	/**
	 * PCM signed 24-bit little-endian planar
	 */
	public static final Encoders PCM_S24LE_PLANAR = new Encoders("pcm_s24le_planar");

	/**
	 * PCM signed 32-bit big-endian
	 */
	public static final Encoders PCM_S32BE = new Encoders("pcm_s32be");

	/**
	 * PCM signed 32-bit little-endian
	 */
	public static final Encoders PCM_S32LE = new Encoders("pcm_s32le");

	/**
	 * PCM signed 32-bit little-endian planar
	 */
	public static final Encoders PCM_S32LE_PLANAR = new Encoders("pcm_s32le_planar");

	/**
	 * PCM signed 8-bit
	 */
	public static final Encoders PCM_S8 = new Encoders("pcm_s8");

	/**
	 * PCM signed 8-bit planar
	 */
	public static final Encoders PCM_S8_PLANAR = new Encoders("pcm_s8_planar");

	/**
	 * PCM unsigned 16-bit big-endian
	 */
	public static final Encoders PCM_U16BE = new Encoders("pcm_u16be");

	/**
	 * PCM unsigned 16-bit little-endian
	 */
	public static final Encoders PCM_U16LE = new Encoders("pcm_u16le");

	/**
	 * PCM unsigned 24-bit big-endian
	 */
	public static final Encoders PCM_U24BE = new Encoders("pcm_u24be");

	/**
	 * PCM unsigned 24-bit little-endian
	 */
	public static final Encoders PCM_U24LE = new Encoders("pcm_u24le");

	/**
	 * PCM unsigned 32-bit big-endian
	 */
	public static final Encoders PCM_U32BE = new Encoders("pcm_u32be");

	/**
	 * PCM unsigned 32-bit little-endian
	 */
	public static final Encoders PCM_U32LE = new Encoders("pcm_u32le");

	/**
	 * PCM unsigned 8-bit
	 */
	public static final Encoders PCM_U8 = new Encoders("pcm_u8");

	/**
	 * PC Paintbrush PCX image
	 */
	public static final Encoders PCX = new Encoders("pcx");

	/**
	 * PGM (Portable GrayMap) image
	 */
	public static final Encoders PGM = new Encoders("pgm");

	/**
	 * PGMYUV (Portable GrayMap YUV) image
	 */
	public static final Encoders PGMYUV = new Encoders("pgmyuv");

	/**
	 * PNG (Portable Network Graphics) image
	 */
	public static final Encoders PNG = new Encoders("png");

	/**
	 * PPM (Portable PixelMap) image
	 */
	public static final Encoders PPM = new Encoders("ppm");

	/**
	 * Apple ProRes
	 */
	public static final Encoders PRORES = new Encoders("prores");

	/**
	 * Apple ProRes (codec prores)
	 */
	public static final Encoders PRORES_AW = new Encoders("prores_aw");

	/**
	 * Apple ProRes (iCodec Pro) (codec prores)
	 */
	public static final Encoders PRORES_KS = new Encoders("prores_ks");

	/**
	 * QuickTime Animation (RLE) video
	 */
	public static final Encoders QTRLE = new Encoders("qtrle");

	/**
	 * AJA Kona 10-bit RGB Codec
	 */
	public static final Encoders R10K = new Encoders("r10k");

	/**
	 * Uncompressed RGB 10-bit
	 */
	public static final Encoders R210 = new Encoders("r210");

	/**
	 * raw video
	 */
	public static final Encoders RAWVIDEO = new Encoders("rawvideo");

	/**
	 * RealAudio 1.0 (14.4K) (codec ra_144)
	 */
	public static final Encoders REAL_144 = new Encoders("real_144");

	/**
	 * id RoQ DPCM
	 */
	public static final Encoders ROQ_DPCM = new Encoders("roq_dpcm");

	/**
	 * id RoQ video (codec roq)
	 */
	public static final Encoders ROQVIDEO = new Encoders("roqvideo");

	/**
	 * RealVideo 1.0
	 */
	public static final Encoders RV10 = new Encoders("rv10");

	/**
	 * RealVideo 2.0
	 */
	public static final Encoders RV20 = new Encoders("rv20");

	/**
	 * SMPTE 302M
	 */
	public static final Encoders S302M = new Encoders("s302m");

	/**
	 * SGI image
	 */
	public static final Encoders SGI = new Encoders("sgi");

	/**
	 * Snow
	 */
	public static final Encoders SNOW = new Encoders("snow");

	/**
	 * Sonic
	 */
	public static final Encoders SONIC = new Encoders("sonic");

	/**
	 * Sonic lossless
	 */
	public static final Encoders SONICLS = new Encoders("sonicls");

	/**
	 * SubRip subtitle with embedded timing
	 */
	public static final Encoders SRT = new Encoders("srt");

	/**
	 * SSA (SubStation Alpha) subtitle
	 */
	public static final Encoders SSA = new Encoders("ssa");

	/**
	 * SubRip subtitle
	 */
	public static final Encoders SUBRIP = new Encoders("subrip");

	/**
	 * Sun Rasterfile image
	 */
	public static final Encoders SUNRAST = new Encoders("sunrast");

	/**
	 * Sorenson Vector Quantizer 1 / Sorenson Video 1 / SVQ1
	 */
	public static final Encoders SVQ1 = new Encoders("svq1");

	/**
	 * Truevision Targa image
	 */
	public static final Encoders TARGA = new Encoders("targa");

	/**
	 * TIFF image
	 */
	public static final Encoders TIFF = new Encoders("tiff");

	/**
	 * TTA (True Audio)
	 */
	public static final Encoders TTA = new Encoders("tta");

	/**
	 * Ut Video
	 */
	public static final Encoders UTVIDEO = new Encoders("utvideo");

	/**
	 * Uncompressed 4:2:2 10-bit
	 */
	public static final Encoders V210 = new Encoders("v210");

	/**
	 * Uncompressed packed 4:4:4
	 */
	public static final Encoders V308 = new Encoders("v308");

	/**
	 * Uncompressed packed QT 4:4:4:4
	 */
	public static final Encoders V408 = new Encoders("v408");

	/**
	 * Uncompressed 4:4:4 10-bit
	 */
	public static final Encoders V410 = new Encoders("v410");

	/**
	 * Vorbis
	 */
	public static final Encoders VORBIS = new Encoders("vorbis");

	/**
	 * WavPack
	 */
	public static final Encoders WAVPACK = new Encoders("wavpack");

	/**
	 * WebVTT subtitle
	 */
	public static final Encoders WEBVTT = new Encoders("webvtt");

	/**
	 * Windows Media Audio 1
	 */
	public static final Encoders WMAV1 = new Encoders("wmav1");

	/**
	 * Windows Media Audio 2
	 */
	public static final Encoders WMAV2 = new Encoders("wmav2");

	/**
	 * Windows Media Video 7
	 */
	public static final Encoders WMV1 = new Encoders("wmv1");

	/**
	 * Windows Media Video 8
	 */
	public static final Encoders WMV2 = new Encoders("wmv2");

	/**
	 * XBM (X BitMap) image
	 */
	public static final Encoders XBM = new Encoders("xbm");

	/**
	 * X-face image
	 */
	public static final Encoders XFACE = new Encoders("xface");

	/**
	 * DivX subtitles (XSUB)
	 */
	public static final Encoders XSUB = new Encoders("xsub");

	/**
	 * XWD (X Window Dump) image
	 */
	public static final Encoders XWD = new Encoders("xwd");

	/**
	 * Uncompressed YUV 4:1:1 12-bit
	 */
	public static final Encoders Y41P = new Encoders("y41p");

	/**
	 * Uncompressed packed 4:2:0
	 */
	public static final Encoders YUV4 = new Encoders("yuv4");

	/**
	 * LCL (LossLess Codec Library) ZLIB
	 */
	public static final Encoders ZLIB = new Encoders("zlib");

	/**
	 * Zip Motion Blocks Video
	 */
	public static final Encoders ZMBV = new Encoders("zmbv");

	/**
	 * @param name
	 */
	Encoders(String name) {
		super(name);
		HELP_CACHE.add(name, this, null);
	}

	/**
	 * @return
	 */
	public boolean exists() {
		return exists(getName());
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
	public static Encoders byName(String name) {
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
	public static List<Encoders> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @see org.fagu.fmv.ffmpeg.coder.Coders#cache()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <H extends CoderHelp> H cache() {
		return (H)HELP_CACHE.cache(getName()).get(0);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.coder.Coders#helpCache()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <H extends CoderHelp, R extends Coders> HelpCache<R, H> helpCache() {
		return (HelpCache<R, H>)HELP_CACHE;
	}

	// **************************************************

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-encoders");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<EncoderHelp> cacheConsumer = HELP_CACHE.consumer();
				Function<String, EncoderHelp> factory = name -> new EncoderHelp(name);

				AvailableHelp<EncoderHelp> availableHelp = AvailableHelp.create();
				availableHelp.title().legend().unreadLine().values(factory, cacheConsumer).parse(executor.execute().getResult());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}

		};
	}

	// ---------------------------------------------

	/**
	 * @return
	 */
	private static class EncoderHelp extends CoderHelp {

		/**
		 * @param name
		 */
		protected EncoderHelp(String name) {
			super(name);
		}
	}

}
