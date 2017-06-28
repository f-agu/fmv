package org.fagu.fmv.utils.collection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * @author fagu
 */
public class LimitedLastQueueTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNegative() {
		new LimitedLastQueue<>( - 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test0() {
		new LimitedLastQueue<>(0);
	}

	@Test
	public void test1() {
		LimitedLastQueue<Integer> queue = new LimitedLastQueue<>(1);
		queue.add(0);
		queue.add(1);
		queue.add(2);
		queue.add(3);

		assertEquals(1, queue.size());
		assertEquals(3, queue.get(0).intValue());
	}

	@Test
	public void test5() {
		LimitedLastQueue<Integer> queue = new LimitedLastQueue<>(5);
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
