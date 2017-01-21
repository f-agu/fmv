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

import java.util.ArrayList;
import java.util.List;

import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.require.Require;
import org.fagu.fmv.ffmpeg.soft.FFMpegSoftProvider;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.ReadLine;


/**
 * @author f.agu
 */
public abstract class FFMPEGOperation<R> extends AbstractIOOperation<R, FFMPEGOperation<R>> {

	private ReadLine outAndErrReadLine;

	private List<String> outAndErr;

	/**
	 *
	 */
	protected FFMPEGOperation() {
		init();
	}

	/**
	 * @param filterNaming
	 * @param require
	 */
	protected FFMPEGOperation(FilterNaming filterNaming, Require require) {
		super(filterNaming, require);
		init();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getFFName()
	 */
	@Override
	public String getFFName() {
		return FFMpegSoftProvider.NAME;
	}

	/**
	 * @return
	 */
	public FFMPEGOperation<R> noStats() {
		return addParameter("-nostats");
	}

	/**
	 * Send program-friendly progress information to url.
	 * 
	 * Progress information is written approximately every second and at the end of the encoding process. It is made of
	 * "key=value" lines. key consists of only alphanumeric characters. The last key of a sequence of progress
	 * information is always "progress".
	 * 
	 * @return
	 */
	public FFMPEGOperation<R> progressTo(String url) {
		return addParameter("-progress", url);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.AbstractOperation#getOutReadLine()
	 */
	@Override
	public ReadLine getOutReadLine() {
		return outAndErrReadLine;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.AbstractOperation#getErrReadLine()
	 */
	@Override
	public ReadLine getErrReadLine() {
		return outAndErrReadLine;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.AbstractOperation#toArguments()
	 */
	@Override
	public List<String> toArguments() {
		List<Filter> filters = new ArrayList<>(getFilters());
		for(Filter filter : filters) {
			filter.upgrade(this);
		}
		return super.toArguments();
	}

	// *********************************************

	/**
	 * @return
	 */
	protected List<String> getSysOutAndErr() {
		return outAndErr;
	}

	// *********************************************

	/**
	 *
	 */
	private void init() {
		outAndErr = new ArrayList<>(40);
		outAndErrReadLine = new BufferedReadLine(outAndErr);
	}

}
