package org.fagu.fmv.mymedia.utils;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import org.fagu.fmv.mymedia.utils.TextProgressBar.TextProgressBarBuilder;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
@Ignore
public class TextProgressBarTestCase {

	/**
	 *
	 */
	public TextProgressBarTestCase() {}

	@Test
	public void test() throws Exception {
		TextProgressBar textProgressBar = TextProgressBarBuilder.width(100).build();

		AtomicInteger prg = new AtomicInteger();

		textProgressBar.schedule(() -> prg.addAndGet(5), null);
		Thread.sleep(100000);
	}

}
