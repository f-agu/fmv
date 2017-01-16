package org.fagu.fmv.utils;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;


/**
 * @author f.agu
 */
public class PlaceHolderTestCase {

	/**
	 *
	 */
	public PlaceHolderTestCase() {}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEmptyPattern() throws Exception {
		assertEquals("", PlaceHolder.with("").format(Collections.emptyMap()));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNullPattern() throws Exception {
		assertEquals("", PlaceHolder.with(null).format(Collections.emptyMap()));
	}

	/**
	 *
	 */
	@Test
	public void testOK_None() {
		assertEquals("toto is big or fat", PlaceHolder.format("toto is big or fat", s -> s));
	}

	/**
	 *
	 */
	@Test
	public void testOK_One_middle() {
		assertEquals("toto is BIG or fat", PlaceHolder.format("toto is ${big} or fat", s -> s.toUpperCase()));
	}

	/**
	 *
	 */
	@Test
	public void testOK_One_start() {
		assertEquals("BIG or fat", PlaceHolder.format("${big} or fat", s -> s.toUpperCase()));
	}

	/**
	 *
	 */
	@Test
	public void testOK_One_end() {
		assertEquals("toto is BIG", PlaceHolder.format("toto is ${big}", s -> s.toUpperCase()));
	}

	/**
	 *
	 */
	@Test
	public void testOK_3_end() {
		Map<String, String> map = new HashMap<>();
		map.put("un", "1");
		map.put("deux", "2");
		map.put("trois", "3");
		assertEquals("replace", PlaceHolder.format("bonjour, ${trois} suivi de [${un}]", map), "bonjour, 3 suivi de [1]");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testSystemEnv() throws Exception {
		Map<String, String> map = System.getenv();
		assertFalse("env empty", map.isEmpty());
		for(Entry<String, String> entry : map.entrySet()) {
			assertEquals(entry.getKey(), PlaceHolder.format(PlaceHolder.wrapKey(entry.getKey())), entry.getValue());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDefined() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("un", "1");
		map.put("deux", "2");
		map.put("trois", "3");
		assertEquals("replace", PlaceHolder.format("bonjour, ${trois} suivi de [${un}]", map), "bonjour, 3 suivi de [1]");
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = PlaceHolderException.class)
	public void testUndefined() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("un", "1");
		map.put("deux", "2");
		map.put("trois", "3");
		PlaceHolder.format("bonjour, ${quatre} suivi de [${un}]", map);
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = PlaceHolderException.class)
	public void testUnclosed_alone() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("un", "1");
		map.put("deux", "2");
		map.put("trois", "3");
		PlaceHolder.format("bonjour, ${trois", map);
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = PlaceHolderException.class)
	public void testUnclosed_after() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("un", "1");
		map.put("deux", "2");
		map.put("trois", "3");
		PlaceHolder.format("bonjour, ${trois suivi de [${un}]", map);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testInstanciate() throws Exception {
		assertEquals("bonjour, TROIS suivi de UN et ${deux}", PlaceHolder.with("bonjour, [trois] suivi de [un] et ${deux}").prefix("[").suffix("]")
				.format(s -> s.toUpperCase()));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testRecursive_1() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("un", "UN");
		map.put("deux", "${un}");
		map.put("trois", "${deux}");
		PlaceHolder placeHolder = PlaceHolder.with("bonjour, ${trois} doit etre 1");
		assertEquals("bonjour, UN doit etre 1", placeHolder.formatUnresolvableIgnored(map));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testRecursive_2() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("un", "UN");
		map.put("deux", "${un}");
		map.put("trois", "${deux}");
		PlaceHolder placeHolder = PlaceHolder.with("bonjour, ${trois} doit etre ${un}");
		assertEquals("bonjour, UN doit etre UN", placeHolder.formatUnresolvableIgnored(map));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testRecursiveLoop() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("un", "${trois}");
		map.put("deux", "${un}");
		map.put("trois", "${deux}");
		try {
			PlaceHolder.format("bonjour, ${trois} doit etre 1", map);
			fail("Should throw PlaceHolderException");
		} catch(PlaceHolderException phe) {
			// it works
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testInner0() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("environment", "local");
		map.put("local.datasource.url", "myurl");

		String str = PlaceHolder.format("${${environment}.datasource.url}", map);
		assertEquals("myurl", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testInner1() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("environment", "local");
		map.put("local.datasource.url", "myurl");

		String str = PlaceHolder.format("0${${environment}.datasource.url}1", map);
		assertEquals("0myurl1", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testInner2() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("a", "b");
		map.put("b", "c");
		map.put("c", "d");
		map.put("d", "e");
		map.put("e", "f");

		String str = PlaceHolder.format("${${${${${a}}}}}", map);
		assertEquals("f", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testInner3() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("a", "B");
		map.put("B", "c");
		map.put("c", "${1}");
		map.put("1", "${0}");
		map.put("0", "the end !");

		String str = PlaceHolder.format("[${${${a}}}]", map);
		assertEquals("[the end !]", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testInner4() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("a", "B");
		map.put("B", "c");
		map.put("c", "${1}");
		map.put("1", "${0}");
		map.put("0", "the end !");

		assertEquals("[the end !}]", PlaceHolder.format("[${${${a}}}}]", map));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testVerif() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("configkey", "negative");
		map.put("graph.graph_title", "Tomcat-Postfiles Traffic");
		map.put("min", "0");
		map.put("fieldname", "${key}");
		map.put("graph.graph_vlabel", "Bytes rec(-)/sent(+) per second");
		map.put("jmxAttributeName", "getAverageAverageSpeeds");
		map.put("graph.graph_category", "  tomcat-Postfiles");
		map.put("jmxObjectName", "share-io:name=stats");
		map.put("key", "archive_download");
		map.put("\\n", "\n");
		map.put("type", "DERIVE");
		map.put("configvalue", "catalina_bytes_received");
		map.put("\\r", "\r");
		map.put("\\t", "\t");
		map.put("label", "${key}");
		map.put("jmxAttributeKey", "null");
		map.put("negative", "catalina_bytes_received");

		String str = PlaceHolder.with("${fieldname}.${configkey} ${configvalue}").format(map);
		assertEquals("archive_download.negative catalina_bytes_received", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoc0() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("name", "toto");

		String str = PlaceHolder.with("my name is ${name}").format(map);
		assertEquals("my name is toto", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoc1() throws Exception {
		String str = PlaceHolder.with("my name is ${name}")
				.format(s -> s.toUpperCase());
		assertEquals("my name is NAME", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoc2() throws Exception {
		String str = PlaceHolder
				.with("the date is ${date:yyyy-MM-dd} before ${one} or ${two}")
				.format(s -> {
					if(s.startsWith("date:")) {
						return new SimpleDateFormat(s.substring("date:".length())).format(new Date(2015 - 1900, 10, 30));
					}
					return "one".equals(s) ? "you" : "everybody";
				});
		assertEquals("the date is 2015-11-30 before you or everybody", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoc3() throws Exception {
		String str = PlaceHolder
				.with("[one], [two], ${three}")
				.prefix("[")
				.suffix("]")
				.format(s -> s.toUpperCase());
		assertEquals("ONE, TWO, ${three}", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoc4() throws Exception {
		String str = PlaceHolder
				.with("[one], ${two}, ${user.name} QQ-${not-found}-WW")
				.format(Replacers.chain()
						.ofEnv()
						.ofSystemProperties()
						.of(s -> s.toUpperCase())
						.unresolvableIgnored());
		assertEquals("[one], TWO, " + System.getProperty("user.name") + " QQ-NOT-FOUND-WW", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testSuffixConflict() throws Exception {
		String str = PlaceHolder
				.with("one}two")
				.format(Replacers.chain().of(s -> s.toUpperCase()));
		assertEquals("one}two", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPrefixConflict1() throws Exception {
		String str = PlaceHolder
				.with(" < {$one} ${two} >")
				.prefix("{$").suffix("}")
				.format(Replacers.chain()
						.of(s -> s.toUpperCase()));
		assertEquals(" < ONE ${two} >", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPrefixConflict2() throws Exception {
		String str = PlaceHolder
				.with(" < ${two} {$one} >")
				.prefix("{$").suffix("}")
				.format(Replacers.chain()
						.of(s -> s.toUpperCase()));
		assertEquals(" < ${two} ONE >", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPrefixConflict3() throws Exception {
		String str = PlaceHolder
				.with(" < {$one} ${two} >")
				.format(Replacers.chain()
						.of(s -> s.toUpperCase()));
		assertEquals(" < {$one} TWO >", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPrefixConflict4() throws Exception {
		String str = PlaceHolder
				.with(" < ${two} {$one} >")
				.format(Replacers.chain()
						.of(s -> s.toUpperCase()));
		assertEquals(" < TWO {$one} >", str);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testListNames0() throws Exception {
		List<String> listNames = PlaceHolder
				.with(" < {two} {one} >")
				.listNames();
		assertTrue(listNames.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testListNames2() throws Exception {
		List<String> listNames = PlaceHolder
				.with(" < ${two} ${one} >")
				.listNames();
		assertEquals(new ArrayList<>(Arrays.asList("two", "one")), listNames);
	}
}
