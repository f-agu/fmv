package org.fagu.fmv.soft.find.strategy;

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


import java.util.Collection;
import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.fagu.fmv.soft.find.FoundStrategy;
import org.fagu.fmv.soft.find.SoftFound;


/**
 * @author f.agu
 */
public class HighestPrecedenceFoundStrategy implements FoundStrategy {

	/**
	 * 
	 */
	public HighestPrecedenceFoundStrategy() {}

	/**
	 * @see org.fagu.fmv.soft.find.FoundStrategy#sort(java.util.Collection)
	 */
	@Override
	public NavigableSet<SoftFound> sort(Collection<SoftFound> founds) {
		if(founds == null || founds.isEmpty()) {
			return Collections.unmodifiableNavigableSet(new TreeSet<>(Collections.singleton(SoftFound.notFound())));
		}
		return Collections.unmodifiableNavigableSet(new TreeSet<>(founds));
	}

}
