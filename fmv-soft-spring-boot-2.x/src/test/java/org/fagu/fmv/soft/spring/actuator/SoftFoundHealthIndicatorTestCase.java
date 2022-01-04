package org.fagu.fmv.soft.spring.actuator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health.CheckPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;


/**
 * @author Oodrive
 * @author f.agu
 * @created 31 déc. 2021 09:32:44
 */
class SoftFoundHealthIndicatorTestCase {

	private static Stream<Arguments> provideParameters() {
		return Stream.of(
				Arguments.of(new Context("CheckPolicy.ALWAYS", CheckPolicy.ALWAYS)),
				Arguments.of(new Context("CheckPolicy.ON_FILE_CHANGE", CheckPolicy.ON_FILE_CHANGE))
		//
		);
	}

	@AfterEach
	void tearDown() {
		Softs.clearHealthIndicators();
	}

	@Test
	void testEmpty() {
		SoftFoundHealthIndicator indicator = new SoftFoundHealthIndicator(new org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health());
		for(int i = 0; i < 3; ++i) {
			Health health = indicator.getHealth(true);
			assertEquals(Status.UNKNOWN, health.getStatus());
			assertTrue(health.getDetails().isEmpty());
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideParameters")
	void test1Undefined(Context context) {
		TestSoftProvider testSoftProvider = new TestSoftProvider("test1", () -> null);
		run(testSoftProvider, context, health -> {
			assertEquals(Status.DOWN, health.getStatus());
			Map<String, Object> details = health.getDetails();
			assertEquals(2, details.size());
			assertEquals(details.get("test1"), "test1 <unknown>");
			assertEquals("test1", details.get("Down soft list"));
		});
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideParameters")
	void test1Notfound(Context context) {
		TestSoftProvider testSoftProvider = new TestSoftProvider("test1", () -> SoftFound.notFound());
		run(testSoftProvider, context, health -> {
			assertEquals(Status.DOWN, health.getStatus());
			Map<String, Object> details = health.getDetails();
			assertEquals(2, details.size());
			assertEquals(details.get("test1"), "test1 <not found>");
			assertEquals("test1", details.get("Down soft list"));
		});
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideParameters")
	void test1BadVersion(Context context) {
		TestSoftProvider testSoftProvider = new TestSoftProvider("test1", () -> SoftFound.foundBadVersion(new TestSoftInfo("1.2"), "1.3"));
		run(testSoftProvider, context, health -> {
			assertEquals(Status.DOWN, health.getStatus());
			Map<String, Object> details = health.getDetails();
			assertEquals(2, details.size());
			String detailTest1 = (String)details.get("test1");
			assertTrue(detailTest1.startsWith("test1 <bad version ("));
			assertTrue(detailTest1.contains("): I need at least 1.3>"));
			assertEquals("test1", details.get("Down soft list"));
		});
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideParameters")
	void test1BadSoft(Context context) {
		TestSoftProvider testSoftProvider = new TestSoftProvider("test1", () -> SoftFound.foundBadSoft(new TestSoftInfo("1.2"), "crazy ?"));
		run(testSoftProvider, context, health -> {
			assertEquals(Status.DOWN, health.getStatus());
			Map<String, Object> details = health.getDetails();
			assertEquals(2, details.size());
			assertEquals("test1 <bad soft: crazy ?>", details.get("test1"));
			assertEquals("test1", details.get("Down soft list"));
		});
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideParameters")
	void test1Found(Context context) {
		TestSoftProvider testSoftProvider = new TestSoftProvider("test1", () -> SoftFound.found(new File("pom.xml")));
		run(testSoftProvider, context, health -> {
			assertEquals(Status.UP, health.getStatus());
			assertEquals(1, health.getDetails().size());
			assertTrue(health.getDetails().toString().startsWith("{test1=test1 ("));
		});
	}

	// ******************************************************************************

	private void run(TestSoftProvider testSoftProvider, Context context, Consumer<Health> healthConsumer) {
		Softs.indicateHealth(testSoftProvider.search());

		org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health hprops = new org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health();
		hprops.setCheckPolicy(context.getCheckPolicy());
		SoftFoundHealthIndicator indicator = new SoftFoundHealthIndicator(hprops);
		for(int i = 0; i < 3; i++) {
			Health health = indicator.getHealth(true);
			healthConsumer.accept(health);
		}
	}

}
