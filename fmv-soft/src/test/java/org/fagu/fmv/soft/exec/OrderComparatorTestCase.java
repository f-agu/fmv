package org.fagu.fmv.soft.exec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fagu.fmv.utils.order.OrderComparator;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 31 juil. 2020 14:04:12
 */
@Ignore
public class OrderComparatorTestCase {

	@Test
	public void testDouble() {
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
