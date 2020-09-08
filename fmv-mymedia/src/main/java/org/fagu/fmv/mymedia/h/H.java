package org.fagu.fmv.mymedia.h;

/*-
 * #%L
 * fmv-mymedia
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
import java.util.regex.Pattern;


/**
 * @author f.agu
 * @created 6 mars 2018 23:48:14
 */
public class H {

	private static final char TAB = '\t';

	private static final Pattern DATE = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4}");

	public static void main(String[] args) throws IOException {
		File file = new File("d:\\tmp\\a\\h");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			Consumer<String> currentConsumer = null;
			Map<Predicate<String>, Consumer<String>> switcherMap = new LinkedHashMap<>();
			switcherMap.put("Mas"::equals, l -> containsSpace(l, H::mas));
			switcherMap.put("Cpl"::equals, l -> containsSpace(l, H::cpl));
			switcherMap.put(l -> l.startsWith("ref"), l -> containsSpace(l, H::dtl));
			switcherMap.put(l -> l.startsWith("dat"), l -> containsSpace(l, H::dtl));
			switcherMap.put(l -> l.startsWith("reg"), l -> containsSpace(l, H::reg));
			switcherMap.put(l -> l.startsWith("vvsy"), l -> containsSpace(l, H::dtl));
			switcherMap.put(l -> l.startsWith("jtm"), l -> containsSpace(l, H::dtl));
			switcherMap.put(l -> l.startsWith("pen"), H::dmtl);

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
				if(currentConsumer != null) {
					currentConsumer.accept(line);
				}
			}
		}
	}

	private static void containsSpace(String line, Consumer<String> consumer) {
		if(line.indexOf(' ') > 0) {
			consumer.accept(line);
		}
	}

	private static void mas(String line) {
		StringBuilder buf = new StringBuilder()
				.append(line.substring(0, 16)).append(TAB)
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

	private static void dtl(String line) {
		StringBuilder buf = new StringBuilder();
		int p = line.indexOf(' ');
		buf.append(line.substring(0, p)).append(TAB)
				.append(line.substring(p + 1));
		System.out.println(buf);
	}

	private static void reg(String line) {
		StringBuilder buf = new StringBuilder()
				.append(line.substring(0, 10)).append(TAB)
				.append(line.substring(14));
		System.out.println(buf);
	}

	private static void dmtl(String line) {
		if(DATE.matcher(line.trim()).matches()) {
			System.out.println();
			System.out.println(line);
			return;
		}
		System.out.println(line);
	}

}
