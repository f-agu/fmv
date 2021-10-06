package org.fagu.fmv.soft.exec;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fagu.fmv.utils.order.OrderComparator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 31 juil. 2020 14:04:12
 */
@Disabled
class OrderComparatorTestCase {

	@Test
	void testDouble() {
		List<ProcessOperator> list = new ArrayList<>();
		list.add(new IgnoreNullOutputStreamProcessOperator());
		list.add(new PIDProcessOperator());
		list.add(new IgnoreNullOutputStreamProcessOperator());
		list.add(new PIDProcessOperator());
		OrderComparator.sort(list);
		list.forEach(p -> System.out.println(p.hashCode() + " : " + p.getClass()));

		Set<Class<? extends ProcessOperator>> set = new HashSet<>();
		list.stream()
				.filter(po -> set.add(po.getClass()))
				.forEach(p -> System.out.println(p.hashCode() + " : " + p.getClass()));
	}

}
