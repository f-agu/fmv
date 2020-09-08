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

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;


/**
 * @author f.agu
 */
public interface ExecListener {

	default void eventPrepare(CommandLine commandLine) {}

	default void eventExecuting(CommandLine commandLine) {}

	default void eventExecuted(CommandLine commandLine, int exitValue, long timeMilleseconds) {}

	default void eventException(FMVExecuteException fmvExecuteException) {}
}
