package org.fagu.fmv.ffmpeg.filter.impl;

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

import java.awt.Font;
import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class Drawtext extends AbstractFilter {

	protected Drawtext() {
		super("drawtext");
		font("arial");
	}

	public static Drawtext build() {
		return new Drawtext();
	}

	public Drawtext font(String fontName) {
		parameter("font", fontName);
		return this;
	}

	public Drawtext font(Font font) {
		return font(font.getName());
	}

	public Drawtext fontFile(File file) {
		String fontPath = file.getPath();
		if(fontPath.matches("[a-zA-Z]\\:.*")) { // for windows
			fontPath = fontPath.substring(2);
		}
		fontPath = fontPath.replace('\\', '/');
		parameter("fontfile", fontPath);
		return this;
	}

	public Drawtext fontColor(org.fagu.fmv.ffmpeg.utils.Color color) {
		parameter("fontcolor", color.toString());
		return this;
	}

	public Drawtext boxColor(org.fagu.fmv.ffmpeg.utils.Color color) {
		parameter("boxcolor", color.toString());
		return this;
	}

	public Drawtext text(String text) {
		parameter("text", text);
		return this;
	}

	public Drawtext textTimestampAndFrameNumber() {
		parameter("text", "%{pts} / %{n}");
		return this;
	}

	public Drawtext textTimestamp() {
		parameter("text", "%{pts}");
		return this;
	}

	public Drawtext textFrameNumber() {
		parameter("text", "%{n}");
		return this;
	}

	public Drawtext enable(String expr) {
		parameter("enable", expr);
		return this;
	}

	public Drawtext enableTime(Time startTime, Duration duration) {
		Time endTime = Time.valueOf(startTime.toSeconds() + duration.toSeconds());
		parameter("enable", "'between(t," + startTime.toSeconds() + "," + endTime.toSeconds() + ")'");
		return this;
	}

	public Drawtext x(String x) {
		parameter("x", x);
		return this;
	}

	public Drawtext x(int x) {
		return x(Integer.toString(x));
	}

	public Drawtext setXSlidingRightToLeft() {
		return x("-50*t");
	}

	public Drawtext y(String y) {
		parameter("y", y);
		return this;
	}

	public Drawtext y(int y) {
		return y(Integer.toString(y));
	}

	public Drawtext yTop() {
		return y(0);
	}

	public Drawtext yBottom() {
		return y("main_h-text_h");
	}

	/**
	 * Like credits at the end of a movie
	 * 
	 * @return
	 */
	public Drawtext ySlidingBottomToTop() {
		return y("h-20*t");
	}

	public Drawtext xyCenter() {
		return x("(w-text_w)/2").y("(h-text_h-line_h)/2");
	}

	public Drawtext fontSize(int size) {
		parameter("fontsize", Integer.toString(size));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

}
