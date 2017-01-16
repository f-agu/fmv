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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.exec.Identifiable;


/**
 * @author f.agu
 */
@Command("execlist")
@Alias("el")
public class ExecList extends AbstractCommand {

	private static final int ID_WIDTH = 10;

	/**
	 *
	 */
	public ExecList() {}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		displayIdentifiable(project.getExecutables(), 0);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Display the executables";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "execlist";
	}

	// *******************************************************

	/**
	 * @param identifiables
	 * @param paddingSize
	 */
	private void displayIdentifiable(List<? extends Identifiable> identifiables, int paddingSize) {
		for(Identifiable identifiable : identifiables) {
			StringBuilder buf = new StringBuilder(100);
			buf.append(StringUtils.rightPad(identifiable.getId(), ID_WIDTH)).append(StringUtils.leftPad("", paddingSize));
			buf.append(identifiable.toString());
			println(buf.toString());
			displayIdentifiable(identifiable.getIdentifiableChildren(), paddingSize + 3);
		}
	}

}
