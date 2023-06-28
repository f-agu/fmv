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

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class Crop extends AbstractFilter {

	protected Crop() {
		super("crop");
	}

	public static Crop build() {
		return new Crop();
	}

	public static Crop to(Size cropSize) {
		return new Crop().size(cropSize);
	}

	public static Crop to(Size cropSize, int x, int y) {
		return new Crop().size(cropSize).x(x).y(y);
	}

	public Crop size(Size cropSize) {
		return width(cropSize.getWidth()).height(cropSize.getHeight());
	}

	public Crop fitByRatio(Ratio ratio) {
		// w='if(gt(a*sar,4/3),in_w*3/4,in_w)'
		// h='if(gt(a*sar,4/3),in_h,in_w*3/4)'
		String aspect = ratio.toFractionOrDouble();
		String aspectInverse = ratio.invert().toFractionOrDouble();

		StringBuilder widthsb = new StringBuilder(100);
		widthsb.append("if(gt(a*sar,").append(aspect).append("),in_w*").append(aspectInverse).append(",in_w)");

		StringBuilder heightsb = new StringBuilder(100);
		heightsb.append("if(gt(a*sar,").append(aspect).append("),in_h,in_w*").append(aspectInverse).append(')');

		return width(widthsb.toString()).height(heightsb.toString());
	}

	public Crop centralArea(Size cropSize) {
		clearParameters();
		return width(cropSize.getWidth()).height(cropSize.getHeight());
	}

	public Crop width(String expr) {
		parameter("w", "'" + expr + "'");
		return this;
	}

	public Crop width(int width) {
		return width(Integer.toString(width));
	}

	public Crop height(String expr) {
		parameter("h", "'" + expr + "'");
		return this;
	}

	public Crop height(int height) {
		return height(Integer.toString(height));
	}

	public Crop x(String expr) {
		parameter("x", "'" + expr + "'");
		return this;
	}

	public Crop x(int x) {
		return x(Integer.toString(x));
	}

	public Crop y(String expr) {
		parameter("y", "'" + expr + "'");
		return this;
	}

	public Crop y(int y) {
		return y(Integer.toString(y));
	}

	public Crop keepAspect(boolean keep) {
		parameter("keep_aspect", keep ? "1" : "0");
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

}
