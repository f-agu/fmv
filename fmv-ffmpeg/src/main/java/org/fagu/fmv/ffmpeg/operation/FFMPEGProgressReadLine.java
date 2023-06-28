package org.fagu.fmv.ffmpeg.operation;

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

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class FFMPEGProgressReadLine implements ProgressReadLine {

	private int frame;

	private int fps;

	private int q;

	private int sizeKb;

	private Time time;

	private Double bitrateKb;

	private Integer dup;

	private Integer drop;

	private Float speed;

	@Override
	public void read(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " \n\r\t=");
		while(tokenizer.hasMoreTokens()) {
			String next = tokenizer.nextToken();
			if( ! tokenizer.hasMoreTokens()) {
				break;
			}
			// frame= 46 fps=0.0 q=0.0 size= 0kB time=00:00:01.56 bitrate= 0.2kbits/s dup=1 drop=0
			// frame= 212 fps= 88 q=-1.0 Lsize= 1158kB time=00:00:01.74 bitrate=5445.7kbits/s dup=160 drop=0
			// speed=0.725x
			String value = tokenizer.nextToken();
			if("frame".equals(next)) {
				frame = NumberUtils.toInt(value);
			} else if("fps".equals(next)) {
				fps = (int)NumberUtils.toFloat(value);
			} else if("q".equals(next)) {
				q = (int)NumberUtils.toFloat(value);
			} else if("size".equals(next) || "Lsize".equals(next)) {
				sizeKb = NumberUtils.toInt(StringUtils.substringBefore(value, "k"));
			} else if("time".equals(next)) {
				try {
					time = Time.parse(value);
				} catch(IllegalArgumentException e) {
					time = null;
				}
			} else if("bitrate".equals(next)) {
				bitrateKb = NumberUtils.toDouble(StringUtils.substringBefore(value, "k"));
			} else if("dup".equals(next)) {
				dup = NumberUtils.toInt(value);
			} else if("drop".equals(next)) {
				drop = NumberUtils.toInt(value);
			} else if("speed".equals(next)) {
				speed = NumberUtils.toFloat(value.substring(0, value.length() - 1));
			}
		}
	}

	@Override
	public int getFrame() {
		return frame;
	}

	@Override
	public int getFps() {
		return fps;
	}

	@Override
	public int getQ() {
		return q;
	}

	@Override
	public int getSizeKb() {
		return sizeKb;
	}

	@Override
	public Time getTime() {
		return time;
	}

	@Override
	public Double getBitRateKb() {
		return bitrateKb;
	}

	@Override
	public Integer getDup() {
		return dup;
	}

	@Override
	public Integer getDrop() {
		return drop;
	}

	@Override
	public Float getSpeed() {
		return speed;
	}

	@Override
	public String toString() {
		return new StringBuilder(100)
				.append("Progress[frame=").append(frame).append(",fps=").append(fps).append(",time=").append(time).append(']')
				.toString();
	}

}
