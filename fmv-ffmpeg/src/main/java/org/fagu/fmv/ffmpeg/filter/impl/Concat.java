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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.filter.GeneratedSource;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class Concat extends FilterComplex {

	private boolean startAtStartTime = true;

	private final FFMPEGExecutorBuilder builder;

	private final List<Object> inputs;

	private Integer countInputs;

	private Integer countAudio;

	private Integer countVideo;

	/**
	 * @param builder
	 */
	protected Concat(FFMPEGExecutorBuilder builder) {
		super("concat");
		this.builder = Objects.requireNonNull(builder);
		// map
		addMap("out");

		inputs = new ArrayList<>();
	}

	/**
	 * @param builder
	 * @param inputs
	 * @return
	 */
	public static Concat create(FFMPEGExecutorBuilder builder, Object... inputs) {
		Concat concat = new Concat(builder);
		for(Object input : inputs) {
			if(input instanceof Collection) {
				concat.inputs.addAll((Collection<?>)input);
			} else if(input instanceof Filter && ! (input instanceof FilterComplex)) {
				throw new IllegalArgumentException("Filter forbidden: " + input.getClass().getName() + ". Wrap it with FilterComplex.create(...).");
			} else {
				concat.inputs.add(input);
			}
		}
		return concat;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterComplex#addInput(org.fagu.fmv.ffmpeg.filter.FilterInput,
	 *      org.fagu.fmv.ffmpeg.operation.Type[])
	 */
	@Override
	public FilterComplex addInput(FilterInput filterInput, Type... types) {
		inputs.add(filterInput);
		return this;
	}

	/**
	 * @return
	 */
	public boolean isStartAtStartTime() {
		return startAtStartTime;
	}

	/**
	 * @param startAtStartTime
	 */
	public Concat setStartAtStartTime(boolean startAtStartTime) {
		this.startAtStartTime = startAtStartTime;
		return this;
	}

	/**
	 * @param countAudio the countAudio to set
	 */
	public Concat countAudio(Integer countAudio) {
		this.countAudio = countAudio;
		return this;
	}

	/**
	 * @param countVideo the countVideo to set
	 */
	public Concat countVideo(Integer countVideo) {
		this.countVideo = countVideo;
		return this;
	}

	/**
	 * @param countInputs
	 */
	public Concat countInputs(Integer countInputs) {
		this.countInputs = countInputs;
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Type.valuesSet(this);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterComplex#toString()
	 */
	@Override
	public String toString() {
		Collection<Type> types = getTypes();
		parameter("n", countInputs != null ? countInputs.toString() : Integer.toString(countVariousInput()));
		parameter("v", countVideo != null ? countVideo.toString() : types.contains(Type.VIDEO) ? "1" : "0");
		parameter("a", countAudio != null ? countAudio.toString() : types.contains(Type.AUDIO) ? "1" : "0");

		return super.toString();
	}

	// ****************************************************

	/**
	 * @param operation
	 * @param filterNaming
	 */
	@Override
	protected void beforeAddAround(Operation<?, ?> operation, FilterNaming filterNaming) {
		ArrayList<Object> arrayList = new ArrayList<>(inputs); // copy pour acces concurrent
		for(Object input : arrayList) {
			Object inputObj = null;
			Set<Type> types = Collections.emptySet();

			if(input instanceof File) {
				InputProcessor inputProcessor = builder.addMediaInputFile((File)input);
				inputObj = inputProcessor;
				types = getTypes(inputProcessor);
			} else if(input instanceof InputProcessor) {
				types = getTypes((InputProcessor)input);
				inputObj = input;
			} else if(input instanceof FilterComplex) {
				inputObj = input;
				types = ((FilterComplex)input).getTypes();
			} else if(input instanceof Filter) {
				throw new IllegalArgumentException("Filter forbidden: " + input.getClass().getName() + ". Wrap it with FilterComplex.create(...).");
			}

			if(startAtStartTime && inputObj != null) {
				boolean startAt = true;
				FilterInput filterInput = (FilterInput)inputObj;
				for(MediaInput mediaInput : filterInput.getInputs()) {
					if(mediaInput instanceof GeneratedSource) {
						startAt = false;
						break;
					}
				}
				if(startAt) {
					if(types.contains(Type.VIDEO) && (countVideo == null || countVideo != 0)) {
						addPTS(builder, SetPTSVideo.build(), inputObj);
					}
					if(types.contains(Type.AUDIO) && (countAudio == null || countAudio != 0)) {
						addPTS(builder, SetPTSAudio.build(), inputObj);
					}
				} else {
					builder.filter((Filter)inputObj);
					super.addInput((FilterInput)inputObj);
				}
			} else if(input instanceof FilterInput) {
				builder.filter((Filter)inputObj);
				super.addInput((FilterInput)input);
			} else {
				throw new IllegalArgumentException(input.getClass().getName());
			}
		}
	}

	// ****************************************************

	/**
	 * @param builder
	 * @param setPTS
	 * @param input
	 */
	private void addPTS(FFMPEGExecutorBuilder builder, SetPTS<?> setPTS, Object input) {
		setPTS.startAtFirstFrame();
		FilterComplex setPTSComplex = FilterComplex.create(setPTS);
		setPTSComplex.addInput((FilterInput)input, setPTS.getTypes().iterator().next());
		if(input instanceof Filter) {
			builder.filter((Filter)input);
		}
		builder.filter(setPTSComplex);
		super.addInput(setPTSComplex);
	}

	/**
	 * @return
	 */
	private Set<Type> getTypes(InputProcessor inputProcessor) {
		Set<Type> types = new HashSet<>(2);
		if(inputProcessor.contains(Type.AUDIO)) {
			types.add(Type.AUDIO);
		}
		try {
			if(inputProcessor.contains(Type.VIDEO) && ! inputProcessor.getMovieMetadatas().getVideoStream().containsAttachedPicture()) {
				types.add(Type.VIDEO);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return types;
	}

}
