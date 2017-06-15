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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.fagu.fmv.ffmpeg.executor.Executed;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.IOKey;
import org.fagu.fmv.ffmpeg.filter.Label;
import org.fagu.fmv.ffmpeg.filter.OutputKey;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.require.Require;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class InputProcessor extends Processor<InputProcessor> implements FilterInput {

	private MovieMetadatas videoMetadatas;

	private final MediaInput input;

	private final InputParameters inputParameters;

	/**
	 * @param inputParameters
	 * @param input
	 * @param index
	 * @param require
	 */
	protected InputProcessor(InputParameters inputParameters, MediaInput input, int index, Require require) {
		super(inputParameters, input, index, require);
		this.inputParameters = inputParameters;
		this.input = input;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public MovieMetadatas getMovieMetadatas() throws IOException {
		if(videoMetadatas == null) {
			InfoOperation infoOperation = new InfoOperation(input);
			FFExecutor<MovieMetadatas> executor = new FFExecutor<>(infoOperation);
			Executed<MovieMetadatas> execute = executor.execute();
			videoMetadatas = execute.getResult();
		}
		return videoMetadatas;
	}

	/**
	 * @param videoMetadatas
	 */
	public void setMovieMetadatas(MovieMetadatas videoMetadatas) {
		this.videoMetadatas = videoMetadatas;
	}

	/**
	 * @return
	 */
	public MediaInput getInput() {
		return input;
	}

	/**
	 * @return
	 */
	public InputParameters getInputParameters() {
		return inputParameters;
	}

	/**
	 * Set the input time offset.
	 *
	 * offset must be a time duration specification, see (ffmpeg-utils)the Time duration section in the ffmpeg-utils(1)
	 * manual.
	 *
	 * The offset is added to the timestamps of the input files. Specifying a positive offset means that the
	 * corresponding streams are delayed by the time duration specified in offset.
	 *
	 * @return
	 */
	public InputProcessor timeOffset(Time time) {
		add(Parameter.before(ioEntity, "-itsoffset", time.toString()));
		return this;
	}

	/**
	 * Read input at native frame rate. Mainly used to simulate a grab device, or live input stream (e.g. when reading
	 * from a file). Should not be used with actual grab devices or live input streams (where it can cause packet loss).
	 * By default ffmpeg attempts to read the input(s) as fast as possible. This option will slow down the reading of
	 * the input(s) to the native frame rate of the input(s). It is useful for real-time output (e.g. live streaming).
	 * 
	 * @return
	 */
	public InputProcessor readRealTime() {
		add(Parameter.before(ioEntity, "-re"));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Processor#codec(org.fagu.fmv.ffmpeg.operation.Type, java.lang.String)
	 */
	@Override
	public InputProcessor codec(Type type, String codec) {
		require().decoder(codec);
		return super.codec(type, codec);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getInputs()
	 */
	@Override
	public List<MediaInput> getInputs() {
		return Collections.singletonList(getInput());
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getInputKeys()
	 */
	@Override
	public Set<IOKey> getInputKeys() {
		return Collections.emptySet(); // ????
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getDuration()
	 */
	@Override
	public Optional<Duration> getDuration() {
		return input.getDuration();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#contains(org.fagu.fmv.ffmpeg.operation.Type)
	 */
	@Override
	public boolean contains(Type type) {
		try {
			return getMovieMetadatas().contains(type);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getOutputKeys()
	 */
	@Override
	public List<OutputKey> getOutputKeys() {
		return Collections.singletonList(new OutputKey(this, Label.input(getIndex())));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Input[" + input + "]";
	}
}
