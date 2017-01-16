package org.fagu.fmv.core.project;

/*
 * #%L
 * fmv-core
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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class ProjectTestCase {

	/**
	 * 
	 */
	private Project project;

	/**
	 * 
	 */
	public ProjectTestCase() {}

	/**
	 * 
	 */
	@Before
	public void setUp() {
		// project = new Project();
	}

	/**
	 * 
	 */
	@Test
	@Ignore
	public void test1() {
		project.addSource(new File("D:\\Video_fagu&Vv\\2014\\2014-06-21 - Chorale de Manon"));

		for(FileSource fileSource : project.getSources()) {
			System.out.println(fileSource.getNumber() + ": " + fileSource.getFile().getPath());
		}
	}

}
