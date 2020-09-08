package org.fagu.fmv.cli.command;

/*-
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;


/**
 * @author f.agu
 */
@Command("viewprevious")
@Alias("v-")
public class ViewPrevious extends ViewShift {

	/**
	 * 
	 */
	public ViewPrevious() {
		super(lastView -> lastView - 1);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "View previous media";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "viewprevious";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length != 0) {
			println(getSyntax());
			return;
		}
		view();
	}

}
