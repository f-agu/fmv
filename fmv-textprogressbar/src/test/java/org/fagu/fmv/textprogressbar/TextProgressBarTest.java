package org.fagu.fmv.textprogressbar;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.fagu.fmv.textprogressbar.part.ProgressPart;
import org.fagu.fmv.textprogressbar.part.ProgressPart.InsideProgressChar;
import org.fagu.fmv.textprogressbar.part.SpinnerPart;
import org.junit.Test;


/**
 * @author fagu
 */
public class TextProgressBarTest {

	@Test
	public void testWidth() throws Exception {
		final int width = 30;
		AtomicInteger value = new AtomicInteger();
		IntSupplier progressInPercent = value::get;

		AtomicReference<String> line = new AtomicReference<String>();
		try (TextProgressBar bar = TextProgressBar.newBar()
				.displayTo(l -> line.set(l.replace("\r", "")))
				.append(ProgressPart.width(width).build())
				.buildAndSchedule(progressInPercent)) {

			Thread.sleep(700);
			assertEquals("[>                           ]", line.get());

			for(int i = 0; i < 100; i += 7) {
				value.set(i);
				Thread.sleep(100);
				String s = line.get();
				s = s.replace("\r", "");
				assertEquals(width, s.length());
				assertEquals('[', s.charAt(0));
				assertEquals(']', s.charAt(s.length() - 1));
			}
		}
		assertEquals("[============================]", line.get());
	}

	@Test
	public void testWidthInside() throws Exception {
		final int width = 30;
		AtomicInteger value = new AtomicInteger();
		AtomicInteger inside = new AtomicInteger( - 1);
		IntSupplier progressInPercent = value::get;
		Supplier<Integer> percentInsideSupplier = () -> inside.get() < 0 ? null : inside.get();

		AtomicReference<String> line = new AtomicReference<String>();
		try (TextProgressBar bar = TextProgressBar.newBar()
				.displayTo(l -> line.set(l.replace("\r", "")))
				.append(ProgressPart.width(width)
						.progressChars(new InsideProgressChar(percentInsideSupplier))
						.build())
				.buildAndSchedule(progressInPercent)) {

			Thread.sleep(700);
			assertEquals("[>                           ]", line.get());

			for(int i = 0; i < 100; i += 7) {
				value.set(i);
				inside.set(i / 2);
				Thread.sleep(100);
				String s = line.get();
				s = s.replace("\r", "");
				assertEquals(width, s.length());
				assertEquals('[', s.charAt(0));
				assertEquals(']', s.charAt(s.length() - 1));
			}
		}
		assertEquals("[#############===============]", line.get());
	}

	@Test
	public void testSpinner() throws Exception {
		final int width = 30;
		AtomicInteger value = new AtomicInteger();
		AtomicInteger inside = new AtomicInteger( - 1);
		IntSupplier progressInPercent = value::get;
		Supplier<Integer> percentInsideSupplier = () -> inside.get() < 0 ? null : inside.get();

		AtomicReference<String> line = new AtomicReference<String>();
		try (TextProgressBar bar = TextProgressBar.newBar()
				// .displayTo(l -> line.set(l.replace("\r", "")))
				.appendText("running...  ")
				.append(new SpinnerPart())
				.buildAndSchedule()) {

			Thread.sleep(5000);

		}
		assertEquals("[#############===============]", line.get());
	}

}
