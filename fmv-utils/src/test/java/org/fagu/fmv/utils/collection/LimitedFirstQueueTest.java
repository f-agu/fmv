package org.fagu.fmv.utils.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/*-
 * #%L
 * fmv-utils
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


/**
 * @author fagu
 */
class LimitedFirstQueueTest {

	@Test
	void testNegative() {
		assertThrows(IllegalArgumentException.class, () -> new LimitedFirstQueue<>( - 1));
	}

	@Test
	void test0() {
		assertThrows(IllegalArgumentException.class, () -> new LimitedFirstQueue<>(0));
	}

	@Test
	void test1() {
		LimitedFirstQueue<Integer> queue = new LimitedFirstQueue<>(1);
		queue.add(0);
		queue.add(1);
		queue.add(2);
		queue.add(3);

		assertEquals(1, queue.size());
		assertEquals(0, queue.get(0).intValue());
	}

	@Test
	void test5() {
		LimitedFirstQueue<Integer> queue = new LimitedFirstQueue<>(5);
		queue.add(0);
		queue.add(1);
		queue.add(2);
		queue.add(3);

		assertEquals(4, queue.size());
		assertEquals(0, queue.get(0).intValue());
		assertEquals(1, queue.get(1).intValue());
		assertEquals(2, queue.get(2).intValue());
		assertEquals(3, queue.get(3).intValue());
	}

}
