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

import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public interface ScaleMode {

	/**
	 * @return
	 */
	public static ScaleMode fitToWidth() {
		return (scale, size) -> {
			scale.width(size.getWidth());
			scale.height("trunc(ow/dar/2)*2");
		};
	}

	/**
	 * Aspect ratio not respect
	 *
	 * @return
	 */
	public static ScaleMode fitToHeight() {
		return (scale, size) -> {
			scale.width("trunc(oh*dar/2)*2");
			scale.height(size.getHeight());
		};
	}

	/**
	 * Aspect ratio not respect
	 *
	 * @return
	 */
	public static ScaleMode fitToBox() {
		return (scale, size) -> {
			scale.width(size.getWidth());
			scale.height(size.getHeight());
		};
	}

	/**
	 * @return
	 */
	public static ScaleMode fitToBoxKeepAspectRatio() {
		return (scale, size) -> {
			int width = size.getWidth();
			int height = size.getHeight();
			String aspect = Integer.toString(width) + '/' + Integer.toString(height);
			// scale='if(gt(a*sar,320/500),320,-1)':'if(gt(a*sar,320/500),-1,500)'

			StringBuilder widthsb = new StringBuilder(100);
			widthsb.append("if(gt(dar,").append(aspect).append("),").append(width).append(",trunc(oh*dar/2)*2)");

			StringBuilder heightsb = new StringBuilder(100);
			heightsb.append("if(gt(dar,").append(aspect).append("),trunc(ow/dar/2)*2,").append(height).append(')');

			scale.width(widthsb.toString());
			scale.height(heightsb.toString());
		};
	}

	/**
	 * @return
	 */
	public static ScaleMode growUpIfNecesserayKeepAspectRatio() {
		return (scale, size) -> {
			int width = size.getWidth();
			int height = size.getHeight();
			String aspect = Integer.toString(width) + '/' + Integer.toString(height);

			StringBuilder widthsb = new StringBuilder(100);
			widthsb.append("if(gt(dar,").append(aspect).append("),min(").append(width).append(",iw),trunc(oh*dar/2)*2)");

			StringBuilder heightsb = new StringBuilder(100);
			heightsb.append("if(gt(dar,").append(aspect).append("),trunc(ow/dar/2)*2,min(").append(height).append(",ih))");

			scale.width(widthsb.toString());
			scale.height(heightsb.toString());
		};
	}

	/**
	 * @param scale
	 * @param size
	 */
	void apply(Scale scale, Size size);

}
