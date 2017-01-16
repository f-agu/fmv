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

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.cli.annotation.Command;


/**
 * @author f.agu
 */
@Command("set")
public class Set extends AbstractCommand {

	/**
	 * 
	 */
	public Set() {}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length == 0) { // list
			for(String name : project.getPropertyNames()) {
				println(name + ": " + project.getPropertyValue(name));
			}
			return;
		}
		String line = StringUtils.join(args, ' ');
		int pos = line.indexOf('=');
		if(pos < 1) {
			String propertyValue = project.getPropertyValue(line);
			if(propertyValue != null) {
				println(line + ": " + propertyValue);
			}
			return;
		}
		String varName = line.substring(0, pos).trim();
		String varValue = line.substring(pos + 1).trim();

		project.setPropertyValue(varName, varValue);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "List, set or update a property";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "set [name[=value]]";
	}

}
