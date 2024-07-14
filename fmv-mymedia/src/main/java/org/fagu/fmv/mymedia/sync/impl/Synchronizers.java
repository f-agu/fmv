package org.fagu.fmv.mymedia.sync.impl;

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

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.sync.Synchronizer;


/**
 * @author f.agu
 */
public class Synchronizers {

	private Synchronizers() {}

	// --------------------------------------------------

	public static Synchronizer getDefault(Logger logger) {
		return new ConfirmDeleteSynchronizer(new LogSynchronizer(new StandardSynchronizer(), logger));
	}

}
