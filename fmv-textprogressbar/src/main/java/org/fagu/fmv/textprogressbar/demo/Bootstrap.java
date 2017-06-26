package org.fagu.fmv.textprogressbar.demo;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.part.ColorPart;
import org.fagu.fmv.textprogressbar.part.ProgressPart;
import org.fagu.fmv.textprogressbar.part.SpinnerPart;
import org.fagu.fmv.textprogressbar.part.TextPart;


/**
 * @author fagu
 */
public class Bootstrap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		color();
		// basicProgress();
		// spinner();
	}

	// *********************************************

	/**
	 * @throws Exception
	 */
	private static void color() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(ColorPart.foreground(Color.RED, new TextPart("red")))
				.appendText("  ")
				.append(ColorPart.foreground(Color.BLUE, ColorPart.background(Color.YELLOW, new TextPart("blue on yellow"))))
				.buildForPrinting()) {
			bar.print(0);
		}
	}

	/**
	 * @throws Exception
	 */
	private static void basicProgress() throws Exception {
		AtomicInteger value = new AtomicInteger();
		IntSupplier progressInPercent = value::get;
		try (TextProgressBar bar = TextProgressBar.newBar()
				.fixWidth(25)
				.withText("Basic progress")
				.append(ProgressPart.width(32).build())
				.buildAndSchedule(progressInPercent)) {

			for(int i = 0; i < 100; i += 3) {
				value.set(i);
				Thread.sleep(100);
			}
		}
	}

	/**
	 * @throws Exception
	 */
	private static void spinner() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.fixWidth(40)
				.centerPad()
				.withText("Spinner: 2 turns per second...")
				.append(new SpinnerPart(2))
				.buildAndSchedule()) {

			Thread.sleep(5000);
		}
	}

}
