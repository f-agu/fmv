package org.fagu.fmv.mymedia.h;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * @author f.agu
 * @created 6 mars 2018 23:48:14
 */
public class H {

	private static final char TAB = '\t';

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("d:\\tmp\\a\\h");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			Consumer<String> currentConsumer = null;
			Map<Predicate<String>, Consumer<String>> switcherMap = new LinkedHashMap<>();
			switcherMap.put("Mas"::equals, H::mas);
			switcherMap.put("Cpl"::equals, H::cpl);
			switcherMap.put(l -> l.startsWith("ref"), H::ref);
			switcherMap.put(l -> l.startsWith("dat"), H::dat);
			switcherMap.put(l -> l.startsWith("reg"), H::reg);

			while((line = reader.readLine()) != null) {
				if("".equals(line.trim())) {
					continue;
				}
				for(Entry<Predicate<String>, Consumer<String>> entry : switcherMap.entrySet()) {
					if(entry.getKey().test(line)) {
						currentConsumer = entry.getValue();
						System.out.println();
						System.out.println("############# " + line + " #############");
						continue;
					}
				}
				if(line.indexOf(' ') < 0) {
					continue;
				}
				if(currentConsumer != null) {
					currentConsumer.accept(line);
				}
			}
		}
	}

	private static void mas(String line) {
		StringBuilder buf = new StringBuilder();
		buf.append(line.substring(0, 16)).append(TAB)
				.append(TAB)
				.append(line.substring(17, 18)).append(TAB)
				.append(TAB)
				.append(line.substring(19));
		System.out.println(buf);
	}

	private static void cpl(String line) {
		StringBuilder buf = new StringBuilder();
		int p = line.indexOf(' ', 19);
		buf.append(line.substring(0, 16)).append(TAB)
				.append(TAB)
				.append(TAB)
				.append(line.substring(19, p)).append(TAB)
				.append(line.substring(17, 18)).append(TAB)
				.append(TAB)
				.append("non").append(TAB)
				.append(line.substring(p + 1));
		System.out.println(buf);
	}

	private static void ref(String line) {
		StringBuilder buf = new StringBuilder();
		int p = line.indexOf(' ');
		buf.append(line.substring(0, p)).append(TAB)
				.append(line.substring(p + 1));
		System.out.println(buf);
	}

	private static void dat(String line) {
		StringBuilder buf = new StringBuilder();
		buf.append(line.substring(0, 10)).append(TAB)
				.append(line.substring(11));
		System.out.println(buf);
	}

	private static void reg(String line) {
		StringBuilder buf = new StringBuilder();
		buf.append(line.substring(0, 10)).append(TAB)
				.append(line.substring(14));
		System.out.println(buf);
	}

}
