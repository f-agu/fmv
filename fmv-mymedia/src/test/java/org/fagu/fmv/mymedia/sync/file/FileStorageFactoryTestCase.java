package org.fagu.fmv.mymedia.sync.file;

/*
 * #%L
 * fmv-mymedia
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

import java.util.Properties;

import org.junit.Test;


/**
 * @author f.agu
 */
public class FileStorageFactoryTestCase {

	/**
	 * 
	 */
	public FileStorageFactoryTestCase() {}

	/**
	 * 
	 */
	@Test
	public void test1() {
		Properties properties = new Properties();
		properties.put("path", "${(?i)device_name_[0-9]+}folder");
		FileStorageFactory fileStorageFactory = new FileStorageFactory();
		fileStorageFactory.create(properties);
	}

}
