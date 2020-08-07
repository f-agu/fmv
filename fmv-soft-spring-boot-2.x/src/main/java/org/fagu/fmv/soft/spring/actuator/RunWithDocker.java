package org.fagu.fmv.soft.spring.actuator;

import java.io.File;


/**
 * @author f.agu
 * @created 7 août 2020 11:37:51
 */
public class RunWithDocker {

	private static final String DOCKER_ENV_FILE = "/.dockerenv";

	private RunWithDocker() {}

	public static boolean isInDocker() {
		return new File(DOCKER_ENV_FILE).exists();
	}

}
