package org.fagu.fmv.soft.exec.exception;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author fagu
 */
public class ExceptionKnown implements Serializable {

	private static final long serialVersionUID = 5134762053343485467L;

	private final NestedException nestedException;

	private final String message;

	public ExceptionKnown(NestedException nestedException, String message) {
		this.nestedException = Objects.requireNonNull(nestedException);
		this.message = Objects.requireNonNull(message);
	}

	public NestedException getNestedException() {
		return nestedException;
	}

	public ExceptionKnown onMessage(Consumer<String> consumer) {
		consumer.accept(message);
		return this;
	}

	public void doThrow() throws IOException {
		throw nestedException.getIOException();
	}

	@Override
	public String toString() {
		return message;
	}
}
