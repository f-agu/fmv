package org.fagu.fmv.ffmpeg;

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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.MockMovieMetadatas.MockVideoStream;
import org.fagu.fmv.ffmpeg.filter.impl.AutoRotate;
import org.fagu.fmv.ffmpeg.filter.impl.FadeType;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class FFHelperTestCase {

	private static String commandRan;

	private static Function<String, MovieMetadatas> movieMetadatasSupplier;

	/**
	 *
	 */
	@Before
	public void setUp() {
		movieMetadatasSupplier = null;
		MockFFMPEGExecutorBuilder.mock(() -> movieMetadatasSupplier, cmd -> commandRan = cmd);
	}

	/**
	 *
	 */
	public FFHelperTestCase() {}

	/**
	 * @throws Exception
	 */
	@Test
	public void testCaptureWebCam() throws Exception {
		FFHelper.captureWebCam(new File("test.avi"), Duration.valueOf(14));
		assertCmd("-f vfwcap -r 25 -t 00:00:14.000 -i 0 test.avi");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAudioConvert2AAC() throws Exception {
		FFHelper.audioConvert2AAC(new File("in.mp3"), new File("out.m4a"));
		assertCmd("-i in.mp3 -movflags +faststart -codec:a libfdk_aac -y out.m4a");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAudioGenerator() throws Exception {
		FFHelper.audioGenerator(new File("out.mp3"));
		assertCmd("-f lavfi -i \"aevalsrc=exprs='sin(440*2*PI*t)':s=44100:d=5.0\" -y out.mp3");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAutoRotate() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.video().rotation(Rotation.R_270);
			return builder.build();
		};

		FFHelper.autoRotate(new File("in.mp3"), new File("out.mp3"));
		if(AutoRotate.isAutoRotateObsolete()) {
			assertCmd("-i in.mp3 -y -metadata:s:v:0 \"rotate=0\" out.mp3");
		} else {
			assertCmd("-i in.mp3 -filter:v \"transpose=dir=cclock\" -y -metadata:s:v:0 \"rotate=0\" out.mp3");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAddFrenchAudioStream() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			if("in.mp4".equals(fileName)) {
				builder.audio();
				builder.video();
				return builder.build();
			}
			if("french.mp3".equals(fileName)) {
				builder.audio();
				return builder.build();
			}

			throw new RuntimeException(fileName);
		};

		File video = new File("in.mp4");
		File frenchAudio = new File("french.mp3");
		File out = new File("out.mp4");
		FFHelper.addFrenchAudioStream(video, frenchAudio, out);
		assertCmd("-i in.mp4 -i french.mp3 -map 0 -map 1 -codec:v copy -shortest -y -metadata:s:a:1 \"language=fra\" out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAudioHalfVolume() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			return builder.build();
		};

		FFHelper.audioHalfVolume(new File("in.mp3"), new File("out.mp3"));
		assertCmd("-i in.mp3 -filter_complex \"[0] aeval=exprs='val(ch)/2':c=same [f_a]\" -map [f_a] -y out.mp3");
	}

	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testAudioVolumeDetect() throws Exception {
		FFHelper.audioVolumeDetect(new File("D:\\tmp\\Compét 2014\\566.mp4"));
		// assertCmd("-i in.mp3 -filter_complex \"[0] aeval=exprs='val(ch)/2':c=same [f_a]\" -map [f_a] -y out.mp3");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testExtractThumbnail() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			builder.video().countEstimateFrames(200);
			return builder.build();
		};

		File file1 = new File("C:\\tmp\\8.mp4");
		File outFolder = new File("c:\\tmp\\out-img");
		FFHelper.extractThumbnails2JPEGS(file1, outFolder, 8);
		assertCmd(
				"-hide_banner -loglevel info -i C:\\tmp\\8.mp4 -filter:v \"select=e='not(mod(n,25))'\" -f image2 -vsync 0 -y c:\\tmp\\out-img\\out%05d.jpg");
	}

	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testResize() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.video();
			builder.audio();
			builder.audio();
			return builder.build();
		};

		File intFile = new File("in.mp4");
		File outFile = new File("out.mp4");
		FFHelper.resize(intFile, Size.valueOf(499, 500), outFile);
		assertCmd(
				"-i in.mp4 -filter:v \"scale=w='if(gt(dar,499/500),499,trunc(oh*dar/2)*2)':h='if(gt(dar,499/500),trunc(ow/dar/2)*2,500)'\" -f mp4 -q:v 0 -codec:a copy -codec:s copy -map 0:0 -map 0:1 -map 0:2 -codec:a copy -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testConcat() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			builder.video();
			return builder.build();
		};

		List<File> files = Arrays.asList(new File("small1.mp4"), new File("left.mp4"), new File("small2.mp4"));
		FFHelper.concat(files, new File("out.mp4"));
		assertCmd(
				"-i small1.mp4 -i left.mp4 -i small2.mp4 -filter_complex \"[0] setpts=PTS-STARTPTS [f_a];[0] asetpts=PTS-STARTPTS [f_b];[1] setpts=PTS-STARTPTS [f_c];[1] asetpts=PTS-STARTPTS [f_d];[2] setpts=PTS-STARTPTS [f_e];[2] asetpts=PTS-STARTPTS [f_f];[f_a][f_b][f_c][f_d][f_e][f_f] concat=n=3:v=1:a=1 [con_g]\" -map [con_g] -movflags +faststart -f mp4 -q:a 0 -q:v 0 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testSpeed() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			builder.video();
			return builder.build();
		};
		FFHelper.speed(new File("left.mp4"), new File("out.mp4"), 2);
		assertCmd("-i left.mp4 -filter_complex \"[0] setpts=1/2.0*PTS [f_a];[0] atempo=2.0 [f_b]\" -map [f_a] -map [f_b] -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testExtract() throws Exception {
		FFHelper.extractPart(new File("left.mp4"), new File("out.mp4"), Time.valueOf(2), Duration.valueOf(3.4));
		assertCmd(
				"-ss 00:00:02.000 -i left.mp4 -avoid_negative_ts make_non_negative -f mp4 -t 00:00:03.400 -q:a 0 -q:v 0 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFade1() throws Exception {
		FFHelper.fade1(new File("left.mp4"), new File("out.mp4"), FadeType.IN, Duration.valueOf(2));
		assertCmd("-i left.mp4 -filter:a \"afade=t=in:st=0.0:d=2.0\" -filter:v \"fade=t=in:st=0.0:d=2.0\" -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFade2() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			builder.video();
			return builder.build();
		};
		FFHelper.fade2(new File("left.mp4"), new File("out.mp4"), FadeType.IN, Duration.valueOf(2));
		assertCmd(
				"-i left.mp4 -filter_complex \"[0] fade=t=in:st=0.0:d=2.0 [f_a];[0] afade=t=in:st=0.0:d=2.0 [f_b]\" -map [f_a] -map [f_b] -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testCrop() throws Exception {
		FFHelper.crop(new File("left.mp4"), new File("out.mp4"), Size.HD480);
		assertCmd("-i left.mp4 -filter:v \"crop=w='852':h='480'\" -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMixAudio() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			if("left.mp4".equals(fileName)) {
				builder.audio();
				builder.video();
				return builder.build();
			}
			if("w.mp3".equals(fileName)) {
				builder.audio();
				return builder.build();
			}

			throw new RuntimeException(fileName);
		};
		FFHelper.mixAudio(new File("left.mp4"), new File("w.mp3"), new File("out.mp4"), new Time(0, 0, 13.5));
		assertCmd(
				"-i left.mp4 -ss 00:00:13.500 -i w.mp3 -filter_complex \"[1][0] amix=duration=shortest:inputs=2 [ami_a]\" -map [ami_a] -map 0:v -codec:v copy -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testOverlay4() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			builder.video();
			return builder.build();
		};

		FFHelper.overlay4(new File("1.mp4"), new File("2.mp4"), new File("3.mp4"), new File("4.mp4"), new File("out.mp4"), Size.HD720);
		assertCmd(
				"-i 1.mp4 -i 2.mp4 -i 3.mp4 -i 4.mp4 -filter_complex \"nullsrc=s=hd720 [f_a];[0] setpts=PTS-STARTPTS,scale=w='640':h='360' [f_b];[1] setpts=PTS-STARTPTS,scale=w='640':h='360' [f_c];[2] setpts=PTS-STARTPTS,scale=w='640':h='360' [f_d];[3] setpts=PTS-STARTPTS,scale=w='640':h='360' [f_e];[f_a][f_b] overlay=shortest=1 [ove_f];[ove_f][f_c] overlay=shortest=1:x=640 [ove_g];[ove_g][f_d] overlay=shortest=1:y=360 [ove_h];[ove_h][f_e] overlay=shortest=1:x=640:y=360 [ove_i]\" -map 0:a -map [ove_i] -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testSplit() throws Exception {
		FFHelper.splitTo3(new File("in.mp4"), new File("out1.mp4"), new File("out2.mp4"), new File("out3.mp4"));
		assertCmd(
				"-i in.mp4 -filter_complex \"[0] split=3 [spl_a][spl_g][spl_h];[0] asplit=3 [asp_b][asp_i][asp_j];[spl_g] fade=t=in:st=0.0:d=1.0 [f_c];[asp_i] afade=t=in:st=0.0:d=1.0 [f_d];[spl_h] fade=t=out:st=0.0:d=1.0 [f_e];[asp_j] afade=t=out:st=0.0:d=1.0 [f_f]\" -map [spl_a] -map [asp_b] -y out1.mp4 -map [f_c] -map [f_d] -y out2.mp4 -map [f_e] -map [f_f] -y out3.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testConcatFade() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			MockVideoStream mockVideoStreams = builder.video().size(Size.HD720);
			if("in1.mp4".equals(fileName)) {
				mockVideoStreams.duration(Duration.valueOf(3.1));
			}
			if("in2.mp4".equals(fileName)) {
				mockVideoStreams.duration(Duration.valueOf(4.8));
			}
			return builder.build();
		};

		File file1 = new File("in1.mp4");
		File file2 = new File("in2.mp4");
		FFHelper.concatFade(file1, file2, Duration.valueOf(2), new File("out.mp4"));
		assertCmd(
				"-i in1.mp4 -i in2.mp4 -filter_complex \"[f_a][f_b] blend=all_mode=addition:repeatlast=1:all_opacity=1.0:all_expr='A*(1-(if(gte(T,3.0998),1,if(lte(T,1.0998),0,min(max((T-1.0998)/2.0,0),1)))))+B*(if(gte(T,3.0998),1,if(lte(T,1.0998),0,min(max((T-1.0998)/2.0,0),1))))',format=yuva422p10le [f_c];[con_d] setsar=ratio=1,format=rgba [f_a];[0] setpts=PTS-STARTPTS [f_e];nullsrc=s=hd720:d=2.8 [f_f];[f_e][f_f] concat=n=2:v=1:a=0 [con_d];[con_g] setsar=ratio=1,format=rgba [f_b];nullsrc=s=hd720:d=1.0998 [f_h];[1] setpts=PTS-STARTPTS [f_i];[f_h][f_i] concat=n=2:v=1:a=0 [con_g];[f_j][f_k] amix=duration=shortest:inputs=3 [ami_l];[con_m] afade=t=out:st=1.0998:d=2.0 [f_j];[0] asetpts=PTS-STARTPTS [f_n];aevalsrc=exprs='0':d=2.8 [f_o];[f_n][f_o] concat=n=2:v=0:a=1 [con_m];[con_p] afade=t=in:st=1.0998:d=2.0 [f_k];aevalsrc=exprs='0':d=1.0998 [f_ba];[1] asetpts=PTS-STARTPTS [f_bb];[f_ba][f_bb] concat=n=2:v=0:a=1 [con_p]\" -map [f_c] -map [ami_l] -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testBackgroundBlurOverlayScale() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			builder.video();
			return builder.build();
		};

		File inFile = new File("in.mp4");
		File outFile = new File("out.mp4");
		FFHelper.backgroundBlurOverlayScale(inFile, outFile);
		assertCmd(
				"-i in.mp4 -filter_complex \"[ove_a] scale=w='852':h='480' [f_b];[f_c][f_d] overlay=shortest=1:x=(W-w)/2:y=(H-h)/2 [ove_a];[0] boxblur=luma_radius=8:luma_power=8,hue=s=0 [f_c];[0] scale=w='852':h='480' [f_d]\" -map 0:a -map [f_b] -y out.mp4");
	}

	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testEncodeMKV_2_MP4_x264_KeepChaptersAndSubtitles() throws Exception {
		movieMetadatasSupplier = fileName -> {
			MockMovieMetadatas builder = MockMovieMetadatas.builder();
			builder.audio();
			builder.video();
			return builder.build();
		};

		File srcFile = new File("title00.mkv");
		File destFile = new File("title00.mkv");
		Collection<Locale> wantLocales = Arrays.asList(Locale.ENGLISH, Locale.FRENCH); // only english & french
		FFHelper.encodeTox264_KeepChaptersAndSubtitles(srcFile, destFile, wantLocales);
		if(AutoRotate.isAutoRotateObsolete()) {
			assertCmd(
					"-hide_banner -i in.mp4 -filter:a \"aresample=sample_rate=44100\" -filter:v \"scale=w='if(gt(dar,930/500),930,trunc(oh*dar/2)*2)':h='if(gt(dar,930/500),trunc(ow/dar/2)*2,500)',format=yuv420p\" -movflags +faststart -f mp4 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -pix_fmt yuv420p -codec:a aac -ac 2 -b:a 128000 -y -metadata:s:v:0 \"rotate=0\" out.mp4");
		} else {
			assertCmd(
					"-hide_banner -i in.mp4 -filter:a \"aresample=sample_rate=44100\" -filter:v \"transpose=dir=cclock,scale=w='if(gt(dar,930/500),930,trunc(oh*dar/2)*2)':h='if(gt(dar,930/500),trunc(ow/dar/2)*2,500)',format=yuv420p\" -movflags +faststart -f mp4 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -pix_fmt yuv420p -codec:a aac -ac 2 -b:a 128000 -y -metadata:s:v:0 \"rotate=0\" out.mp4");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testOo_Rotate180() throws Exception {
		final MovieMetadatas movieMetadatas = mock(MovieMetadatas.class);
		VideoStream videoStream = mock(VideoStream.class);
		doReturn(videoStream).when(movieMetadatas).getVideoStream();
		doReturn(Rotation.R_180).when(videoStream).rotate();

		movieMetadatasSupplier = fileName -> movieMetadatas;

		File inFile = new File("in.mp4");
		File outFile = new File("out.mp4");
		FFHelper.oo(inFile, outFile);
		if(AutoRotate.isAutoRotateObsolete()) {
			assertCmd(
					"-hide_banner -i in.mp4 -filter:a \"aresample=sample_rate=44100\" -filter:v \"scale=w='if(gt(dar,930/500),930,trunc(oh*dar/2)*2)':h='if(gt(dar,930/500),trunc(ow/dar/2)*2,500)',format=yuv420p\" -movflags +faststart -f mp4 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -pix_fmt yuv420p -codec:a aac -ac 2 -b:a 128000 -y -metadata:s:v:0 \"rotate=0\" out.mp4");
		} else {
			assertCmd(
					"-hide_banner -i in.mp4 -filter:a \"aresample=sample_rate=44100\" -filter:v \"transpose=dir=clock,transpose=dir=clock,scale=w='if(gt(dar,930/500),930,trunc(oh*dar/2)*2)':h='if(gt(dar,930/500),trunc(ow/dar/2)*2,500)',format=yuv420p\" -movflags +faststart -f mp4 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -pix_fmt yuv420p -codec:a aac -ac 2 -b:a 128000 -y -metadata:s:v:0 \"rotate=0\" out.mp4");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testOo_Rotate270() throws Exception {
		final MovieMetadatas movieMetadatas = mock(MovieMetadatas.class);
		VideoStream videoStream = mock(VideoStream.class);
		doReturn(videoStream).when(movieMetadatas).getVideoStream();
		doReturn(Rotation.R_270).when(videoStream).rotate();

		movieMetadatasSupplier = fileName -> movieMetadatas;

		File inFile = new File("in.mp4");
		File outFile = new File("out.mp4");
		FFHelper.oo(inFile, outFile);
		if(AutoRotate.isAutoRotateObsolete()) {
			assertCmd(
					"-hide_banner -i in.mp4 -filter:a \"aresample=sample_rate=44100\" -filter:v \"scale=w='if(gt(dar,930/500),930,trunc(oh*dar/2)*2)':h='if(gt(dar,930/500),trunc(ow/dar/2)*2,500)',format=yuv420p\" -movflags +faststart -f mp4 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -pix_fmt yuv420p -codec:a aac -ac 2 -b:a 128000 -y -metadata:s:v:0 \"rotate=0\" out.mp4");
		} else {
			assertCmd(
					"-hide_banner -i in.mp4 -filter:a \"aresample=sample_rate=44100\" -filter:v \"transpose=dir=cclock,scale=w='if(gt(dar,930/500),930,trunc(oh*dar/2)*2)':h='if(gt(dar,930/500),trunc(ow/dar/2)*2,500)',format=yuv420p\" -movflags +faststart -f mp4 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -pix_fmt yuv420p -codec:a aac -ac 2 -b:a 128000 -y -metadata:s:v:0 \"rotate=0\" out.mp4");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testOo_NoRotate() throws Exception {
		movieMetadatasSupplier = fileName -> null;

		File inFile = new File("in.mp4");
		File outFile = new File("out.mp4");
		FFHelper.oo(inFile, outFile);
		assertCmd(
				"-hide_banner -i in.mp4 -filter:a \"aresample=sample_rate=44100\" -filter:v \"scale=w='if(gt(dar,930/500),930,trunc(oh*dar/2)*2)':h='if(gt(dar,930/500),trunc(ow/dar/2)*2,500)',format=yuv420p\" -movflags +faststart -f mp4 -codec:v libx264 -preset medium -profile:v baseline -level 3.0 -crf 22 -pix_fmt yuv420p -codec:a aac -ac 2 -b:a 128000 -y -metadata:s:v:0 \"rotate=0\" out.mp4");
	}

	// ********************************************

	/**
	 * @param expected
	 */
	private void assertCmd(String expected) {
		int ffIndex = commandRan.indexOf("ffmpeg");
		String cmd = commandRan.substring(commandRan.indexOf(' ', ffIndex) + 1);

		// -metadata:s:v:0 "comment=fmvversion:0.1.9-SNAPSHOT"
		cmd = cmd.replaceAll(" -metadata[0-9a-z:]* \"[A-Za-z0-9:]+=fmvversion:[A-Za-z0-9-\\\\.\\\\?]+\"", "");
		assertEquals(replaceMapLabel(expected), replaceMapLabel(cmd));

		Set<String> expectedMapLabel = extractMapLabel(expected);
		Set<String> cmdMapLabel = extractMapLabel(cmd);
		assertEquals(expectedMapLabel.size(), cmdMapLabel.size());
		for(String label : expectedMapLabel) {
			if( ! cmdMapLabel.contains(label)) {
				fail("Label " + label + " not found");
			}
		}
	}

	/**
	 * @param cmd
	 * @return
	 */
	private Set<String> extractMapLabel(String cmd) {
		String str = cmd;
		Set<String> set = new HashSet<>();
		while(true) {
			str = StringUtils.substringAfter(str, "-map ");
			if(StringUtils.isBlank(str)) {
				return set;
			}
			set.add(StringUtils.substringBefore(str, " "));
			str = StringUtils.substringAfter(str, " ");
		}
	}

	/**
	 * @param cmd
	 * @return
	 */
	private String replaceMapLabel(String cmd) {
		return cmd.replaceAll("-map [\\[\\]a-z0-9\\:_]+ ", "-map ");
	}

}
