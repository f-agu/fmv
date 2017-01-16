package org.fagu.fmv.ffmpeg.format;

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
import java.util.stream.Collectors;

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;
import org.fagu.fmv.ffmpeg.utils.AvailableHelp;
import org.fagu.fmv.ffmpeg.utils.Help;
import org.fagu.fmv.ffmpeg.utils.HelpCache;


/**
 * @author f.agu
 */
public class Formats {

	/**
	 * 
	 */
	private static final HelpCache<Formats, FormatsHelp> HELP_CACHE = new HelpCache<Formats, FormatsHelp>(runnable(), Formats::new);

	/**
	 * 3GP2 (3GPP2 file format), QuickTime / MOV
	 */
	public static final Formats _3G2 = new Formats("3g2");

	/**
	 * 3GP (3GPP file format), QuickTime / MOV
	 */
	public static final Formats _3GP = new Formats("3gp");

	/**
	 * 4X Technologies
	 */
	public static final Formats _4XM = new Formats("4xm");

	/**
	 * a64 - video for Commodore 64
	 */
	public static final Formats A64 = new Formats("a64");

	/**
	 * raw ADTS AAC (Advanced Audio Coding)
	 */
	public static final Formats AAC = new Formats("aac");

	/**
	 * raw AC-3
	 */
	public static final Formats AC3 = new Formats("ac3");

	/**
	 * ACT Voice file format
	 */
	public static final Formats ACT = new Formats("act");

	/**
	 * Artworx Data Format
	 */
	public static final Formats ADF = new Formats("adf");

	/**
	 * ADP
	 */
	public static final Formats ADP = new Formats("adp");

	/**
	 * ADTS AAC (Advanced Audio Coding)
	 */
	public static final Formats ADTS = new Formats("adts");

	/**
	 * CRI ADX
	 */
	public static final Formats ADX = new Formats("adx");

	/**
	 * MD STUDIO audio
	 */
	public static final Formats AEA = new Formats("aea");

	/**
	 * AFC
	 */
	public static final Formats AFC = new Formats("afc");

	/**
	 * Audio IFF
	 */
	public static final Formats AIFF = new Formats("aiff");

	/**
	 * PCM A-law
	 */
	public static final Formats ALAW = new Formats("alaw");

	/**
	 * Alias/Wavefront PIX image
	 */
	public static final Formats ALIAS_PIX = new Formats("alias_pix");

	/**
	 * 3GPP AMR
	 */
	public static final Formats AMR = new Formats("amr");

	/**
	 * Deluxe Paint Animation
	 */
	public static final Formats ANM = new Formats("anm");

	/**
	 * CRYO APC
	 */
	public static final Formats APC = new Formats("apc");

	/**
	 * Monkey's Audio
	 */
	public static final Formats APE = new Formats("ape");

	/**
	 * Apple HTTP Live Streaming
	 */
	public static final Formats APPLEHTTP = new Formats("applehttp");

	/**
	 * AQTitle subtitles
	 */
	public static final Formats AQTITLE = new Formats("aqtitle");

	/**
	 * ASF (Advanced / Active Streaming Format)
	 */
	public static final Formats ASF = new Formats("asf");

	/**
	 * ASF (Advanced / Active Streaming Format)
	 */
	public static final Formats ASF_STREAM = new Formats("asf_stream");

	/**
	 * SSA (SubStation Alpha) subtitle
	 */
	public static final Formats ASS = new Formats("ass");

	/**
	 * AST (Audio Stream)
	 */
	public static final Formats AST = new Formats("ast");

	/**
	 * Sun AU
	 */
	public static final Formats AU = new Formats("au");

	/**
	 * AVI (Audio Video Interleaved)
	 */
	public static final Formats AVI = new Formats("avi");

	/**
	 * AviSynth script
	 */
	public static final Formats AVISYNTH = new Formats("avisynth");

	/**
	 * SWF (ShockWave Flash) (AVM2)
	 */
	public static final Formats AVM2 = new Formats("avm2");

	/**
	 * AVR (Audio Visual Research)
	 */
	public static final Formats AVR = new Formats("avr");

	/**
	 * AVS
	 */
	public static final Formats AVS = new Formats("avs");

	/**
	 * Bethesda Softworks VID
	 */
	public static final Formats BETHSOFTVID = new Formats("bethsoftvid");

	/**
	 * Brute Force & Ignorance
	 */
	public static final Formats BFI = new Formats("bfi");

	/**
	 * Binary text
	 */
	public static final Formats BIN = new Formats("bin");

	/**
	 * Bink
	 */
	public static final Formats BINK = new Formats("bink");

	/**
	 * G.729 BIT file format
	 */
	public static final Formats BIT = new Formats("bit");

	/**
	 * piped bmp sequence
	 */
	public static final Formats BMP_PIPE = new Formats("bmp_pipe");

	/**
	 * Discworld II BMV
	 */
	public static final Formats BMV = new Formats("bmv");

	/**
	 * Black Ops Audio
	 */
	public static final Formats BOA = new Formats("boa");

	/**
	 * BRender PIX image
	 */
	public static final Formats BRENDER_PIX = new Formats("brender_pix");

	/**
	 * BRSTM (Binary Revolution Stream)
	 */
	public static final Formats BRSTM = new Formats("brstm");

	/**
	 * Interplay C93
	 */
	public static final Formats C93 = new Formats("c93");

	/**
	 * caca (color ASCII art) output device
	 */
	public static final Formats CACA = new Formats("caca");

	/**
	 * Apple CAF (Core Audio Format)
	 */
	public static final Formats CAF = new Formats("caf");

	/**
	 * raw Chinese AVS (Audio Video Standard) video
	 */
	public static final Formats CAVSVIDEO = new Formats("cavsvideo");

	/**
	 * CD Graphics
	 */
	public static final Formats CDG = new Formats("cdg");

	/**
	 * Commodore CDXL video
	 */
	public static final Formats CDXL = new Formats("cdxl");

	/**
	 * Phantom Cine
	 */
	public static final Formats CINE = new Formats("cine");

	/**
	 * Virtual concatenation script
	 */
	public static final Formats CONCAT = new Formats("concat");

	/**
	 * CRC testing
	 */
	public static final Formats CRC = new Formats("crc");

	/**
	 * raw data
	 */
	public static final Formats DATA = new Formats("data");

	/**
	 * D-Cinema audio
	 */
	public static final Formats DAUD = new Formats("daud");

	/**
	 * Chronomaster DFA
	 */
	public static final Formats DFA = new Formats("dfa");

	/**
	 * raw Dirac
	 */
	public static final Formats DIRAC = new Formats("dirac");

	/**
	 * raw DNxHD (SMPTE VC-3)
	 */
	public static final Formats DNXHD = new Formats("dnxhd");

	/**
	 * piped dpx sequence
	 */
	public static final Formats DPX_PIPE = new Formats("dpx_pipe");

	/**
	 * DSD Stream File (DSF)
	 */
	public static final Formats DSF = new Formats("dsf");

	/**
	 * DirectShow capture
	 */
	public static final Formats DSHOW = new Formats("dshow");

	/**
	 * Delphine Software International CIN
	 */
	public static final Formats DSICIN = new Formats("dsicin");

	/**
	 * raw DTS
	 */
	public static final Formats DTS = new Formats("dts");

	/**
	 * raw DTS-HD
	 */
	public static final Formats DTSHD = new Formats("dtshd");

	/**
	 * DV (Digital Video)
	 */
	public static final Formats DV = new Formats("dv");

	/**
	 * MPEG-2 PS (DVD VOB)
	 */
	public static final Formats DVD = new Formats("dvd");

	/**
	 * DXA
	 */
	public static final Formats DXA = new Formats("dxa");

	/**
	 * Electronic Arts Multimedia
	 */
	public static final Formats EA = new Formats("ea");

	/**
	 * Electronic Arts cdata
	 */
	public static final Formats EA_CDATA = new Formats("ea_cdata");

	/**
	 * raw E-AC-3
	 */
	public static final Formats EAC3 = new Formats("eac3");

	/**
	 * Ensoniq Paris Audio File
	 */
	public static final Formats EPAF = new Formats("epaf");

	/**
	 * piped exr sequence
	 */
	public static final Formats EXR_PIPE = new Formats("exr_pipe");

	/**
	 * PCM 32-bit floating-point big-endian
	 */
	public static final Formats F32BE = new Formats("f32be");

	/**
	 * PCM 32-bit floating-point little-endian
	 */
	public static final Formats F32LE = new Formats("f32le");

	/**
	 * F4V Adobe Flash Video
	 */
	public static final Formats F4V = new Formats("f4v");

	/**
	 * PCM 64-bit floating-point big-endian
	 */
	public static final Formats F64BE = new Formats("f64be");

	/**
	 * PCM 64-bit floating-point little-endian
	 */
	public static final Formats F64LE = new Formats("f64le");

	/**
	 * FFM (FFserver live feed)
	 */
	public static final Formats FFM = new Formats("ffm");

	/**
	 * FFmpeg metadata in text
	 */
	public static final Formats FFMETADATA = new Formats("ffmetadata");

	/**
	 * Sega FILM / CPK
	 */
	public static final Formats FILM_CPK = new Formats("film_cpk");

	/**
	 * Adobe Filmstrip
	 */
	public static final Formats FILMSTRIP = new Formats("filmstrip");

	/**
	 * raw FLAC
	 */
	public static final Formats FLAC = new Formats("flac");

	/**
	 * FLI/FLC/FLX animation
	 */
	public static final Formats FLIC = new Formats("flic");

	/**
	 * FLV (Flash Video)
	 */
	public static final Formats FLV = new Formats("flv");

	/**
	 * framecrc testing
	 */
	public static final Formats FRAMECRC = new Formats("framecrc");

	/**
	 * Per-frame MD5 testing
	 */
	public static final Formats FRAMEMD5 = new Formats("framemd5");

	/**
	 * Megalux Frame
	 */
	public static final Formats FRM = new Formats("frm");

	/**
	 * raw G.722
	 */
	public static final Formats G722 = new Formats("g722");

	/**
	 * raw G.723.1
	 */
	public static final Formats G723_1 = new Formats("g723_1");

	/**
	 * G.729 raw format demuxer
	 */
	public static final Formats G729 = new Formats("g729");

	/**
	 * GDI API Windows frame grabber
	 */
	public static final Formats GDIGRAB = new Formats("gdigrab");

	/**
	 * GIF Animation
	 */
	public static final Formats GIF = new Formats("gif");

	/**
	 * raw GSM
	 */
	public static final Formats GSM = new Formats("gsm");

	/**
	 * GXF (General eXchange Format)
	 */
	public static final Formats GXF = new Formats("gxf");

	/**
	 * raw H.261
	 */
	public static final Formats H261 = new Formats("h261");

	/**
	 * raw H.263
	 */
	public static final Formats H263 = new Formats("h263");

	/**
	 * raw H.264 video
	 */
	public static final Formats H264 = new Formats("h264");

	/**
	 * HDS Muxer
	 */
	public static final Formats HDS = new Formats("hds");

	/**
	 * raw HEVC video
	 */
	public static final Formats HEVC = new Formats("hevc");

	/**
	 * Apple HTTP Live Streaming, Apple HTTP Live Streaming
	 */
	public static final Formats HLS = new Formats("hls");

	/**
	 * Cryo HNM v4
	 */
	public static final Formats HNM = new Formats("hnm");

	/**
	 * Microsoft Windows ICO
	 */
	public static final Formats ICO = new Formats("ico");

	/**
	 * id Cinematic
	 */
	public static final Formats IDCIN = new Formats("idcin");

	/**
	 * iCE Draw File
	 */
	public static final Formats IDF = new Formats("idf");

	/**
	 * IFF (Interchange File Format)
	 */
	public static final Formats IFF = new Formats("iff");

	/**
	 * iLBC storage
	 */
	public static final Formats ILBC = new Formats("ilbc");

	/**
	 * image2 sequence
	 */
	public static final Formats IMAGE2 = new Formats("image2");

	/**
	 * piped image2 sequence
	 */
	public static final Formats IMAGE2PIPE = new Formats("image2pipe");

	/**
	 * raw Ingenient MJPEG
	 */
	public static final Formats INGENIENT = new Formats("ingenient");

	/**
	 * Interplay MVE
	 */
	public static final Formats IPMOVIE = new Formats("ipmovie");

	/**
	 * iPod H.264 MP4 (MPEG-4 Part 14)
	 */
	public static final Formats IPOD = new Formats("ipod");

	/**
	 * Berkeley/IRCAM/CARL Sound Format
	 */
	public static final Formats IRCAM = new Formats("ircam");

	/**
	 * ISMV/ISMA (Smooth Streaming)
	 */
	public static final Formats ISMV = new Formats("ismv");

	/**
	 * Funcom ISS
	 */
	public static final Formats ISS = new Formats("iss");

	/**
	 * IndigoVision 8000 video
	 */
	public static final Formats IV8 = new Formats("iv8");

	/**
	 * On2 IVF
	 */
	public static final Formats IVF = new Formats("ivf");

	/**
	 * piped j2k sequence
	 */
	public static final Formats J2K_PIPE = new Formats("j2k_pipe");

	/**
	 * JACOsub subtitle format
	 */
	public static final Formats JACOSUB = new Formats("jacosub");

	/**
	 * piped jpeg sequence
	 */
	public static final Formats JPEG_PIPE = new Formats("jpeg_pipe");

	/**
	 * piped jpegls sequence
	 */
	public static final Formats JPEGLS_PIPE = new Formats("jpegls_pipe");

	/**
	 * Bitmap Brothers JV
	 */
	public static final Formats JV = new Formats("jv");

	/**
	 * LOAS/LATM
	 */
	public static final Formats LATM = new Formats("latm");

	/**
	 * Libavfilter virtual input device
	 */
	public static final Formats LAVFI = new Formats("lavfi");

	/**
	 * Game Music Emu demuxer
	 */
	public static final Formats LIBGME = new Formats("libgme");

	/**
	 * ModPlug demuxer
	 */
	public static final Formats LIBMODPLUG = new Formats("libmodplug");

	/**
	 * live RTMP FLV (Flash Video)
	 */
	public static final Formats LIVE_FLV = new Formats("live_flv");

	/**
	 * raw lmlm4
	 */
	public static final Formats LMLM4 = new Formats("lmlm4");

	/**
	 * LOAS AudioSyncStream
	 */
	public static final Formats LOAS = new Formats("loas");

	/**
	 * LRC lyrics
	 */
	public static final Formats LRC = new Formats("lrc");

	/**
	 * LVF
	 */
	public static final Formats LVF = new Formats("lvf");

	/**
	 * VR native stream (LXF)
	 */
	public static final Formats LXF = new Formats("lxf");

	/**
	 * QuickTime / MOV
	 */
	public static final Formats M4A = new Formats("m4a");

	/**
	 * raw MPEG-4 video
	 */
	public static final Formats M4V = new Formats("m4v");

	/**
	 * Matroska, Matroska / WebM
	 */
	public static final Formats MATROSKA = new Formats("matroska");

	/**
	 * MD5 testing
	 */
	public static final Formats MD5 = new Formats("md5");

	/**
	 * Metal Gear Solid: The Twin Snakes
	 */
	public static final Formats MGSTS = new Formats("mgsts");

	/**
	 * MicroDVD subtitle format
	 */
	public static final Formats MICRODVD = new Formats("microdvd");

	/**
	 * QuickTime / MOV
	 */
	public static final Formats MJ2 = new Formats("mj2");

	/**
	 * raw MJPEG video
	 */
	public static final Formats MJPEG = new Formats("mjpeg");

	/**
	 * extract pts as timecode v2 format, as defined by mkvtoolnix
	 */
	public static final Formats MKVTIMESTAMP_V2 = new Formats("mkvtimestamp_v2");

	/**
	 * raw MLP
	 */
	public static final Formats MLP = new Formats("mlp");

	/**
	 * Magic Lantern Video (MLV)
	 */
	public static final Formats MLV = new Formats("mlv");

	/**
	 * American Laser Games MM
	 */
	public static final Formats MM = new Formats("mm");

	/**
	 * Yamaha SMAF
	 */
	public static final Formats MMF = new Formats("mmf");

	/**
	 * QuickTime / MOV, QuickTime / MOV
	 */
	public static final Formats MOV = new Formats("mov");

	/**
	 * MP2 (MPEG audio layer 2)
	 */
	public static final Formats MP2 = new Formats("mp2");

	/**
	 * MP3 (MPEG audio layer 3)
	 */
	public static final Formats MP3 = new Formats("mp3");

	/**
	 * QuickTime / MOV, MP4 (MPEG-4 Part 14)
	 */
	public static final Formats MP4 = new Formats("mp4");

	/**
	 * Musepack
	 */
	public static final Formats MPC = new Formats("mpc");

	/**
	 * Musepack SV8
	 */
	public static final Formats MPC8 = new Formats("mpc8");

	/**
	 * MPEG-1 Systems / MPEG program stream
	 */
	public static final Formats MPEG = new Formats("mpeg");

	/**
	 * raw MPEG-1 video
	 */
	public static final Formats MPEG1VIDEO = new Formats("mpeg1video");

	/**
	 * raw MPEG-2 video
	 */
	public static final Formats MPEG2VIDEO = new Formats("mpeg2video");

	/**
	 * MPEG-TS (MPEG-2 Transport Stream)
	 */
	public static final Formats MPEGTS = new Formats("mpegts");

	/**
	 * raw MPEG-TS (MPEG-2 Transport Stream)
	 */
	public static final Formats MPEGTSRAW = new Formats("mpegtsraw");

	/**
	 * raw MPEG video
	 */
	public static final Formats MPEGVIDEO = new Formats("mpegvideo");

	/**
	 * MIME multipart JPEG
	 */
	public static final Formats MPJPEG = new Formats("mpjpeg");

	/**
	 * MPL2 subtitles
	 */
	public static final Formats MPL2 = new Formats("mpl2");

	/**
	 * MPlayer subtitles
	 */
	public static final Formats MPSUB = new Formats("mpsub");

	/**
	 * MSN TCP Webcam stream
	 */
	public static final Formats MSNWCTCP = new Formats("msnwctcp");

	/**
	 * MTV
	 */
	public static final Formats MTV = new Formats("mtv");

	/**
	 * PCM mu-law
	 */
	public static final Formats MULAW = new Formats("mulaw");

	/**
	 * Silicon Graphics Movie
	 */
	public static final Formats MV = new Formats("mv");

	/**
	 * Motion Pixels MVI
	 */
	public static final Formats MVI = new Formats("mvi");

	/**
	 * MXF (Material eXchange Format)
	 */
	public static final Formats MXF = new Formats("mxf");

	/**
	 * MXF (Material eXchange Format) D-10 Mapping
	 */
	public static final Formats MXF_D10 = new Formats("mxf_d10");

	/**
	 * MxPEG clip
	 */
	public static final Formats MXG = new Formats("mxg");

	/**
	 * NC camera feed
	 */
	public static final Formats NC = new Formats("nc");

	/**
	 * NIST SPeech HEader REsources
	 */
	public static final Formats NISTSPHERE = new Formats("nistsphere");

	/**
	 * Nullsoft Streaming Video
	 */
	public static final Formats NSV = new Formats("nsv");

	/**
	 * raw null video
	 */
	public static final Formats NULL = new Formats("null");

	/**
	 * NUT
	 */
	public static final Formats NUT = new Formats("nut");

	/**
	 * NuppelVideo
	 */
	public static final Formats NUV = new Formats("nuv");

	/**
	 * Ogg Audio
	 */
	public static final Formats OGA = new Formats("oga");

	/**
	 * Ogg
	 */
	public static final Formats OGG = new Formats("ogg");

	/**
	 * Sony OpenMG audio
	 */
	public static final Formats OMA = new Formats("oma");

	/**
	 * Ogg Opus
	 */
	public static final Formats OPUS = new Formats("opus");

	/**
	 * Amazing Studio Packed Animation File
	 */
	public static final Formats PAF = new Formats("paf");

	/**
	 * piped pictor sequence
	 */
	public static final Formats PICTOR_PIPE = new Formats("pictor_pipe");

	/**
	 * PJS (Phoenix Japanimation Society) subtitles
	 */
	public static final Formats PJS = new Formats("pjs");

	/**
	 * Playstation Portable PMP
	 */
	public static final Formats PMP = new Formats("pmp");

	/**
	 * piped png sequence
	 */
	public static final Formats PNG_PIPE = new Formats("png_pipe");

	/**
	 * PSP MP4 (MPEG-4 Part 14)
	 */
	public static final Formats PSP = new Formats("psp");

	/**
	 * Sony Playstation STR
	 */
	public static final Formats PSXSTR = new Formats("psxstr");

	/**
	 * TechnoTrend PVA
	 */
	public static final Formats PVA = new Formats("pva");

	/**
	 * PVF (Portable Voice Format)
	 */
	public static final Formats PVF = new Formats("pvf");

	/**
	 * QCP
	 */
	public static final Formats QCP = new Formats("qcp");

	/**
	 * REDCODE R3D
	 */
	public static final Formats R3D = new Formats("r3d");

	/**
	 * raw video
	 */
	public static final Formats RAWVIDEO = new Formats("rawvideo");

	/**
	 * RealText subtitle format
	 */
	public static final Formats REALTEXT = new Formats("realtext");

	/**
	 * RedSpark
	 */
	public static final Formats REDSPARK = new Formats("redspark");

	/**
	 * RL2
	 */
	public static final Formats RL2 = new Formats("rl2");

	/**
	 * RealMedia
	 */
	public static final Formats RM = new Formats("rm");

	/**
	 * raw id RoQ
	 */
	public static final Formats ROQ = new Formats("roq");

	/**
	 * RPL / ARMovie
	 */
	public static final Formats RPL = new Formats("rpl");

	/**
	 * GameCube RSD
	 */
	public static final Formats RSD = new Formats("rsd");

	/**
	 * Lego Mindstorms RSO
	 */
	public static final Formats RSO = new Formats("rso");

	/**
	 * RTP output
	 */
	public static final Formats RTP = new Formats("rtp");

	/**
	 * RTSP output
	 */
	public static final Formats RTSP = new Formats("rtsp");

	/**
	 * PCM signed 16-bit big-endian
	 */
	public static final Formats S16BE = new Formats("s16be");

	/**
	 * PCM signed 16-bit little-endian
	 */
	public static final Formats S16LE = new Formats("s16le");

	/**
	 * PCM signed 24-bit big-endian
	 */
	public static final Formats S24BE = new Formats("s24be");

	/**
	 * PCM signed 24-bit little-endian
	 */
	public static final Formats S24LE = new Formats("s24le");

	/**
	 * PCM signed 32-bit big-endian
	 */
	public static final Formats S32BE = new Formats("s32be");

	/**
	 * PCM signed 32-bit little-endian
	 */
	public static final Formats S32LE = new Formats("s32le");

	/**
	 * PCM signed 8-bit
	 */
	public static final Formats S8 = new Formats("s8");

	/**
	 * SAMI subtitle format
	 */
	public static final Formats SAMI = new Formats("sami");

	/**
	 * SAP output
	 */
	public static final Formats SAP = new Formats("sap");

	/**
	 * SBaGen binaural beats script
	 */
	public static final Formats SBG = new Formats("sbg");

	/**
	 * SDL output device
	 */
	public static final Formats SDL = new Formats("sdl");

	/**
	 * SDP
	 */
	public static final Formats SDP = new Formats("sdp");

	/**
	 * SDR2
	 */
	public static final Formats SDR2 = new Formats("sdr2");

	/**
	 * segment
	 */
	public static final Formats SEGMENT = new Formats("segment");

	/**
	 * piped sgi sequence
	 */
	public static final Formats SGI_PIPE = new Formats("sgi_pipe");

	/**
	 * raw Shorten
	 */
	public static final Formats SHN = new Formats("shn");

	/**
	 * Beam Software SIFF
	 */
	public static final Formats SIFF = new Formats("siff");

	/**
	 * Asterisk raw pcm
	 */
	public static final Formats SLN = new Formats("sln");

	/**
	 * Loki SDL MJPEG
	 */
	public static final Formats SMJPEG = new Formats("smjpeg");

	/**
	 * Smacker
	 */
	public static final Formats SMK = new Formats("smk");

	/**
	 * Smooth Streaming Muxer
	 */
	public static final Formats SMOOTHSTREAMING = new Formats("smoothstreaming");

	/**
	 * LucasArts Smush
	 */
	public static final Formats SMUSH = new Formats("smush");

	/**
	 * Sierra SOL
	 */
	public static final Formats SOL = new Formats("sol");

	/**
	 * SoX native
	 */
	public static final Formats SOX = new Formats("sox");

	/**
	 * IEC 61937 (used on S/PDIF - IEC958)
	 */
	public static final Formats SPDIF = new Formats("spdif");

	/**
	 * Ogg Speex
	 */
	public static final Formats SPX = new Formats("spx");

	/**
	 * SubRip subtitle
	 */
	public static final Formats SRT = new Formats("srt");

	/**
	 * streaming segment muxer
	 */
	public static final Formats SSEGMENT = new Formats("ssegment");

	/**
	 * streaming segment muxer
	 */
	public static final Formats STREAM_SEGMENT = new Formats("stream_segment");

	/**
	 * SubViewer subtitle format
	 */
	public static final Formats SUBVIEWER = new Formats("subviewer");

	/**
	 * SubViewer v1 subtitle format
	 */
	public static final Formats SUBVIEWER1 = new Formats("subviewer1");

	/**
	 * piped sunrast sequence
	 */
	public static final Formats SUNRAST_PIPE = new Formats("sunrast_pipe");

	/**
	 * raw HDMV Presentation Graphic Stream subtitles
	 */
	public static final Formats SUP = new Formats("sup");

	/**
	 * MPEG-2 PS (SVCD)
	 */
	public static final Formats SVCD = new Formats("svcd");

	/**
	 * SWF (ShockWave Flash)
	 */
	public static final Formats SWF = new Formats("swf");

	/**
	 * raw TAK
	 */
	public static final Formats TAK = new Formats("tak");

	/**
	 * TED Talks captions
	 */
	public static final Formats TEDCAPTIONS = new Formats("tedcaptions");

	/**
	 * Multiple muxer tee
	 */
	public static final Formats TEE = new Formats("tee");

	/**
	 * THP
	 */
	public static final Formats THP = new Formats("thp");

	/**
	 * Tiertex Limited SEQ
	 */
	public static final Formats TIERTEXSEQ = new Formats("tiertexseq");

	/**
	 * piped tiff sequence
	 */
	public static final Formats TIFF_PIPE = new Formats("tiff_pipe");

	/**
	 * 8088flex TMV
	 */
	public static final Formats TMV = new Formats("tmv");

	/**
	 * raw TrueHD
	 */
	public static final Formats TRUEHD = new Formats("truehd");

	/**
	 * TTA (True Audio)
	 */
	public static final Formats TTA = new Formats("tta");

	/**
	 * Tele-typewriter
	 */
	public static final Formats TTY = new Formats("tty");

	/**
	 * Renderware TeXture Dictionary
	 */
	public static final Formats TXD = new Formats("txd");

	/**
	 * PCM unsigned 16-bit big-endian
	 */
	public static final Formats U16BE = new Formats("u16be");

	/**
	 * PCM unsigned 16-bit little-endian
	 */
	public static final Formats U16LE = new Formats("u16le");

	/**
	 * PCM unsigned 24-bit big-endian
	 */
	public static final Formats U24BE = new Formats("u24be");

	/**
	 * PCM unsigned 24-bit little-endian
	 */
	public static final Formats U24LE = new Formats("u24le");

	/**
	 * PCM unsigned 32-bit big-endian
	 */
	public static final Formats U32BE = new Formats("u32be");

	/**
	 * PCM unsigned 32-bit little-endian
	 */
	public static final Formats U32LE = new Formats("u32le");

	/**
	 * PCM unsigned 8-bit
	 */
	public static final Formats U8 = new Formats("u8");

	/**
	 * uncoded framecrc testing
	 */
	public static final Formats UNCODEDFRAMECRC = new Formats("uncodedframecrc");

	/**
	 * raw VC-1 video
	 */
	public static final Formats VC1 = new Formats("vc1");

	/**
	 * VC-1 test bitstream
	 */
	public static final Formats VC1TEST = new Formats("vc1test");

	/**
	 * MPEG-1 Systems / MPEG program stream (VCD)
	 */
	public static final Formats VCD = new Formats("vcd");

	/**
	 * VfW video capture
	 */
	public static final Formats VFWCAP = new Formats("vfwcap");

	/**
	 * Vivo
	 */
	public static final Formats VIVO = new Formats("vivo");

	/**
	 * Sierra VMD
	 */
	public static final Formats VMD = new Formats("vmd");

	/**
	 * MPEG-2 PS (VOB)
	 */
	public static final Formats VOB = new Formats("vob");

	/**
	 * VobSub subtitle format
	 */
	public static final Formats VOBSUB = new Formats("vobsub");

	/**
	 * Creative Voice
	 */
	public static final Formats VOC = new Formats("voc");

	/**
	 * VPlayer subtitles
	 */
	public static final Formats VPLAYER = new Formats("vplayer");

	/**
	 * Nippon Telegraph and Telephone Corporation (NTT) TwinVQ
	 */
	public static final Formats VQF = new Formats("vqf");

	/**
	 * Sony Wave64
	 */
	public static final Formats W64 = new Formats("w64");

	/**
	 * WAV / WAVE (Waveform Audio)
	 */
	public static final Formats WAV = new Formats("wav");

	/**
	 * Wing Commander III movie
	 */
	public static final Formats WC3MOVIE = new Formats("wc3movie");

	/**
	 * Matroska / WebM, WebM
	 */
	public static final Formats WEBM = new Formats("webm");

	/**
	 * WebM DASH Manifest
	 */
	public static final Formats WEBM_DASH_MANIFEST = new Formats("webm_dash_manifest");

	/**
	 * piped webp sequence
	 */
	public static final Formats WEBP_PIPE = new Formats("webp_pipe");

	/**
	 * WebVTT subtitle
	 */
	public static final Formats WEBVTT = new Formats("webvtt");

	/**
	 * Westwood Studios audio
	 */
	public static final Formats WSAUD = new Formats("wsaud");

	/**
	 * Westwood Studios VQA
	 */
	public static final Formats WSVQA = new Formats("wsvqa");

	/**
	 * Windows Television (WTV)
	 */
	public static final Formats WTV = new Formats("wtv");

	/**
	 * raw WavPack
	 */
	public static final Formats WV = new Formats("wv");

	/**
	 * Maxis XA
	 */
	public static final Formats XA = new Formats("xa");

	/**
	 * eXtended BINary text (XBIN)
	 */
	public static final Formats XBIN = new Formats("xbin");

	/**
	 * Microsoft XMV
	 */
	public static final Formats XMV = new Formats("xmv");

	/**
	 * Microsoft xWMA
	 */
	public static final Formats XWMA = new Formats("xwma");

	/**
	 * Psygnosis YOP
	 */
	public static final Formats YOP = new Formats("yop");

	/**
	 * YUV4MPEG pipe
	 */
	public static final Formats YUV4MPEGPIPE = new Formats("yuv4mpegpipe");

	/**
	 * 
	 */
	private final String name;

	/**
	 * @param name
	 */
	private Formats(String name) {
		this.name = name;
		HELP_CACHE.add(name, this, null);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public boolean isDemuxingSupported() {
		for(FormatsHelp h : HELP_CACHE.cache(name)) {
			if(h.contains('D')) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean isMuxingSupported() {
		for(FormatsHelp h : HELP_CACHE.cache(name)) {
			if(h.contains('E')) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return HELP_CACHE.cache(name).stream().map(b -> b.getText()).collect(Collectors.joining(", "));
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
	public static Formats byName(String name) {
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
	public static List<Formats> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-formats");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<FormatsHelp> cacheConsumer = HELP_CACHE.consumer();
				Function<String, FormatsHelp> factory = name -> new FormatsHelp(name);

				AvailableHelp<FormatsHelp> availableHelp = AvailableHelp.create();
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
	private static class FormatsHelp extends Help {

		/**
		 * @param name
		 */
		protected FormatsHelp(String name) {
			super(name);
		}
	}
}
