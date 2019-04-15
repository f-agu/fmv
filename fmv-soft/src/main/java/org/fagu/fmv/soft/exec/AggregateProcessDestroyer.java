package org.fagu.fmv.soft.exec;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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


/**
 * @author f.agu
 */
public class AggregateProcessDestroyer implements ProcessDestroyer {

	private final List<ProcessDestroyer> processDestroyers;

	public AggregateProcessDestroyer() {
		processDestroyers = new ArrayList<>();
	}

	@Override
	public boolean add(Process process) {
		boolean b = true;
		for(ProcessDestroyer processDestroyer : processDestroyers) {
			b &= processDestroyer.add(process);
		}
		return b;
	}

	public void add(ProcessDestroyer processDestroyer) {
		processDestroyers.add(processDestroyer);
	}

	@Override
	public boolean remove(Process process) {
		boolean b = true;
		for(ProcessDestroyer processDestroyer : processDestroyers) {
			b &= processDestroyer.remove(process);
		}
		return b;
	}

	@Override
	public int size() {
		throw new RuntimeException("Not implemented !");
	}

}
