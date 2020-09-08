package org.fagu.fmv.soft.find;

/*-
 * #%L
 * fmv-soft
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author f.agu
 * @created 3 août 2020 14:45:18
 */
public class MultiSoftFoundFactory implements SoftFoundFactory {

	private final List<SoftFoundFactory> factories;

	public MultiSoftFoundFactory(List<SoftFoundFactory> factories) {
		this.factories = Collections.unmodifiableList(new ArrayList<>(factories));
	}

	@Override
	public SoftFound create(File file, Locator locator, SoftPolicy softPolicy) throws IOException {
		SoftFound softFound;
		for(SoftFoundFactory factory : factories) {
			softFound = factory.create(file, locator, softPolicy);
			if(softFound.isFound()) {
				return softFound;
			}
		}
		return SoftFound.notFound();
	}

}
