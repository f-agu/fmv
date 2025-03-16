package org.fagu.fmv.ffmpeg;

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

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.fagu.fmv.ffmpeg.executor.Executed;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFExecutorFactory;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.DefaultFFMPEGOperation;
import org.fagu.fmv.ffmpeg.operation.InputParameters;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.InputProcessorFactory;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.require.Require;


/**
 * @author f.agu
 */
public class MockFFMPEGExecutorBuilder {

	/**
	 * 
	 */
	private MockFFMPEGExecutorBuilder() {}

	/**
	 * @param movieMetadatasSupplier
	 * @param commandExecute
	 */
	public static void mock(Supplier<Function<String, MovieMetadatas>> movieMetadatasSupplier, final Consumer<String> commandExecute) {
		FFMPEGExecutorBuilder.setFactory(() -> {
			final FFExecutorFactory ffExecutorFactory = new FFExecutorFactory() {

				@Override
				public <R> FFExecutor<R> create(Operation<R, ?> operation, FFMPEGExecutorBuilder ffmpegExecutorBuilder) {
					return new FFExecutor<R>(operation) {

						@Override
						public Executed<R> execute() throws IOException {
							commandExecute.accept(getCommandLineString());
							return null;
						}
					};
				}
			};

			Require require = new Require(false);
			DefaultFFMPEGOperation<?> defaultFFMPEGOperation = new DefaultFFMPEGOperation<Object>(new FilterNaming(), require);
			defaultFFMPEGOperation.getInputParameters().setInputProcessorFactory(new InputProcessorFactory() {

				/**
				 * @see org.fagu.fmv.ffmpeg.operation.InputProcessorFactory#create(org.fagu.fmv.ffmpeg.operation.InputParameters,
				 *      org.fagu.fmv.ffmpeg.operation.MediaInput, int, org.fagu.fmv.ffmpeg.require.Require)
				 */
				@Override
				public InputProcessor create(InputParameters inputParameters, MediaInput input, int index, Require require) {
					return new MyInputProcessor(movieMetadatasSupplier.get(), inputParameters, input, index, require);
				}
			});

			return new MyFFMPEGExecutorBuilder(ffExecutorFactory, defaultFFMPEGOperation);
		});
	}

	// --------------------------------------------------------

	/**
	 * @author f.agu
	 */
	private static class MyInputProcessor extends InputProcessor {

		private final Function<String, MovieMetadatas> movieMetadatasSupplier;

		private final String inputName;

		/**
		 * @param movieMetadatasSupplier
		 * @param inputParameters
		 * @param input
		 * @param index
		 * @param require
		 */
		private MyInputProcessor(Function<String, MovieMetadatas> movieMetadatasSupplier, InputParameters inputParameters, MediaInput input,
				int index, Require require) {
			super(inputParameters, input, index, require);
			this.movieMetadatasSupplier = movieMetadatasSupplier;
			inputName = input.toString();
		}

		/**
		 * @see org.fagu.fmv.ffmpeg.operation.InputProcessor#getMovieMetadatas()
		 */
		@Override
		public MovieMetadatas getMovieMetadatas() throws IOException {
			if(movieMetadatasSupplier == null) {
				return super.getMovieMetadatas();
			}
			try {
				return movieMetadatasSupplier.apply(inputName);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	// --------------------------------------------------------

	/**
	 * @author f.agu
	 */
	private static class MyFFMPEGExecutorBuilder extends FFMPEGExecutorBuilder {

		/**
		 * @param ffExecutorFactory
		 * @param defaultFFMPEGOperation
		 */
		private MyFFMPEGExecutorBuilder(FFExecutorFactory ffExecutorFactory, DefaultFFMPEGOperation<?> defaultFFMPEGOperation) {
			super(ffExecutorFactory, defaultFFMPEGOperation);
		}
	}

}
