package org.fagu.fmv.soft;

import org.fagu.fmv.soft.find.Founds;


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

/**
 * @author f.agu
 */
public class TestSoftName implements SoftName {

	private final String name;

	/**
	 * @param name
	 */
	public TestSoftName(String name) {
		this.name = name;
	}

	/**
	 * @see org.fagu.fmv.soft.SoftName#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see org.fagu.fmv.soft.SoftName#create(org.fagu.fmv.soft.find.Founds)
	 */
	@Override
	public Soft create(Founds founds) {
		return new TestSoft(founds, new TestSoftProvider("toto"));
	}

}
