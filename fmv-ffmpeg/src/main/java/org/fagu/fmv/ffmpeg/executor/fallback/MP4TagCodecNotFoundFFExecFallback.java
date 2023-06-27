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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.ffmpeg.executor.FFEnv;
import org.fagu.fmv.ffmpeg.executor.FFExecFallback;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.OutputParameters;
import org.fagu.fmv.ffmpeg.operation.Parameter;
import org.fagu.fmv.ffmpeg.operation.Parameter.Way;


/**
 * @author f.agu
 */
public class MP4TagCodecNotFoundFFExecFallback implements FFExecFallback {

	@Override
	public boolean prepare(FFEnv ffEnv, IOException ioException) throws IOException {
		FFExecutor<Object> executor = ffEnv.getExecutor();
		Pattern pattern = Pattern.compile(".* stream #([0-9]+).*");
		boolean change = false;
		for(String line : executor.getOutputReadLine().getLines()) {
			if(line.startsWith("[mp4 @") && line.contains("Could not find tag for codec")) {
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()) {
					int stream = Integer.parseInt(matcher.group(1));
					OutputParameters outputParameters = executor.getOperation().getOutputParameters();
					for(IOEntity ioEntity : outputParameters.getIOEntities()) {
						List<Parameter> parameters = outputParameters.getParameters(ioEntity, Way.BEFORE);
						for(Parameter parameter : parameters) {
							if("-map".equals(parameter.getName()) && parameter.hasValue() && parameter.getValue().endsWith(":" + stream)) {
								change = outputParameters.removeParameter(parameter);
							}
						}
					}
				}
			}
		}
		return change;
	}

	@Override
	public String toString() {
		return "MP4: Codec not found";
	}

}
