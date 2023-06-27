package org.fagu.fmv.textprogressbar.demo;

/*-
 * #%L
 * fmv-textprogressbar
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

	public static void main(String... args) throws Exception {
		color();
		basicProgress();
		spinner();
	}

	// *********************************************

	private static void color() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.append(ColorPart.foreground(Color.RED, new TextPart("red")))
				.appendText("  ")
				.append(ColorPart.foreground(Color.BLUE, ColorPart.background(Color.YELLOW, new TextPart("blue on yellow"))))
				.buildForPrinting()) {
			bar.print(0);
		}
	}

	private static void basicProgress() throws Exception {
		AtomicInteger value = new AtomicInteger();
		IntSupplier progressInPercent = value::get;
		try (TextProgressBar bar = TextProgressBar.newBar()
				.fixWidth(25).withText("Basic progress")
				.append(ProgressPart.width(32).build())
				.buildAndSchedule(progressInPercent)) {

			for(int i = 0; i < 100; i += 3) {
				value.set(i);
				Thread.sleep(100);
			}
		}
	}

	private static void spinner() throws Exception {
		try (TextProgressBar bar = TextProgressBar.newBar()
				.fixWidth(40).centerPad().withText("Spinner: 2 turns per second...")
				.append(new SpinnerPart(2))
				.buildAndSchedule()) {

			Thread.sleep(5000);
		}
	}

}
