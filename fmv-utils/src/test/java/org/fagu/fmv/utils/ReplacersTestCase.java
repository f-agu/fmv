package org.fagu.fmv.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ReplacersTestCase {

	private PlaceHolder placeHolder;

	@BeforeEach
	void setUp() {
		placeHolder = PlaceHolder.with("toto");
	}

	@Test
	void testEmpty() {
		Replacer replacer = Replacers.chain();
		replacer.init(placeHolder);
		assertThrows(PlaceHolderException.class, () -> replacer.replace("key"));
	}

	@Test
	void testMap_OK() {
		Replacer replacer = Replacers.chain().map(Collections.singletonMap("key", "value"));
		replacer.init(placeHolder);
		assertEquals("value", replacer.replace("key"));
	}

	@Test
	void testMap_notFound() {
		Replacer replacer = Replacers.chain().map(Collections.singletonMap("key", "value"));
		replacer.init(placeHolder);
		assertThrows(PlaceHolderException.class, () -> replacer.replace("testMap_notFound"));
	}

	@Test
	void testOf() {
		Replacer replacer = Replacers.chain().of(s -> s.toUpperCase());
		replacer.init(placeHolder);
		assertEquals("KEY", replacer.replace("key"));
		assertEquals("XXX0", replacer.replace("xxx0"));
	}

	@Test
	void testOfEnv() {
		String firstKey = System.getenv().keySet().iterator().next();
		Replacer replacer = Replacers.chain().ofEnv();
		replacer.init(placeHolder);
		assertNotNull(replacer.replace(firstKey));
	}

	@Test
	void testOfSystemProperties() {
		String firstKey = String.valueOf(System.getProperties().keySet().iterator().next());
		Replacer replacer = Replacers.chain().ofSystemProperties();
		replacer.init(placeHolder);
		assertNotNull(replacer.replace(firstKey));
	}

	@Test
	void testUnresolvableCopy() {
		Replacer replacer = Replacers.chain().unresolvableCopy();
		replacer.init(placeHolder);
		assertEquals("${not-found}", replacer.replace("not-found"));
	}

	@Test
	void testUnresolvableThrow() {
		Replacer replacer = Replacers.chain().unresolvableThrow();
		replacer.init(placeHolder);
		assertThrows(PlaceHolderException.class, () -> replacer.replace("not-found"));
	}

	@Test
	void testMap_of_ofEnv() {
		Entry<String, String> firstEnv = System.getenv().entrySet().iterator().next();
		Replacer replacer = Replacers.chain()
				.map(Collections.singletonMap("key", "value"))
				.ofEnv()
				.of(s -> s.toUpperCase());
		replacer.init(placeHolder);
		assertEquals("value", replacer.replace("key"));
		assertEquals(firstEnv.getValue(), replacer.replace(firstEnv.getKey()));
		assertEquals("KEY4", replacer.replace("key4"));
	}

	@Test
	void testMap_of_unresolvableCopy() {
		Entry<String, String> firstEnv = System.getenv().entrySet().iterator().next();
		Replacer replacer = Replacers.chain()
				.map(Collections.singletonMap("key", "value"))
				.ofEnv()
				.unresolvableCopy();
		replacer.init(placeHolder);
		assertEquals("value", replacer.replace("key"));
		assertEquals(firstEnv.getValue(), replacer.replace(firstEnv.getKey()));
		assertEquals("${key4}", replacer.replace("key4"));
	}

	@Test
	void testKeyValue() {
		Replacer replacer = Replacers.chain().keyValue("key", "value");
		assertEquals("value", replacer.replace("key"));
	}

	@Test
	void testFunctionNamed() {
		Replacer replacer = Replacers.chain().functionNamed("date", s -> s).unresolvableCopy();
		assertEquals("now", replacer.replace("date:now"));

		String k = PlaceHolder
				.with("the date is ${date:yyyy-MM-dd} before ${one} or ${two}")
				.format(Replacers.chain()
						.functionNamed("date", s -> DateTimeFormatter.ofPattern(s).format(LocalDate.of(2016, 2, 19)))
						.keyValue("one", "you")
						.unresolvableIgnored("everybody"));
		assertEquals("the date is 2016-02-19 before you or everybody", k);
	}

	@Test
	void testToString_1() {
		assertEquals("unresolvableCopy", Replacers.chain().unresolvableCopy().toString());
		assertEquals("unresolvableIgnore()", Replacers.chain().unresolvableIgnored().toString());
		assertEquals("unresolvableIgnore(PRouT4)", Replacers.chain().unresolvableIgnored("PRouT4").toString());
		assertEquals("unresolvableThrow", Replacers.chain().unresolvableThrow().toString());
		assertEquals("unresolvableThrow", Replacers.chain().unresolvableThrow(RuntimeException::new).toString());
		assertEquals("keyValue{key0=-valUe-}", Replacers.chain().keyValue("key0", "-valUe-").toString());
		assertEquals("map{}", Replacers.chain().map(new HashMap<>()).toString());
		String s = Replacers.chain().ofEnv().toString();
		assertTrue(s.startsWith("env{") && s.endsWith("}"));
		s = Replacers.chain().ofSystemProperties().toString();
		assertTrue(s.startsWith("systemproperties{") && s.endsWith("}"));

		assertEquals("touppercase", Replacers.chain().of(k -> k.toUpperCase(), "touppercase").toString());
	}

	@Test
	void testToString_multi() {
		assertEquals("[keyValue{k=v}, keyValue{A=5}, unresolvableIgnore()]", Replacers.chain().keyValue("k", "v").keyValue("A", "5")
				.unresolvableIgnored().toString());
	}

}
