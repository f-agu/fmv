package org.fagu.version;

import java.util.Comparator;


/**
 * @author f.agu
 */
public interface VersionComparator extends Comparator<Version> {

	/**
	 * Useful constant for the highest precedence value.
	 * 
	 * @see java.lang.Integer#MIN_VALUE
	 */
	int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

	/**
	 * Useful constant for the lowest precedence value.
	 * 
	 * @see java.lang.Integer#MAX_VALUE
	 */
	int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

	int getOrder();
}
