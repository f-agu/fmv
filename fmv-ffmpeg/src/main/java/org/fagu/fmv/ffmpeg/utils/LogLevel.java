package org.fagu.fmv.ffmpeg.utils;

/*
 * #%L
 * fmv-ffmpeg
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
public enum LogLevel {

	/**
	 * Show nothing at all; be silent.
	 */
	QUIET,
	/**
	 * Only show fatal errors which could lead the process to crash, such as and assert failure. This is not currently
	 * used for anything.
	 */
	PANIC,
	/**
	 * Only show fatal errors. These are errors after which the process absolutely cannot continue after.
	 */
	FATAL,
	/**
	 * Show all errors, including ones which can be recovered from.
	 */
	ERROR,
	/**
	 * Show all warnings and errors. Any message related to possibly incorrect or unexpected events will be shown.
	 */
	WARNING,
	/**
	 * Show informative messages during processing. This is in addition to warnings and errors. This is the default
	 * value.
	 */
	INFO,
	/**
	 * Same as info, except more verbose.
	 */
	VERBOSE,
	/**
	 * Show everything, including debugging information.
	 */
	DEBUG,
	/**
	 * 
	 */
	TRACE
}
