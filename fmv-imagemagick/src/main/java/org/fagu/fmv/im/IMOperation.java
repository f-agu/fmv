package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-imagemagick
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.exec.CommandLineUtils;


/**
 * @author f.agu
 * @created 16 janv. 2017 10:33:32
 */
public class IMOperation {

	private final LinkedList<String> arguments = new LinkedList<>();

	/**
	 * @param arg0
	 * @param args
	 * @return
	 */
	public IMOperation add(String arg0, String... args) {
		arguments.add(Objects.requireNonNull(arg0));
		for(String arg : args) {
			arguments.add(Objects.requireNonNull(arg));
		}
		return this;
	}

	/**
	 * @param args
	 * @return
	 */
	public IMOperation addAll(Collection<String> args) {
		arguments.addAll(Objects.requireNonNull(args));
		return this;
	}

	// ======= image =======

	public IMOperation image(String file) {
		return add(file);
	}

	public IMOperation image(String file, String special) {
		return add(file + (special != null ? special : ""));
	}

	public IMOperation image(File file) {
		return image(file.getAbsolutePath());
	}

	public IMOperation image(File file, String special) {
		return image(file.getAbsolutePath(), special);
	}

	// ======= -adaptive-blur =======
	public IMOperation adaptiveBlur() {
		return add("-adaptive-blur");
	}

	public IMOperation adaptiveBlur(double radius) {
		return adaptiveBlur().add(Double.toString(radius));
	}

	public IMOperation adaptiveBlur(double radius, double sigma) {
		return adaptiveBlur(radius).add(Double.toString(radius) + "x" + Double.toString(sigma));
	}

	// ======= -adaptive-resize =======
	public IMOperation adaptiveResize() {
		return add("-adaptive-resize");
	}

	public IMOperation adaptiveResize(int width) {
		return adaptiveResize().add(Integer.toString(width));
	}

	public IMOperation adaptiveResize(int width, int height) {
		return adaptiveResize(width).add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation adaptiveResize(int width, int height, Character special) {
		adaptiveResize(width, height);
		if(special != null) {
			add(special.toString());
		}
		return this;
	}

	public IMOperation adaptiveResize(int width, int height, String special) {
		adaptiveResize(width, height);
		if(StringUtils.isNotEmpty(special)) {
			add(special.toString());
		}
		return this;
	}

	// ======= -adaptive-sharpen =======
	public IMOperation adaptiveSharpen() {
		return add("-adaptive-sharpen");
	}

	public IMOperation adaptiveSharpen(double radius) {
		return adaptiveSharpen().add(Double.toString(radius));
	}

	public IMOperation adaptiveSharpen(double radius, double sigma) {
		return adaptiveSharpen(radius).add(Double.toString(radius) + "x" + Double.toString(sigma));
	}

	// ======= -adjoin =======
	public IMOperation adjoin() {
		return add("-adjoin");
	}

	public IMOperation p_adjoin() {
		return add("+adjoin");
	}

	// ======= -alpha =======
	public IMOperation alpha() {
		return add("-alpha");
	}

	public IMOperation alpha(String type) {
		alpha();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -antialias =======
	public IMOperation antialias() {
		return add("-antialias");
	}

	public IMOperation p_antialias() {
		return add("+antialias");
	}

	// ======= -append =======
	public IMOperation append() {
		return add("-append");
	}

	public IMOperation p_append() {
		return add("+append");
	}

	// ======= -attenuate =======
	public IMOperation attenuate() {
		return add("-attenuate");
	}

	public IMOperation attenuate(double value) {
		return attenuate().add(Double.toString(value));
	}

	// ======= -authenticate =======
	public IMOperation authenticate() {
		return add("-authenticate");
	}

	public IMOperation authenticate(String password) {
		authenticate();
		if(StringUtils.isNotEmpty(password)) {
			add(password);
		}
		return this;
	}

	// ======= -auto-gamma =======
	public IMOperation autoGamma() {
		return add("-auto-gamma");
	}

	// ======= -auto-level =======
	public IMOperation autoLevel() {
		return add("-auto-level");
	}

	// ======= -auto-orient =======
	public IMOperation autoOrient() {
		return add("-auto-orient");
	}

	// ======= -average =======
	public IMOperation average() {
		return add("-average");
	}

	// ======= -backdrop =======
	public IMOperation backdrop() {
		return add("-backdrop");
	}

	public IMOperation backdrop(String color) {
		backdrop();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -background =======
	public IMOperation background() {
		return add("-background");
	}

	public IMOperation background(String color) {
		background();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -bench =======
	public IMOperation bench() {
		return add("-bench");
	}

	public IMOperation bench(int iterations) {
		return bench().add(Integer.toString(iterations));
	}

	// ======= -blend =======
	public IMOperation blend() {
		return add("-blend");
	}

	public IMOperation blend(int srcPercent) {
		return blend().add(Integer.toString(srcPercent));
	}

	public IMOperation blend(int srcPercent, int dstPercent) {
		return blend().add(Integer.toString(srcPercent) + "x" + Integer.toString(dstPercent));
	}

	// ======= -bias =======
	public IMOperation bias() {
		return add("-bias");
	}

	public IMOperation bias(int value) {
		return bias().add(Integer.toString(value));
	}

	public IMOperation bias(int value, boolean percent) {
		return bias().add(Integer.toString(value) + (percent ? "%" : ""));
	}

	// ======= -black-point-compensation =======
	public IMOperation blackPointCompensation() {
		return add("-black-point-compensation");
	}

	// ======= -black-threshold =======
	public IMOperation blackThreshold() {
		return add("-black-threshold");
	}

	public IMOperation blackThreshold(double threshold) {
		return blackThreshold().add(Double.toString(threshold));
	}

	public IMOperation blackThreshold(double threshold, boolean percent) {
		return blackThreshold().add(Double.toString(threshold) + (percent ? "%" : ""));
	}

	// ======= -blue-primary =======
	public IMOperation bluePrimary() {
		return add("-blue-primary");
	}

	public IMOperation bluePrimary(double x) {
		return bluePrimary().add(Double.toString(x));
	}

	public IMOperation bluePrimary(double x, double y) {
		return bluePrimary().add(Double.toString(x) + "," + Double.toString(y));
	}

	// ======= -blue-shift =======
	public IMOperation blueShift() {
		return add("-blue-shift");
	}

	public IMOperation blueShift(double factor) {
		return blueShift().add(Double.toString(factor));
	}

	// ======= -blur =======
	public IMOperation blur() {
		return add("-blur");
	}

	public IMOperation blur(double radius) {
		return blur().add(Double.toString(radius));
	}

	public IMOperation blur(double radius, double sigma) {
		return blur().add(Double.toString(radius) + "x" + Double.toString(sigma));
	}

	// ======= -bordercolor =======
	public IMOperation bordercolor() {
		return add("-bordercolor");
	}

	public IMOperation bordercolor(String color) {
		bordercolor();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -border =======
	public IMOperation border() {
		return add("-border");
	}

	public IMOperation border(int width) {
		return border().add(Integer.toString(width));
	}

	public IMOperation border(int width, int height) {
		return border().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	// ======= -borderwidth =======
	public IMOperation borderwidth() {
		return add("-borderwidth");
	}

	public IMOperation borderwidth(int width) {
		return borderwidth().add(Integer.toString(width));
	}

	public IMOperation borderwidth(int width, int height) {
		return borderwidth().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation borderwidth(int width, int height, int xOffset) {
		return borderwidth().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Double.toString(xOffset));
	}

	public IMOperation borderwidth(int width, int height, int xOffset, int yOffset) {
		return borderwidth().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Double.toString(xOffset) + "+" + Double.toString(
				yOffset));
	}

	// ======= -brightness-contrast =======
	public IMOperation brightnessContrast() {
		return add("-brightness-contrast");
	}

	public IMOperation brightnessContrast(double brightness) {
		return brightnessContrast().add(Double.toString(brightness));
	}

	public IMOperation brightnessContrast(double brightness, double contrast) {
		return brightnessContrast().add(Double.toString(brightness) + "x" + Double.toString(contrast));
	}

	public IMOperation brightnessContrast(double brightness, double contrast, boolean percent) {
		return brightnessContrast().add(Double.toString(brightness) + "x" + Double.toString(contrast) + (percent ? "%" : ""));
	}

	// ======= -cache =======
	public IMOperation cache() {
		return add("-cache");
	}

	public IMOperation cache(double threshold) {
		return cache().add(Double.toString(threshold));
	}

	// ======= -caption =======
	public IMOperation caption() {
		return add("-caption");
	}

	public IMOperation caption(String text) {
		caption();
		if(StringUtils.isNotEmpty(text)) {
			add(text);
		}
		return this;
	}

	// ======= -cdl =======
	public IMOperation cdl() {
		return add("-cdl");
	}

	public IMOperation cdl(String filename) {
		cdl();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	// ======= -channel =======
	public IMOperation channel() {
		return add("-channel");
	}

	public IMOperation channel(String type) {
		channel();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	public IMOperation p_channel() {
		return add("+channel");
	}

	// ======= -charcoal =======
	public IMOperation charcoal() {
		return add("-charcoal");
	}

	public IMOperation charcoal(int factor) {
		return charcoal().add(Integer.toString(factor));
	}

	// ======= -clamp =======
	public IMOperation clamp() {
		return add("-clamp");
	}

	// ======= -clip =======
	public IMOperation clip() {
		return add("-clip");
	}

	// ======= -clipMask =======
	public IMOperation clipMask() {
		return add("-clip-mask");
	}

	// ======= -clipPath =======
	public IMOperation clipPath() {
		return add("-clip-path");
	}

	public IMOperation clipPath(String id) {
		clipPath();
		if(StringUtils.isNotEmpty(id)) {
			add(id);
		}
		return this;
	}

	public IMOperation p_clipPath() {
		return add("+clip-path");
	}

	public IMOperation p_clipPath(String id) {
		p_clipPath();
		if(StringUtils.isNotEmpty(id)) {
			add(id);
		}
		return this;
	}

	// ======= -clut =======
	public IMOperation clut() {
		return add("-clut");
	}

	// ======= -coalesce =======
	public IMOperation coalesce() {
		return add("-coalesce");
	}

	// ======= -colorize =======
	public IMOperation colorize() {
		return add("-colorize");
	}

	public IMOperation colorize(int red) {
		return colorize().add(Integer.toString(red));
	}

	public IMOperation colorize(int red, int blue) {
		return colorize().add(Integer.toString(red) + "," + Integer.toString(blue));
	}

	public IMOperation colorize(int red, int blue, int green) {
		return colorize().add(Integer.toString(red) + "," + Integer.toString(blue) + "," + Integer.toString(green));
	}

	// ======= -colormap =======
	public IMOperation colormap() {
		return add("-colormap");
	}

	public IMOperation colormap(String type) {
		colormap();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -colorMatrix =======
	public IMOperation colorMatrix() {
		return add("-color-matrix");
	}

	public IMOperation colorMatrix(String matrix) {
		colorMatrix();
		if(StringUtils.isNotEmpty(matrix)) {
			add(matrix);
		}
		return this;
	}

	// ======= -colors =======
	public IMOperation colors() {
		return add("-colors");
	}

	public IMOperation colors(int value) {
		return colors().add(Integer.toString(value));
	}

	// ======= -colorspace =======
	public IMOperation colorspace() {
		return add("-colorspace");
	}

	public IMOperation colorspace(String value) {
		colorspace();
		if(StringUtils.isNotEmpty(value)) {
			add(value);
		}
		return this;
	}

	// ======= -combine =======
	public IMOperation combine() {
		return add("-combine");
	}

	// ======= -comment =======
	public IMOperation comment() {
		return add("-comment");
	}

	public IMOperation comment(String text) {
		comment();
		if(StringUtils.isNotEmpty(text)) {
			add(text);
		}
		return this;
	}

	// ======= -compose =======
	public IMOperation compose() {
		return add("-compose");
	}

	public IMOperation compose(String operator) {
		compose();
		if(StringUtils.isNotEmpty(operator)) {
			add(operator);
		}
		return this;
	}

	// ======= -composite =======
	public IMOperation composite() {
		return add("-composite");
	}

	// ======= -compress =======
	public IMOperation compress() {
		return add("-compress");
	}

	public IMOperation compress(String type) {
		compress();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	public IMOperation p_compress() {
		return add("+compress");
	}

	// ======= -contrast =======
	public IMOperation contrast() {
		return add("-contrast");
	}

	public IMOperation p_contrast() {
		return add("+contrast");
	}

	// ======= -cycle =======
	public IMOperation cycle() {
		return add("-cycle");
	}

	public IMOperation cycle(int amount) {
		return cycle().add(Integer.toString(amount));
	}

	// ======= -debug =======
	public IMOperation debug() {
		return add("-debug");
	}

	public IMOperation debug(String events) {
		debug();
		if(StringUtils.isNotEmpty(events)) {
			add(events);
		}
		return this;
	}

	public IMOperation p_debug() {
		return add("+debug");
	}

	// ======= -decipher =======
	public IMOperation decipher() {
		return add("-decipher");
	}

	public IMOperation decipher(String filename) {
		decipher();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	// ======= -deconstruct =======
	public IMOperation deconstruct() {
		return add("-deconstruct");
	}

	// ======= -define =======
	public IMOperation define() {
		return add("-define");
	}

	public IMOperation define(String keyValue) {
		define();
		if(StringUtils.isNotEmpty(keyValue)) {
			add(keyValue);
		}
		return this;
	}

	public IMOperation p_define() {
		return add("+define");
	}

	public IMOperation p_define(String key) {
		p_define();
		if(StringUtils.isNotEmpty(key)) {
			add(key);
		}
		return this;
	}

	// ======= -density =======
	public IMOperation density() {
		return add("-density");
	}

	public IMOperation density(int width) {
		return density().add(Integer.toString(width));
	}

	public IMOperation density(int width, int height) {
		return density().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	// ======= -depth =======
	public IMOperation depth() {
		return add("-depth");
	}

	public IMOperation depth(int value) {
		return depth().add(Integer.toString(value));
	}

	// ======= -descend =======
	public IMOperation descend() {
		return add("-descend");
	}

	// ======= -deskew =======
	public IMOperation deskew() {
		return add("-deskew");
	}

	public IMOperation deskew(double threshold) {
		return deskew().add(Double.toString(threshold));
	}

	// ======= -despeckle =======
	public IMOperation despeckle() {
		return add("-despeckle");
	}

	// ======= -direction =======
	public IMOperation direction() {
		return add("-direction");
	}

	public IMOperation direction(String type) {
		direction();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -displace =======
	public IMOperation displace() {
		return add("-displace");
	}

	public IMOperation displace(double horizontalScale) {
		return displace().add(Double.toString(horizontalScale));
	}

	public IMOperation displace(double horizontalScale, double verticalScale) {
		return displace().add(Double.toString(horizontalScale) + "x" + Double.toString(verticalScale));
	}

	// ======= -dispose =======
	public IMOperation dispose() {
		return add("-dispose");
	}

	public IMOperation dispose(String method) {
		dispose();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	public IMOperation p_dispose() {
		return add("+dispose");
	}

	// ======= -dissimilarityThreshold =======
	public IMOperation dissimilarityThreshold() {
		return add("-dissimilarity-threshold");
	}

	public IMOperation dissimilarityThreshold(double value) {
		return dissimilarityThreshold().add(Double.toString(value));
	}

	// ======= -dissolve =======
	public IMOperation dissolve() {
		return add("-dissolve");
	}

	public IMOperation dissolve(int percent) {
		return dissolve().add(Integer.toString(percent));
	}

	// ======= -distort =======
	public IMOperation distort() {
		return add("-distort");
	}

	public IMOperation distort(String method) {
		distort();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	public IMOperation distort(String method, String arguments) {
		distort();
		StringBuilder buf = new StringBuilder();
		if(method != null) {
			buf.append(method.toString());
		}
		if(method != null || arguments != null) {
			add(buf.toString());
			buf.setLength(0);
		}
		if(arguments != null) {
			buf.append(arguments.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	public IMOperation p_distort() {
		return add("+distort");
	}

	public IMOperation p_distort(String method) {
		p_distort();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	public IMOperation p_distort(String method, String arguments) {
		p_distort();
		StringBuilder buf = new StringBuilder();
		if(method != null) {
			buf.append(method.toString());
		}
		if(method != null || arguments != null) {
			add(buf.toString());
			buf.setLength(0);
		}
		if(arguments != null) {
			buf.append(arguments.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	// ======= -dither =======
	public IMOperation dither() {
		return add("-dither");
	}

	public IMOperation dither(String method) {
		dither();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	public IMOperation p_dither() {
		return add("+dither");
	}

	// ======= -draw =======
	public IMOperation draw() {
		return add("-draw");
	}

	public IMOperation draw(String string) {
		draw();
		if(StringUtils.isNotEmpty(string)) {
			add(string);
		}
		return this;
	}

	// ======= -duplicate =======
	public IMOperation duplicate() {
		return add("-duplicate");
	}

	public IMOperation duplicate(int count) {
		return duplicate().add(Integer.toString(count));
	}

	public IMOperation duplicate(int count, String indices) {
		return duplicate().add(Integer.toString(count) + (StringUtils.isEmpty(indices) ? "" : "," + indices));
	}

	public IMOperation p_duplicate() {
		return add("+duplicate");
	}

	// ======= -edge =======
	public IMOperation edge() {
		return add("-edge");
	}

	public IMOperation edge(double radius) {
		return edge().add(Double.toString(radius));
	}

	// ======= -emboss =======
	public IMOperation emboss() {
		return add("-emboss");
	}

	public IMOperation emboss(double radius) {
		return emboss().add(Double.toString(radius));
	}

	// ======= -encipher =======
	public IMOperation encipher() {
		return add("-encipher");
	}

	public IMOperation encipher(String filename) {
		encipher();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	// ======= -encoding =======
	public IMOperation encoding() {
		return add("-encoding");
	}

	public IMOperation encoding(String type) {
		encoding();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -endian =======
	public IMOperation endian() {
		return add("-endian");
	}

	public IMOperation endian(String type) {
		endian();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	public IMOperation p_endian() {
		return add("+endian");
	}

	// ======= -enhance =======
	public IMOperation enhance() {
		return add("-enhance");
	}

	// ======= -equalize =======
	public IMOperation equalize() {
		return add("-equalize");
	}

	// ======= -evaluate =======
	public IMOperation evaluate() {
		return add("-evaluate");
	}

	public IMOperation evaluate(String operator) {
		evaluate();
		if(StringUtils.isNotEmpty(operator)) {
			add(operator);
		}
		return this;
	}

	public IMOperation evaluate(String operator, String constant) {
		evaluate();
		StringBuilder buf = new StringBuilder();
		if(operator != null) {
			buf.append(operator.toString());
		}
		if(operator != null || constant != null) {
			add(buf.toString());
			buf.setLength(0);
		}
		if(constant != null) {
			buf.append(constant.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	// ======= -evaluateSequence =======
	public IMOperation evaluateSequence() {
		return add("-evaluate-sequence");
	}

	public IMOperation evaluateSequence(String operator) {
		evaluateSequence();
		if(StringUtils.isNotEmpty(operator)) {
			add(operator);
		}
		return this;
	}

	// ======= -extent =======
	public IMOperation extent() {
		return add("-extent");
	}

	public IMOperation extent(int width) {
		return extent().add(Integer.toString(width));
	}

	public IMOperation extent(int width, int height) {
		return extent().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation extent(int width, int height, int xOffset) {
		return extent().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(xOffset));
	}

	public IMOperation extent(int width, int height, int xOffset, int yOffset) {
		return extent().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(xOffset) + "+" + Integer.toString(
				yOffset));
	}

	// ======= -family =======
	public IMOperation family() {
		return add("-family");
	}

	public IMOperation family(String fontFamily) {
		family();
		if(StringUtils.isNotEmpty(fontFamily)) {
			add(fontFamily);
		}
		return this;
	}

	// ======= -features =======
	public IMOperation features() {
		return add("-features");
	}

	public IMOperation features(String distance) {
		features();
		if(StringUtils.isNotEmpty(distance)) {
			add(distance);
		}
		return this;
	}

	// ======= -fft =======
	public IMOperation fft() {
		return add("-fft");
	}

	// ======= -fill =======
	public IMOperation fill() {
		return add("-fill");
	}

	public IMOperation fill(String color) {
		fill();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -filter =======
	public IMOperation filter() {
		return add("-filter");
	}

	public IMOperation filter(String type) {
		filter();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -flatten =======
	public IMOperation flatten() {
		return add("-flatten");
	}

	// ======= -flip =======
	public IMOperation flip() {
		return add("-flip");
	}

	// ======= -flop =======
	public IMOperation flop() {
		return add("-flop");
	}

	// ======= -font =======
	public IMOperation font() {
		return add("-font");
	}

	public IMOperation font(String name) {
		font();
		if(StringUtils.isNotEmpty(name)) {
			add(name);
		}
		return this;
	}

	// ======= -foreground =======
	public IMOperation foreground() {
		return add("-foreground");
	}

	public IMOperation foreground(String color) {
		foreground();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -format =======
	public IMOperation format() {
		return add("-format");
	}

	public IMOperation format(String type) {
		format();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -frame =======
	public IMOperation frame() {
		return add("-frame");
	}

	public IMOperation frame(int width) {
		return frame().add(Integer.toString(width));
	}

	// ======= -function =======
	public IMOperation function() {
		return add("-function");
	}

	public IMOperation function(String name) {
		function();
		if(StringUtils.isNotEmpty(name)) {
			add(name);
		}
		return this;
	}

	public IMOperation function(String name, String parameter) {
		function();
		StringBuilder buf = new StringBuilder(); // local buffer for option-args
		if(name != null) {
			buf.append(name.toString());
		}
		if(name != null || parameter != null) {
			add(buf.toString());
			buf.setLength(0);
		}
		if(parameter != null) {
			buf.append(parameter.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	// ======= -fuzz =======
	public IMOperation fuzz() {
		return add("-fuzz");
	}

	public IMOperation fuzz(double distance) {
		return fuzz().add(Double.toString(distance));
	}

	public IMOperation fuzz(double distance, boolean percent) {
		return fuzz().add(Double.toString(distance) + (percent ? "%" : ""));
	}

	// ======= -fx =======
	public IMOperation fx() {
		return add("-fx");
	}

	public IMOperation fx(String expression) {
		fx();
		if(StringUtils.isNotEmpty(expression)) {
			add(expression);
		}
		return this;
	}

	// ======= -gamma =======
	public IMOperation gamma() {
		return add("-gamma");
	}

	public IMOperation gamma(double value) {
		return gamma().add(Double.toString(value));
	}

	public IMOperation p_gamma() {
		return add("+gamma");
	}

	public IMOperation p_gamma(double value) {
		return p_gamma().add(Double.toString(value));
	}

	// ======= -gaussian-blur =======
	public IMOperation gaussianBlur() {
		return add("-gaussian-blur");
	}

	public IMOperation gaussianBlur(double radius) {
		return gaussianBlur().add(Double.toString(radius));
	}

	public IMOperation gaussianBlur(double radius, double sigma) {
		return gaussianBlur().add(Double.toString(radius) + "x" + Double.toString(sigma));
	}

	// ======= -gravity =======
	public IMOperation gravity() {
		return add("-gravity");
	}

	public IMOperation gravity(String type) {
		gravity();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -greenPrimary =======
	public IMOperation greenPrimary() {
		return add("-green-primary");
	}

	public IMOperation greenPrimary(double x) {
		return greenPrimary().add(Double.toString(x));
	}

	public IMOperation greenPrimary(double x, double y) {
		return greenPrimary().add(Double.toString(x) + "," + Double.toString(y));
	}

	// ======= -help =======
	public IMOperation help() {
		return add("-help");
	}

	// ======= -haldClut =======
	public IMOperation haldClut() {
		return add("-hald-clut");
	}

	// ======= -highlightColor =======
	public IMOperation highlightColor() {
		return add("-highlight-color");
	}

	public IMOperation highlightColor(String color) {
		highlightColor();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -iconic =======
	public IMOperation iconic() {
		return add("-iconic");
	}

	// ======= -identify =======
	public IMOperation identify() {
		return add("-identify");
	}

	// ======= -ift =======
	public IMOperation ift() {
		return add("-ift");
	}

	// ======= -immutable =======
	public IMOperation immutable() {
		return add("-immutable");
	}

	// ======= -implode =======
	public IMOperation implode() {
		return add("-implode");
	}

	public IMOperation implode(double factor) {
		return implode().add(Double.toString(factor));
	}

	// ======= -insert =======
	public IMOperation insert() {
		return add("-insert");
	}

	public IMOperation insert(int index) {
		return insert().add(Integer.toString(index));
	}

	// ======= -intent =======
	public IMOperation intent() {
		return add("-intent");
	}

	public IMOperation intent(String type) {
		intent();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -interlace =======
	public IMOperation interlace() {
		return add("-interlace");
	}

	public IMOperation interlace(String type) {
		interlace();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -interlineSpacing =======
	public IMOperation interlineSpacing() {
		return add("-interline-spacing");
	}

	public IMOperation interlineSpacing(double value) {
		return interlineSpacing().add(Double.toString(value));
	}

	// ======= -interpolate =======
	public IMOperation interpolate() {
		return add("-interpolate");
	}

	public IMOperation interpolate(String type) {
		interpolate();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -interwordSpacing =======
	public IMOperation interwordSpacing() {
		return add("-interword-spacing");
	}

	public IMOperation interwordSpacing(double value) {
		return interwordSpacing().add(Double.toString(value));
	}

	// ======= -kerning =======
	public IMOperation kerning() {
		return add("-kerning");
	}

	public IMOperation kerning(int value) {
		return kerning().add(Integer.toString(value));
	}

	// ======= -p_label =======
	public IMOperation p_label() {
		return add("+label");
	}

	// ======= -label =======
	public IMOperation label() {
		return add("-label");
	}

	public IMOperation label(String name) {
		label();
		if(StringUtils.isNotEmpty(name)) {
			add(name);
		}
		return this;
	}

	// ======= -layers =======
	public IMOperation layers() {
		return add("-layers");
	}

	public IMOperation layers(String method) {
		layers();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	// ======= -limit =======
	public IMOperation limit() {
		return add("-limit");
	}

	public IMOperation limit(String type) {
		limit();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -linewidth =======
	public IMOperation linewidth() {
		return add("-linewidth");
	}

	// ======= -log =======
	public IMOperation log() {
		return add("-log");
	}

	public IMOperation log(String text) {
		log();
		if(StringUtils.isNotEmpty(text)) {
			add(text);
		}
		return this;
	}

	// ======= -loop =======
	public IMOperation loop() {
		return add("-loop");
	}

	public IMOperation loop(int iterations) {
		return loop().add(Integer.toString(iterations));
	}

	// ======= -magnify =======
	public IMOperation magnify() {
		return add("-magnify");
	}

	public IMOperation magnify(double factor) {
		return magnify().add(Double.toString(factor));
	}

	// ======= -map =======
	public IMOperation map() {
		return add("-map");
	}

	public IMOperation map(String components) {
		map();
		if(StringUtils.isNotEmpty(components)) {
			add(components);
		}
		return this;
	}

	public IMOperation p_map() {
		return add("+map");
	}

	// ======= -mask =======
	public IMOperation mask() {
		return add("-mask");
	}

	public IMOperation mask(String filename) {
		mask();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	public IMOperation p_mask() {
		return add("+mask");
	}

	// ======= -mattecolor =======
	public IMOperation mattecolor() {
		return add("-mattecolor");
	}

	public IMOperation mattecolor(String color) {
		mattecolor();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -median =======
	public IMOperation median() {
		return add("-median");
	}

	public IMOperation median(double radius) {
		return median().add(Double.toString(radius));
	}

	// ======= -metric =======
	public IMOperation metric() {
		return add("-metric");
	}

	public IMOperation metric(String type) {
		metric();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -mode =======
	public IMOperation mode() {
		return add("-mode");
	}

	public IMOperation mode(String value) {
		mode();
		if(StringUtils.isNotEmpty(value)) {
			add(value);
		}
		return this;
	}

	// ======= -monitor =======
	public IMOperation monitor() {
		return add("-monitor");
	}

	// ======= -monochrome =======
	public IMOperation monochrome() {
		return add("-monochrome");
	}

	// ======= -morph =======
	public IMOperation morph() {
		return add("-morph");
	}

	public IMOperation morph(int frames) {
		return morph().add(Integer.toString(frames));
	}

	// ======= -morphology =======
	public IMOperation morphology() {
		return add("-morphology");
	}

	public IMOperation morphology(String method) {
		morphology();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	public IMOperation morphology(String method, String kernel) {
		morphology();
		StringBuilder buf = new StringBuilder(); // local buffer for option-args
		if(method != null) {
			buf.append(method.toString());
		}
		if(method != null || kernel != null) {
			add(buf.toString());
			buf.setLength(0);
		}
		if(kernel != null) {
			buf.append(kernel.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	// ======= -mosaic =======
	public IMOperation mosaic() {
		return add("-mosaic");
	}

	// ======= -name =======
	public IMOperation name() {
		return add("-name");
	}

	// ======= -negate =======
	public IMOperation negate() {
		return add("-negate");
	}

	// ======= -p_negate =======
	public IMOperation p_negate() {
		return add("+negate");
	}

	// ======= -noise =======
	public IMOperation noise() {
		return add("-noise");
	}

	public IMOperation noise(double radius) {
		return noise().add(Double.toString(radius));
	}

	public IMOperation p_noise() {
		return add("+noise");
	}

	public IMOperation p_noise(String type) {
		p_noise();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -normalize =======
	public IMOperation normalize() {
		return add("-normalize");
	}

	// ======= -opaque =======
	public IMOperation opaque() {
		return add("-opaque");
	}

	public IMOperation opaque(String color) {
		opaque();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	public IMOperation p_opaque() {
		return add("+opaque");
	}

	public IMOperation p_opaque(String color) {
		p_opaque();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -orderedDither =======
	public IMOperation orderedDither() {
		return add("-ordered-dither");
	}

	public IMOperation orderedDither(String threshold_map) {
		orderedDither();
		if(StringUtils.isNotEmpty(threshold_map)) {
			add(threshold_map);
		}
		return this;
	}

	public IMOperation orderedDither(String threshold_map, String level) {
		orderedDither();
		StringBuilder buf = new StringBuilder(); // local buffer for option-args
		if(threshold_map != null) {
			buf.append(threshold_map.toString());
		}
		if(threshold_map != null || level != null) {
			buf.append(",");
		}
		if(level != null) {
			buf.append(level.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	// ======= -orient =======
	public IMOperation orient() {
		return add("-orient");
	}

	public IMOperation orient(String imageOrientation) {
		orient();
		if(StringUtils.isNotEmpty(imageOrientation)) {
			add(imageOrientation);
		}
		return this;
	}

	// ======= -paint =======
	public IMOperation paint() {
		return add("-paint");
	}

	public IMOperation paint(double radius) {
		return paint().add(Double.toString(radius));
	}

	// ======= -path =======
	public IMOperation path() {
		return add("-path");
	}

	public IMOperation path(String path) {
		path();
		if(StringUtils.isNotEmpty(path)) {
			add(path);
		}
		return this;
	}

	// ======= -passphrase =======
	public IMOperation passphrase() {
		return add("-passphrase");
	}

	public IMOperation passphrase(String filename) {
		passphrase();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	// ======= -pause =======
	public IMOperation pause() {
		return add("-pause");
	}

	public IMOperation pause(int seconds) {
		return pause().add(Integer.toString(seconds));
	}

	// ======= -perceptible =======
	public IMOperation perceptible() {
		return add("-perceptible");
	}

	public IMOperation perceptible(double epsilon) {
		return perceptible().add(Double.toString(epsilon));
	}

	// ======= -ping =======
	public IMOperation ping() {
		return add("-ping");
	}

	// ======= -pointsize =======
	public IMOperation pointsize() {
		return add("-pointsize");
	}

	public IMOperation pointsize(int value) {
		return pointsize().add(Integer.toString(value));
	}

	// ======= -polaroid =======
	public IMOperation polaroid() {
		return add("-polaroid");
	}

	public IMOperation polaroid(double angle) {
		return polaroid().add(Double.toString(angle));
	}

	public IMOperation p_polaroid() {
		return add("+polaroid");
	}

	// ======= -poly =======
	public IMOperation poly() {
		return add("-poly");
	}

	public IMOperation poly(String args) {
		poly();
		if(StringUtils.isNotEmpty(args)) {
			add(args);
		}
		return this;
	}

	// ======= -posterize =======
	public IMOperation posterize() {
		return add("-posterize");
	}

	public IMOperation posterize(int levels) {
		return posterize().add(Integer.toString(levels));
	}

	// ======= -precision =======
	public IMOperation precision() {
		return add("-precision");
	}

	public IMOperation precision(int digits) {
		return precision().add(Integer.toString(digits));
	}

	// ======= -preview =======
	public IMOperation preview() {
		return add("-preview");
	}

	public IMOperation preview(String type) {
		preview();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -print =======
	public IMOperation print() {
		return add("-print");
	}

	public IMOperation print(String text) {
		print();
		if(StringUtils.isNotEmpty(text)) {
			add(text);
		}
		return this;
	}

	// ======= -process =======
	public IMOperation process() {
		return add("-process");
	}

	public IMOperation process(String command) {
		process();
		if(StringUtils.isNotEmpty(command)) {
			add(command);
		}
		return this;
	}

	// ======= -profile =======
	public IMOperation profile() {
		return add("-profile");
	}

	public IMOperation profile(String filename) {
		profile();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	// ======= -p_profile =======
	public IMOperation p_profile() {
		return add("+profile");
	}

	public IMOperation p_profile(String profileName) {
		p_profile();
		if(StringUtils.isNotEmpty(profileName)) {
			add(profileName);
		}
		return this;
	}

	// ======= -quality =======
	public IMOperation quality() {
		return add("-quality");
	}

	public IMOperation quality(double value) {
		return quality().add(Double.toString(value));
	}

	// ======= -quantize =======
	public IMOperation quantize() {
		return add("-quantize");
	}

	public IMOperation quantize(String colorspace) {
		quantize();
		if(StringUtils.isNotEmpty(colorspace)) {
			add(colorspace);
		}
		return this;
	}

	// ======= -quiet =======
	public IMOperation quiet() {
		return add("-quiet");
	}

	// ======= -radialBlur =======
	public IMOperation radialBlur() {
		return add("-radial-blur");
	}

	public IMOperation radialBlur(double angle) {
		return radialBlur().add(Double.toString(angle));
	}

	// ======= -raise =======
	public IMOperation raise() {
		return add("-raise");
	}

	public IMOperation raise(int width) {
		return raise().add(Integer.toString(width));
	}

	public IMOperation raise(int width, int height) {
		return raise().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation p_raise() {
		return add("+raise");
	}

	public IMOperation p_raise(int width) {
		return p_raise().add(Integer.toString(width));
	}

	public IMOperation p_raise(int width, int height) {
		return p_raise().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	// ======= -recolor =======
	public IMOperation recolor() {
		return add("-recolor");
	}

	public IMOperation recolor(String matrix) {
		recolor();
		if(StringUtils.isNotEmpty(matrix)) {
			add(matrix);
		}
		return this;
	}

	// ======= -redPrimary =======
	public IMOperation redPrimary() {
		return add("-red-primary");
	}

	public IMOperation redPrimary(double x) {
		return redPrimary().add(Double.toString(x));
	}

	public IMOperation redPrimary(double x, double y) {
		return redPrimary().add(Double.toString(x) + "," + Double.toString(y));
	}

	// ======= -regardWarnings =======
	public IMOperation regardWarnings() {
		return add("-regard-warnings");
	}

	// ======= -region =======
	public IMOperation region() {
		return add("-region");
	}

	public IMOperation region(int width) {
		return region().add(Integer.toString(width));
	}

	public IMOperation region(int width, int height) {
		return region().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation region(int width, int height, int x) {
		return region().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(x));
	}

	public IMOperation region(int width, int height, int x, int y) {
		return region().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(x) + "+" + Integer.toString(y));
	}

	// ======= -remap =======
	public IMOperation remap() {
		return add("-remap");
	}

	public IMOperation remap(String filename) {
		remap();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	public IMOperation p_remap() {
		return add("+remap");
	}

	// ======= -remote =======
	public IMOperation remote() {
		return add("-remote");
	}

	// ======= -render =======
	public IMOperation render() {
		return add("-render");
	}

	public IMOperation p_render() {
		return add("+render");
	}

	// ======= -p_repage =======
	public IMOperation p_repage() {
		return add("+repage");
	}

	// ======= -resample =======
	public IMOperation resample() {
		return add("-resample");
	}

	public IMOperation resample(int horizontal) {
		return resample().add(Integer.toString(horizontal));
	}

	public IMOperation resample(int horizontal, int vertical) {
		return resample().add(Integer.toString(horizontal) + "x" + Integer.toString(vertical));
	}

	// ======= -resize =======
	public IMOperation resize() {
		return add("-resize");
	}

	public IMOperation resize(int width) {
		return resize().add(Integer.toString(width));
	}

	public IMOperation resize(int width, int height) {
		return resize().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation resize(int width, int height, Character special) {
		return resize().add(Integer.toString(width) + "x" + Integer.toString(height) + (special != null ? special : ""));
	}

	public IMOperation resize(int width, int height, String special) {
		return resize().add(Integer.toString(width) + Integer.toString(height) + (special != null ? special : ""));
	}

	// ======= -respectParentheses =======
	public IMOperation respectParentheses() {
		return add("-respect-parentheses");
	}

	// ======= -respectParenthesis =======
	public IMOperation respectParenthesis() {
		return add("-respect-parenthesis");
	}

	// ======= -reverse =======
	public IMOperation reverse() {
		return add("-reverse");
	}

	// ======= -roll =======
	public IMOperation roll() {
		return add("-roll");
	}

	public IMOperation roll(int x) {
		return roll().add(Integer.toString(x));
	}

	public IMOperation roll(int x, int y) {
		return roll().add(Integer.toString(x) + "+" + Integer.toString(y));
	}

	// ======= -rotate =======
	public IMOperation rotate() {
		return add("-rotate");
	}

	public IMOperation rotate(double degrees) {
		return rotate().add(Double.toString(degrees));
	}

	public IMOperation rotate(double degrees, Character special) {
		return rotate().add(Double.toString(degrees) + (special != null ? special : ""));
	}

	// ======= -samplingFactor =======
	public IMOperation samplingFactor() {
		return add("-sampling-factor");
	}

	public IMOperation samplingFactor(double horizontalFactor) {
		return samplingFactor().add(Double.toString(horizontalFactor));
	}

	public IMOperation samplingFactor(double horizontalFactor, double verticalFactor) {
		return samplingFactor().add(Double.toString(horizontalFactor) + "x" + Double.toString(verticalFactor));
	}

	// ======= -selectiveBlur =======
	public IMOperation selectiveBlur() {
		return add("-selective-blur");
	}

	public IMOperation selectiveBlur(double radius) {
		return selectiveBlur().add(Double.toString(radius));
	}

	public IMOperation selectiveBlur(double radius, double sigma) {
		return selectiveBlur().add(Double.toString(radius) + "x" + Double.toString(sigma));
	}

	public IMOperation selectiveBlur(double radius, double sigma, double threshold) {
		return selectiveBlur().add(Double.toString(radius) + "x" + Double.toString(sigma) + "+" + Double.toString(threshold));
	}

	// ======= -sparseColor =======
	public IMOperation sparseColor() {
		return add("-sparse-color");
	}

	public IMOperation sparseColor(String method) {
		sparseColor();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	public IMOperation sparseColor(String method, String cinfo) {
		sparseColor();
		StringBuilder buf = new StringBuilder(); // local buffer for option-args
		if(method != null) {
			buf.append(method.toString());
		}
		if(method != null || cinfo != null) {
			add(buf.toString());
			buf.setLength(0);
		}
		if(cinfo != null) {
			buf.append(cinfo.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	// ======= -scale =======
	public IMOperation scale() {
		return add("-scale");
	}

	public IMOperation scale(int width) {
		return scale().add(Integer.toString(width));
	}

	public IMOperation scale(int width, int height) {
		return scale().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation scale(int width, int height, int xOffset) {
		return scale().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(xOffset));
	}

	public IMOperation scale(int width, int height, int xOffset, int yOffset) {
		return scale().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(xOffset) + "+" + Integer.toString(
				yOffset));
	}

	public IMOperation scale(String value) {
		scale();
		if(StringUtils.isNotEmpty(value)) {
			add(value);
		}
		return this;
	}

	// ======= -scene =======
	public IMOperation scene() {
		return add("-scene");
	}

	public IMOperation scene(int value) {
		return scene().add(Integer.toString(value));
	}

	// ======= -screen =======
	public IMOperation screen() {
		return add("-screen");
	}

	// ======= -seed =======
	public IMOperation seed() {
		return add("-seed");
	}

	// ======= -segment =======
	public IMOperation segment() {
		return add("-segment");
	}

	public IMOperation segment(int clusterThreshold) {
		return segment().add(Integer.toString(clusterThreshold));
	}

	public IMOperation segment(int clusterThreshold, double smoothingThreshold) {
		return segment().add(Integer.toString(clusterThreshold) + "x" + Double.toString(smoothingThreshold));
	}

	// ======= -separate =======
	public IMOperation separate() {
		return add("-separate");
	}

	// ======= -sepiaTone =======
	public IMOperation sepiaTone() {
		return add("-sepia-tone");
	}

	public IMOperation sepiaTone(double threshold) {
		return sepiaTone().add(Double.toString(threshold));
	}

	// ======= -set =======
	public IMOperation set() {
		return add("-set");
	}

	public IMOperation set(String attribute) {
		set();
		if(StringUtils.isNotEmpty(attribute)) {
			add(attribute);
		}
		return this;
	}

	public IMOperation set(String attribute, String value) {
		set();
		StringBuilder buf = new StringBuilder(); // local buffer for option-args
		if(attribute != null) {
			buf.append(attribute.toString());
		}
		if(attribute != null || value != null) {
			add(buf.toString());
			buf.setLength(0);
		}
		if(value != null) {
			buf.append(value.toString());
		}
		if(buf.length() > 0) {
			add(buf.toString());
		}
		return this;
	}

	// ======= -shade =======
	public IMOperation shade() {
		return add("-shade");
	}

	public IMOperation shade(double azimuth) {
		return shade().add(Double.toString(azimuth));
	}

	public IMOperation shade(double azimuth, double elevation) {
		return shade().add(Double.toString(azimuth) + "x" + Double.toString(elevation));
	}

	public IMOperation p_shade() {
		return add("+shade");
	}

	public IMOperation p_shade(double azimuth) {
		return p_shade().add(Double.toString(azimuth));
	}

	public IMOperation p_shade(double azimuth, double elevation) {
		return p_shade().add(Double.toString(azimuth) + "x" + Double.toString(elevation));
	}

	// ======= -sharedMemory =======
	public IMOperation sharedMemory() {
		return add("-shared-memory");
	}

	// ======= -sharpen =======
	public IMOperation sharpen() {
		return add("-sharpen");
	}

	public IMOperation sharpen(double radius) {
		return sharpen().add(Double.toString(radius));
	}

	public IMOperation sharpen(double radius, double sigma) {
		return sharpen().add(Double.toString(radius) + "x" + Double.toString(sigma));
	}

	// ======= -silent =======
	public IMOperation silent() {
		return add("-silent");
	}

	// ======= -size =======
	public IMOperation size() {
		return add("-size");
	}

	public IMOperation size(int width) {
		return size().add(Integer.toString(width));
	}

	public IMOperation size(int width, int height) {
		return size().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation size(int width, int height, int offset) {
		return size().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(offset));
	}

	// ======= -smush =======
	public IMOperation smush() {
		return add("-smush");
	}

	public IMOperation smush(int offset) {
		return smush().add(Integer.toString(offset));
	}

	// ======= -snaps =======
	public IMOperation snaps() {
		return add("-snaps");
	}

	public IMOperation snaps(int value) {
		return snaps().add(Integer.toString(value));
	}

	// ======= -solarize =======
	public IMOperation solarize() {
		return add("-solarize");
	}

	public IMOperation solarize(double threshold) {
		return solarize().add(Double.toString(threshold));
	}

	// ======= -spread =======
	public IMOperation spread() {
		return add("-spread");
	}

	public IMOperation spread(int amount) {
		return spread().add(Integer.toString(amount));
	}

	// ======= -stegano =======
	public IMOperation stegano() {
		return add("-stegano");
	}

	public IMOperation stegano(int offset) {
		return stegano().add(Integer.toString(offset));
	}

	// ======= -stereo =======
	public IMOperation stereo() {
		return add("-stereo");
	}

	public IMOperation stereo(int x) {
		return stereo().add(Integer.toString(x));
	}

	public IMOperation stereo(int x, int y) {
		return stereo().add(Integer.toString(x) + "+" + Integer.toString(y));
	}

	// ======= -storageType =======
	public IMOperation storageType() {
		return add("-storage-type");
	}

	public IMOperation storageType(String type) {
		storageType();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -stretch =======
	public IMOperation stretch() {
		return add("-stretch");
	}

	public IMOperation stretch(String fontStretchType) {
		stretch();
		if(StringUtils.isNotEmpty(fontStretchType)) {
			add(fontStretchType);
		}
		return this;
	}

	// ======= -strip =======
	public IMOperation strip() {
		return add("-strip");
	}

	// ======= -stroke =======
	public IMOperation stroke() {
		return add("-stroke");
	}

	public IMOperation stroke(String color) {
		stroke();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -strokewidth =======
	public IMOperation strokewidth() {
		return add("-strokewidth");
	}

	public IMOperation strokewidth(int value) {
		return strokewidth().add(Integer.toString(value));
	}

	// ======= -style =======
	public IMOperation style() {
		return add("-style");
	}

	public IMOperation style(String fontStyle) {
		style();
		if(StringUtils.isNotEmpty(fontStyle)) {
			add(fontStyle);
		}
		return this;
	}

	// ======= -subimageSearch =======
	public IMOperation subimageSearch() {
		return add("-subimage-search");
	}

	// ======= -p_swap =======
	public IMOperation p_swap() {
		return add("+swap");
	}

	// ======= -swap =======
	public IMOperation swap() {
		return add("-swap");
	}

	public IMOperation swap(int pos1) {
		return swap().add(Integer.toString(pos1));
	}

	public IMOperation swap(int pos1, int pos2) {
		return swap().add(Integer.toString(pos1) + "," + Integer.toString(pos2));
	}

	// ======= -swirl =======
	public IMOperation swirl() {
		return add("-swirl");
	}

	public IMOperation swirl(double degrees) {
		return swirl().add(Double.toString(degrees));
	}

	// ======= -synchronize =======
	public IMOperation synchronize() {
		return add("-synchronize");
	}

	// ======= -taint =======
	public IMOperation taint() {
		return add("-taint");
	}

	// ======= -textFont =======
	public IMOperation textFont() {
		return add("-text-font");
	}

	public IMOperation textFont(String name) {
		textFont();
		if(StringUtils.isNotEmpty(name)) {
			add(name);
		}
		return this;
	}

	// ======= -texture =======
	public IMOperation texture() {
		return add("-texture");
	}

	public IMOperation texture(String filename) {
		texture();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	// ======= -thumbnail =======
	public IMOperation thumbnail() {
		return add("-thumbnail");
	}

	public IMOperation thumbnail(int width) {
		return thumbnail().add(Integer.toString(width));
	}

	public IMOperation thumbnail(int width, int height) {
		return thumbnail().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation thumbnail(int width, int height, Character special) {
		return thumbnail().add(Integer.toString(width) + "x" + Integer.toString(height) + (special != null ? special : ""));
	}

	public IMOperation thumbnail(int width, int height, String special) {
		return thumbnail().add(Integer.toString(width) + "x" + Integer.toString(height) + (special != null ? special : ""));
	}

	// ======= -tile =======
	public IMOperation tile() {
		return add("-tile");
	}

	public IMOperation tile(int width) {
		return tile().add(Integer.toString(width));
	}

	public IMOperation tile(int width, int height) {
		return tile().add(Integer.toString(width) + "x" + Integer.toString(height));
	}

	public IMOperation tile(int width, int height, int xOffset) {
		return tile().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(xOffset));
	}

	public IMOperation tile(int width, int height, int xOffset, int yOffset) {
		return tile().add(Integer.toString(width) + "x" + Integer.toString(height) + "+" + Integer.toString(xOffset) + "+" + Integer.toString(
				yOffset));
	}

	public IMOperation tile(String filename) {
		tile();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	// ======= -tileOffset =======
	public IMOperation tileOffset() {
		return add("-tile-offset");
	}

	public IMOperation tileOffset(int x) {
		return tileOffset().add(Integer.toString(x));
	}

	public IMOperation tileOffset(int x, int y) {
		return tileOffset().add(Integer.toString(x) + "+" + Integer.toString(y));
	}

	// ======= -tint =======
	public IMOperation tint() {
		return add("-tint");
	}

	public IMOperation tint(double value) {
		return tint().add(Double.toString(value));
	}

	// ======= -title =======
	public IMOperation title() {
		return add("-title");
	}

	public IMOperation title(String text) {
		title();
		if(StringUtils.isNotEmpty(text)) {
			add(text);
		}
		return this;
	}

	// ======= -transform =======
	public IMOperation transform() {
		return add("-transform");
	}

	// ======= -transparentColor =======
	public IMOperation transparentColor() {
		return add("-transparent-color");
	}

	public IMOperation transparentColor(String color) {
		transparentColor();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -transparent =======
	public IMOperation transparent() {
		return add("-transparent");
	}

	public IMOperation transparent(String color) {
		transparent();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -transpose =======
	public IMOperation transpose() {
		return add("-transpose");
	}

	// ======= -transverse =======
	public IMOperation transverse() {
		return add("-transverse");
	}

	// ======= -treedepth =======
	public IMOperation treedepth() {
		return add("-treedepth");
	}

	public IMOperation treedepth(int value) {
		return treedepth().add(Integer.toString(value));
	}

	// ======= -trim =======
	public IMOperation trim() {
		return add("-trim");
	}

	// ======= -type =======
	public IMOperation type() {
		return add("-type");
	}

	public IMOperation type(String type) {
		type();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -undercolor =======
	public IMOperation undercolor() {
		return add("-undercolor");
	}

	public IMOperation undercolor(String color) {
		undercolor();
		if(StringUtils.isNotEmpty(color)) {
			add(color);
		}
		return this;
	}

	// ======= -uniqueColors =======
	public IMOperation uniqueColors() {
		return add("-unique-colors");
	}

	// ======= -units =======
	public IMOperation units() {
		return add("-units");
	}

	public IMOperation units(String type) {
		units();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -update =======
	public IMOperation update() {
		return add("-update");
	}

	public IMOperation update(int seconds) {
		return update().add(Integer.toString(seconds));
	}

	// ======= -verbose =======
	public IMOperation verbose() {
		return add("-verbose");
	}

	// ======= -version =======
	public IMOperation version() {
		return add("-version");
	}

	// ======= -view =======
	public IMOperation view() {
		return add("-view");
	}

	public IMOperation view(String text) {
		view();
		if(StringUtils.isNotEmpty(text)) {
			add(text);
		}
		return this;
	}

	// ======= -virtualPixel =======
	public IMOperation virtualPixel() {
		return add("-virtual-pixel");
	}

	public IMOperation virtualPixel(String method) {
		virtualPixel();
		if(StringUtils.isNotEmpty(method)) {
			add(method);
		}
		return this;
	}

	// ======= -visual =======
	public IMOperation visual() {
		return add("-visual");
	}

	public IMOperation visual(String type) {
		visual();
		if(StringUtils.isNotEmpty(type)) {
			add(type);
		}
		return this;
	}

	// ======= -watermark =======
	public IMOperation watermark() {
		return add("-watermark");
	}

	public IMOperation watermark(double brightness) {
		return watermark().add(Double.toString(brightness));
	}

	// ======= -wave =======
	public IMOperation wave() {
		return add("-wave");
	}

	public IMOperation wave(double amplitude) {
		return wave().add(Double.toString(amplitude));
	}

	public IMOperation wave(double amplitude, double wavelength) {
		return wave().add(Double.toString(amplitude) + "x" + Double.toString(wavelength));
	}

	// ======= -weight =======
	public IMOperation weight() {
		return add("-weight");
	}

	public IMOperation weight(String fontWeight) {
		weight();
		if(StringUtils.isNotEmpty(fontWeight)) {
			add(fontWeight);
		}
		return this;
	}

	public IMOperation weight(int fontWeight) {
		return weight().add(Integer.toString(fontWeight));
	}

	// ======= -whitePoint =======
	public IMOperation whitePoint() {
		return add("-white-point");
	}

	public IMOperation whitePoint(double x) {
		return whitePoint().add(Double.toString(x));
	}

	public IMOperation whitePoint(double x, double y) {
		return whitePoint().add(Double.toString(x) + "," + Double.toString(y));
	}

	// ======= -whiteThreshold =======
	public IMOperation whiteThreshold() {
		return add("-white-threshold");
	}

	public IMOperation whiteThreshold(double threshold) {
		return whiteThreshold().add(Double.toString(threshold));
	}

	public IMOperation whiteThreshold(double threshold, boolean percent) {
		return whiteThreshold().add(Double.toString(threshold) + (percent ? "%" : ""));
	}

	// ======= -windowGroup =======
	public IMOperation windowGroup() {
		return add("-window-group");
	}

	// ======= -window =======
	public IMOperation window() {
		return add("-window");
	}

	public IMOperation window(String id) {
		window();
		if(StringUtils.isNotEmpty(id)) {
			add(id);
		}
		return this;
	}

	// ======= -write =======
	public IMOperation write() {
		return add("-write");
	}

	public IMOperation write(String filename) {
		write();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}

	public IMOperation p_write() {
		return add("+write");
	}

	public IMOperation p_write(String filename) {
		p_write();
		if(StringUtils.isNotEmpty(filename)) {
			add(filename);
		}
		return this;
	}
	// =======

	/**
	 * @return
	 */
	public List<String> toList() {
		return Collections.unmodifiableList(arguments);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return CommandLineUtils.toLine(arguments);
	}

}
