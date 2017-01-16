package org.fagu.fmv.utils.collection;

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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class MultiValueMapsTestCase {

	/**
	 * 
	 */
	public MultiValueMapsTestCase() {}

	/**
	 * 
	 */
	@Test
	@Ignore
	public void testPourMail() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		// ...
		List<String> list = map.get("key");
		if(list == null) {
			list = new ArrayList<String>();
			map.put("key", list);
		}
		list.add("value");

		// devient :

		MapList<String, String> listValueMap = MultiValueMaps.<String, String>hashMapArrayList();
		listValueMap.add("key", "value");
	}

	/**
	 * 
	 */
	@Test
	public void test1() {
		MapList<String, Integer> listValueMap = MultiValueMaps.<String, Integer>hashMapArrayList();
		listValueMap.add("toto", 4);
		listValueMap.add("toto", 3);
		listValueMap.add("toto", 2);
		listValueMap.add("toto", 1);
		listValueMap.add("TI", 99);
		listValueMap.add("TI", 99);

		assertEquals(2, listValueMap.size());
		assertNull(listValueMap.get("titI"));
		List<Integer> list = listValueMap.get("TI");
		assertEquals(Arrays.asList(99, 99), list);
		list = listValueMap.get("toto");
		assertEquals(Arrays.asList(4, 3, 2, 1), list);
	}
}
