package org.fagu.fmv.mymedia.sync;

/*
 * #%L
 * fmv-mymedia
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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


/**
 * @author f.agu
 */
public interface Item {

	/**
	 * @return
	 */
	boolean isFile();

	/**
	 * @return
	 */
	boolean isDirectory();

	/**
	 * @return
	 */
	String getName();

	/**
	 * @return
	 */
	long size();

	/**
	 * @param name
	 * @return
	 * @throws IOException
	 */
	Item mkdir(String name) throws IOException;

	/**
	 * @param name
	 * @return
	 * @throws IOException
	 */
	Item createFile(String name) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	Map<String, Item> listChildren() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	InputStream openInputStream() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	OutputStream openOutputStream() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 * 
	 */
	boolean delete() throws IOException;
}
