package org.fagu.fmv.soft;

import java.io.File;

import org.fagu.fmv.soft.find.SoftInfo;

/*
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import static org.mockito.Mockito.mock;


/**
 * @author f.agu
 */
public class TestSoftInfo extends SoftInfo {

	private final int build;

	public TestSoftInfo(int build) {
		super(mock(File.class), "prout");
		this.build = build;
	}

	@Override
	public int compareTo(SoftInfo o) {
		return Integer.compare(build, ((TestSoftInfo)o).build);
	}

	@Override
	public String getInfo() {
		return Integer.toString(build);
	}

	@Override
	public String toString() {
		return getInfo();
	}

}
