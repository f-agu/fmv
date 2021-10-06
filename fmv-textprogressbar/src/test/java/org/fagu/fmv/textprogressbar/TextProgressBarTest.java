package org.fagu.fmv.textprogressbar;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.fagu.fmv.textprogressbar.part.ProgressPart;
import org.fagu.fmv.textprogressbar.part.ProgressPart.InsideProgressChar;
import org.junit.jupiter.api.Test;


/**
 * @author fagu
 */
class TextProgressBarTest {

	@Test
	void testWidth() throws Exception {
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
	void testWidthInside() throws Exception {
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

}
