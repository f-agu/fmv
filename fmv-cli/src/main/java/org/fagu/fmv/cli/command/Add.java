package org.fagu.fmv.cli.command;

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

import java.io.File;

import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.cli.annotation.Completion;
import org.fagu.fmv.cli.completion.FileCompleterFactory;


/**
 * @author f.agu
 */
@Command("add")
@Completion(FileCompleterFactory.CLS_NAME)
public class Add extends AbstractCommand {

	@Override
	public void run(String[] args) {
		for(String arg : args) {
			project.addSource(new File(arg));
		}
	}

	@Override
	public String getShortDescription() {
		return "Add some media sources: image, movies";
	}

	@Override
	public String getSyntax() {
		return "add [file/folder] [file/folder] ...";
	}

}
