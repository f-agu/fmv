package org.fagu.fmv.utils.order;

/*-
 * #%L
 * fmv-utils
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
import java.util.Comparator;
import java.util.List;

import org.fagu.fmv.utils.AnnotationUtils;


/**
 * @author f.agu
 */
public class OrderComparator implements Comparator<Object> {

	public static final OrderComparator INSTANCE = new OrderComparator();

	/**
	 * 
	 */
	public OrderComparator() {}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		int i1 = getOrder(o1);
		int i2 = getOrder(o2);
		return Integer.compare(i1, i2);
	}

	/**
	 * @param list
	 */
	public static void sort(List<?> list) {
		if(list.size() > 1) {
			Collections.sort(list, INSTANCE);
		}
	}

	// ******************************************

	/**
	 * @param obj
	 * @return
	 */
	protected int getOrder(Object obj) {
		if(obj instanceof Ordered) {
			return ((Ordered)obj).getOrder();
		}
		if(obj != null) {
			Class<?> clazz = obj instanceof Class ? (Class<?>)obj : obj.getClass();
			Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
			if(order != null) {
				return order.value();
			}
		}
		return Ordered.LOWEST_PRECEDENCE;
	}

}
