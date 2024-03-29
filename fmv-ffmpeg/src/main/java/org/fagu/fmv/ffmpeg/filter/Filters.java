package org.fagu.fmv.ffmpeg.filter;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;
import org.fagu.fmv.ffmpeg.utils.AvailableHelp;
import org.fagu.fmv.ffmpeg.utils.Help;
import org.fagu.fmv.ffmpeg.utils.HelpCache;


/**
 * @author f.agu
 */
public class Filters {

	private static final HelpCache<Filters, FilterHelp> HELP_CACHE = new HelpCache<>(runnable(), Filters::new);

	// -------------------------------------

	/**
	 * @author f.agu
	 */
	public static class IOType {

		public static final IOType AUDIO = new IOType('A');

		public static final IOType VIDEO = new IOType('V');

		public static final IOType NUMBER = new IOType('N');

		public static final IOType SOURCE_OR_SINK = new IOType('|');

		private char code;

		private IOType[] types;

		private IOType(char code) {
			this.code = code;
			types = new IOType[] {this};
		}

		private IOType(IOType[] types) {
			this.types = types;
		}

		public boolean isAudio() {
			return ArrayUtils.contains(types, AUDIO);
		}

		public boolean isVideo() {
			return ArrayUtils.contains(types, VIDEO);
		}

		public boolean isNumber() {
			return ArrayUtils.contains(types, NUMBER);
		}

		public boolean isSourceOrSink() {
			return ArrayUtils.contains(types, SOURCE_OR_SINK);
		}

		public boolean isMultiple() {
			return types.length > 1;
		}

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder(2);
			for(IOType ioType : types) {
				buf.append(ioType.code);
			}
			return buf.toString();
		}

		public static IOType byCode(char c) {
			switch(c) {
				case 'A':
					return AUDIO;
				case 'V':
					return VIDEO;
				case 'N':
					return NUMBER;
				case '|':
					return SOURCE_OR_SINK;
				default:
			}
			throw new IllegalArgumentException("Unknown type: " + c);
		}

		public static IOType parse(String str) {
			List<IOType> list = new ArrayList<>();
			for(char c : str.toCharArray()) {
				list.add(byCode(c));
			}
			return new IOType(list.toArray(new IOType[list.size()]));
		}
	}

	// -------------------------------------

	/**
	 * |->A, Buffer audio frames, and make them accessible to the filterchain.
	 */
	public static final Filters ABUFFER = new Filters("abuffer");

	/**
	 * A->|, Buffer audio frames, and make them available to the end of the filter graph.
	 */
	public static final Filters ABUFFERSINK = new Filters("abuffersink");

	/**
	 * A->A, Delay one or more audio channels.
	 */
	public static final Filters ADELAY = new Filters("adelay");

	/**
	 * A->A, Add echoing to the audio.
	 */
	public static final Filters AECHO = new Filters("aecho");

	/**
	 * A->A, Filter audio signal according to a specified expression.
	 */
	public static final Filters AEVAL = new Filters("aeval");

	/**
	 * |->A, Generate an audio signal generated by an expression.
	 */
	public static final Filters AEVALSRC = new Filters("aevalsrc");

	/**
	 * A->A, Fade in/out input audio.
	 */
	public static final Filters AFADE = new Filters("afade");

	/**
	 * A->A, Buffer input frames and send them when they are requested.
	 */
	public static final Filters AFIFO = new Filters("afifo");

	/**
	 * A->A, Convert the input audio to one of the specified formats.
	 */
	public static final Filters AFORMAT = new Filters("aformat");

	/**
	 * N->A, Temporally interleave audio inputs.
	 */
	public static final Filters AINTERLEAVE = new Filters("ainterleave");

	/**
	 * A->A, Apply a two-pole all-pass filter.
	 */
	public static final Filters ALLPASS = new Filters("allpass");

	/**
	 * V->N, Extract an alpha channel as a grayscale image component.
	 */
	public static final Filters ALPHAEXTRACT = new Filters("alphaextract");

	/**
	 * VV->V, Copy the luma value of the second input into the alpha channel of the first input.
	 */
	public static final Filters ALPHAMERGE = new Filters("alphamerge");

	/**
	 * N->A, Merge two or more audio streams into a single multi-channel stream.
	 */
	public static final Filters AMERGE = new Filters("amerge");

	/**
	 * N->A, Audio mixing.
	 */
	public static final Filters AMIX = new Filters("amix");

	/**
	 * |->N, Read audio from a movie source.
	 */
	public static final Filters AMOVIE = new Filters("amovie");

	/**
	 * A->A, Pass the source unchanged to the output.
	 */
	public static final Filters ANULL = new Filters("anull");

	/**
	 * A->|, Do absolutely nothing with the input audio.
	 */
	public static final Filters ANULLSINK = new Filters("anullsink");

	/**
	 * |->A, Null audio source, return empty audio frames.
	 */
	public static final Filters ANULLSRC = new Filters("anullsrc");

	/**
	 * A->A, Pad audio with silence.
	 */
	public static final Filters APAD = new Filters("apad");

	/**
	 * A->A, Set permissions for the output audio frame.
	 */
	public static final Filters APERMS = new Filters("aperms");

	/**
	 * A->A, Add a phasing effect to the audio.
	 */
	public static final Filters APHASER = new Filters("aphaser");

	/**
	 * A->A, Resample audio data.
	 */
	public static final Filters ARESAMPLE = new Filters("aresample");

	/**
	 * A->N, Select audio frames to pass in output.
	 */
	public static final Filters ASELECT = new Filters("aselect");

	/**
	 * A->A, Send commands to filters.
	 */
	public static final Filters ASENDCMD = new Filters("asendcmd");

	/**
	 * A->A, Set the number of samples for each output audio frames.
	 */
	public static final Filters ASETNSAMPLES = new Filters("asetnsamples");

	/**
	 * A->A, Set PTS for the output audio frame.
	 */
	public static final Filters ASETPTS = new Filters("asetpts");

	/**
	 * A->A, Change the sample rate without altering the data.
	 */
	public static final Filters ASETRATE = new Filters("asetrate");

	/**
	 * A->A, Set timebase for the audio output link.
	 */
	public static final Filters ASETTB = new Filters("asettb");

	/**
	 * A->A, Show textual information for each audio frame.
	 */
	public static final Filters ASHOWINFO = new Filters("ashowinfo");

	/**
	 * A->N, Pass on the audio input to N audio outputs.
	 */
	public static final Filters ASPLIT = new Filters("asplit");

	/**
	 * V->V, Render ASS subtitles onto input video using the libass library.
	 */
	public static final Filters ASS = new Filters("ass");

	/**
	 * A->A, Show time domain statistics about audio frames.
	 */
	public static final Filters ASTATS = new Filters("astats");

	/**
	 * AA->AA, Copy two streams of audio data in a configurable order.
	 */
	public static final Filters ASTREAMSYNC = new Filters("astreamsync");

	/**
	 * A->A, Adjust audio tempo.
	 */
	public static final Filters ATEMPO = new Filters("atempo");

	/**
	 * A->A, Pick one continuous section from the input, drop the rest.
	 */
	public static final Filters ATRIM = new Filters("atrim");

	/**
	 * A->V, Convert input audio to vectorscope video output.
	 */
	public static final Filters AVECTORSCOPE = new Filters("avectorscope");

	/**
	 * A->A, Apply a two-pole Butterworth band-pass filter.
	 */
	public static final Filters BANDPASS = new Filters("bandpass");

	/**
	 * A->A, Apply a two-pole Butterworth band-reject filter.
	 */
	public static final Filters BANDREJECT = new Filters("bandreject");

	/**
	 * A->A, Boost or cut lower frequencies.
	 */
	public static final Filters BASS = new Filters("bass");

	/**
	 * V->V, Compute bounding box for each frame.
	 */
	public static final Filters BBOX = new Filters("bbox");

	/**
	 * A->A, Apply a biquad IIR filter with the given coefficients.
	 */
	public static final Filters BIQUAD = new Filters("biquad");

	/**
	 * V->V, Detect video intervals that are (almost) black.
	 */
	public static final Filters BLACKDETECT = new Filters("blackdetect");

	/**
	 * V->V, Detect frames that are (almost) black.
	 */
	public static final Filters BLACKFRAME = new Filters("blackframe");

	/**
	 * VV->V, Blend two video frames into each other.
	 */
	public static final Filters BLEND = new Filters("blend");

	/**
	 * V->V, Blur the input.
	 */
	public static final Filters BOXBLUR = new Filters("boxblur");

	/**
	 * A->A, Bauer stereo-to-binaural filter.
	 */
	public static final Filters BS2B = new Filters("bs2b");

	/**
	 * |->V, Buffer video frames, and make them accessible to the filterchain.
	 */
	public static final Filters BUFFER = new Filters("buffer");

	/**
	 * V->|, Buffer video frames, and make them available to the end of the filter graph.
	 */
	public static final Filters BUFFERSINK = new Filters("buffersink");

	/**
	 * |->V, Create pattern generated by an elementary cellular automaton.
	 */
	public static final Filters CELLAUTO = new Filters("cellauto");

	/**
	 * A->A, Remap audio channels.
	 */
	public static final Filters CHANNELMAP = new Filters("channelmap");

	/**
	 * A->N, Split audio into per-channel streams.
	 */
	public static final Filters CHANNELSPLIT = new Filters("channelsplit");

	/**
	 * V->V, Visualize information about some codecs
	 */
	public static final Filters CODECVIEW = new Filters("codecview");

	/**
	 * |->V, Provide an uniformly colored input.
	 */
	public static final Filters COLOR = new Filters("color");

	/**
	 * V->V, Adjust the color balance.
	 */
	public static final Filters COLORBALANCE = new Filters("colorbalance");

	/**
	 * V->V, Adjust colors by mixing color channels.
	 */
	public static final Filters COLORCHANNELMIXER = new Filters("colorchannelmixer");

	/**
	 * V->V, Convert color matrix.
	 */
	public static final Filters COLORMATRIX = new Filters("colormatrix");

	/**
	 * A->A, Compress or expand audio dynamic range.
	 */
	public static final Filters COMPAND = new Filters("compand");

	/**
	 * N->N, Concatenate audio and video streams.
	 */
	public static final Filters CONCAT = new Filters("concat");

	/**
	 * V->V, Copy the input video unchanged to the output.
	 */
	public static final Filters COPY = new Filters("copy");

	/**
	 * V->V, Crop the input video.
	 */
	public static final Filters CROP = new Filters("crop");

	/**
	 * V->V, Auto-detect crop size.
	 */
	public static final Filters CROPDETECT = new Filters("cropdetect");

	/**
	 * V->V, Adjust components curves.
	 */
	public static final Filters CURVES = new Filters("curves");

	/**
	 * V->V, Denoise frames using 2D DCT.
	 */
	public static final Filters DCTDNOIZ = new Filters("dctdnoiz");

	/**
	 * N->V, Decimate frames (post field matching filter).
	 */
	public static final Filters DECIMATE = new Filters("decimate");

	/**
	 * V->V, Remove judder produced by pullup.
	 */
	public static final Filters DEJUDDER = new Filters("dejudder");

	/**
	 * V->V, Remove logo from input video.
	 */
	public static final Filters DELOGO = new Filters("delogo");

	/**
	 * V->V, Stabilize shaky video.
	 */
	public static final Filters DESHAKE = new Filters("deshake");

	/**
	 * V->V, Draw a colored box on the input video.
	 */
	public static final Filters DRAWBOX = new Filters("drawbox");

	/**
	 * V->V, Draw a colored grid on the input video.
	 */
	public static final Filters DRAWGRID = new Filters("drawgrid");

	/**
	 * V->V, Draw text on top of video frames using libfreetype library.
	 */
	public static final Filters DRAWTEXT = new Filters("drawtext");

	/**
	 * A->A, Widen the stereo image.
	 */
	public static final Filters EARWAX = new Filters("earwax");

	/**
	 * A->N, EBU R128 scanner.
	 */
	public static final Filters EBUR128 = new Filters("ebur128");

	/**
	 * V->V, Detect and draw edge.
	 */
	public static final Filters EDGEDETECT = new Filters("edgedetect");

	/**
	 * V->V, Apply posterize effect, using the ELBG algorithm.
	 */
	public static final Filters ELBG = new Filters("elbg");

	/**
	 * A->A, Apply two-pole peaking equalization (EQ) filter.
	 */
	public static final Filters EQUALIZER = new Filters("equalizer");

	/**
	 * V->N, Extract planes as grayscale frames.
	 */
	public static final Filters EXTRACTPLANES = new Filters("extractplanes");

	/**
	 * V->V, Fade in/out input video.
	 */
	public static final Filters FADE = new Filters("fade");

	/**
	 * A->|, Buffer audio frames, and make them available to the end of the filter graph.
	 */
	public static final Filters FFABUFFERSINK = new Filters("ffabuffersink");

	/**
	 * V->|, Buffer video frames, and make them available to the end of the filter graph.
	 */
	public static final Filters FFBUFFERSINK = new Filters("ffbuffersink");

	/**
	 * V->V, Extract a field from the input video.
	 */
	public static final Filters FIELD = new Filters("field");

	/**
	 * N->V, Field matching for inverse telecine.
	 */
	public static final Filters FIELDMATCH = new Filters("fieldmatch");

	/**
	 * V->V, Set the field order.
	 */
	public static final Filters FIELDORDER = new Filters("fieldorder");

	/**
	 * V->V, Buffer input images and send them when they are requested.
	 */
	public static final Filters FIFO = new Filters("fifo");

	/**
	 * A->A, Apply a flanging effect to the audio.
	 */
	public static final Filters FLANGER = new Filters("flanger");

	/**
	 * V->V, Convert the input video to one of the specified pixel formats.
	 */
	public static final Filters FORMAT = new Filters("format");

	/**
	 * V->V, Force constant framerate.
	 */
	public static final Filters FPS = new Filters("fps");

	/**
	 * VV->V, Generate a frame packed stereoscopic video.
	 */
	public static final Filters FRAMEPACK = new Filters("framepack");

	/**
	 * V->V, Select one frame every N frames.
	 */
	public static final Filters FRAMESTEP = new Filters("framestep");

	/**
	 * V->V, Apply a frei0r effect.
	 */
	public static final Filters FREI0R = new Filters("frei0r");

	/**
	 * |->V, Generate a frei0r source.
	 */
	public static final Filters FREI0R_SRC = new Filters("frei0r_src");

	/**
	 * V->V, Apply generic equation to each pixel.
	 */
	public static final Filters GEQ = new Filters("geq");

	/**
	 * V->V, Debands video quickly using gradients.
	 */
	public static final Filters GRADFUN = new Filters("gradfun");

	/**
	 * VV->V, Adjust colors using a Hald CLUT.
	 */
	public static final Filters HALDCLUT = new Filters("haldclut");

	/**
	 * |->V, Provide an identity Hald CLUT.
	 */
	public static final Filters HALDCLUTSRC = new Filters("haldclutsrc");

	/**
	 * V->V, Horizontally flip the input video.
	 */
	public static final Filters HFLIP = new Filters("hflip");

	/**
	 * A->A, Apply a high-pass filter with 3dB point frequency.
	 */
	public static final Filters HIGHPASS = new Filters("highpass");

	/**
	 * V->V, Apply global color histogram equalization.
	 */
	public static final Filters HISTEQ = new Filters("histeq");

	/**
	 * V->V, Compute and draw a histogram.
	 */
	public static final Filters HISTOGRAM = new Filters("histogram");

	/**
	 * V->V, Apply a High Quality 3D Denoiser.
	 */
	public static final Filters HQDN3D = new Filters("hqdn3d");

	/**
	 * V->V, Scale the input by 2, 3 or 4 using the hq*x magnification algorithm.
	 */
	public static final Filters HQX = new Filters("hqx");

	/**
	 * V->V, Adjust the hue and saturation of the input video.
	 */
	public static final Filters HUE = new Filters("hue");

	/**
	 * V->V, Interlace detect Filter.
	 */
	public static final Filters IDET = new Filters("idet");

	/**
	 * V->V, Deinterleave or interleave fields.
	 */
	public static final Filters IL = new Filters("il");

	/**
	 * V->V, Convert progressive video into interlaced.
	 */
	public static final Filters INTERLACE = new Filters("interlace");

	/**
	 * N->V, Temporally interleave video inputs.
	 */
	public static final Filters INTERLEAVE = new Filters("interleave");

	/**
	 * N->A, Join multiple audio streams into multi-channel output.
	 */
	public static final Filters JOIN = new Filters("join");

	/**
	 * V->V, Apply kernel deinterlacing to the input.
	 */
	public static final Filters KERNDEINT = new Filters("kerndeint");

	/**
	 * V->V, Rectify the image by correcting for lens distortion.
	 */
	public static final Filters LENSCORRECTION = new Filters("lenscorrection");

	/**
	 * |->V, Create life.
	 */
	public static final Filters LIFE = new Filters("life");

	/**
	 * A->A, Apply a low-pass filter with 3dB point frequency.
	 */
	public static final Filters LOWPASS = new Filters("lowpass");

	/**
	 * V->V, Compute and apply a lookup table to the RGB/YUV input video.
	 */
	public static final Filters LUT = new Filters("lut");

	/**
	 * V->V, Adjust colors using a 3D LUT.
	 */
	public static final Filters LUT3D = new Filters("lut3d");

	/**
	 * V->V, Compute and apply a lookup table to the RGB input video.
	 */
	public static final Filters LUTRGB = new Filters("lutrgb");

	/**
	 * V->V, Compute and apply a lookup table to the YUV input video.
	 */
	public static final Filters LUTYUV = new Filters("lutyuv");

	/**
	 * |->V, Render a Mandelbrot fractal.
	 */
	public static final Filters MANDELBROT = new Filters("mandelbrot");

	/**
	 * V->V, Apply motion compensating deinterlacing.
	 */
	public static final Filters MCDEINT = new Filters("mcdeint");

	/**
	 * N->V, Merge planes.
	 */
	public static final Filters MERGEPLANES = new Filters("mergeplanes");

	/**
	 * |->N, Read from a movie source.
	 */
	public static final Filters MOVIE = new Filters("movie");

	/**
	 * V->V, Apply a libmpcodecs filter to the input video.
	 */
	public static final Filters MP = new Filters("mp");

	/**
	 * V->V, Remove near-duplicate frames.
	 */
	public static final Filters MPDECIMATE = new Filters("mpdecimate");

	/**
	 * |->V, Generate various test pattern.
	 */
	public static final Filters MPTESTSRC = new Filters("mptestsrc");

	/**
	 * V->V, Negate input video.
	 */
	public static final Filters NEGATE = new Filters("negate");

	/**
	 * V->V, Force libavfilter not to use any of the specified pixel formats for the input to the next filter.
	 */
	public static final Filters NOFORMAT = new Filters("noformat");

	/**
	 * V->V, Add noise.
	 */
	public static final Filters NOISE = new Filters("noise");

	/**
	 * V->V, Pass the source unchanged to the output.
	 */
	public static final Filters NULL = new Filters("null");

	/**
	 * V->|, Do absolutely nothing with the input video.
	 */
	public static final Filters NULLSINK = new Filters("nullsink");

	/**
	 * |->V, Null video source, return unprocessed video frames.
	 */
	public static final Filters NULLSRC = new Filters("nullsrc");

	/**
	 * VV->V, Overlay a video source on top of the input.
	 */
	public static final Filters OVERLAY = new Filters("overlay");

	/**
	 * V->V, Denoise using wavelets.
	 */
	public static final Filters OWDENOISE = new Filters("owdenoise");

	/**
	 * V->V, Pad the input video.
	 */
	public static final Filters PAD = new Filters("pad");

	/**
	 * A->A, Remix channels with coefficients (panning).
	 */
	public static final Filters PAN = new Filters("pan");

	/**
	 * V->V, Set permissions for the output video frame.
	 */
	public static final Filters PERMS = new Filters("perms");

	/**
	 * V->V, Correct the perspective of video.
	 */
	public static final Filters PERSPECTIVE = new Filters("perspective");

	/**
	 * V->V, Phase shift fields.
	 */
	public static final Filters PHASE = new Filters("phase");

	/**
	 * V->V, Test pixel format definitions.
	 */
	public static final Filters PIXDESCTEST = new Filters("pixdesctest");

	/**
	 * V->V, Filter video using libpostproc.
	 */
	public static final Filters PP = new Filters("pp");

	/**
	 * VV->V, Calculate the PSNR between two video streams.
	 */
	public static final Filters PSNR = new Filters("psnr");

	/**
	 * V->V, Pullup from field sequence to frames.
	 */
	public static final Filters PULLUP = new Filters("pullup");

	/**
	 * V->V, Remove a TV logo based on a mask image.
	 */
	public static final Filters REMOVELOGO = new Filters("removelogo");

	/**
	 * A->A, ReplayGain scanner.
	 */
	public static final Filters REPLAYGAIN = new Filters("replaygain");

	/**
	 * |->V, Generate RGB test pattern.
	 */
	public static final Filters RGBTESTSRC = new Filters("rgbtestsrc");

	/**
	 * V->V, Rotate the input image.
	 */
	public static final Filters ROTATE = new Filters("rotate");

	/**
	 * V->V, Apply shape adaptive blur.
	 */
	public static final Filters SAB = new Filters("sab");

	/**
	 * V->V, Scale the input video size and/or convert the image format.
	 */
	public static final Filters SCALE = new Filters("scale");

	/**
	 * V->N, Select video frames to pass in output.
	 */
	public static final Filters SELECT = new Filters("select");

	/**
	 * V->V, Send commands to filters.
	 */
	public static final Filters SENDCMD = new Filters("sendcmd");

	/**
	 * V->V, Split input video frames into fields.
	 */
	public static final Filters SEPARATEFIELDS = new Filters("separatefields");

	/**
	 * V->V, Set the frame display aspect ratio.
	 */
	public static final Filters SETDAR = new Filters("setdar");

	/**
	 * V->V, Force field for the output video frame.
	 */
	public static final Filters SETFIELD = new Filters("setfield");

	/**
	 * V->V, Set PTS for the output video frame.
	 */
	public static final Filters SETPTS = new Filters("setpts");

	/**
	 * V->V, Set the pixel sample aspect ratio.
	 */
	public static final Filters SETSAR = new Filters("setsar");

	/**
	 * V->V, Set timebase for the video output link.
	 */
	public static final Filters SETTB = new Filters("settb");

	/**
	 * A->V, Convert input audio to a CQT (Constant Q Transform) spectrum video output.
	 */
	public static final Filters SHOWCQT = new Filters("showcqt");

	/**
	 * V->V, Show textual information for each video frame.
	 */
	public static final Filters SHOWINFO = new Filters("showinfo");

	/**
	 * A->V, Convert input audio to a spectrum video output.
	 */
	public static final Filters SHOWSPECTRUM = new Filters("showspectrum");

	/**
	 * A->V, Convert input audio to a video output.
	 */
	public static final Filters SHOWWAVES = new Filters("showwaves");

	/**
	 * V->V, Shuffle video planes
	 */
	public static final Filters SHUFFLEPLANES = new Filters("shuffleplanes");

	/**
	 * V->V, Generate statistics from video analysis.
	 */
	public static final Filters SIGNALSTATS = new Filters("signalstats");

	/**
	 * A->A, Detect silence.
	 */
	public static final Filters SILENCEDETECT = new Filters("silencedetect");

	/**
	 * A->A, Remove silence.
	 */
	public static final Filters SILENCEREMOVE = new Filters("silenceremove");

	/**
	 * |->A, Generate sine wave audio signal.
	 */
	public static final Filters SINE = new Filters("sine");

	/**
	 * V->V, Blur the input video without impacting the outlines.
	 */
	public static final Filters SMARTBLUR = new Filters("smartblur");

	/**
	 * |->V, Generate SMPTE color bars.
	 */
	public static final Filters SMPTEBARS = new Filters("smptebars");

	/**
	 * |->V, Generate SMPTE HD color bars.
	 */
	public static final Filters SMPTEHDBARS = new Filters("smptehdbars");

	/**
	 * V->N, Pass on the input to N video outputs.
	 */
	public static final Filters SPLIT = new Filters("split");

	/**
	 * V->V, Apply a simple post processing filter.
	 */
	public static final Filters SPP = new Filters("spp");

	/**
	 * V->V, Convert video stereoscopic 3D view.
	 */
	public static final Filters STEREO3D = new Filters("stereo3d");

	/**
	 * V->V, Render text subtitles onto input video using the libass library.
	 */
	public static final Filters SUBTITLES = new Filters("subtitles");

	/**
	 * V->V, Scale the input by 2x using the Super2xSaI pixel art algorithm.
	 */
	public static final Filters SUPER2XSAI = new Filters("super2xsai");

	/**
	 * V->V, Swap U and V components.
	 */
	public static final Filters SWAPUV = new Filters("swapuv");

	/**
	 * V->V, Apply a telecine pattern.
	 */
	public static final Filters TELECINE = new Filters("telecine");

	/**
	 * |->V, Generate test pattern.
	 */
	public static final Filters TESTSRC = new Filters("testsrc");

	/**
	 * V->V, Select the most representative frame in a given sequence of consecutive frames.
	 */
	public static final Filters THUMBNAIL = new Filters("thumbnail");

	/**
	 * V->V, Tile several successive frames together.
	 */
	public static final Filters TILE = new Filters("tile");

	/**
	 * V->V, Perform temporal field interlacing.
	 */
	public static final Filters TINTERLACE = new Filters("tinterlace");

	/**
	 * V->V, Transpose input video.
	 */
	public static final Filters TRANSPOSE = new Filters("transpose");

	/**
	 * A->A, Boost or cut upper frequencies.
	 */
	public static final Filters TREBLE = new Filters("treble");

	/**
	 * V->V, Pick one continuous section from the input, drop the rest.
	 */
	public static final Filters TRIM = new Filters("trim");

	/**
	 * V->V, Sharpen or blur the input video.
	 */
	public static final Filters UNSHARP = new Filters("unsharp");

	/**
	 * V->V, Flip the input video vertically.
	 */
	public static final Filters VFLIP = new Filters("vflip");

	/**
	 * V->V, Extract relative transformations, pass 1 of 2 for stabilization (see vidstabtransform for pass 2).
	 */
	public static final Filters VIDSTABDETECT = new Filters("vidstabdetect");

	/**
	 * V->V, Transform the frames, pass 2 of 2 for stabilization (see vidstabdetect for pass 1).
	 */
	public static final Filters VIDSTABTRANSFORM = new Filters("vidstabtransform");

	/**
	 * V->V, Make or reverse a vignette effect.
	 */
	public static final Filters VIGNETTE = new Filters("vignette");

	/**
	 * A->A, Change input volume.
	 */
	public static final Filters VOLUME = new Filters("volume");

	/**
	 * A->A, Detect audio volume.
	 */
	public static final Filters VOLUMEDETECT = new Filters("volumedetect");

	/**
	 * V->V, Apply Martin Weston three field deinterlace.
	 */
	public static final Filters W3FDIF = new Filters("w3fdif");

	/**
	 * V->V, Deinterlace the input image.
	 */
	public static final Filters YADIF = new Filters("yadif");

	/**
	 * V->V, Apply Zoom & Pan effect.
	 */
	public static final Filters ZOOMPAN = new Filters("zoompan");

	private final String name;

	private Filters(String name) {
		this.name = name;
		HELP_CACHE.add(name, this, null);
	}

	public String getName() {
		return name;
	}

	public boolean isTimelineSupported() {
		return cache().contains('T');
	}

	public boolean isSliceThreading() {
		return cache().contains('S');
	}

	public boolean isCommandSupport() {
		return cache().contains('C');
	}

	public IOType getInputType() {
		return cache().inputType;
	}

	public IOType getOutputType() {
		return cache().outputType;
	}

	public String getDescription() {
		return cache().description;
	}

	public boolean exists() {
		return exists(name);
	}

	@Override
	public String toString() {
		return name;
	}

	// **************************************************

	public static boolean exists(String name) {
		return HELP_CACHE.exists(name);
	}

	public static Filters byName(String name) {
		return HELP_CACHE.byName(name);
	}

	public static Set<String> availableNames() {
		return HELP_CACHE.availableNames();
	}

	public static List<Filters> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	private FilterHelp cache() {
		return HELP_CACHE.cache(name).get(0);
	}

	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-filters");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<FilterHelp> cacheConsumer = HELP_CACHE.consumer();
				final Pattern pattern = Pattern.compile("([AVN\\|]+)-\\>([AVN\\|]+)\\s+(\\w+.*)");
				Function<String, FilterHelp> factory = name -> new FilterHelp(name);
				Consumer<FilterHelp> consumer = help -> {
					Matcher matcher = pattern.matcher(help.getText());
					if(matcher.matches()) {
						help.inputType = IOType.parse(matcher.group(1));
						help.outputType = IOType.parse(matcher.group(2));
						help.description = matcher.group(3);
						cacheConsumer.accept(help);
					} else {
						throw new RuntimeException("Filter description unparsable: " + help.getText());
					}
				};

				AvailableHelp<FilterHelp> availableHelp = AvailableHelp.create();
				availableHelp.title().legend(true).values(factory, consumer).parse(executor.execute().getResult());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	// ---------------------------------------------

	/**
	 * @return
	 */
	private static class FilterHelp extends Help {

		private IOType inputType;

		private IOType outputType;

		private String description;

		protected FilterHelp(String name) {
			super(name);
		}
	}
}
