package org.fagu.fmv.ffmpeg.ioe;

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

/**
 * @author f.agu
 */
public class AbstractPipeMedia<P extends AbstractPipeMedia<?>> extends AbstractIOEntity<P> {

	private final Pipe pipe;

	/**
	 * @param pipe
	 */
	public AbstractPipeMedia(Pipe pipe) {
		super(pipe.toCode());
		this.pipe = pipe;
	}

	/**
	 * Set I/O operation maximum block size (from 1 to INT_MAX) (default INT_MAX)
	 * 
	 * @param blocksize
	 * @return
	 */
	public P blocksize(int blocksize) {
		if(blocksize <= 0) {
			throw new IllegalArgumentException("");
		}
		return getThis();
	}

	/**
	 * @return
	 */
	public Pipe getPipe() {
		return pipe;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "pipe:" + pipe.ordinal();
	}

	// ******************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private P getThis() {
		return (P)this;
	}
}
