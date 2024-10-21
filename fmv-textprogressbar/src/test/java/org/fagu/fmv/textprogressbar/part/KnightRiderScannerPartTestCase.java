package org.fagu.fmv.textprogressbar.part;

import org.fagu.fmv.textprogressbar.ProgressStatus;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * @author Oodrive
 * @author f.agu
 * @created 21 oct. 2024 10:00:41
 */
class KnightRiderScannerPartTestCase {

	@Test
	void test0() {
		KnightRiderScannerPart part = new KnightRiderScannerPart(10);
		ProgressStatus progressStatus = mock(ProgressStatus.class);
		doReturn(false).when(progressStatus).isFinished();
		for(int i = 0; i < 30; ++i) {
			part.getWith(progressStatus);
		}
	}

	@Test
	void testBasic() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(new KnightRiderScannerPart(10))
				.buildAndSchedule()) {
			Thread.sleep(100_000);
		}
	}

	@Test
	void testDots() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(KnightRiderScannerPart.withWidth(10).withPresetDots().withRefreshInMilliseconds(80L).build())
				.buildAndSchedule()) {
			Thread.sleep(100_000);
		}
	}

	@Test
	void testTarget() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(KnightRiderScannerPart.withWidth(10).withPresetTarget().withRefreshInMilliseconds(80L).build())
				.buildAndSchedule()) {
			Thread.sleep(100_000);
		}
	}
}
