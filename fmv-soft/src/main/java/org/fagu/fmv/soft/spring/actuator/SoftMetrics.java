package org.fagu.fmv.soft.spring.actuator;

import java.util.ArrayList;
import java.util.Collection;

import org.fagu.fmv.soft.exec.ExecStats;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;


/**
 * @author f.agu
 */
public class SoftMetrics implements PublicMetrics {

	/**
	 * @see org.springframework.boot.actuate.endpoint.PublicMetrics#metrics()
	 */
	@Override
	public Collection<Metric<?>> metrics() {
		Collection<Metric<?>> metrics = new ArrayList<>();
		ExecStats.getInstance().getStats().forEach((softName, stats) -> {
			metrics.add(metrics(softName, "count", stats.countExec()));
			metrics.add(metrics(softName, "countok", stats.countExecOK()));
			metrics.add(metrics(softName, "countfailed", stats.countExecFailed()));
			metrics.add(metrics(softName, "time", stats.getDurationSum()));
		});
		return metrics;
	}

	// *************************************

	/**
	 * @param softName
	 * @param key
	 * @param number
	 * @return
	 */
	private <T extends Number> Metric<T> metrics(String softName, String key, T number) {
		return new Metric<>("soft." + softName + ".exec." + key, number);
	}

}
