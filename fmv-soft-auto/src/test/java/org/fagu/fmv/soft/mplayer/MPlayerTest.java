package org.fagu.fmv.soft.mplayer;

/*-
 * #%L
 * fmv-soft-auto
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

import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.LogExecuteDelegate;
import org.fagu.fmv.soft.find.SoftFound;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author fagu
 */
@Ignore
public class MPlayerTest {

	@Test
	public void testSearch() {
		ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		MPlayer.search();
	}

	@Test
	public void testMPlayer() {
		for(SoftFound softFound : MPlayer.search().getFounds()) {
			System.out.println(softFound);
		}
	}

	@Test
	public void testMEncoder() {
		for(SoftFound softFound : MEncoder.search().getFounds()) {
			System.out.println(softFound);
		}
	}

}
