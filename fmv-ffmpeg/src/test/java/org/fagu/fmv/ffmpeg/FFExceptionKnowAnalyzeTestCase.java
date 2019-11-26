package org.fagu.fmv.ffmpeg;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 janv. 2017 14:39:11
 */
public class FFExceptionKnowAnalyzeTestCase {

	@Test
	public void test() throws IOException {
		extractMetadatas(null, "Permission denied");
		extractMetadatas("cheese.zip", "Invalid data");
		extractMetadatas("pdf.pdf", "Invalid data");
	}

	// ****************************************************************

	private void extractMetadatas(String resource, String expectedMessage) throws IOException {
		File folder = new File(System.getProperty("java.io.tmpdir"), "ff-extractmetadatas-test");
		try {
			FileUtils.deleteDirectory(folder);
			folder.mkdirs();
			File file = resource != null ? ResourceUtils.extract(resource, folder) : folder;
			try {
				MovieMetadatas extract = MovieMetadatas.with(file)
						.customizeExecutor(e -> e.debug())
						.customizeExecutor(e -> System.out.println(CommandLineUtils.toLine(e.getCommandLine())))
						.extract();
				fail(expectedMessage + ": " + extract.toJSON());
			} catch(FMVExecuteException e) {
				if(e.isKnown()) {
					assertEquals(expectedMessage, e.getExceptionKnown().toString());
				} else {
					throw e;
				}
			}
		} finally {
			FileUtils.deleteDirectory(folder);
		}
	}

}
