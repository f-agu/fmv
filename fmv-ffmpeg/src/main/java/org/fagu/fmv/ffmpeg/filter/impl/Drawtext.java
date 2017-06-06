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
import org.fagu.fmv.ffmpeg.utils.FontUtils;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class Drawtext extends AbstractFilter {

	/**
	 * 
	 */
	protected Drawtext() {
		super("drawtext");
		font("arial");
	}

	/**
	 * @return
	 */
	public static Drawtext build() {
		return new Drawtext();
	}

	/**
	 * @param fontName
	 */
	public Drawtext font(String fontName) {
		File file = FontUtils.getFile(fontName);
		return fontFile(file);
	}

	/**
	 * @param font
	 * @return
	 */
	public Drawtext font(Font font) {
		File file = FontUtils.getFile(font);
		return fontFile(file);
	}

	/**
	 * @param file
	 * @return
	 */
	public Drawtext fontFile(File file) {
		String fontPath = file.getPath();
		if(fontPath.matches("[a-zA-Z]\\:.*")) { // for windows
			fontPath = fontPath.substring(2);
		}
		fontPath = fontPath.replace('\\', '/');
		parameter("fontfile", fontPath);
		return this;
	}

	/**
	 * @param color
	 * @return
	 */
	public Drawtext fontColor(org.fagu.fmv.ffmpeg.utils.Color color) {
		parameter("fontcolor", color.toString());
		return this;
	}

	/**
	 * @param color
	 * @return
	 */
	public Drawtext boxColor(org.fagu.fmv.ffmpeg.utils.Color color) {
		parameter("boxcolor", color.toString());
		return this;
	}

	/**
	 * @param text
	 * @return
	 */
	public Drawtext text(String text) {
		parameter("text", text);
		return this;
	}

	/**
	 * @return
	 */
	public Drawtext textTimestampAndFrameNumber() {
		parameter("text", "%{pts} / %{n}");
		return this;
	}

	/**
	 * @return
	 */
	public Drawtext textTimestamp() {
		parameter("text", "%{pts}");
		return this;
	}

	/**
	 * @return
	 */
	public Drawtext textFrameNumber() {
		parameter("text", "%{n}");
		return this;
	}

	/**
	 * @param expr
	 * @return
	 */
	public Drawtext enable(String expr) {
		parameter("enable", expr);
		return this;
	}

	/**
	 * @param startTime
	 * @param duration
	 * @return
	 */
	public Drawtext enableTime(Time startTime, Duration duration) {
		Time endTime = Time.valueOf(startTime.toSeconds() + duration.toSeconds());
		parameter("enable", "'between(t," + startTime.toSeconds() + "," + endTime.toSeconds() + ")'");
		return this;
	}

	/**
	 * @param x
	 * @return
	 */
	public Drawtext x(String x) {
		parameter("x", x);
		return this;
	}

	/**
	 * @param x
	 * @return
	 */
	public Drawtext x(int x) {
		return x(Integer.toString(x));
	}

	/**
	 * @param x
	 * @return
	 */
	public Drawtext setXSlidingRightToLeft() {
		return x("-50*t");
	}

	/**
	 * @param y
	 * @return
	 */
	public Drawtext y(String y) {
		parameter("y", y);
		return this;
	}

	/**
	 * @param y
	 * @return
	 */
	public Drawtext y(int y) {
		return y(Integer.toString(y));
	}

	/**
	 * @return
	 */
	public Drawtext yTop() {
		return y(0);
	}

	/**
	 * @return
	 */
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

	/**
	 * @return
	 */
	public Drawtext xyCenter() {
		return x("(w-text_w)/2").y("(h-text_h-line_h)/2");
	}

	/**
	 * @param size
	 * @return
	 */
	public Drawtext fontSize(int size) {
		parameter("fontsize", Integer.toString(size));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

}
