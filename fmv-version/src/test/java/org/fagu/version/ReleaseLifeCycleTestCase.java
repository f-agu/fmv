package org.fagu.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class ReleaseLifeCycleTestCase {

	@Test
	void testRC() {
		assertEquals("RC0", ReleaseLifeCycle.parse("RC").toString());
		assertEquals("RC1", ReleaseLifeCycle.parse("RC1").toString());
		assertEquals("RC9856", ReleaseLifeCycle.parse("Rc9856").toString());
		assertEquals("RELEASE-CANDIDATE8", ReleaseLifeCycle.parse("reLEASE-candidatE8").toString());
	}

	@Test
	void testCompare() {
		ReleaseLifeCycle rlcSnapshot = ReleaseLifeCycle.parse("SNAPSHOT");
		ReleaseLifeCycle rlcPrealpha4 = ReleaseLifeCycle.parse("prealpha4");
		ReleaseLifeCycle rlcPrealpha7 = ReleaseLifeCycle.parse("prealpha7");
		ReleaseLifeCycle rlcAlpha = ReleaseLifeCycle.parse("alpha");
		ReleaseLifeCycle rlcBeta2 = ReleaseLifeCycle.parse("BETA2");
		ReleaseLifeCycle rlcBeta3 = ReleaseLifeCycle.parse("BETA3");
		ReleaseLifeCycle rlcRC1 = ReleaseLifeCycle.parse("RC1");
		ReleaseLifeCycle rlcRC2 = ReleaseLifeCycle.parse("RC2");
		ReleaseLifeCycle rlcRTM = ReleaseLifeCycle.parse("RTM");
		ReleaseLifeCycle rlcGA = ReleaseLifeCycle.parse("GA");
		ReleaseLifeCycle rlcFinal = ReleaseLifeCycle.parse("FINAL");

		Set<ReleaseLifeCycle> set = new TreeSet<>();
		set.add(rlcSnapshot);
		set.add(rlcPrealpha4);
		set.add(rlcPrealpha7);
		set.add(rlcAlpha);
		set.add(rlcBeta2);
		set.add(rlcBeta3);
		set.add(rlcRC1);
		set.add(rlcRC2);
		set.add(rlcRTM);
		set.add(rlcGA);
		set.add(rlcFinal);

		Iterator<ReleaseLifeCycle> iterator = set.iterator();

		assertSame(rlcSnapshot, iterator.next());
		assertSame(rlcPrealpha4, iterator.next());
		assertSame(rlcPrealpha7, iterator.next());
		assertSame(rlcAlpha, iterator.next());
		assertSame(rlcBeta2, iterator.next());
		assertSame(rlcBeta3, iterator.next());
		assertSame(rlcRC1, iterator.next());
		assertSame(rlcRC2, iterator.next());
		assertSame(rlcRTM, iterator.next());
		assertSame(rlcGA, iterator.next());
		assertSame(rlcFinal, iterator.next());
	}

	@Test
	void testToString() {
		assertEquals("SNAPSHOT", ReleaseLifeCycle.snapshot().toString());
		assertEquals("RC4", ReleaseLifeCycle.rc(4).toString());
	}
}
