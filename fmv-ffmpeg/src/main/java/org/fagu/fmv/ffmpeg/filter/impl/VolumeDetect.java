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
import org.fagu.fmv.ffmpeg.operation.ParsedLibLog;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class VolumeDetect extends AbstractFilter implements ParsedLibLog {

	private final VolumeDetection volumeDetectReadLine;

	/**
	 *
	 */
	protected VolumeDetect() {
		super("volumedetect");
		volumeDetectReadLine = new VolumeDetection();
	}

	/**
	 * @return
	 */
	public static VolumeDetect build() {
		return new VolumeDetect();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.LibLog#log(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void log(String title, String somethings, String log) {
		volumeDetectReadLine.add(log);
	}

	/**
	 * @return
	 */
	public boolean isDetected() {
		return volumeDetectReadLine.isDetected();
	}

	/**
	 * @return
	 */
	public VolumeDetected getDetected() {
		return volumeDetectReadLine.getDetected();
	}

}
