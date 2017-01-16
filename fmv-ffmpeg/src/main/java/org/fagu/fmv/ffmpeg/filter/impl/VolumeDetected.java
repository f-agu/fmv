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
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class VolumeDetected {

	private final long countSample;

	private final float mean, max;

	private final SortedMap<Integer, Long> histogram;

	/**
	 * @param countSample
	 * @param mean
	 * @param max
	 * @param histogram
	 */
	VolumeDetected(long countSample, float mean, float max, SortedMap<Integer, Long> histogram) {
		this.countSample = countSample;
		this.mean = mean;
		this.max = max;
		this.histogram = Collections.unmodifiableSortedMap(histogram);
	}

	/**
	 * @return
	 */
	public long countSample() {
		return countSample;
	}

	/**
	 * @return
	 */
	public float getMean() {
		return mean;
	}

	/**
	 * @return
	 */
	public float getMax() {
		return max;
	}

	/**
	 * @return
	 */
	public Volume toMaxVolume() {
		return Volume.build().increaseToMax(this);
	}

	/**
	 * @return
	 */
	public SortedMap<Integer, Long> getHistogram() {
		return histogram;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("count:").append(countSample).append(",mean:").append(mean).append(",max:").append(max);
		buf.append(",histogram:[").append(histogram.entrySet().stream().map(e -> e.getKey().toString() + "=" + e.getValue()).collect(Collectors
				.joining(";"))).append(']');
		return buf.toString();
	}

	/**
	 * @param line
	 * @return
	 */
	public static VolumeDetected parse(String line) {
		Long countSample = null;
		Float mean = null, max = null;
		SortedMap<Integer, Long> histogram = null;
		for(String split : line.split(",")) {
			String[] kv = split.split(":");
			if("count".equals(kv[0])) {
				countSample = Long.parseLong(kv[1]);
			} else if("mean".equals(kv[0])) {
				mean = Float.parseFloat(kv[1]);
			} else if("max".equals(kv[0])) {
				max = Float.parseFloat(kv[1]);
			} else if("histogram".equals(kv[0])) {
				String between = StringUtils.substringBetween(kv[1], "[", "]");
				histogram = new TreeMap<>();
				for(String b : between.split(";")) {
					histogram.put(Integer.parseInt(StringUtils.substringBefore(b, "=")), Long.parseLong(StringUtils.substringAfter(b, "=")));
				}
			}
		}
		Objects.requireNonNull(countSample);
		Objects.requireNonNull(mean);
		Objects.requireNonNull(max);
		Objects.requireNonNull(histogram);
		return new VolumeDetected(countSample, mean, max, histogram);
	}
}
