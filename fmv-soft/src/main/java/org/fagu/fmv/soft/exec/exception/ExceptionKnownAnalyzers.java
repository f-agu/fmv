package org.fagu.fmv.soft.exec.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

import org.fagu.fmv.utils.order.OrderComparator;


/**
 * @author fagu
 */
public class ExceptionKnownAnalyzers {

	/**
	 * 
	 */
	private ExceptionKnownAnalyzers() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @param cls
	 * @return
	 */
	public static <A extends ExceptionKnownAnalyzer> List<A> getExceptionKnownAnalyzers(Class<A> cls) {
		List<A> list = new ArrayList<>();
		for(A exceptionKnown : ServiceLoader.load(cls)) {
			list.add(exceptionKnown);
		}
		OrderComparator.sort(list);
		return list;
	}

	/**
	 * @param cls
	 * @param e
	 * @return
	 */
	public static Optional<ExceptionKnown> getKnown(Class<? extends ExceptionKnownAnalyzer> cls, Exception e) {
		NestedException nestedException = new NestedException(e);
		return getExceptionKnownAnalyzers(cls).stream().map(ek -> ek.anaylze(nestedException)).filter(Objects::nonNull).findFirst();
	}

}
