package org.fagu.fmv.ffmpeg.utils;

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

import org.fagu.fmv.ffmpeg.utils.srcgen.ClassNameUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ProtocolTestCase {

	@Test
	@Disabled
	void generator() {
		for(Protocol protocol : Protocol.available()) {
			String name = protocol.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			boolean i = protocol.isInput();
			boolean o = protocol.isOutput();
			System.out.println("// " + (i ? "input" : "") + (i && o ? " & " : "") + (o ? "output" : ""));
			System.out.println("public static final Protocol " + fieldName + " = new Protocol(\"" + name + "\");");
		}
	}

}
