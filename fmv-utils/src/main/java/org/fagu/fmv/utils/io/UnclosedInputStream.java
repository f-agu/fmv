package org.fagu.fmv.utils.io;

/*
 * #%L
 * fmv-utils
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author f.agu
 */
public class UnclosedInputStream extends FilterInputStream {

	public UnclosedInputStream(InputStream in) {
		super(in);
	}

	@Override
	public void close() throws IOException {
		// DO NOTHING
	}

	public void closeForce() throws IOException {
		super.close();
	}

}
