package org.fagu.fmv.soft.exec.exception;

import java.io.IOException;


/**
 * @author f.agu
 * @created 14 nov. 2020 10:29:16
 */
public class NoEnoughSpaceOnDeviceException extends IOException {

	private static final long serialVersionUID = - 646688719382093751L;

	public NoEnoughSpaceOnDeviceException() {}

	public NoEnoughSpaceOnDeviceException(String message) {
		super(message);
	}

	public NoEnoughSpaceOnDeviceException(Throwable cause) {
		super(cause);
	}

	public NoEnoughSpaceOnDeviceException(String message, Throwable cause) {
		super(message, cause);
	}

}
