package org.fagu.fmv.ffmpeg.utils.srcgen;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.executor.Executed;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;


/**
 * @author f.agu
 */
public class FullHelpExtract {

	private FullHelpExtract() {}

	/**
	 * @return
	 */
	public static Map<String, Group> extract() {
		LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
		operation.addParameter("-h", "full");
		try {
			FFExecutor<List<String>> executor = new FFExecutor<>(operation);
			Executed<List<String>> execute = executor.execute();

			Pattern paramPattern = Pattern.compile("(\\s+)([\\-\\w]+)\\s+(?:\\<(\\w+)\\>\\s+)?([EDFVASXR\\.]{8})(.*)");

			int countEmpty = 0;
			String currentTitle = null;
			Group currentGroup = null;
			Param currentParam = null;
			Map<String, Group> groupMap = new HashMap<>(256);
			boolean start = false;
			for(String line : execute.getResult()) {
				boolean isEmpty = "".equals(line);
				if(isEmpty) {
					++countEmpty;
					if( ! start && countEmpty == 2) {
						start = true;
					}
					continue;
				} else {
					countEmpty = 0;
				}
				if( ! start) {
					continue;
				}

				if(line.charAt(0) != ' ') {
					currentTitle = StringUtils.substringBefore(line, " AVOptions:");
					currentGroup = null;
					continue;
				}

				//
				Matcher matcher = paramPattern.matcher(line);
				if(matcher.matches()) {
					String paramName = matcher.group(2);
					String paramType = matcher.group(3);
					Flags paramFlags = Flags.parse(matcher.group(4));
					String desc = matcher.group(5);
					if(desc != null) {
						desc = desc.trim();
					}
					if(currentGroup == null) {
						currentGroup = new Group(currentTitle, desc);
						groupMap.put(currentTitle, currentGroup);
					}
					if(matcher.group(1).length() == 2) {
						currentParam = new Param(paramName, ParamType.valueOf(paramType.toUpperCase()), paramFlags, desc);
						currentGroup.addParam(currentParam);
					} else {
						currentParam.addValue(new ParamValue(paramName, paramFlags, desc));
					}
				} else {
					System.out.println(line);
				}
			}
			return groupMap;
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
