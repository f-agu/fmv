package org.fagu.fmv.soft.exec;

import java.util.concurrent.Future;
import java.util.function.Function;


/**
 * @author fagu
 */
public class UnaryWrapFuture<V> extends WrapFuture<V, V> {

	/**
	 * @param future
	 */
	public UnaryWrapFuture(Future<V> future) {
		super(future, Function.identity());
	}
}
