package org.fagu.fmv.soft.io;

/*-
 * #%L
 * fmv-soft
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;


/**
 * @author f.agu
 * @created 8 juil. 2019 17:35:09
 */
public class StreamLog {

	private static boolean enabled = false;

	private StreamLog() {}

	public static void debug(boolean enabled) {
		StreamLog.enabled = enabled;
	}

	public static InputStream wrap(InputStream inputStream, Consumer<String> logger) {
		if(enabled) {
			return new LogInputStream(inputStream, logger);
		}
		return inputStream;
	}

	public static OutputStream wrap(OutputStream outputStream, Consumer<String> logger) {
		if(enabled) {
			return new LogOutputStream(outputStream, logger);
		}
		return outputStream;
	}

}
