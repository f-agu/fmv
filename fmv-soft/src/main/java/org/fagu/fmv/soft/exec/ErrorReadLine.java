package org.fagu.fmv.soft.exec;

/*-
 * #%L
 * fmv-soft
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.lang3.mutable.MutableObject;


/**
 * @author f.agu
 */
public class ErrorReadLine implements ReadLine {

	private final int maxLineBuffer;

	private final int killProcessWhenNumberOfLines;

	private int countErrLines;

	private ProcessDestroyer processDestroyer;

	private MutableObject<Process> processMutableObject;

	private List<String> errList;

	public ErrorReadLine(int maxLineBuffer, int killProcessWhenNumberOfLines) {
		this.maxLineBuffer = maxLineBuffer;
		this.killProcessWhenNumberOfLines = killProcessWhenNumberOfLines;
		errList = new ArrayList<>(maxLineBuffer);

		if(killProcessWhenNumberOfLines > 1) {
			processMutableObject = new MutableObject<>();
			processDestroyer = createProcessDestroyer(processMutableObject);
		}
	}

	@Override
	public void read(String line) {
		++countErrLines;
		if(countErrLines < maxLineBuffer) {
			errList.add(line);
		} else if(countErrLines == maxLineBuffer) {
			errList.add("...");
		}
		if(countErrLines == killProcessWhenNumberOfLines && processMutableObject != null) {
			Process process = processMutableObject.getValue();
			if(process != null) {
				process.destroyForcibly();
			}
		}
	}

	public List<String> getErrList() {
		return errList;
	}

	public ErrorReadLine applyProcessDestroyer(FMVExecutor executor) {
		if(processDestroyer != null) {
			executor.addProcessDestroyer(processDestroyer);
		}
		return this;
	}

	// *******************************************************************

	private static ProcessDestroyer createProcessDestroyer(MutableObject<Process> mutableObject) {
		return new ProcessDestroyer() {

			@Override
			public int size() {
				return 0;
			}

			@Override
			public boolean remove(Process process) {
				return false;
			}

			@Override
			public boolean add(Process process) {
				mutableObject.setValue(process);
				return false;
			}
		};
	}

}
