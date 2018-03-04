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

import java.util.Map;


/**
 * @author f.agu
 */
public class GenerateEncoder {

	/**
	 * @param name
	 */
	public void generate(String name) {
		Map<String, Group> extract = FullHelpExtract.extract();
		String n = name;
		Group group = extract.get(n);
		if(group == null) {
			throw new RuntimeException("Not found: " + n);
		}

		GenerateAVContext.writeClass(System.out, group, false, false, true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateEncoder generateFilter = new GenerateEncoder();
		generateFilter.generate("libx264");
	}

}
