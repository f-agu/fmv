package org.fagu.fmv.ffmpeg.filter;

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

import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class IOKey {

	private final OutputKey outputKey;

	private final Type type;

	private final boolean explicitType;

	/**
	 * @param outputKey
	 * @param type
	 * @param explicitType
	 */
	private IOKey(OutputKey outputKey, Type type, boolean explicitType) {
		if(outputKey == null) {
			throw new IllegalArgumentException("OutputKey is null");
		}
		this.outputKey = outputKey;
		this.type = type;
		this.explicitType = explicitType;
	}

	/**
	 * @param outputKey
	 * @return
	 */
	public static IOKey of(OutputKey outputKey) {
		return new IOKey(outputKey, null, false);
	}

	/**
	 * @param outputKey
	 * @param type
	 * @param explicitType
	 * @return
	 */
	public static IOKey of(OutputKey outputKey, Type type, boolean explicitType) {
		return new IOKey(outputKey, type, explicitType);
	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return outputKey.getLabel();
	}

	/**
	 * @return the outputKey
	 */
	public OutputKey getOutputKey() {
		return outputKey;
	}

	/**
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return
	 */
	public boolean hasTypeDefined() {
		return type != null;
	}

	/**
	 * @return the explicitType
	 */
	public boolean isExplicitType() {
		return explicitType;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof IOKey)) {
			return false;
		}
		IOKey other = (IOKey)obj;
		return getLabel().equals(other) && type == other.type;
	}

	/**
	 * @param filterNaming
	 * @return
	 */
	public String toString(FilterNaming filterNaming) {
		StringBuilder buf = new StringBuilder(12);
		buf.append('[').append(filterNaming.generate(getLabel()));
		if(type != null && explicitType) {
			buf.append(':').append(type.code());
		}
		buf.append(']');
		return buf.toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IOKey(" + type + ")";
	}

}
