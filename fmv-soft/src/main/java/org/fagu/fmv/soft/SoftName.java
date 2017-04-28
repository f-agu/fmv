package org.fagu.fmv.soft;

import java.util.function.Consumer;

import org.fagu.fmv.soft.find.Founds;


/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 fagu
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

/**
 * @author f.agu
 */
public interface SoftName {

	/**
	 * @return
	 */
	String getName();

	/**
	 * @param founds
	 * @return
	 */
	Soft create(Founds founds);

	/**
	 * @return
	 */
	default String getDownloadURL() {
		return null;
	}

	/**
	 * @param infoConsumer
	 */
	default void moreInfo(Consumer<String> infoConsumer) {
		String downloadURL = getDownloadURL();
		if(downloadURL != null && ! "".equals(downloadURL.trim())) {
			infoConsumer.accept("    Download " + getName() + " at " + downloadURL + " and add the path in your system environment PATH");
		}
	}
}
