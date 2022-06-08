package org.fagu.fmv.mymedia.classify;

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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public abstract class Classifier<F extends FileFinder<M>, M extends Media> implements Closeable {

	protected final F finder;

	protected final File destFolder;

	public Classifier(F finder, File destFolder) {
		this.finder = finder;
		this.destFolder = Objects.requireNonNull(destFolder);
	}

	// *******************************

	abstract public void add(FileFinder<M>.InfosFile infosFile);

	abstract public List<File> classify(Converter<M> converter, ConverterListener<M> listener) throws IOException;

	// *******************************

	protected void addMyFinder() {
		addFinder(finder);
	}

	protected void addFinder(F finder) {
		for(FileFinder<M>.InfosFile infosFile : finder.getAll()) {
			add(infosFile);
		}
	}

}
