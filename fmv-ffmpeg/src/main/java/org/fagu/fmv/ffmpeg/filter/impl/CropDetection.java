package org.fagu.fmv.ffmpeg.filter.impl;

import java.text.DecimalFormat;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class CropDetection {

	private static final Pattern LOG_PATTERN = Pattern.compile(
			"x1:([0-9]+) x2:([0-9]+) y1:([0-9]+) y2:([0-9]+) w:([0-9]+) h:([0-9]+) x:([0-9]+) y:([0-9]+) pts:([0-9]+) t:([0-9\\.]+) crop=([0-9]+):([0-9]+):([0-9]+):([0-9]+)");

	// ----------------------------------------

	/**
	 * @author f.agu
	 */
	public static class CropSize {

		private final int x1;

		private final int x2;

		private final int y1;

		private final int y2;

		private final int w;

		private final int h;

		private final int x;

		private final int y;

		private int startPTS;

		private int endPTS;

		private Time startTime;

		private Time endTime;

		private int count;

		private final AtomicInteger totalCount;

		CropSize(Matcher matcher, AtomicInteger totalCount) {
			x1 = Integer.parseInt(matcher.group(1));
			x2 = Integer.parseInt(matcher.group(2));
			y1 = Integer.parseInt(matcher.group(3));
			y2 = Integer.parseInt(matcher.group(4));
			w = Integer.parseInt(matcher.group(5));
			h = Integer.parseInt(matcher.group(6));
			x = Integer.parseInt(matcher.group(7));
			y = Integer.parseInt(matcher.group(8));
			count = 1;
			this.totalCount = totalCount;
		}

		public int getX1() {
			return x1;
		}

		public int getX2() {
			return x2;
		}

		public int getY1() {
			return y1;
		}

		public int getY2() {
			return y2;
		}

		public int getW() {
			return w;
		}

		public int getH() {
			return h;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getStartPTS() {
			return startPTS;
		}

		public int getEndPTS() {
			return endPTS;
		}

		public Time getStartTime() {
			return startTime;
		}

		public Time getEndTime() {
			return endTime;
		}

		public Duration duration() {
			return Duration.valueOf(endTime.toSeconds() - startTime.toSeconds());
		}

		public int getCount() {
			return count;
		}

		public int getCountTotal() {
			return totalCount.get();
		}

		public float getCountPercent() {
			return 100F * count / totalCount.get();
		}

		public Size toSize() {
			return Size.valueOf(w, h);
		}

		public Crop toCrop() {
			return Crop.build().x(x).y(y).height(h).width(w);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + h;
			result = prime * result + w;
			result = prime * result + x;
			result = prime * result + x1;
			result = prime * result + x2;
			result = prime * result + y;
			result = prime * result + y1;
			result = prime * result + y2;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			CropSize other = (CropSize)obj;
			if(h != other.h)
				return false;
			if(w != other.w)
				return false;
			if(x != other.x)
				return false;
			if(x1 != other.x1)
				return false;
			if(x2 != other.x2)
				return false;
			if(y != other.y)
				return false;
			if(y1 != other.y1)
				return false;
			if(y2 != other.y2)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return new StringBuilder(100)
					.append("pts[").append(startPTS).append("->").append(endPTS).append("],")
					.append("time[").append(startTime).append("->").append(endTime).append(']')
					.append(",x1:").append(x1)
					.append(",y1:").append(y1)
					.append(",x2:").append(x2)
					.append(",y2:").append(y2)
					.append(",w:").append(w)
					.append(",h:").append(h)
					.append(",x:").append(x)
					.append(",y:").append(y)
					.append(",count:").append(count)
					.append('(').append(new DecimalFormat("###.##").format(getCountPercent())).append("%)")
					.toString();
		}

	}

	// ----------------------------------------

	/**
	 * @author f.agu
	 * @created 14 nov. 2016 16:49:17
	 */
	public static class CropSizeComparator implements Comparator<CropSize> {

		@Override
		public int compare(CropSize o1, CropSize o2) {
			int c = Integer.compare(o1.count, o2.count);
			if(c != 0) {
				return c;
			}
			return Integer.compare(o1.hashCode(), o2.hashCode());
		}

	}

	// ----------------------------------------

	private final AtomicInteger totalCount = new AtomicInteger();

	private final List<CropSize> cropSizes = new ArrayList<>();

	private final List<Consumer<CropSize>> doneCropSize = new ArrayList<>();

	CropDetection() {}

	void add(String log) {
		Matcher matcher = LOG_PATTERN.matcher(log);
		if(matcher.matches()) {
			CropSize cropSize = new CropSize(matcher, totalCount);
			int pts = Integer.parseInt(matcher.group(9));
			Time time = Time.parse(matcher.group(10));
			// parse crop=...:...:...:... ?

			cropSize.startPTS = pts;
			cropSize.startTime = time;
			totalCount.incrementAndGet();
			if(cropSizes.isEmpty()) {
				cropSizes.add(cropSize);

			} else {
				CropSize previousCropSize = cropSizes.get(cropSizes.size() - 1);
				previousCropSize.endPTS = pts;
				previousCropSize.endTime = time;
				if(previousCropSize.equals(cropSize)) {
					previousCropSize.count++;
				} else {
					cropSizes.add(cropSize);
					doneCropSize.forEach(c -> c.accept(previousCropSize));
				}
			}
		}
	}

	public void addDoneCropSize(Consumer<CropSize> consumer) {
		if(consumer != null) {
			doneCropSize.add(consumer);
		}
	}

	public int getTotalCount() {
		return totalCount.get();
	}

	public List<CropSize> getCropSizes() {
		return Collections.unmodifiableList(cropSizes);
	}

	public SortedSet<CropSize> getOrderedCropSizes() {
		SortedSet<CropSize> set = new TreeSet<>(Collections.reverseOrder(new CropSizeComparator()));
		set.addAll(cropSizes);
		return set;
	}

}
