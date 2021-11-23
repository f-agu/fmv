package org.fagu.fmv.soft.exec;

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
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author f.agu
 * @created 5 juin 2017 18:03:29
 */
public class WrapProcess extends Process {

	private final Process delegated;

	public WrapProcess(Process delegated) {
		this.delegated = Objects.requireNonNull(delegated);
	}

	@Override
	public OutputStream getOutputStream() {
		return delegated.getOutputStream();
	}

	@Override
	public InputStream getInputStream() {
		return delegated.getInputStream();
	}

	@Override
	public InputStream getErrorStream() {
		return delegated.getErrorStream();
	}

	@Override
	public int waitFor() throws InterruptedException {
		return delegated.waitFor();
	}

	@Override
	public int exitValue() {
		return delegated.exitValue();
	}

	@Override
	public void destroy() {
		delegated.destroy();
	}

	@Override
	public Process destroyForcibly() {
		return delegated.destroyForcibly();
	}

	@Override
	public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
		return delegated.waitFor(timeout, unit);
	}

	@Override
	public boolean isAlive() {
		return delegated.isAlive();
	}

	@Override
	public boolean equals(Object obj) {
		return delegated.equals(obj);
	}

	@Override
	public int hashCode() {
		return delegated.hashCode();
	}

	@Override
	public String toString() {
		return delegated.toString();
	}

}
