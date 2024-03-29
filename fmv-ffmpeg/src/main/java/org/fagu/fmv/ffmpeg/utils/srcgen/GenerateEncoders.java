package org.fagu.fmv.ffmpeg.utils.srcgen;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.util.List;

import org.fagu.fmv.ffmpeg.coder.Encoders;
import org.fagu.fmv.ffmpeg.coder.Encoders.EncoderHelp;
import org.fagu.fmv.ffmpeg.soft.FFMpeg;
import org.fagu.fmv.ffmpeg.utils.HelpCache;


/**
 * @author f.agu
 */
public class GenerateEncoders {

	public static void main(String[] args) {
		System.out.println("// Generated by " + GenerateEncoders.class.getName());
		System.out.println("// with " + FFMpeg.search().getFirstFound().getSoftInfo().toString());
		System.out.println();

		HelpCache<Encoders, EncoderHelp> helpCache = Encoders.getHelpCache();
		for(String name : helpCache.availableNames()) {
			List<EncoderHelp> helps = helpCache.cache(name);
			if(helps.isEmpty()) {
				continue;
			}
			if(helps.size() > 1) {
				throw new RuntimeException("Duplicate name: " + name + " => " + helps);
			}
			String comment = helps.get(0).getText();
			System.out.println("// " + comment);
			String fieldName = ClassNameUtils.fieldStatic(name);
			StringBuilder sb = new StringBuilder(50);
			sb.append("public static final Encoders ").append(fieldName).append(" = new Encoders(\"").append(name).append('"');
			if(comment.contains("image")) {
				sb.append(", SubType.IMAGE");
			}
			sb.append(");");
			System.out.println(sb);
		}
	}

}
