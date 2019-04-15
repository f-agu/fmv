package org.fagu.fmv.ffmpeg.executor;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.FMVExecListener;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2016 10:45:55
 */
public interface FFExecListener extends FMVExecListener {

	default void eventExecFailed(IOException e, CommandLine command) {}

	default void eventPreExecFallbacks(CommandLine command, Collection<FFExecFallback> fallbacks) {}

	default void eventFallbackNotFound(CommandLine command, List<String> outputs) {}

}
