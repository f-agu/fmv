package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Parameter;
import org.fagu.fmv.ffmpeg.operation.Parameter.Way;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.media.Rotation;


/**
 * @author f.agu
 */
public class Transpose extends AbstractFilter {

	// -------------------------------

	/**
	 * @author f.agu
	 */
	public enum Passthrough {
		NONE, PORTRAIT, LANDSCAPE;
	}

	// -------------------------------

	/**
	 * @author f.agu
	 */
	public enum Direction {
		CCLOCK_FLIP, CLOCK, CCLOCK, CLOCK_FLIP;
	}

	// -------------------------------

	private boolean writeMetadatas;

	/**
	 *
	 */
	protected Transpose() {
		super("transpose");
	}

	/**
	 * @param direction
	 */
	protected Transpose(Direction direction) {
		this();
		direction(direction);
	}

	/**
	 * @return
	 */
	public static Transpose build() {
		return new Transpose();
	}

	/**
	 * @param direction
	 * @return
	 */
	public static Transpose to(Direction direction) {
		return new Transpose(direction);
	}

	/**
	 * @param writeMetadatas
	 * @return
	 */
	public Transpose writeMetadatas(boolean writeMetadatas) {
		this.writeMetadatas = writeMetadatas;
		return this;
	}

	/**
	 * @return the writeMetadatas
	 */
	public boolean isWriteMetadatas() {
		return writeMetadatas;
	}

	/**
	 * @param direction
	 * @return
	 */
	public Transpose direction(Direction direction) {
		parameter("dir", direction.name().toLowerCase());
		return this;
	}

	/**
	 * @param passthrough
	 * @return
	 */
	public Transpose passthrough(Passthrough passthrough) {
		parameter("passthrough", passthrough.name().toLowerCase());
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#upgradeOutputProcessor(org.fagu.fmv.ffmpeg.operation.OutputProcessor)
	 */
	@Override
	public void upgradeOutputProcessor(OutputProcessor outputProcessor) {
		if(writeMetadatas) {
			addMetadataRotate(outputProcessor, Rotation.R_0);
		}
	}

	/**
	 * @param outputProcessor
	 * @return
	 */
	public static boolean isMetadataRotateDefined(OutputProcessor outputProcessor) {
		List<Parameter> parameters = outputProcessor.getParameters(Way.BEFORE);
		return parameters.stream().anyMatch(p -> p.getName().startsWith("rotate="));
	}

	/**
	 * @param outputProcessor
	 * @param rotation
	 */
	public static void addMetadataRotate(OutputProcessor outputProcessor, Rotation rotation) {
		if( ! isMetadataRotateDefined(outputProcessor)) {
			outputProcessor.metadataStream(Type.VIDEO, "rotate", Integer.toString(rotation.getValue()));
		}
	}

}
