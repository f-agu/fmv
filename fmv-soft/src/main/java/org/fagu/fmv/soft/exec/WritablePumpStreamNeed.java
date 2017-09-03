package org.fagu.fmv.soft.exec;

/**
 * @author fagu
 */
@FunctionalInterface
public interface WritablePumpStreamNeed {

	/**
	 * @param writablePumpStreamHandler
	 */
	void apply(WritablePumpStreamHandler writablePumpStreamHandler);
}
