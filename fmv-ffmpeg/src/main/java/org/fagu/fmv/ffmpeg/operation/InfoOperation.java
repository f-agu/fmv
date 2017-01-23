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
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.ReadLine;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;


/**
 * ffprobe -v quiet -print_format json -show_format -show_streams
 *
 * @author f.agu
 */
public class InfoOperation extends FFProbeOperation<MovieMetadatas> {

	// ------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum PrintFormat {
		DEFAULT, COMPACT, CSV, FLAT, INI, JSON, XML
	}

	// ------------------------------------------

	private final MediaInput input;

	private final List<String> out;

	private final List<String> err;

	private final ReadLine outReadLine;

	private final ReadLine errReadLine;

	/**
	 * @param input
	 */
	public InfoOperation(MediaInput input) {
		super();
		this.input = Objects.requireNonNull(input);
		add(input);
		out = new ArrayList<>(100);
		err = new ArrayList<>();
		outReadLine = new BufferedReadLine(out);
		errReadLine = new BufferedReadLine(err);

		hideBanner();
		printFormat(PrintFormat.JSON).showFormat().showStreams().showChapters();
	}

	/**
	 * @param printFormat
	 * @return
	 */
	public InfoOperation printFormat(PrintFormat printFormat) {
		add(Parameter.createGlobal("-print_format", printFormat.name().toLowerCase()));
		return this;
	}

	/**
	 * Show packets data
	 *
	 * @return
	 */
	public InfoOperation showData() {
		add(Parameter.createGlobal("-show_data"));
		return this;
	}

	/**
	 * Show packets data hash
	 *
	 * @return
	 */
	public InfoOperation showDataHash() {
		add(Parameter.createGlobal("-show_data_hash"));
		return this;
	}

	/**
	 * Show probing error
	 *
	 * @return
	 */
	public InfoOperation showError() {
		add(Parameter.createGlobal("-show_error"));
		return this;
	}

	/**
	 * Show format/container info
	 *
	 * @return
	 */
	public InfoOperation showFormat() {
		add(Parameter.createGlobal("-show_format"));
		return this;
	}

	/**
	 * Show frames info
	 *
	 * @return
	 */
	public InfoOperation showFrames() {
		add(Parameter.createGlobal("-show_frames"));
		return this;
	}

	/**
	 * Show a particular entry from the format/container info
	 *
	 * @return
	 */
	// public InfoOperation showformatEntry(input) {
	// add(Parameter.createGlobal("-show_format_entry"));
	// return this;
	// }

	/**
	 * Show a set of specified entries
	 *
	 * @return
	 */
	// public InfoOperation showformatEntry(input) {
	// add(Parameter.createGlobal("-show_entries"));
	// return this;
	// }

	/**
	 * Show packets info
	 *
	 * @return
	 */
	public InfoOperation showPackets() {
		add(Parameter.createGlobal("-show_packets"));
		return this;
	}

	/**
	 * Show programs info
	 *
	 * @return
	 */
	public InfoOperation showPrograms() {
		add(Parameter.createGlobal("-show_programs"));
		return this;
	}

	/**
	 * Show streams info
	 *
	 * @return
	 */
	public InfoOperation showStreams() {
		add(Parameter.createGlobal("-show_streams"));
		return this;
	}

	/**
	 * Show chapters info
	 *
	 * @return
	 */
	public InfoOperation showChapters() {
		add(Parameter.createGlobal("-show_chapters"));
		return this;
	}

	/**
	 * @param microseconds
	 * @return
	 */
	public InfoOperation analyzeDuration(long microseconds) {
		add(Parameter.before(input, "-analyzeduration", Long.toString(microseconds)));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.AbstractOperation#getOutReadLine()
	 */
	@Override
	public ReadLine getOutReadLine() {
		return outReadLine;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.AbstractOperation#getErrReadLine()
	 */
	@Override
	public ReadLine getErrReadLine() {
		return errReadLine;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getResult()
	 */
	@Override
	public MovieMetadatas getResult() {
		try {
			JSONObject jsonObject = JSONObject.fromObject(StringUtils.join(out, ' '));
			return MovieMetadatas.create(jsonObject);
		} catch(JSONException e) {
			// debug
			// for(String str : out) {
			// System.out.println(str);
			// }
			throw e;
		}
	}

}
