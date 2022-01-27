package org.fagu.fmv.ffmpeg.utils.srcgen;

import java.util.List;

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

import org.fagu.fmv.ffmpeg.coder.Decoders;
import org.fagu.fmv.ffmpeg.coder.Decoders.DecoderHelp;
import org.fagu.fmv.ffmpeg.soft.FFMpeg;
import org.fagu.fmv.ffmpeg.utils.HelpCache;


/**
 * @author f.agu
 */
public class GenerateDecoders {

	public static void main(String[] args) {
		System.out.println("// Generated by " + GenerateDecoders.class.getName());
		System.out.println("// with " + FFMpeg.search().getFirstFound().getSoftInfo().toString());
		System.out.println();

		HelpCache<Decoders, DecoderHelp> helpCache = Decoders.getHelpCache();
		for(String name : helpCache.availableNames()) {
			List<DecoderHelp> helps = helpCache.cache(name);
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
			sb.append("public static final Decoders ").append(fieldName).append(" = new Decoders(\"").append(name).append('"');
			if(comment.contains("image")) {
				sb.append(", SubType.IMAGE");
			}
			sb.append(");");
			System.out.println(sb);
		}
	}

}
