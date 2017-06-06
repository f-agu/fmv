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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.math.NumberUtils;
import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.LibLog;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Time;


/**
 * https://ffmpeg.org/ffmpeg-filters.html#showinfo
 *
 * @author f.agu
 */
public class ShowInfo extends AbstractFilter implements LibLog {

	// -------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class Info {

		private int frameNumber;

		private Time ptsTime;

		private long pts, position;

		private PixelFormat pixelFormat;

		private Ratio sar;

		private Size size;

		private Interlace interlace;

		private boolean isFrameKey;

		private PictureType pictureType;

		/**
		 * @param line
		 * @return
		 */
		static Info parse(String line) {
			Info info = new Info();
			String[] split = line.split(":");
			String previousName = null;
			for(String s : split) {
				s = s.trim();
				int lastSpace = s.lastIndexOf(' ');
				if(lastSpace <= 0) {
					previousName = s;
					continue;
				}
				String value = s.substring(0, lastSpace).trim();

				if("n".equals(previousName)) {
					info.frameNumber = NumberUtils.toInt(value);
				} else if("pts".equals(previousName)) {
					info.pts = NumberUtils.toLong(value);
				} else if("pts_time".equals(previousName)) {
					info.ptsTime = Time.parse(value);
				} else if("pos".equals(previousName)) {
					info.position = NumberUtils.toLong(value);
				} else if("fmt".equals(previousName)) {
					info.pixelFormat = PixelFormat.byName(value);
				} else if("sar".equals(previousName)) {
					info.sar = Ratio.parse(value);
				} else if("s".equals(previousName)) {
					info.size = Size.parse(value);
				} else if("i".equals(previousName)) {
					info.interlace = Interlace.parse(value.charAt(0));
				} else if("iskey".equals(previousName)) {
					info.isFrameKey = "1".equals(value);
				} else if("type".equals(previousName)) {
					info.pictureType = PictureType.parse(value.charAt(0));
				}
				// TODO checksum

				String name = s.substring(lastSpace + 1);
				previousName = name;
			}

			return info;
		}

		/**
		 * @return
		 */
		boolean isComplete() {
			return frameNumber >= 0 && ptsTime != null && pts > 0 && position > 0 && pixelFormat != null && sar != null && size != null
					&& interlace != null && pictureType != null;
		}

		/**
		 * @return
		 */
		public int getFrameNumber() {
			return frameNumber;
		}

		/**
		 * @return
		 */
		public Time getPtsTime() {
			return ptsTime;
		}

		/**
		 * @return
		 */
		public long getPts() {
			return pts;
		}

		/**
		 * @return
		 */
		public long getPosition() {
			return position;
		}

		/**
		 * @return
		 */
		public PixelFormat getPixelFormat() {
			return pixelFormat;
		}

		/**
		 * @return
		 */
		public Ratio getSar() {
			return sar;
		}

		/**
		 * @return
		 */
		public Size getSize() {
			return size;
		}

		/**
		 * @return
		 */
		public Interlace getInterlace() {
			return interlace;
		}

		/**
		 * @return
		 */
		public boolean isFrameKey() {
			return isFrameKey;
		}

		/**
		 * @return
		 */
		public PictureType getPictureType() {
			return pictureType;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder(150);
			buf.append("n:").append(frameNumber).append(" pts:").append(pts).append(" pts_time:").append(ptsTime);
			buf.append(" pos:").append(position).append(" fmt:").append(pixelFormat.getName()).append(" sar:").append(sar);
			buf.append(" s:").append(size).append(" i:").append(interlace.name().charAt(0)).append(" iskey:").append(isFrameKey ? '1' : '0');
			buf.append(" type:").append(pictureType.name().charAt(0));
			return buf.toString();
		}

	}

	// -------------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Interlace {
		/**
		 * progressive
		 */
		PROGRESSIVE,
		/**
		 * top field first
		 */
		TOP,
		/**
		 * bottom field first
		 */
		BOTTOM;

		/**
		 * @param c
		 * @return
		 */
		static Interlace parse(char c) {
			if('P' == c) {
				return PROGRESSIVE;
			}
			if('T' == c) {
				return PROGRESSIVE;
			}
			if('B' == c) {
				return PROGRESSIVE;
			}
			throw new IllegalArgumentException("Unknown type: " + c);
		}
	}

	// -------------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum PictureType {
		IFRAME, PFRAME, BFRAME, UNKNOW;

		/**
		 * @param c
		 * @return
		 */
		static PictureType parse(char c) {
			if('I' == c) {
				return IFRAME;
			}
			if('P' == c) {
				return PFRAME;
			}
			if('B' == c) {
				return BFRAME;
			}
			return UNKNOW;
		}
	}

	// -------------------------------------------------

	private final List<ShowInfoListener> showInfoListeners;

	/**
	 *
	 */
	protected ShowInfo() {
		super("showinfo");
		showInfoListeners = new ArrayList<>();
	}

	/**
	 * @return
	 */
	public static ShowInfo build() {
		return new ShowInfo();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.LibLog#getLibLogFilter()
	 */
	@Override
	public Predicate<String> getLibLogFilter() {
		return s -> s.startsWith("Parsed_showinfo_");
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.LibLog#log(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void log(String title, String somethings, String log) {
		Info info = null;
		try {
			info = Info.parse(log);
			if( ! info.isComplete()) {
				return;
			}
		} catch(Exception e) {
			e.printStackTrace();
			// ignore
			return;
		}
		for(ShowInfoListener listener : showInfoListeners) {
			try {
				listener.event(info);
			} catch(Exception e) {
				System.out.println("[" + title + "] " + log);
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param showInfoListener
	 */
	public ShowInfo addListener(ShowInfoListener showInfoListener) {
		showInfoListeners.add(Objects.requireNonNull(showInfoListener));
		return this;
	}
}
