package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * @author f.agu
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ConcatTestCase {

	/**
	 *
	 */
	private Concat concat;

	/**
	 *
	 */
	public ConcatTestCase() {}

	/**
	 *
	 */
	@Before
	public void setUp() {
		concat = new Concat(null);
	}

	/**
	 *
	 */
	@Test
	public void test2Inputs() {
		concat.addInput(mockInputProcessor(0));
		concat.addInput(mockInputProcessor(1));

		assertEquals("[0][1] concat=n=2:v=1:a=1 [con_a]", concat.toString());
	}

	/**
	 *
	 */
	@Test
	public void test10Inputs() {
		for(int i = 9; i >= 0; --i) {
			concat.addInput(mockInputProcessor(i));
		}
		assertEquals("[9][8][7][6][5][4][3][2][1][0] concat=n=10:v=1:a=1 [con_a]", concat.toString());
	}

	// *****************************************************

	/**
	 * @param index
	 * @return
	 */
	private InputProcessor mockInputProcessor(int index) {
		InputProcessor inputProcessor = mock(InputProcessor.class);
		doReturn(Collections.singletonList(Integer.toString(index))).when(inputProcessor).getOutputKeys();
		MediaInput mediaInput = mock(MediaInput.class);
		doReturn(Collections.singletonList(mediaInput)).when(inputProcessor).getInputs();
		return inputProcessor;
	}
}
