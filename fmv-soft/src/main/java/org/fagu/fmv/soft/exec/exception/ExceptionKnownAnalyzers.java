package org.fagu.fmv.soft.exec.exception;

import java.io.IOException;
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
		return getExceptionKnownAnalyzers(cls).stream() //
				.map(ek -> ek.anaylze(nestedException)) //
				.filter(Objects::nonNull) //
				.findFirst();
	}

	/**
	 * @param cls
	 * @param e
	 * @param exceptionKnowConsumer
	 * @throws E
	 */
	@SuppressWarnings("unchecked")
	public static <E extends IOException> void doOrThrows(Class<? extends ExceptionKnownAnalyzer> cls, E exception,
			ExceptionKnowConsumer exceptionKnowConsumer) throws E {

		boolean isKnown = false;
		if(exceptionKnowConsumer != null) {
			Optional<ExceptionKnown> known = ExceptionKnownAnalyzers.getKnown(cls, exception);
			if(known.isPresent()) {
				isKnown = true;
				try {
					exceptionKnowConsumer.accept(known.get());
				} catch(IOException e1) {
					throw (E)e1;
				}
			}
		}
		if( ! isKnown) {
			throw exception;
		}
	}

}
