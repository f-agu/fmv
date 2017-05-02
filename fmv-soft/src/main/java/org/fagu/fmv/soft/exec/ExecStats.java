package org.fagu.fmv.soft.exec;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.io.FilenameUtils;


/**
 * @author f.agu
 */
public class ExecStats {

	private static final ExecStats INSTANCE = new ExecStats();

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class Stats {

		private final AtomicInteger countExecOK = new AtomicInteger();

		private final AtomicInteger countExecFailed = new AtomicInteger();

		private final AtomicLong durationOKSum = new AtomicLong();

		private final AtomicLong durationFailedSum = new AtomicLong();

		public int countExec() {
			return countExecOK() + countExecFailed();
		}

		public int countExecFailed() {
			return countExecFailed.get();
		}

		public int countExecOK() {
			return countExecOK.get();
		}

		public long getDurationSum() {
			return getDurationOKSum() + getDurationFailedSum();
		}

		public long getDurationOKSum() {
			return durationOKSum.get();
		}

		public long getDurationOKAverage() {
			return durationOKSum.get() / countExecOK();
		}

		public long getDurationFailedSum() {
			return durationFailedSum.get();
		}

		public long getDurationFailedAverage() {
			return durationFailedSum.get() / countExecFailed();
		}
	}

	// -----------------------------------------------

	private final Map<String, Stats> statsMap = new HashMap<>();

	/**
	 * 
	 */
	public ExecStats() {}

	/**
	 * @return
	 */
	public static ExecStats getInstance() {
		return INSTANCE;
	}

	/**
	 * @return
	 */
	public FMVExecListener getExecListener() {
		return new StatsListener();
	}

	/**
	 * @return
	 */
	public Map<String, Stats> getStats() {
		return Collections.unmodifiableMap(statsMap);
	}

	// -----------------------------------------

	/**
	 * @author f.agu
	 */
	private class StatsListener implements FMVExecListener {

		private String softName;

		private long startTime;

		private long duration;

		@Override
		public void eventPreExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
				ExecuteResultHandler handler) {
			softName = FilenameUtils.getBaseName(command.getExecutable());
			startTime = System.currentTimeMillis();
		}

		@Override
		public void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
				ExecuteResultHandler handler) {
			eventFailed(fmvExecutor, command, environment, handler, null);
		}

		@Override
		public void eventFailed(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
				ExecuteResultHandler handler, IOException ioe) {
			duration = System.currentTimeMillis() - startTime;

			Stats stats = statsMap.computeIfAbsent(softName.toLowerCase(), k -> new Stats());
			if(ioe == null) {
				stats.countExecOK.incrementAndGet();
				stats.durationOKSum.addAndGet(duration);
			} else {
				stats.countExecFailed.incrementAndGet();
				stats.durationFailedSum.addAndGet(duration);
			}
		}

	}
}
