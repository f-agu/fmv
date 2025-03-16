package org.fagu.fmv.mymedia.file;

/*
 * #%L
 * fmv-mymedia
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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author f.agu
 */
public interface InfoFile {

	public record Line(char code, String value) {}

	List<Character> getCodes();

	boolean isMine(Object object);

	List<Line> toLines(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException;

	Object parse(File file, String line) throws IOException;
}
