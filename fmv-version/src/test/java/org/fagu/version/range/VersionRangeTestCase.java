package org.fagu.version.range;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fagu.version.Version;
import org.fagu.version.range.VersionRange.Combinatoric;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class VersionRangeTestCase {

	@Test
	void test1() {
		assertEquals("[1,2)", VersionRange
				.between(new Version(1), Combinatoric.INCLUDE)
				.and(new Version(2), Combinatoric.EXCLUDE)
				.toString());
	}

}
