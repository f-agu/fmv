package org.fagu.fmv.soft;

/*-
 * #%L
 * fmv-soft
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

import org.fagu.fmv.soft.find.SoftFound;


/**
 * @author f.agu
 * @created 4 mars 2020 11:05:08
 */
public interface SoftLogListener {

	default void eventFound(Soft soft, SoftFound softFound, Consumer<String> logger) {}

	default void eventNotFoundEmpty(Soft soft, SoftFound softFound, Consumer<String> logger) {}

	default void eventNotFoundNotEmpty(Soft soft, SoftFound softFound, Consumer<String> logger) {}

}
