package org.fagu.fmv.textprogressbar.part;

import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 18 oct. 2024 11:12:31
 */
@Disabled
public class ChainCharsPartTestCase {

	@Test
	void testSpinner() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(new SpinnerPart(2D))
				.buildAndSchedule()) {
			Thread.sleep(4_000);
		}
	}

	@Test
	void testFadeInOut() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(ChainedCharPart.fadeInOut())
				.buildAndSchedule()) {
			Thread.sleep(4_000);
		}
	}

	@Test
	void testCustom() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(new ChainedCharPart(new char[] {'┌', '┐', '┘', '└'}))
				.buildAndSchedule()) {
			Thread.sleep(4_000);
		}
	}

	@Test
	void testCustom2() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(new ChainedCharPart(new char[] {'┤', '┘', '┴', '└', '├', '┌', '┬', '┐'}))
				.buildAndSchedule()) {
			Thread.sleep(4_000);
		}
	}

	@Test
	void testCustom3() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(new ChainedCharPart(new char[] {'ᚋ', 'ᚌ', 'ᚍ', 'ᚎ', 'ᚏ'}))
				.buildAndSchedule()) {
			Thread.sleep(4_000);
		}
	}

	@Test
	void testCustom4() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(new ChainedCharPart(new char[] {'⊕', '⊗'}))
				.buildAndSchedule()) {
			Thread.sleep(4_000);
		}
	}

	@Test
	void testCustom5() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(new ChainedCharPart(new char[] {'⊕', '⊗'}))
				.buildAndSchedule()) {
			Thread.sleep(4_000);
		}
	}

}
