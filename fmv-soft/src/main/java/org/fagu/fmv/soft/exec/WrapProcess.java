package org.fagu.fmv.soft.exec;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author f.agu
 * @created 5 juin 2017 18:03:29
 */
public class WrapProcess extends Process {

	private final Process delegated;

	/**
	 * @param delegated
	 */
	public WrapProcess(Process delegated) {
		this.delegated = Objects.requireNonNull(delegated);
	}

	/**
	 * @see java.lang.Process#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return delegated.getOutputStream();
	}

	/**
	 * @see java.lang.Process#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return delegated.getInputStream();
	}

	/**
	 * @see java.lang.Process#getErrorStream()
	 */
	@Override
	public InputStream getErrorStream() {
		return delegated.getErrorStream();
	}

	/**
	 * @see java.lang.Process#waitFor()
	 */
	@Override
	public int waitFor() throws InterruptedException {
		return delegated.waitFor();
	}

	/**
	 * @see java.lang.Process#exitValue()
	 */
	@Override
	public int exitValue() {
		return delegated.exitValue();
	}

	/**
	 * @see java.lang.Process#destroy()
	 */
	@Override
	public void destroy() {
		delegated.destroy();
	}

	/**
	 * @see java.lang.Process#destroyForcibly()
	 */
	@Override
	public Process destroyForcibly() {
		return delegated.destroyForcibly();
	}

	/**
	 * @see java.lang.Process#waitFor(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
		return delegated.waitFor(timeout, unit);
	}

	/**
	 * @see java.lang.Process#isAlive()
	 */
	@Override
	public boolean isAlive() {
		return delegated.isAlive();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return delegated.equals(obj);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return delegated.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return delegated.toString();
	}

}
