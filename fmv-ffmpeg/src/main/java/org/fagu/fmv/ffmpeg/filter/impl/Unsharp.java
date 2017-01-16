package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
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

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class Unsharp extends AbstractFilter {

	/**
	 * 
	 */
	protected Unsharp() {
		super("unsharp");
	}

	/**
	 * @return
	 */
	public static Unsharp build() {
		return new Unsharp();
	}

	/**
	 * Set luma matrix horizontal size (from 3 to 63) (default 5)
	 * 
	 * @param lumaMsizeX
	 * @return
	 */
	public Unsharp lumaMsizeX(int lumaMsizeX) {
		if(3 > lumaMsizeX || lumaMsizeX > 63) {
			throw new IllegalArgumentException("lumaMsizeX must be between 3 and 63: " + lumaMsizeX);
		}
		parameter("luma_msize_x", Integer.toString(lumaMsizeX));
		return this;
	}

	/**
	 * Set luma matrix horizontal size (from 3 to 63) (default 5)
	 * 
	 * @param lx
	 * @return
	 */
	public Unsharp lx(int lx) {
		if(3 > lx || lx > 63) {
			throw new IllegalArgumentException("lx must be between 3 and 63: " + lx);
		}
		parameter("lx", Integer.toString(lx));
		return this;
	}

	/**
	 * Set luma matrix vertical size (from 3 to 63) (default 5)
	 * 
	 * @param lumaMsizeY
	 * @return
	 */
	public Unsharp lumaMsizeY(int lumaMsizeY) {
		if(3 > lumaMsizeY || lumaMsizeY > 63) {
			throw new IllegalArgumentException("lumaMsizeY must be between 3 and 63: " + lumaMsizeY);
		}
		parameter("luma_msize_y", Integer.toString(lumaMsizeY));
		return this;
	}

	/**
	 * Set luma matrix vertical size (from 3 to 63) (default 5)
	 * 
	 * @param ly
	 * @return
	 */
	public Unsharp ly(int ly) {
		if(3 > ly || ly > 63) {
			throw new IllegalArgumentException("ly must be between 3 and 63: " + ly);
		}
		parameter("ly", Integer.toString(ly));
		return this;
	}

	/**
	 * Set luma effect strength (from -2 to 5) (default 1)
	 * 
	 * @param lumaAmount
	 * @return
	 */
	public Unsharp lumaAmount(float lumaAmount) {
		if( - 2.0 > lumaAmount || lumaAmount > 5.0) {
			throw new IllegalArgumentException("lumaAmount must be between -2.0 and 5.0: " + lumaAmount);
		}
		parameter("luma_amount", Float.toString(lumaAmount));
		return this;
	}

	/**
	 * Set luma effect strength (from -2 to 5) (default 1)
	 * 
	 * @param la
	 * @return
	 */
	public Unsharp la(float la) {
		if( - 2.0 > la || la > 5.0) {
			throw new IllegalArgumentException("la must be between -2.0 and 5.0: " + la);
		}
		parameter("la", Float.toString(la));
		return this;
	}

	/**
	 * Set chroma matrix horizontal size (from 3 to 63) (default 5)
	 * 
	 * @param chromaMsizeX
	 * @return
	 */
	public Unsharp chromaMsizeX(int chromaMsizeX) {
		if(3 > chromaMsizeX || chromaMsizeX > 63) {
			throw new IllegalArgumentException("chromaMsizeX must be between 3 and 63: " + chromaMsizeX);
		}
		parameter("chroma_msize_x", Integer.toString(chromaMsizeX));
		return this;
	}

	/**
	 * Set chroma matrix horizontal size (from 3 to 63) (default 5)
	 * 
	 * @param cx
	 * @return
	 */
	public Unsharp cx(int cx) {
		if(3 > cx || cx > 63) {
			throw new IllegalArgumentException("cx must be between 3 and 63: " + cx);
		}
		parameter("cx", Integer.toString(cx));
		return this;
	}

	/**
	 * Set chroma matrix vertical size (from 3 to 63) (default 5)
	 * 
	 * @param chromaMsizeY
	 * @return
	 */
	public Unsharp chromaMsizeY(int chromaMsizeY) {
		if(3 > chromaMsizeY || chromaMsizeY > 63) {
			throw new IllegalArgumentException("chromaMsizeY must be between 3 and 63: " + chromaMsizeY);
		}
		parameter("chroma_msize_y", Integer.toString(chromaMsizeY));
		return this;
	}

	/**
	 * Set chroma matrix vertical size (from 3 to 63) (default 5)
	 * 
	 * @param cy
	 * @return
	 */
	public Unsharp cy(int cy) {
		if(3 > cy || cy > 63) {
			throw new IllegalArgumentException("cy must be between 3 and 63: " + cy);
		}
		parameter("cy", Integer.toString(cy));
		return this;
	}

	/**
	 * Set chroma effect strength (from -2 to 5) (default 0)
	 * 
	 * @param chromaAmount
	 * @return
	 */
	public Unsharp chromaAmount(float chromaAmount) {
		if( - 2.0 > chromaAmount || chromaAmount > 5.0) {
			throw new IllegalArgumentException("chromaAmount must be between -2.0 and 5.0: " + chromaAmount);
		}
		parameter("chroma_amount", Float.toString(chromaAmount));
		return this;
	}

	/**
	 * Set chroma effect strength (from -2 to 5) (default 0)
	 * 
	 * @param ca
	 * @return
	 */
	public Unsharp ca(float ca) {
		if( - 2.0 > ca || ca > 5.0) {
			throw new IllegalArgumentException("ca must be between -2.0 and 5.0: " + ca);
		}
		parameter("ca", Float.toString(ca));
		return this;
	}

	/**
	 * Use OpenCL filtering capabilities (from 0 to 1) (default 0)
	 * 
	 * @param opencl
	 * @return
	 */
	public Unsharp opencl(boolean opencl) {
		parameter("opencl", Integer.toString(opencl ? 1 : 0));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
