package org.fagu.fmv.ffmpeg.filter.impl;

import java.util.Optional;

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

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.filter.GeneratedSource;
import org.fagu.fmv.ffmpeg.ioe.GeneratedSourceMediaInput;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.utils.Color;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public abstract class SrcVideo<T> extends AbstractFilter implements MediaInput, GeneratedSource {

	private Duration duration;

	/**
	 * @param name
	 */
	protected SrcVideo(String name) {
		super(name);
	}

	/**
	 * @param color
	 * @return
	 */
	public T color(Color color) {
		parameter("c", color.toString());
		return getThis();
	}

	/**
	 * @param size
	 * @return
	 */
	public T size(Size size) {
		parameter("s", size.toString());
		return getThis();
	}

	/**
	 * @param rate
	 * @return
	 */
	public T rate(int rate) {
		parameter("r", Integer.toString(rate));
		return getThis();
	}

	/**
	 * @param duration
	 * @return
	 */
	public T duration(Duration duration) {
		this.duration = duration;
		parameter("d", Double.toString(duration.toSeconds()));
		return getThis();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)
	 */
	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.GeneratedSource#forInput()
	 */
	@Override
	public MediaInput forInput() {
		return new GeneratedSourceMediaInput(this);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.GeneratedSource#getDuration()
	 */
	@Override
	public Optional<Duration> getDuration() {
		return Optional.ofNullable(duration);
	}

	// **********************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T)this;
	}

}
