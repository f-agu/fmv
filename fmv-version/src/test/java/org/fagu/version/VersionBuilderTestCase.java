package org.fagu.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.fagu.version.Version.VersionBuilder;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class VersionBuilderTestCase {

	@Test
	void test() {
		assertSame(Version.V1, VersionBuilder.v(1).build());
		assertEquals("4.5.6-RC7", VersionBuilder.v(4, 5, 6).rc(7).build().toString());
	}

}
