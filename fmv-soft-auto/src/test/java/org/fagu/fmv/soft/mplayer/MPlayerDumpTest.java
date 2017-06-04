package org.fagu.fmv.soft.mplayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.mplayer.MPlayerDump.MPlayerDumpBuilder;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class MPlayerDumpTest {

	@Test
	public void testBuilderLineParse() {
		System.out.println(MPlayerDumpBuilder.parse("audio stream: 0 format: ac3 (5.1) language: en aid: 128."));
	}

	@Test
	public void testBuilderLineParse2() {
		Pattern pattern = Pattern.compile("dump: \\d+ bytes written \\(~(\\d+).\\d+%\\)");
		Matcher matcher = pattern.matcher("dump: 35966976 bytes written (~73.8%)");
		if(matcher.matches()) {
			System.out.println(matcher.group(1));
		}
	}

}
