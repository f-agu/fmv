package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import org.fagu.fmv.ffmpeg.operation.ParsedLibLog;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class CropDetect extends AbstractFilter implements ParsedLibLog {

	private final CropDetection cropDetection;

	protected CropDetect() {
		super("cropdetect");
		cropDetection = new CropDetection();
	}

	public static CropDetect build() {
		return new CropDetect();
	}

	/**
	 * Threshold below which the pixel is considered black (from 0 to 65535) (default 0.0941176)
	 *
	 * @param limit
	 * @return
	 */
	public CropDetect limit(float limit) {
		if(0.0 > limit || limit > 65535.0) {
			throw new IllegalArgumentException("limit must be between 0.0 and 65535.0: " + limit);
		}
		parameter("limit", Float.toString(limit));
		return this;
	}

	/**
	 * Value by which the width/height should be divisible (from 0 to INT_MAX) (default 16)
	 *
	 * @param round
	 * @return
	 */
	public CropDetect round(int round) {
		if(round < 0) {
			throw new IllegalArgumentException("round must be at least 0: " + round);
		}
		parameter("round", Integer.toString(round));
		return this;
	}

	/**
	 * Recalculate the crop area after this many frames (from 0 to INT_MAX) (default 0)
	 *
	 * @param reset
	 * @return
	 */
	public CropDetect reset(int reset) {
		if(reset < 0) {
			throw new IllegalArgumentException("reset must be at least 0: " + reset);
		}
		parameter("reset", Integer.toString(reset));
		return this;
	}

	/**
	 * Recalculate the crop area after this many frames (from 0 to INT_MAX) (default 0)
	 *
	 * @param resetCount
	 * @return
	 */
	public CropDetect resetCount(int resetCount) {
		if(resetCount < 0) {
			throw new IllegalArgumentException("resetCount must be at least 0: " + resetCount);
		}
		parameter("reset_count", Integer.toString(resetCount));
		return this;
	}

	/**
	 * Threshold count of outliers (from 0 to INT_MAX) (default 0)
	 *
	 * @param maxOutliers
	 * @return
	 */
	public CropDetect maxOutliers(int maxOutliers) {
		if(maxOutliers < 0) {
			throw new IllegalArgumentException("maxOutliers must be at least 0: " + maxOutliers);
		}
		parameter("max_outliers", Integer.toString(maxOutliers));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

	@Override
	public void log(String title, String somethings, String log) {
		cropDetection.add(log);
	}

	public CropDetection getCropSizeDetected() {
		return cropDetection;
	}
}
