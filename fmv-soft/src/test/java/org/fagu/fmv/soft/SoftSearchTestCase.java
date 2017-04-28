package org.fagu.fmv.soft;

import static org.junit.Assert.assertEquals;

/*
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.fagu.fmv.soft.find.SoftProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * @author f.agu
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SoftProvider.class)
public class SoftSearchTestCase {

	/**
	 *
	 */
	public SoftSearchTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testSearch() {
		List<SoftProvider> providers = new ArrayList<>();
		providers.add(new Test2ISoftProvider()); // 2
		providers.add(new Test2ASoftProvider()); // 4
		providers.add(new Test1ISoftProvider()); // 1
		providers.add(new Test1ASoftProvider()); // 3

		PowerMockito.mockStatic(SoftProvider.class);

		BDDMockito.given(SoftProvider.getSoftProviders()).willAnswer(new Answer<Stream<SoftProvider>>() {

			@Override
			public Stream<SoftProvider> answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				return providers.stream();
			}
		});

		assertEquals("Test1ASoftProvider", Soft.search("provider1").getSoftProvider().getClass().getSimpleName());
		assertEquals("Test2ASoftProvider", Soft.search("provider2").getSoftProvider().getClass().getSimpleName());
	}
}
