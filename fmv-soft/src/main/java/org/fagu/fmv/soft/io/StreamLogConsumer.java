package org.fagu.fmv.soft.io;

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
