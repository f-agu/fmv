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
public class Decoders extends Coders {

	private static final HelpCache<Decoders, DecoderHelp> HELP_CACHE = new HelpCache<>(runnable(), Decoders::new);

	/**
	 * Uncompressed 4:2:2 10-bit
	 */
	public static final Decoders _012V = new Decoders("012v");

	/**
	 * 4X Movie
	 */
	public static final Decoders _4XM = new Decoders("4xm");

	/**
	 * QuickTime 8BPS video
	 */
	public static final Decoders _8BPS = new Decoders("8bps");

	/**
	 * 8SVX exponential
	 */
	public static final Decoders _8SVX_EXP = new Decoders("8svx_exp");

	/**
	 * 8SVX fibonacci
	 */
	public static final Decoders _8SVX_FIB = new Decoders("8svx_fib");

	/**
	 * AAC (Advanced Audio Coding)
	 */
	public static final Decoders AAC = new Decoders("aac");

	/**
	 * AAC LATM (Advanced Audio Coding LATM syntax)
	 */
	public static final Decoders AAC_LATM = new Decoders("aac_latm");

	/**
	 * Autodesk RLE
	 */
	public static final Decoders AASC = new Decoders("aasc");

	/**
	 * ATSC A/52A (AC-3)
	 */
	public static final Decoders AC3 = new Decoders("ac3");

	/**
	 * ATSC A/52A (AC-3) (codec ac3)
	 */
	public static final Decoders AC3_FIXED = new Decoders("ac3_fixed");

	/**
	 * ADPCM 4X Movie
	 */
	public static final Decoders ADPCM_4XM = new Decoders("adpcm_4xm");

	/**
	 * SEGA CRI ADX ADPCM
	 */
	public static final Decoders ADPCM_ADX = new Decoders("adpcm_adx");

	/**
	 * ADPCM Nintendo Gamecube AFC
	 */
	public static final Decoders ADPCM_AFC = new Decoders("adpcm_afc");

	/**
	 * ADPCM Creative Technology
	 */
	public static final Decoders ADPCM_CT = new Decoders("adpcm_ct");

	/**
	 * ADPCM Nintendo Gamecube DTK
	 */
	public static final Decoders ADPCM_DTK = new Decoders("adpcm_dtk");

	/**
	 * ADPCM Electronic Arts
	 */
	public static final Decoders ADPCM_EA = new Decoders("adpcm_ea");

	/**
	 * ADPCM Electronic Arts Maxis CDROM XA
	 */
	public static final Decoders ADPCM_EA_MAXIS_XA = new Decoders("adpcm_ea_maxis_xa");

	/**
	 * ADPCM Electronic Arts R1
	 */
	public static final Decoders ADPCM_EA_R1 = new Decoders("adpcm_ea_r1");

	/**
	 * ADPCM Electronic Arts R2
	 */
	public static final Decoders ADPCM_EA_R2 = new Decoders("adpcm_ea_r2");

	/**
	 * ADPCM Electronic Arts R3
	 */
	public static final Decoders ADPCM_EA_R3 = new Decoders("adpcm_ea_r3");

	/**
	 * ADPCM Electronic Arts XAS
	 */
	public static final Decoders ADPCM_EA_XAS = new Decoders("adpcm_ea_xas");

	/**
	 * ADPCM IMA AMV
	 */
	public static final Decoders ADPCM_IMA_AMV = new Decoders("adpcm_ima_amv");

	/**
	 * ADPCM IMA CRYO APC
	 */
	public static final Decoders ADPCM_IMA_APC = new Decoders("adpcm_ima_apc");

	/**
	 * ADPCM IMA Duck DK3
	 */
	public static final Decoders ADPCM_IMA_DK3 = new Decoders("adpcm_ima_dk3");

	/**
	 * ADPCM IMA Duck DK4
	 */
	public static final Decoders ADPCM_IMA_DK4 = new Decoders("adpcm_ima_dk4");

	/**
	 * ADPCM IMA Electronic Arts EACS
	 */
	public static final Decoders ADPCM_IMA_EA_EACS = new Decoders("adpcm_ima_ea_eacs");

	/**
	 * ADPCM IMA Electronic Arts SEAD
	 */
	public static final Decoders ADPCM_IMA_EA_SEAD = new Decoders("adpcm_ima_ea_sead");

	/**
	 * ADPCM IMA Funcom ISS
	 */
	public static final Decoders ADPCM_IMA_ISS = new Decoders("adpcm_ima_iss");

	/**
	 * ADPCM IMA Dialogic OKI
	 */
	public static final Decoders ADPCM_IMA_OKI = new Decoders("adpcm_ima_oki");

	/**
	 * ADPCM IMA QuickTime
	 */
	public static final Decoders ADPCM_IMA_QT = new Decoders("adpcm_ima_qt");

	/**
	 * ADPCM IMA Radical
	 */
	public static final Decoders ADPCM_IMA_RAD = new Decoders("adpcm_ima_rad");

	/**
	 * ADPCM IMA Loki SDL MJPEG
	 */
	public static final Decoders ADPCM_IMA_SMJPEG = new Decoders("adpcm_ima_smjpeg");

	/**
	 * ADPCM IMA WAV
	 */
	public static final Decoders ADPCM_IMA_WAV = new Decoders("adpcm_ima_wav");

	/**
	 * ADPCM IMA Westwood
	 */
	public static final Decoders ADPCM_IMA_WS = new Decoders("adpcm_ima_ws");

	/**
	 * ADPCM Microsoft
	 */
	public static final Decoders ADPCM_MS = new Decoders("adpcm_ms");

	/**
	 * ADPCM Sound Blaster Pro 2-bit
	 */
	public static final Decoders ADPCM_SBPRO_2 = new Decoders("adpcm_sbpro_2");

	/**
	 * ADPCM Sound Blaster Pro 2.6-bit
	 */
	public static final Decoders ADPCM_SBPRO_3 = new Decoders("adpcm_sbpro_3");

	/**
	 * ADPCM Sound Blaster Pro 4-bit
	 */
	public static final Decoders ADPCM_SBPRO_4 = new Decoders("adpcm_sbpro_4");

	/**
	 * ADPCM Shockwave Flash
	 */
	public static final Decoders ADPCM_SWF = new Decoders("adpcm_swf");

	/**
	 * ADPCM Nintendo Gamecube THP
	 */
	public static final Decoders ADPCM_THP = new Decoders("adpcm_thp");

	/**
	 * LucasArts VIMA audio, LucasArts VIMA audio (codec vima)
	 */
	public static final Decoders ADPCM_VIMA = new Decoders("adpcm_vima");

	/**
	 * ADPCM CDROM XA
	 */
	public static final Decoders ADPCM_XA = new Decoders("adpcm_xa");

	/**
	 * ADPCM Yamaha
	 */
	public static final Decoders ADPCM_YAMAHA = new Decoders("adpcm_yamaha");

	/**
	 * Apple Intermediate Codec
	 */
	public static final Decoders AIC = new Decoders("aic");

	/**
	 * ALAC (Apple Lossless Audio Codec)
	 */
	public static final Decoders ALAC = new Decoders("alac");

	/**
	 * Alias/Wavefront PIX image
	 */
	public static final Decoders ALIAS_PIX = new Decoders("alias_pix");

	/**
	 * MPEG-4 Audio Lossless Coding (ALS) (codec mp4als)
	 */
	public static final Decoders ALS = new Decoders("als");

	/**
	 * AMR-NB (Adaptive Multi-Rate NarrowBand) (codec amr_nb)
	 */
	public static final Decoders AMRNB = new Decoders("amrnb");

	/**
	 * AMR-WB (Adaptive Multi-Rate WideBand) (codec amr_wb)
	 */
	public static final Decoders AMRWB = new Decoders("amrwb");

	/**
	 * AMV Video
	 */
	public static final Decoders AMV = new Decoders("amv");

	/**
	 * Deluxe Paint Animation
	 */
	public static final Decoders ANM = new Decoders("anm");

	/**
	 * ASCII/ANSI art
	 */
	public static final Decoders ANSI = new Decoders("ansi");

	/**
	 * Monkey's Audio
	 */
	public static final Decoders APE = new Decoders("ape");

	/**
	 * ASS (Advanced SubStation Alpha) subtitle
	 */
	public static final Decoders ASS = new Decoders("ass");

	/**
	 * ASUS V1
	 */
	public static final Decoders ASV1 = new Decoders("asv1");

	/**
	 * ASUS V2
	 */
	public static final Decoders ASV2 = new Decoders("asv2");

	/**
	 * ATRAC1 (Adaptive TRansform Acoustic Coding)
	 */
	public static final Decoders ATRAC1 = new Decoders("atrac1");

	/**
	 * ATRAC3 (Adaptive TRansform Acoustic Coding 3)
	 */
	public static final Decoders ATRAC3 = new Decoders("atrac3");

	/**
	 * ATRAC3+ (Adaptive TRansform Acoustic Coding 3+) (codec atrac3p)
	 */
	public static final Decoders ATRAC3PLUS = new Decoders("atrac3plus");

	/**
	 * Auravision AURA
	 */
	public static final Decoders AURA = new Decoders("aura");

	/**
	 * Auravision Aura 2
	 */
	public static final Decoders AURA2 = new Decoders("aura2");

	/**
	 * Avid AVI Codec
	 */
	public static final Decoders AVRN = new Decoders("avrn");

	/**
	 * Avid 1:1 10-bit RGB Packer
	 */
	public static final Decoders AVRP = new Decoders("avrp");

	/**
	 * AVS (Audio Video Standard) video
	 */
	public static final Decoders AVS = new Decoders("avs");

	/**
	 * Avid Meridien Uncompressed
	 */
	public static final Decoders AVUI = new Decoders("avui");

	/**
	 * Uncompressed packed MS 4:4:4:4
	 */
	public static final Decoders AYUV = new Decoders("ayuv");

	/**
	 * Bethesda VID video
	 */
	public static final Decoders BETHSOFTVID = new Decoders("bethsoftvid");

	/**
	 * Brute Force & Ignorance
	 */
	public static final Decoders BFI = new Decoders("bfi");

	/**
	 * Bink Audio (DCT)
	 */
	public static final Decoders BINKAUDIO_DCT = new Decoders("binkaudio_dct");

	/**
	 * Bink Audio (RDFT)
	 */
	public static final Decoders BINKAUDIO_RDFT = new Decoders("binkaudio_rdft");

	/**
	 * Bink video
	 */
	public static final Decoders BINKVIDEO = new Decoders("binkvideo");

	/**
	 * Binary text
	 */
	public static final Decoders BINTEXT = new Decoders("bintext");

	/**
	 * BMP (Windows and OS/2 bitmap)
	 */
	public static final Decoders BMP = new Decoders("bmp");

	/**
	 * Discworld II BMV audio
	 */
	public static final Decoders BMV_AUDIO = new Decoders("bmv_audio");

	/**
	 * Discworld II BMV video
	 */
	public static final Decoders BMV_VIDEO = new Decoders("bmv_video");

	/**
	 * BRender PIX image
	 */
	public static final Decoders BRENDER_PIX = new Decoders("brender_pix");

	/**
	 * Interplay C93
	 */
	public static final Decoders C93 = new Decoders("c93");

	/**
	 * CamStudio (codec cscd)
	 */
	public static final Decoders CAMSTUDIO = new Decoders("camstudio");

	/**
	 * TechSmith Screen Capture Codec (codec tscc)
	 */
	public static final Decoders CAMTASIA = new Decoders("camtasia");

	/**
	 * Chinese AVS (Audio Video Standard) (AVS1-P2, JiZhun profile)
	 */
	public static final Decoders CAVS = new Decoders("cavs");

	/**
	 * CD Graphics video
	 */
	public static final Decoders CDGRAPHICS = new Decoders("cdgraphics");

	/**
	 * Commodore CDXL video
	 */
	public static final Decoders CDXL = new Decoders("cdxl");

	/**
	 * Cinepak
	 */
	public static final Decoders CINEPAK = new Decoders("cinepak");

	/**
	 * Cirrus Logic AccuPak
	 */
	public static final Decoders CLJR = new Decoders("cljr");

	/**
	 * Canopus Lossless Codec
	 */
	public static final Decoders CLLC = new Decoders("cllc");

	/**
	 * RFC 3389 comfort noise generator
	 */
	public static final Decoders COMFORTNOISE = new Decoders("comfortnoise");

	/**
	 * Cook / Cooker / Gecko (RealAudio G2)
	 */
	public static final Decoders COOK = new Decoders("cook");

	/**
	 * CPiA video format
	 */
	public static final Decoders CPIA = new Decoders("cpia");

	/**
	 * Creative YUV (CYUV)
	 */
	public static final Decoders CYUV = new Decoders("cyuv");

	/**
	 * DCA (DTS Coherent Acoustics) (codec dts)
	 */
	public static final Decoders DCA = new Decoders("dca");

	/**
	 * Chronomaster DFA
	 */
	public static final Decoders DFA = new Decoders("dfa");

	/**
	 * BBC Dirac VC-2
	 */
	public static final Decoders DIRAC = new Decoders("dirac");

	/**
	 * VC3/DNxHD
	 */
	public static final Decoders DNXHD = new Decoders("dnxhd");

	/**
	 * DPX (Digital Picture Exchange) image
	 */
	public static final Decoders DPX = new Decoders("dpx");

	/**
	 * DSD (Direct Stream Digital), least significant bit first
	 */
	public static final Decoders DSD_LSBF = new Decoders("dsd_lsbf");

	/**
	 * DSD (Direct Stream Digital), least significant bit first, planar
	 */
	public static final Decoders DSD_LSBF_PLANAR = new Decoders("dsd_lsbf_planar");

	/**
	 * DSD (Direct Stream Digital), most significant bit first
	 */
	public static final Decoders DSD_MSBF = new Decoders("dsd_msbf");

	/**
	 * DSD (Direct Stream Digital), most significant bit first, planar
	 */
	public static final Decoders DSD_MSBF_PLANAR = new Decoders("dsd_msbf_planar");

	/**
	 * Delphine Software International CIN audio
	 */
	public static final Decoders DSICINAUDIO = new Decoders("dsicinaudio");

	/**
	 * Delphine Software International CIN video
	 */
	public static final Decoders DSICINVIDEO = new Decoders("dsicinvideo");

	/**
	 * DVB subtitles (codec dvb_subtitle)
	 */
	public static final Decoders DVBSUB = new Decoders("dvbsub");

	/**
	 * DVD subtitles (codec dvd_subtitle)
	 */
	public static final Decoders DVDSUB = new Decoders("dvdsub");

	/**
	 * DV (Digital Video)
	 */
	public static final Decoders DVVIDEO = new Decoders("dvvideo");

	/**
	 * Feeble Files/ScummVM DXA
	 */
	public static final Decoders DXA = new Decoders("dxa");

	/**
	 * Dxtory
	 */
	public static final Decoders DXTORY = new Decoders("dxtory");

	/**
	 * ATSC A/52B (AC-3, E-AC-3)
	 */
	public static final Decoders EAC3 = new Decoders("eac3");

	/**
	 * Electronic Arts CMV video (codec cmv)
	 */
	public static final Decoders EACMV = new Decoders("eacmv");

	/**
	 * Electronic Arts Madcow Video (codec mad)
	 */
	public static final Decoders EAMAD = new Decoders("eamad");

	/**
	 * Electronic Arts TGQ video (codec tgq)
	 */
	public static final Decoders EATGQ = new Decoders("eatgq");

	/**
	 * Electronic Arts TGV video (codec tgv)
	 */
	public static final Decoders EATGV = new Decoders("eatgv");

	/**
	 * Electronic Arts TQI Video (codec tqi)
	 */
	public static final Decoders EATQI = new Decoders("eatqi");

	/**
	 * Escape 124
	 */
	public static final Decoders ESCAPE124 = new Decoders("escape124");

	/**
	 * Escape 130
	 */
	public static final Decoders ESCAPE130 = new Decoders("escape130");

	/**
	 * EVRC (Enhanced Variable Rate Codec)
	 */
	public static final Decoders EVRC = new Decoders("evrc");

	/**
	 * OpenEXR image
	 */
	public static final Decoders EXR = new Decoders("exr");

	/**
	 * FFmpeg video codec #1
	 */
	public static final Decoders FFV1 = new Decoders("ffv1");

	/**
	 * Huffyuv FFmpeg variant
	 */
	public static final Decoders FFVHUFF = new Decoders("ffvhuff");

	/**
	 * Mirillis FIC
	 */
	public static final Decoders FIC = new Decoders("fic");

	/**
	 * FLAC (Free Lossless Audio Codec)
	 */
	public static final Decoders FLAC = new Decoders("flac");

	/**
	 * Flash Screen Video v1
	 */
	public static final Decoders FLASHSV = new Decoders("flashsv");

	/**
	 * Flash Screen Video v2
	 */
	public static final Decoders FLASHSV2 = new Decoders("flashsv2");

	/**
	 * Autodesk Animator Flic video
	 */
	public static final Decoders FLIC = new Decoders("flic");

	/**
	 * FLV / Sorenson Spark / Sorenson H.263 (Flash Video) (codec flv1)
	 */
	public static final Decoders FLV = new Decoders("flv");

	/**
	 * Fraps
	 */
	public static final Decoders FRAPS = new Decoders("fraps");

	/**
	 * Forward Uncompressed
	 */
	public static final Decoders FRWU = new Decoders("frwu");

	/**
	 * Go2Meeting
	 */
	public static final Decoders G2M = new Decoders("g2m");

	/**
	 * G.722 ADPCM (codec adpcm_g722)
	 */
	public static final Decoders G722 = new Decoders("g722");

	/**
	 * G.723.1
	 */
	public static final Decoders G723_1 = new Decoders("g723_1");

	/**
	 * G.726 ADPCM (codec adpcm_g726)
	 */
	public static final Decoders G726 = new Decoders("g726");

	/**
	 * G.726 ADPCM little-endian (codec adpcm_g726le)
	 */
	public static final Decoders G726LE = new Decoders("g726le");

	/**
	 * G.729
	 */
	public static final Decoders G729 = new Decoders("g729");

	/**
	 * GIF (Graphics Interchange Format)
	 */
	public static final Decoders GIF = new Decoders("gif");

	/**
	 * GSM
	 */
	public static final Decoders GSM = new Decoders("gsm");

	/**
	 * GSM Microsoft variant
	 */
	public static final Decoders GSM_MS = new Decoders("gsm_ms");

	/**
	 * H.261
	 */
	public static final Decoders H261 = new Decoders("h261");

	/**
	 * H.263 / H.263-1996, H.263+ / H.263-1998 / H.263 version 2
	 */
	public static final Decoders H263 = new Decoders("h263");

	/**
	 * Intel H.263
	 */
	public static final Decoders H263I = new Decoders("h263i");

	/**
	 * H.263 / H.263-1996, H.263+ / H.263-1998 / H.263 version 2
	 */
	public static final Decoders H263P = new Decoders("h263p");

	/**
	 * H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10
	 */
	public static final Decoders H264 = new Decoders("h264");

	/**
	 * HEVC (High Efficiency Video Coding)
	 */
	public static final Decoders HEVC = new Decoders("hevc");

	/**
	 * HNM 4 video
	 */
	public static final Decoders HNM4VIDEO = new Decoders("hnm4video");

	/**
	 * Huffyuv / HuffYUV
	 */
	public static final Decoders HUFFYUV = new Decoders("huffyuv");

	/**
	 * IAC (Indeo Audio Coder)
	 */
	public static final Decoders IAC = new Decoders("iac");

	/**
	 * id Quake II CIN video (codec idcin)
	 */
	public static final Decoders IDCINVIDEO = new Decoders("idcinvideo");

	/**
	 * iCEDraw text
	 */
	public static final Decoders IDF = new Decoders("idf");

	/**
	 * IFF (codec iff_byterun1), IFF (codec iff_ilbm)
	 */
	public static final Decoders IFF = new Decoders("iff");

	/**
	 * IMC (Intel Music Coder)
	 */
	public static final Decoders IMC = new Decoders("imc");

	/**
	 * Intel Indeo 2
	 */
	public static final Decoders INDEO2 = new Decoders("indeo2");

	/**
	 * Intel Indeo 3
	 */
	public static final Decoders INDEO3 = new Decoders("indeo3");

	/**
	 * Intel Indeo Video Interactive 4
	 */
	public static final Decoders INDEO4 = new Decoders("indeo4");

	/**
	 * Intel Indeo Video Interactive 5
	 */
	public static final Decoders INDEO5 = new Decoders("indeo5");

	/**
	 * DPCM Interplay
	 */
	public static final Decoders INTERPLAY_DPCM = new Decoders("interplay_dpcm");

	/**
	 * Interplay MVE video
	 */
	public static final Decoders INTERPLAYVIDEO = new Decoders("interplayvideo");

	/**
	 * JACOsub subtitle
	 */
	public static final Decoders JACOSUB = new Decoders("jacosub");

	/**
	 * JPEG 2000
	 */
	public static final Decoders JPEG2000 = new Decoders("jpeg2000");

	/**
	 * JPEG-LS
	 */
	public static final Decoders JPEGLS = new Decoders("jpegls");

	/**
	 * Bitmap Brothers JV video
	 */
	public static final Decoders JV = new Decoders("jv");

	/**
	 * Kega Game Video
	 */
	public static final Decoders KGV1 = new Decoders("kgv1");

	/**
	 * Karl Morton's video codec
	 */
	public static final Decoders KMVC = new Decoders("kmvc");

	/**
	 * Lagarith lossless
	 */
	public static final Decoders LAGARITH = new Decoders("lagarith");

	/**
	 * libgsm GSM (codec gsm)
	 */
	public static final Decoders LIBGSM = new Decoders("libgsm");

	/**
	 * libgsm GSM Microsoft variant (codec gsm_ms)
	 */
	public static final Decoders LIBGSM_MS = new Decoders("libgsm_ms");

	/**
	 * iLBC (Internet Low Bitrate Codec) (codec ilbc)
	 */
	public static final Decoders LIBILBC = new Decoders("libilbc");

	/**
	 * OpenCORE AMR-NB (Adaptive Multi-Rate Narrow-Band) (codec amr_nb)
	 */
	public static final Decoders LIBOPENCORE_AMRNB = new Decoders("libopencore_amrnb");

	/**
	 * OpenCORE AMR-WB (Adaptive Multi-Rate Wide-Band) (codec amr_wb)
	 */
	public static final Decoders LIBOPENCORE_AMRWB = new Decoders("libopencore_amrwb");

	/**
	 * OpenJPEG JPEG 2000 (codec jpeg2000)
	 */
	public static final Decoders LIBOPENJPEG = new Decoders("libopenjpeg");

	/**
	 * libopus Opus (codec opus)
	 */
	public static final Decoders LIBOPUS = new Decoders("libopus");

	/**
	 * libschroedinger Dirac 2.2 (codec dirac)
	 */
	public static final Decoders LIBSCHROEDINGER = new Decoders("libschroedinger");

	/**
	 * libspeex Speex (codec speex)
	 */
	public static final Decoders LIBSPEEX = new Decoders("libspeex");

	/**
	 * libvorbis (codec vorbis)
	 */
	public static final Decoders LIBVORBIS = new Decoders("libvorbis");

	/**
	 * libvpx VP8 (codec vp8)
	 */
	public static final Decoders LIBVPX = new Decoders("libvpx");

	/**
	 * libvpx VP9 (codec vp9)
	 */
	public static final Decoders LIBVPX_VP9 = new Decoders("libvpx-vp9");

	/**
	 * LOCO
	 */
	public static final Decoders LOCO = new Decoders("loco");

	/**
	 * MACE (Macintosh Audio Compression/Expansion) 3:1
	 */
	public static final Decoders MACE3 = new Decoders("mace3");

	/**
	 * MACE (Macintosh Audio Compression/Expansion) 6:1
	 */
	public static final Decoders MACE6 = new Decoders("mace6");

	/**
	 * Sony PlayStation MDEC (Motion DECoder)
	 */
	public static final Decoders MDEC = new Decoders("mdec");

	/**
	 * Voxware MetaSound
	 */
	public static final Decoders METASOUND = new Decoders("metasound");

	/**
	 * MicroDVD subtitle
	 */
	public static final Decoders MICRODVD = new Decoders("microdvd");

	/**
	 * Mimic
	 */
	public static final Decoders MIMIC = new Decoders("mimic");

	/**
	 * MJPEG (Motion JPEG)
	 */
	public static final Decoders MJPEG = new Decoders("mjpeg");

	/**
	 * Apple MJPEG-B
	 */
	public static final Decoders MJPEGB = new Decoders("mjpegb");

	/**
	 * MLP (Meridian Lossless Packing)
	 */
	public static final Decoders MLP = new Decoders("mlp");

	/**
	 * American Laser Games MM Video
	 */
	public static final Decoders MMVIDEO = new Decoders("mmvideo");

	/**
	 * Motion Pixels video
	 */
	public static final Decoders MOTIONPIXELS = new Decoders("motionpixels");

	/**
	 * 3GPP Timed Text subtitle
	 */
	public static final Decoders MOV_TEXT = new Decoders("mov_text");

	/**
	 * MP1 (MPEG audio layer 1)
	 */
	public static final Decoders MP1 = new Decoders("mp1");

	/**
	 * MP1 (MPEG audio layer 1) (codec mp1)
	 */
	public static final Decoders MP1FLOAT = new Decoders("mp1float");

	/**
	 * MP2 (MPEG audio layer 2)
	 */
	public static final Decoders MP2 = new Decoders("mp2");

	/**
	 * MP2 (MPEG audio layer 2) (codec mp2)
	 */
	public static final Decoders MP2FLOAT = new Decoders("mp2float");

	/**
	 * MP3 (MPEG audio layer 3)
	 */
	public static final Decoders MP3 = new Decoders("mp3");

	/**
	 * ADU (Application Data Unit) MP3 (MPEG audio layer 3)
	 */
	public static final Decoders MP3ADU = new Decoders("mp3adu");

	/**
	 * ADU (Application Data Unit) MP3 (MPEG audio layer 3) (codec mp3adu)
	 */
	public static final Decoders MP3ADUFLOAT = new Decoders("mp3adufloat");

	/**
	 * MP3 (MPEG audio layer 3) (codec mp3)
	 */
	public static final Decoders MP3FLOAT = new Decoders("mp3float");

	/**
	 * MP3onMP4
	 */
	public static final Decoders MP3ON4 = new Decoders("mp3on4");

	/**
	 * MP3onMP4 (codec mp3on4)
	 */
	public static final Decoders MP3ON4FLOAT = new Decoders("mp3on4float");

	/**
	 * Musepack SV7 (codec musepack7)
	 */
	public static final Decoders MPC7 = new Decoders("mpc7");

	/**
	 * Musepack SV8 (codec musepack8)
	 */
	public static final Decoders MPC8 = new Decoders("mpc8");

	/**
	 * MPEG-1 video
	 */
	public static final Decoders MPEG1VIDEO = new Decoders("mpeg1video");

	/**
	 * MPEG-2 video
	 */
	public static final Decoders MPEG2VIDEO = new Decoders("mpeg2video");

	/**
	 * MPEG-4 part 2
	 */
	public static final Decoders MPEG4 = new Decoders("mpeg4");

	/**
	 * MPEG-1 video (codec mpeg2video)
	 */
	public static final Decoders MPEGVIDEO = new Decoders("mpegvideo");

	/**
	 * MPL2 subtitle
	 */
	public static final Decoders MPL2 = new Decoders("mpl2");

	/**
	 * MS ATC Screen
	 */
	public static final Decoders MSA1 = new Decoders("msa1");

	/**
	 * MPEG-4 part 2 Microsoft variant version 3 (codec msmpeg4v3)
	 */
	public static final Decoders MSMPEG4 = new Decoders("msmpeg4");

	/**
	 * MPEG-4 part 2 Microsoft variant version 1
	 */
	public static final Decoders MSMPEG4V1 = new Decoders("msmpeg4v1");

	/**
	 * MPEG-4 part 2 Microsoft variant version 2
	 */
	public static final Decoders MSMPEG4V2 = new Decoders("msmpeg4v2");

	/**
	 * Microsoft RLE
	 */
	public static final Decoders MSRLE = new Decoders("msrle");

	/**
	 * MS Screen 1
	 */
	public static final Decoders MSS1 = new Decoders("mss1");

	/**
	 * MS Windows Media Video V9 Screen
	 */
	public static final Decoders MSS2 = new Decoders("mss2");

	/**
	 * Microsoft Video 1
	 */
	public static final Decoders MSVIDEO1 = new Decoders("msvideo1");

	/**
	 * LCL (LossLess Codec Library) MSZH
	 */
	public static final Decoders MSZH = new Decoders("mszh");

	/**
	 * MS Expression Encoder Screen
	 */
	public static final Decoders MTS2 = new Decoders("mts2");

	/**
	 * Silicon Graphics Motion Video Compressor 1
	 */
	public static final Decoders MVC1 = new Decoders("mvc1");

	/**
	 * Silicon Graphics Motion Video Compressor 2
	 */
	public static final Decoders MVC2 = new Decoders("mvc2");

	/**
	 * Mobotix MxPEG video
	 */
	public static final Decoders MXPEG = new Decoders("mxpeg");

	/**
	 * Nellymoser Asao
	 */
	public static final Decoders NELLYMOSER = new Decoders("nellymoser");

	/**
	 * NuppelVideo/RTJPEG
	 */
	public static final Decoders NUV = new Decoders("nuv");

	/**
	 * On2 Audio for Video Codec (codec avc)
	 */
	public static final Decoders ON2AVC = new Decoders("on2avc");

	/**
	 * Opus
	 */
	public static final Decoders OPUS = new Decoders("opus");

	/**
	 * Amazing Studio Packed Animation File Audio
	 */
	public static final Decoders PAF_AUDIO = new Decoders("paf_audio");

	/**
	 * Amazing Studio Packed Animation File Video
	 */
	public static final Decoders PAF_VIDEO = new Decoders("paf_video");

	/**
	 * PAM (Portable AnyMap) image
	 */
	public static final Decoders PAM = new Decoders("pam");

	/**
	 * PBM (Portable BitMap) image
	 */
	public static final Decoders PBM = new Decoders("pbm");

	/**
	 * PCM A-law / G.711 A-law
	 */
	public static final Decoders PCM_ALAW = new Decoders("pcm_alaw");

	/**
	 * PCM signed 16|20|24-bit big-endian for Blu-ray media
	 */
	public static final Decoders PCM_BLURAY = new Decoders("pcm_bluray");

	/**
	 * PCM signed 16|20|24-bit big-endian for DVD media
	 */
	public static final Decoders PCM_DVD = new Decoders("pcm_dvd");

	/**
	 * PCM 32-bit floating point big-endian
	 */
	public static final Decoders PCM_F32BE = new Decoders("pcm_f32be");

	/**
	 * PCM 32-bit floating point little-endian
	 */
	public static final Decoders PCM_F32LE = new Decoders("pcm_f32le");

	/**
	 * PCM 64-bit floating point big-endian
	 */
	public static final Decoders PCM_F64BE = new Decoders("pcm_f64be");

	/**
	 * PCM 64-bit floating point little-endian
	 */
	public static final Decoders PCM_F64LE = new Decoders("pcm_f64le");

	/**
	 * PCM signed 20-bit little-endian planar
	 */
	public static final Decoders PCM_LXF = new Decoders("pcm_lxf");

	/**
	 * PCM mu-law / G.711 mu-law
	 */
	public static final Decoders PCM_MULAW = new Decoders("pcm_mulaw");

	/**
	 * PCM signed 16-bit big-endian
	 */
	public static final Decoders PCM_S16BE = new Decoders("pcm_s16be");

	/**
	 * PCM signed 16-bit big-endian planar
	 */
	public static final Decoders PCM_S16BE_PLANAR = new Decoders("pcm_s16be_planar");

	/**
	 * PCM signed 16-bit little-endian
	 */
	public static final Decoders PCM_S16LE = new Decoders("pcm_s16le");

	/**
	 * PCM signed 16-bit little-endian planar
	 */
	public static final Decoders PCM_S16LE_PLANAR = new Decoders("pcm_s16le_planar");

	/**
	 * PCM signed 24-bit big-endian
	 */
	public static final Decoders PCM_S24BE = new Decoders("pcm_s24be");

	/**
	 * PCM D-Cinema audio signed 24-bit
	 */
	public static final Decoders PCM_S24DAUD = new Decoders("pcm_s24daud");

	/**
	 * PCM signed 24-bit little-endian
	 */
	public static final Decoders PCM_S24LE = new Decoders("pcm_s24le");

	/**
	 * PCM signed 24-bit little-endian planar
	 */
	public static final Decoders PCM_S24LE_PLANAR = new Decoders("pcm_s24le_planar");

	/**
	 * PCM signed 32-bit big-endian
	 */
	public static final Decoders PCM_S32BE = new Decoders("pcm_s32be");

	/**
	 * PCM signed 32-bit little-endian
	 */
	public static final Decoders PCM_S32LE = new Decoders("pcm_s32le");

	/**
	 * PCM signed 32-bit little-endian planar
	 */
	public static final Decoders PCM_S32LE_PLANAR = new Decoders("pcm_s32le_planar");

	/**
	 * PCM signed 8-bit
	 */
	public static final Decoders PCM_S8 = new Decoders("pcm_s8");

	/**
	 * PCM signed 8-bit planar
	 */
	public static final Decoders PCM_S8_PLANAR = new Decoders("pcm_s8_planar");

	/**
	 * PCM unsigned 16-bit big-endian
	 */
	public static final Decoders PCM_U16BE = new Decoders("pcm_u16be");

	/**
	 * PCM unsigned 16-bit little-endian
	 */
	public static final Decoders PCM_U16LE = new Decoders("pcm_u16le");

	/**
	 * PCM unsigned 24-bit big-endian
	 */
	public static final Decoders PCM_U24BE = new Decoders("pcm_u24be");

	/**
	 * PCM unsigned 24-bit little-endian
	 */
	public static final Decoders PCM_U24LE = new Decoders("pcm_u24le");

	/**
	 * PCM unsigned 32-bit big-endian
	 */
	public static final Decoders PCM_U32BE = new Decoders("pcm_u32be");

	/**
	 * PCM unsigned 32-bit little-endian
	 */
	public static final Decoders PCM_U32LE = new Decoders("pcm_u32le");

	/**
	 * PCM unsigned 8-bit
	 */
	public static final Decoders PCM_U8 = new Decoders("pcm_u8");

	/**
	 * PCM Zork
	 */
	public static final Decoders PCM_ZORK = new Decoders("pcm_zork");

	/**
	 * PC Paintbrush PCX image
	 */
	public static final Decoders PCX = new Decoders("pcx");

	/**
	 * PGM (Portable GrayMap) image
	 */
	public static final Decoders PGM = new Decoders("pgm");

	/**
	 * PGMYUV (Portable GrayMap YUV) image
	 */
	public static final Decoders PGMYUV = new Decoders("pgmyuv");

	/**
	 * HDMV Presentation Graphic Stream subtitles (codec hdmv_pgs_subtitle)
	 */
	public static final Decoders PGSSUB = new Decoders("pgssub");

	/**
	 * Pictor/PC Paint
	 */
	public static final Decoders PICTOR = new Decoders("pictor");

	/**
	 * PJS subtitle
	 */
	public static final Decoders PJS = new Decoders("pjs");

	/**
	 * PNG (Portable Network Graphics) image
	 */
	public static final Decoders PNG = new Decoders("png");

	/**
	 * PPM (Portable PixelMap) image
	 */
	public static final Decoders PPM = new Decoders("ppm");

	/**
	 * ProRes
	 */
	public static final Decoders PRORES = new Decoders("prores");

	/**
	 * Apple ProRes (iCodec Pro) (codec prores)
	 */
	public static final Decoders PRORES_LGPL = new Decoders("prores_lgpl");

	/**
	 * V.Flash PTX image
	 */
	public static final Decoders PTX = new Decoders("ptx");

	/**
	 * QCELP / PureVoice
	 */
	public static final Decoders QCELP = new Decoders("qcelp");

	/**
	 * QDesign Music Codec 2
	 */
	public static final Decoders QDM2 = new Decoders("qdm2");

	/**
	 * Apple QuickDraw
	 */
	public static final Decoders QDRAW = new Decoders("qdraw");

	/**
	 * Q-team QPEG
	 */
	public static final Decoders QPEG = new Decoders("qpeg");

	/**
	 * QuickTime Animation (RLE) video
	 */
	public static final Decoders QTRLE = new Decoders("qtrle");

	/**
	 * AJA Kona 10-bit RGB Codec
	 */
	public static final Decoders R10K = new Decoders("r10k");

	/**
	 * Uncompressed RGB 10-bit
	 */
	public static final Decoders R210 = new Decoders("r210");

	/**
	 * RealAudio Lossless
	 */
	public static final Decoders RALF = new Decoders("ralf");

	/**
	 * raw video
	 */
	public static final Decoders RAWVIDEO = new Decoders("rawvideo");

	/**
	 * RealAudio 1.0 (14.4K) (codec ra_144)
	 */
	public static final Decoders REAL_144 = new Decoders("real_144");

	/**
	 * RealAudio 2.0 (28.8K) (codec ra_288)
	 */
	public static final Decoders REAL_288 = new Decoders("real_288");

	/**
	 * RealText subtitle
	 */
	public static final Decoders REALTEXT = new Decoders("realtext");

	/**
	 * RL2 video
	 */
	public static final Decoders RL2 = new Decoders("rl2");

	/**
	 * DPCM id RoQ
	 */
	public static final Decoders ROQ_DPCM = new Decoders("roq_dpcm");

	/**
	 * id RoQ video (codec roq)
	 */
	public static final Decoders ROQVIDEO = new Decoders("roqvideo");

	/**
	 * QuickTime video (RPZA)
	 */
	public static final Decoders RPZA = new Decoders("rpza");

	/**
	 * RealVideo 1.0
	 */
	public static final Decoders RV10 = new Decoders("rv10");

	/**
	 * RealVideo 2.0
	 */
	public static final Decoders RV20 = new Decoders("rv20");

	/**
	 * RealVideo 3.0
	 */
	public static final Decoders RV30 = new Decoders("rv30");

	/**
	 * RealVideo 4.0
	 */
	public static final Decoders RV40 = new Decoders("rv40");

	/**
	 * SMPTE 302M
	 */
	public static final Decoders S302M = new Decoders("s302m");

	/**
	 * SAMI subtitle
	 */
	public static final Decoders SAMI = new Decoders("sami");

	/**
	 * LucasArts SANM/Smush video
	 */
	public static final Decoders SANM = new Decoders("sanm");

	/**
	 * SGI image
	 */
	public static final Decoders SGI = new Decoders("sgi");

	/**
	 * Silicon Graphics RLE 8-bit video
	 */
	public static final Decoders SGIRLE = new Decoders("sgirle");

	/**
	 * Shorten
	 */
	public static final Decoders SHORTEN = new Decoders("shorten");

	/**
	 * RealAudio SIPR / ACELP.NET
	 */
	public static final Decoders SIPR = new Decoders("sipr");

	/**
	 * Smacker audio (codec smackaudio)
	 */
	public static final Decoders SMACKAUD = new Decoders("smackaud");

	/**
	 * Smacker video (codec smackvideo)
	 */
	public static final Decoders SMACKVID = new Decoders("smackvid");

	/**
	 * QuickTime Graphics (SMC)
	 */
	public static final Decoders SMC = new Decoders("smc");

	/**
	 * SMV JPEG (codec smv)
	 */
	public static final Decoders SMVJPEG = new Decoders("smvjpeg");

	/**
	 * Snow
	 */
	public static final Decoders SNOW = new Decoders("snow");

	/**
	 * DPCM Sol
	 */
	public static final Decoders SOL_DPCM = new Decoders("sol_dpcm");

	/**
	 * Sonic
	 */
	public static final Decoders SONIC = new Decoders("sonic");

	/**
	 * Sunplus JPEG (SP5X)
	 */
	public static final Decoders SP5X = new Decoders("sp5x");

	/**
	 * SubRip subtitle (codec subrip)
	 */
	public static final Decoders SRT = new Decoders("srt");

	/**
	 * SSA (SubStation Alpha) subtitle
	 */
	public static final Decoders SSA = new Decoders("ssa");

	/**
	 * SubRip subtitle
	 */
	public static final Decoders SUBRIP = new Decoders("subrip");

	/**
	 * SubViewer subtitle
	 */
	public static final Decoders SUBVIEWER = new Decoders("subviewer");

	/**
	 * SubViewer1 subtitle
	 */
	public static final Decoders SUBVIEWER1 = new Decoders("subviewer1");

	/**
	 * Sun Rasterfile image
	 */
	public static final Decoders SUNRAST = new Decoders("sunrast");

	/**
	 * Sorenson Vector Quantizer 1 / Sorenson Video 1 / SVQ1
	 */
	public static final Decoders SVQ1 = new Decoders("svq1");

	/**
	 * Sorenson Vector Quantizer 3 / Sorenson Video 3 / SVQ3
	 */
	public static final Decoders SVQ3 = new Decoders("svq3");

	/**
	 * TAK (Tom's lossless Audio Kompressor)
	 */
	public static final Decoders TAK = new Decoders("tak");

	/**
	 * Truevision Targa image
	 */
	public static final Decoders TARGA = new Decoders("targa");

	/**
	 * Pinnacle TARGA CineWave YUV16
	 */
	public static final Decoders TARGA_Y216 = new Decoders("targa_y216");

	/**
	 * Raw text subtitle
	 */
	public static final Decoders TEXT = new Decoders("text");

	/**
	 * Theora
	 */
	public static final Decoders THEORA = new Decoders("theora");

	/**
	 * Nintendo Gamecube THP video
	 */
	public static final Decoders THP = new Decoders("thp");

	/**
	 * Tiertex Limited SEQ video
	 */
	public static final Decoders TIERTEXSEQVIDEO = new Decoders("tiertexseqvideo");

	/**
	 * TIFF image
	 */
	public static final Decoders TIFF = new Decoders("tiff");

	/**
	 * 8088flex TMV
	 */
	public static final Decoders TMV = new Decoders("tmv");

	/**
	 * TrueHD
	 */
	public static final Decoders TRUEHD = new Decoders("truehd");

	/**
	 * Duck TrueMotion 1.0
	 */
	public static final Decoders TRUEMOTION1 = new Decoders("truemotion1");

	/**
	 * Duck TrueMotion 2.0
	 */
	public static final Decoders TRUEMOTION2 = new Decoders("truemotion2");

	/**
	 * DSP Group TrueSpeech
	 */
	public static final Decoders TRUESPEECH = new Decoders("truespeech");

	/**
	 * TechSmith Screen Codec 2
	 */
	public static final Decoders TSCC2 = new Decoders("tscc2");

	/**
	 * TTA (True Audio)
	 */
	public static final Decoders TTA = new Decoders("tta");

	/**
	 * VQF TwinVQ
	 */
	public static final Decoders TWINVQ = new Decoders("twinvq");

	/**
	 * Renderware TXD (TeXture Dictionary) image
	 */
	public static final Decoders TXD = new Decoders("txd");

	/**
	 * IBM UltiMotion (codec ulti)
	 */
	public static final Decoders ULTIMOTION = new Decoders("ultimotion");

	/**
	 * Ut Video
	 */
	public static final Decoders UTVIDEO = new Decoders("utvideo");

	/**
	 * Uncompressed 4:2:2 10-bit
	 */
	public static final Decoders V210 = new Decoders("v210");

	/**
	 * Uncompressed 4:2:2 10-bit
	 */
	public static final Decoders V210X = new Decoders("v210x");

	/**
	 * Uncompressed packed 4:4:4
	 */
	public static final Decoders V308 = new Decoders("v308");

	/**
	 * Uncompressed packed QT 4:4:4:4
	 */
	public static final Decoders V408 = new Decoders("v408");

	/**
	 * Uncompressed 4:4:4 10-bit
	 */
	public static final Decoders V410 = new Decoders("v410");

	/**
	 * Beam Software VB
	 */
	public static final Decoders VB = new Decoders("vb");

	/**
	 * VBLE Lossless Codec
	 */
	public static final Decoders VBLE = new Decoders("vble");

	/**
	 * SMPTE VC-1
	 */
	public static final Decoders VC1 = new Decoders("vc1");

	/**
	 * Windows Media Video 9 Image v2
	 */
	public static final Decoders VC1IMAGE = new Decoders("vc1image");

	/**
	 * ATI VCR1
	 */
	public static final Decoders VCR1 = new Decoders("vcr1");

	/**
	 * LucasArts VIMA audio (codec adpcm_vima), LucasArts VIMA audio
	 */
	public static final Decoders VIMA = new Decoders("vima");

	/**
	 * Sierra VMD audio
	 */
	public static final Decoders VMDAUDIO = new Decoders("vmdaudio");

	/**
	 * Sierra VMD video
	 */
	public static final Decoders VMDVIDEO = new Decoders("vmdvideo");

	/**
	 * VMware Screen Codec / VMware Video
	 */
	public static final Decoders VMNC = new Decoders("vmnc");

	/**
	 * Vorbis
	 */
	public static final Decoders VORBIS = new Decoders("vorbis");

	/**
	 * On2 VP3
	 */
	public static final Decoders VP3 = new Decoders("vp3");

	/**
	 * On2 VP5
	 */
	public static final Decoders VP5 = new Decoders("vp5");

	/**
	 * On2 VP6
	 */
	public static final Decoders VP6 = new Decoders("vp6");

	/**
	 * On2 VP6 (Flash version, with alpha channel)
	 */
	public static final Decoders VP6A = new Decoders("vp6a");

	/**
	 * On2 VP6 (Flash version)
	 */
	public static final Decoders VP6F = new Decoders("vp6f");

	/**
	 * On2 VP7
	 */
	public static final Decoders VP7 = new Decoders("vp7");

	/**
	 * On2 VP8
	 */
	public static final Decoders VP8 = new Decoders("vp8");

	/**
	 * Google VP9
	 */
	public static final Decoders VP9 = new Decoders("vp9");

	/**
	 * VPlayer subtitle
	 */
	public static final Decoders VPLAYER = new Decoders("vplayer");

	/**
	 * Westwood Studios VQA (Vector Quantized Animation) video (codec ws_vqa)
	 */
	public static final Decoders VQAVIDEO = new Decoders("vqavideo");

	/**
	 * Wave synthesis pseudo-codec
	 */
	public static final Decoders WAVESYNTH = new Decoders("wavesynth");

	/**
	 * WavPack
	 */
	public static final Decoders WAVPACK = new Decoders("wavpack");

	/**
	 * WebP image
	 */
	public static final Decoders WEBP = new Decoders("webp");

	/**
	 * WebVTT subtitle
	 */
	public static final Decoders WEBVTT = new Decoders("webvtt");

	/**
	 * Windows Media Audio Lossless
	 */
	public static final Decoders WMALOSSLESS = new Decoders("wmalossless");

	/**
	 * Windows Media Audio 9 Professional
	 */
	public static final Decoders WMAPRO = new Decoders("wmapro");

	/**
	 * Windows Media Audio 1
	 */
	public static final Decoders WMAV1 = new Decoders("wmav1");

	/**
	 * Windows Media Audio 2
	 */
	public static final Decoders WMAV2 = new Decoders("wmav2");

	/**
	 * Windows Media Audio Voice
	 */
	public static final Decoders WMAVOICE = new Decoders("wmavoice");

	/**
	 * Windows Media Video 7
	 */
	public static final Decoders WMV1 = new Decoders("wmv1");

	/**
	 * Windows Media Video 8
	 */
	public static final Decoders WMV2 = new Decoders("wmv2");

	/**
	 * Windows Media Video 9
	 */
	public static final Decoders WMV3 = new Decoders("wmv3");

	/**
	 * Windows Media Video 9 Image
	 */
	public static final Decoders WMV3IMAGE = new Decoders("wmv3image");

	/**
	 * Winnov WNV1
	 */
	public static final Decoders WNV1 = new Decoders("wnv1");

	/**
	 * Westwood Audio (SND1) (codec westwood_snd1)
	 */
	public static final Decoders WS_SND1 = new Decoders("ws_snd1");

	/**
	 * DPCM Xan
	 */
	public static final Decoders XAN_DPCM = new Decoders("xan_dpcm");

	/**
	 * Wing Commander III / Xan
	 */
	public static final Decoders XAN_WC3 = new Decoders("xan_wc3");

	/**
	 * Wing Commander IV / Xxan
	 */
	public static final Decoders XAN_WC4 = new Decoders("xan_wc4");

	/**
	 * eXtended BINary text
	 */
	public static final Decoders XBIN = new Decoders("xbin");

	/**
	 * XBM (X BitMap) image
	 */
	public static final Decoders XBM = new Decoders("xbm");

	/**
	 * X-face image
	 */
	public static final Decoders XFACE = new Decoders("xface");

	/**
	 * Miro VideoXL (codec vixl)
	 */
	public static final Decoders XL = new Decoders("xl");

	/**
	 * XSUB
	 */
	public static final Decoders XSUB = new Decoders("xsub");

	/**
	 * XWD (X Window Dump) image
	 */
	public static final Decoders XWD = new Decoders("xwd");

	/**
	 * Uncompressed YUV 4:1:1 12-bit
	 */
	public static final Decoders Y41P = new Decoders("y41p");

	/**
	 * Psygnosis YOP Video
	 */
	public static final Decoders YOP = new Decoders("yop");

	/**
	 * Uncompressed packed 4:2:0
	 */
	public static final Decoders YUV4 = new Decoders("yuv4");

	/**
	 * ZeroCodec Lossless Video
	 */
	public static final Decoders ZEROCODEC = new Decoders("zerocodec");

	/**
	 * LCL (LossLess Codec Library) ZLIB
	 */
	public static final Decoders ZLIB = new Decoders("zlib");

	/**
	 * Zip Motion Blocks Video
	 */
	public static final Decoders ZMBV = new Decoders("zmbv");

	/**
	 * @param name
	 */
	Decoders(String name) {
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
	public static Decoders byName(String name) {
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
	public static List<Decoders> available() {
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
			operation.addParameter("-decoders");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<DecoderHelp> cacheConsumer = HELP_CACHE.consumer();
				Function<String, DecoderHelp> factory = name -> new DecoderHelp(name);

				AvailableHelp<DecoderHelp> availableHelp = AvailableHelp.create();
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
	private static class DecoderHelp extends CoderHelp {

		/**
		 * @param name
		 */
		protected DecoderHelp(String name) {
			super(name);
		}
	}

}
