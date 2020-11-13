package org.fagu.fmv.ffmpeg.executor.fallback;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2016 fagu
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.fagu.fmv.ffmpeg.executor.FFEnv;
import org.fagu.fmv.ffmpeg.executor.FFExecFallback;
import org.fagu.fmv.ffmpeg.operation.IOParameters;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Parameter;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;


/**
 * @author f.agu
 */
public class CodecSubripFFExecFallback implements FFExecFallback {

	private static final String TEXT = "Could not find tag for codec subrip in stream #";

	@Override
	public boolean prepare(FFEnv ffEnv, IOException ioException) throws IOException {
		MutableBoolean change = new MutableBoolean(false);
		Consumer<Boolean> addValue = v -> change.setValue(change.getValue() | v);
		if(ioException instanceof FMVExecuteException) {
			FMVExecuteException fmvExecuteException = (FMVExecuteException)ioException;
			for(String line : fmvExecuteException.getOutputLines()) {
				if(line.contains(TEXT)) {
					int streamNum = Integer.parseInt(StringUtils.substringBetween(line, TEXT, ","));
					List<OutputProcessor> streamToRemove = new ArrayList<>();
					ffEnv.getOperation().getOutputProcessorStream().forEach(outputProcessor -> {
						List<Parameter> mapParams = outputProcessor.getParameters().stream()
								.filter(p -> "-map".equals(p.getName()))
								.collect(Collectors.toList());
						List<Parameter> toRemove = mapParams.stream()
								.filter(p -> p.getValue().endsWith(":" + streamNum))
								.collect(Collectors.toList());
						if( ! toRemove.isEmpty()) {
							addValue.accept(true);
						}
						IOParameters ioParameters = outputProcessor.getIOParameters();
						toRemove.forEach(ioParameters::removeParameter);
						if(toRemove.size() == mapParams.size()) {
							streamToRemove.add(outputProcessor);
						}
					});
					streamToRemove.stream()
							.map(p -> ffEnv.getOperation().removeProcessorStream(p))
							.reduce((b, a) -> b | a)
							.ifPresent(addValue::accept);
				}
			}
		}
		return change.booleanValue();
	}

	@Override
	public String toString() {
		return "Codec subrip";
	}

}
