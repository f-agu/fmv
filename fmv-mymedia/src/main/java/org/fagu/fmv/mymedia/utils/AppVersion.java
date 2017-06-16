package org.fagu.fmv.mymedia.utils;

import java.util.function.Consumer;


/**
 * @author fagu
 */
public class AppVersion {

	/**
	 * 
	 */
	private AppVersion() {}

	/**
	 * @param consumer
	 */
	public static void logMyVersion(Consumer<String> consumer) {
		String ver = getMyVersion();
		consumer.accept("******************* FMV v" + (ver != null ? ver : "?") + " *******************");
	}

	/**
	 * @return
	 */
	public static String getMyVersion() {
		return getVersionOf(AppVersion.class);
	}

	/**
	 * @param cls
	 * @return
	 */
	public static String getVersionOf(Class<?> cls) {
		String version = null;
		Package aPackage = cls.getPackage();
		if(aPackage != null) {
			version = aPackage.getImplementationVersion();
			if(version == null) {
				version = aPackage.getSpecificationVersion();
			}
		}
		return version;
	}

}
