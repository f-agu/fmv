package org.fagu.fmv.mymedia.utils;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
