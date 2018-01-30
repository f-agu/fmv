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

import java.util.Optional;

import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Aliases;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.exec.BaseIdentifiable;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.Identifiable;


/**
 * @author f.agu
 */
@Command("toexec")
@Aliases({@Alias("te"), @Alias("teo"), @Alias("toexecutable")})
public class ToExecutable extends AbstractCommand {

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length != 1) {
			help();
			return;
		}
		String id = args[0];
		if(".".equals(id)) {
			environnement.setCurrentExecutable(null);
			return;
		}
		Optional<Identifiable> opt = BaseIdentifiable.findById(project, id);
		if( ! opt.isPresent()) {
			println("ID not found: " + id);
			return;
		}
		Identifiable identifiable = opt.get();
		if( ! (identifiable instanceof Executable)) {
			println("ID is not an executable: " + id);
			return;
		}
		Executable executable = (Executable)identifiable;
		environnement.setCurrentExecutable(executable);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Go into an executable";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "te [id of executable]";
	}

}
