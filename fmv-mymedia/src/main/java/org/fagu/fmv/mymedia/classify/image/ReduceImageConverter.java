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
import java.util.Objects;

import org.fagu.fmv.im.Gravity;
import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class ReduceImageConverter extends AbstractImageConverter {

	public enum Reduce {
		BOX, WIDTH, HEIGHT
	}

	private Reduce reduce = Reduce.BOX;

	private Size size;

	private double quality = 65D;

	public ReduceImageConverter(File destFolder) {
		super(destFolder);
	}

	public ReduceImageConverter(File destFolder, int nThreads) {
		super(destFolder, nThreads);
	}

	@Override
	public String getTitle() {
		return "Organiser & reduire la taille des images";
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Reduce getReduce() {
		return reduce;
	}

	public void setReduce(Reduce reduce) {
		this.reduce = Objects.requireNonNull(reduce);
	}

	public double getQuality() {
		return quality;
	}

	public void setQuality(double quality) {
		if(quality < 0 || quality > 100D) {
			throw new IllegalArgumentException("Quality must be between 0 and 100: " + quality);
		}
		this.quality = quality;
	}

	// **********************************************************

	@Override
	protected void populateOperation(IMOperation op) {
		op.autoOrient();
		if(size != null) {
			switch(reduce) {
				case BOX:
					op.scale(size.getWidth(), size.getHeight());
					break;
				case WIDTH:
					op.scaleByWidth(size.getWidth());
					break;
				case HEIGHT:
					op.scaleByHeight(size.getHeight());
					break;
				default:
			}
			op.background("black")
					.gravity(Gravity.CENTER)
					.extent(size.getWidth(), size.getHeight());
		}
		op.quality(quality);
	}
}
