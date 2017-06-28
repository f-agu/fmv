package org.fagu.fmv.utils.collection;

import java.util.LinkedList;


/**
 * @author fagu
 *
 * @param <E>
 */
public class LimitedFirstQueue<E> extends LinkedList<E> {

	private static final long serialVersionUID = - 4919982990481713127L;

	private final int limit;

	/**
	 * @param limit
	 */
	public LimitedFirstQueue(int limit) {
		if(limit <= 0) {
			throw new IllegalArgumentException("limit must at least 1: " + limit);
		}
		this.limit = limit;
	}

	/**
	 * @see java.util.LinkedList#add(java.lang.Object)
	 */
	@Override
	public boolean add(E o) {
		if(size() >= limit) {
			return false;
		}
		return super.add(o);
	}
}
