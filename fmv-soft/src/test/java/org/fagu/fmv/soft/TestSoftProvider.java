package org.fagu.fmv.soft;

/*-
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


import java.util.Collections;

import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;


/**
 * @author f.agu
 */
public class TestSoftProvider extends SoftProvider {

	/**
	 * @param name
	 */
	public TestSoftProvider(String name) {
		super(name);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		return null;
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftPolicy()
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return null;
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#search()
	 */
	@Override
	public Soft search() {
		SoftName softName = new SoftName() {

			@Override
			public String getName() {
				return TestSoftProvider.this.toString();
			}

			@Override
			public Soft create(Founds founds) {
				return null;
			}
		};
		return new Soft(new Founds(softName, Collections.emptyNavigableSet()), this);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
