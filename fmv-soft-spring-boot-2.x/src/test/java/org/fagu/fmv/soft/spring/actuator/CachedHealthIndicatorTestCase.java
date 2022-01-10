package org.fagu.fmv.soft.spring.actuator;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * @author Oodrive
 * @author f.agu
 * @created 6 janv. 2022 18:16:02
 */
class CachedHealthIndicatorTestCase {

	@Test
	void testCacheWithTimeOut() throws Exception {
		HealthIndicator healthIndicator = mock(HealthIndicator.class);
		doReturn(Health.down().build()).when(healthIndicator).health();
		CachedHealthIndicator indicator = new CachedHealthIndicator(healthIndicator, Duration.ofSeconds(1));

		indicator.health();
		verify(healthIndicator, times(1)).health();

		indicator.health();
		verify(healthIndicator, times(2)).health();

		Thread.sleep(1_500);
		indicator.health();
		verify(healthIndicator, times(3)).health();
	}

	@Test
	void testUpWithTimeOut() throws Exception {
		HealthIndicator healthIndicator = mock(HealthIndicator.class);
		doReturn(Health.up().build()).when(healthIndicator).health();
		CachedHealthIndicator indicator = new CachedHealthIndicator(healthIndicator, Duration.ofSeconds(2));

		indicator.health();
		verify(healthIndicator, times(1)).health();

		indicator.health();
		verify(healthIndicator, times(1)).health();

		Thread.sleep(2_500);
		indicator.health();
		verify(healthIndicator, times(2)).health();
	}

	@Test
	void testDownWithoutTimeOut() throws Exception {
		HealthIndicator healthIndicator = mock(HealthIndicator.class);
		doReturn(Health.down().build()).when(healthIndicator).health();
		CachedHealthIndicator indicator = new CachedHealthIndicator(healthIndicator, null);

		indicator.health();
		verify(healthIndicator, times(1)).health();

		indicator.health();
		verify(healthIndicator, times(2)).health();

		Thread.sleep(1_000);
		indicator.health();
		verify(healthIndicator, times(3)).health();
	}

	@Test
	void testUpWithoutTimeOut() throws Exception {
		HealthIndicator healthIndicator = mock(HealthIndicator.class);
		doReturn(Health.up().build()).when(healthIndicator).health();
		CachedHealthIndicator indicator = new CachedHealthIndicator(healthIndicator, null);

		indicator.health();
		verify(healthIndicator, times(1)).health();

		indicator.health();
		verify(healthIndicator, times(1)).health();

		Thread.sleep(1_000);
		indicator.health();
		verify(healthIndicator, times(1)).health();
	}

}
