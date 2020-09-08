package org.fagu.fmv.soft.gs;

/*-
 * #%L
 * fmv-soft-auto
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

import org.fagu.fmv.soft.exec.CommandLineHidePolicy;
import org.fagu.fmv.soft.exec.CommandLineToString;
import org.fagu.fmv.soft.exec.CommandLineToString.CommandLineToStringBuilder;


/**
 * @author f.agu
 * @created 26 août 2020 12:17:02
 */
public class GSPasswordCommandLineHidePolicy implements CommandLineHidePolicy {

	@Override
	public void applyPolicy(CommandLineToStringBuilder builder) {
		builder
				.whenArg().verify(a -> a.startsWith("-sOwnerPassword=")).replaceBy("-sOwnerPassword=" + CommandLineToString.HIDE)
				.whenArg().verify(a -> a.startsWith("-sUserPassword=")).replaceBy("-sUserPassword=" + CommandLineToString.HIDE)
				.whenArg().verify(a -> a.startsWith("-sPDFPassword=")).replaceBy("-sPDFPassword=" + CommandLineToString.HIDE);
	}

}
