package org.fagu.fmv.ffmpeg.executor;

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

import java.io.File;
import java.util.function.Supplier;

import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.format.Demuxer;
import org.fagu.fmv.ffmpeg.format.Muxer;
import org.fagu.fmv.ffmpeg.ioe.FileMediaInput;
import org.fagu.fmv.ffmpeg.ioe.FileMediaOutput;
import org.fagu.fmv.ffmpeg.ioe.IntMediaInput;
import org.fagu.fmv.ffmpeg.operation.DefaultFFMPEGOperation;
import org.fagu.fmv.ffmpeg.operation.FFMPEGOperation;
import org.fagu.fmv.ffmpeg.operation.InputParameters;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.MediaOutput;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.OperationListener;
import org.fagu.fmv.ffmpeg.operation.OutputParameters;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.operation.ProgressReadLine;
import org.fagu.fmv.ffmpeg.require.Require;
import org.fagu.fmv.ffmpeg.utils.Devices;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.ffmpeg.utils.LogLevel;


/**
 * @author f.agu
 */
public class FFMPEGExecutorBuilder {

	private static Supplier<FFMPEGExecutorBuilder> factory;

	private FFExecutorFactory ffExecutorFactory;

	protected DefaultFFMPEGOperation<?> defaultFFMPEGOperation;

	/**
	 *
	 */
	protected FFMPEGExecutorBuilder() {
		this(null, null);
	}

	/**
	 * @param ffExecutorFactory
	 * @param filterNaming
	 * @param defaultFFMPEGOperation
	 * @param require
	 */
	protected FFMPEGExecutorBuilder(FFExecutorFactory ffExecutorFactory, DefaultFFMPEGOperation<?> defaultFFMPEGOperation) {
		this.ffExecutorFactory = ffExecutorFactory != null ? ffExecutorFactory : new FFExecutorFactoryImpl();
		this.defaultFFMPEGOperation = defaultFFMPEGOperation != null ? defaultFFMPEGOperation
				: new DefaultFFMPEGOperation<>(new FilterNaming(),
						new Require());
	}

	/**
	 * @return
	 */
	public static FFMPEGExecutorBuilder create() {
		if(factory != null) {
			return factory.get();
		}
		return new FFMPEGExecutorBuilder();
	}

	/**
	 * @param factory
	 */
	public static void setFactory(Supplier<FFMPEGExecutorBuilder> factory) {
		FFMPEGExecutorBuilder.factory = factory;
	}

	/**
	 * @return
	 */
	public static Supplier<FFMPEGExecutorBuilder> getFactory() {
		return factory;
	}

	/**
	 * @return the ffExecutorFactory
	 */
	public FFExecutorFactory getFFExecutorFactory() {
		return ffExecutorFactory;
	}

	/**
	 * @param filter
	 * @return
	 */
	public FFMPEGExecutorBuilder filter(Filter filter) {
		if(filter != null) {
			filter.beforeAdd(defaultFFMPEGOperation);
		}
		return this;
	}

	// =========== IO

	/**
	 * @param file
	 * @return
	 */
	public InputProcessor addMediaInputFile(File file) {
		return addMediaInput(new FileMediaInput(file));
	}

	/**
	 * @return
	 */
	public InputProcessor addMediaInputWebCam() {
		String format = Devices.webCam().getName();
		InputProcessor input = addMediaInput(new IntMediaInput(0));
		input.format(format);
		input.frameRate(FrameRate.PAL);
		return input;
	}

	/**
	 * @param input
	 * @return
	 */
	public InputProcessor addMediaInput(MediaInput input) {
		if(input instanceof Demuxer) {
			return demux((Demuxer<?>)input);
		}
		Processor<?> processor = defaultFFMPEGOperation.getProcessor(input);
		if(processor != null && ! (processor instanceof InputProcessor)) {
			throw new IllegalArgumentException("Already defined: " + input);
		}

		InputParameters inputParameters = defaultFFMPEGOperation.getInputParameters();
		return inputParameters.addInput(input);
	}

	/**
	 * @param file
	 * @return
	 */
	public OutputProcessor addMediaOutputFile(File file) {
		return addMediaOutput(new FileMediaOutput(file));
	}

	/**
	 * @param output
	 * @return
	 */
	public OutputProcessor addMediaOutput(MediaOutput output) {
		if(output instanceof Muxer) {
			return mux((Muxer<?>)output);
		}
		Processor<?> processor = defaultFFMPEGOperation.getProcessor(output);
		if(processor != null && ! (processor instanceof OutputProcessor)) {
			throw new IllegalArgumentException("Already defined: " + output);
		}
		OutputParameters outputParameters = defaultFFMPEGOperation.getOutputParameters();
		return outputParameters.addOutput(output);
	}

	/**
	 * @param muxer
	 * @return
	 */
	public InputProcessor demux(Demuxer<?> demuxer) {
		MediaInput mediaInput = demuxer.getMediaInput();
		InputProcessor inputProcessor = addMediaInput(mediaInput);
		demuxer.eventAdded(inputProcessor, mediaInput);
		inputProcessor.format(demuxer.name());
		return inputProcessor;
	}

	/**
	 * @param muxer
	 * @return
	 */
	public OutputProcessor mux(Muxer<?> muxer) {
		MediaOutput mediaOutput = muxer.getMediaOutput();
		OutputProcessor outputProcessor = addMediaOutput(mediaOutput);
		muxer.eventAdded(outputProcessor, mediaOutput);
		outputProcessor.format(muxer.name());
		return outputProcessor;
	}

	// ===========

	/**
	 * @param listener
	 * @return
	 */
	public FFMPEGExecutorBuilder addListener(OperationListener listener) {
		defaultFFMPEGOperation.addListener(listener);
		return this;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <R> FFExecutor<R> build() {
		return ffExecutorFactory.create((Operation<R, ?>)defaultFFMPEGOperation, this);
	}

	/**
	 * @return
	 */
	public FFMPEGOperation<?> getFFMPEGOperation() {
		return defaultFFMPEGOperation;
	}

	// =======================================

	/**
	 * @return
	 */
	public FFMPEGExecutorBuilder hideBanner() {
		defaultFFMPEGOperation.hideBanner();
		return this;
	}

	/**
	 * @param logLevel
	 * @return
	 */
	public FFMPEGExecutorBuilder logLevel(LogLevel logLevel) {
		defaultFFMPEGOperation.logLevel(logLevel);
		return this;
	}

	/**
	 * @return
	 */
	public FFMPEGExecutorBuilder noStats() {
		defaultFFMPEGOperation.noStats();
		return this;
	}

	/**
	 * @param name
	 * @return
	 */
	public FFMPEGExecutorBuilder parameter(String name) {
		defaultFFMPEGOperation.addParameter(name);
		return this;
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public FFMPEGExecutorBuilder parameter(String name, String value) {
		defaultFFMPEGOperation.addParameter(name, value);
		return this;
	}

	/**
	 * @param progressReadLine
	 * @return
	 */
	public FFMPEGExecutorBuilder progressReadLine(ProgressReadLine progressReadLine) {
		defaultFFMPEGOperation.setProgressReadLine(progressReadLine);
		return this;
	}

}
