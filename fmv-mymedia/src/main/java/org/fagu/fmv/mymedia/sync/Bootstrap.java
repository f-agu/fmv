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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.fagu.fmv.mymedia.sync.impl.Synchronizers;


/**
 * @author f.agu
 */
public class Bootstrap {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if(args.length != 3) {
			System.out.println("Usage: " + Bootstrap.class.getName() + " <source.properties> <destination.properties> <file.log>");
			return;
		}

		try (Storage sourceStorage = StorageFactory.create(new File(args[0])); //
				Storage destStorage = StorageFactory.create(new File(args[1]))) {
			PrintStream printStream = new PrintStream(new FileOutputStream(new File(args[2]), true));

			Sync sync = new Sync(sourceStorage, destStorage, Synchronizers.getDefault(printStream));
			sync.synchronize();
		}
	}

}
