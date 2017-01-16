package org.fagu.fmv.mymedia.classify.image;

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

import org.fagu.fmv.utils.media.Size;
import org.im4java.core.IMOperation;


/**
 * @author f.agu
 */
public class ReduceImageConverter extends AbstractImageConverter {

	private Size size;

	/**
	 * @param destFolder
	 */
	public ReduceImageConverter(File destFolder) {
		super(destFolder);
	}

	/**
	 * @param destFolder
	 * @param nThreads
	 */
	public ReduceImageConverter(File destFolder, int nThreads) {
		super(destFolder, nThreads);
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.Converter#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Organiser & reduire la taille des images";
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Size size) {
		this.size = size;
	}

	// **********************************************************

	/**
	 * @see org.fagu.fmv.mymedia.classify.image.AbstractImageConverter#populateOperation(org.im4java.core.IMOperation)
	 */
	@Override
	protected void populateOperation(IMOperation op) {
		op.autoOrient();
		if(size != null) {
			op.scale(size.getWidth(), size.getHeight());
			op.background("black");
			op.gravity("center");
			op.extent(size.getWidth(), size.getHeight());
		}
		op.quality(65D);
	}
}
