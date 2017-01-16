package org.fagu.fmv.cli.completion;

/*
 * #%L
 * fmv-cli
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

import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;


/**
 * @author f.agu
 */
public class FileCompleterFactory implements CompleterFactory {

	public static final String CLS_NAME = "org.fagu.fmv.cli.completion.FileCompleterFactory";

	/**
	 *
	 */
	public FileCompleterFactory() {}

	/**
	 * @see org.fagu.fmv.cli.completion.CompleterFactory#create(java.lang.String)
	 */
	@Override
	public Completer create(String commandName) {
		StringsCompleter stringsCompleter = new StringsCompleter(commandName);
		return new ArgumentCompleter(stringsCompleter, new FileNameCompleter());
	}

}
