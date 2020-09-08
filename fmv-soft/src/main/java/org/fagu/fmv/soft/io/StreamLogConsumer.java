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

import java.util.function.Consumer;


/**
 * @author f.agu
 * @created 8 juil. 2019 17:22:06
 */
public class StreamLogConsumer {

	private StreamLogConsumer() {}

	public static Consumer<String> in() {
		return in(System.out::println);
	}

	public static Consumer<String> out() {
		return out(System.out::println);
	}

	public static Consumer<String> err() {
		return err(System.out::println);
	}

	public static Consumer<String> in(Consumer<String> consumer) {
		return to("IN:  ", consumer);
	}

	public static Consumer<String> out(Consumer<String> consumer) {
		return to("OUT: ", consumer);
	}

	public static Consumer<String> err(Consumer<String> consumer) {
		return to("ERR: ", consumer);
	}

	public static Consumer<String> to(String title, Consumer<String> consumer) {
		return l -> consumer.accept(title + l);
	}

}
