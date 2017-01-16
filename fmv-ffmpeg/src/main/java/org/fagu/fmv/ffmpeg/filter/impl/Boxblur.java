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
public class Boxblur extends AbstractFilter {

	/**
	 * 
	 */
	protected Boxblur() {
		super("boxblur");
	}

	/**
	 * @return
	 */
	public static Boxblur build() {
		return new Boxblur();
	}

	/**
	 * Radius of the luma blurring box (default "2")
	 * 
	 * @param lumaRadius
	 * @return
	 */
	public Boxblur lumaRadius(String lumaRadius) {
		parameter("luma_radius", lumaRadius);
		return this;
	}

	/**
	 * Radius of the luma blurring box (default "2")
	 * 
	 * @param lr
	 * @return
	 */
	public Boxblur lr(String lr) {
		return lumaRadius(lr);
	}

	/**
	 * How many times should the boxblur be applied to luma (from 0 to INT_MAX) (default 2)
	 * 
	 * @param lumaPower
	 * @return
	 */
	public Boxblur lumaPower(int lumaPower) {
		if(lumaPower < 0) {
			throw new IllegalArgumentException("lumaPower must be at least 0: " + lumaPower);
		}
		parameter("luma_power", Integer.toString(lumaPower));
		return this;
	}

	/**
	 * How many times should the boxblur be applied to luma (from 0 to INT_MAX) (default 2)
	 * 
	 * @param lp
	 * @return
	 */
	public Boxblur lp(int lp) {
		return lumaPower(lp);
	}

	/**
	 * Radius of the chroma blurring box
	 * 
	 * @param chromaRadius
	 * @return
	 */
	public Boxblur chromaRadius(String chromaRadius) {
		parameter("chroma_radius", chromaRadius);
		return this;
	}

	/**
	 * Radius of the chroma blurring box
	 * 
	 * @param cr
	 * @return
	 */
	public Boxblur cr(String cr) {
		return chromaRadius(cr);
	}

	/**
	 * How many times should the boxblur be applied to chroma (from -1 to INT_MAX) (default -1)
	 * 
	 * @param chromaPower
	 * @return
	 */
	public Boxblur chromaPower(int chromaPower) {
		if(chromaPower < - 1) {
			throw new IllegalArgumentException("chromaPower must be at least -1: " + chromaPower);
		}
		parameter("chroma_power", Integer.toString(chromaPower));
		return this;
	}

	/**
	 * How many times should the boxblur be applied to chroma (from -1 to INT_MAX) (default -1)
	 * 
	 * @param cp
	 * @return
	 */
	public Boxblur cp(int cp) {
		return chromaPower(cp);
	}

	/**
	 * Radius of the alpha blurring box
	 * 
	 * @param alphaRadius
	 * @return
	 */
	public Boxblur alphaRadius(String alphaRadius) {
		parameter("alpha_radius", alphaRadius);
		return this;
	}

	/**
	 * Radius of the alpha blurring box
	 * 
	 * @param ar
	 * @return
	 */
	public Boxblur ar(String ar) {
		return alphaRadius(ar);
	}

	/**
	 * How many times should the boxblur be applied to alpha (from -1 to INT_MAX) (default -1)
	 * 
	 * @param alphaPower
	 * @return
	 */
	public Boxblur alphaPower(int alphaPower) {
		if(alphaPower < - 1) {
			throw new IllegalArgumentException("alphaPower must be at least -1: " + alphaPower);
		}
		parameter("alpha_power", Integer.toString(alphaPower));
		return this;
	}

	/**
	 * How many times should the boxblur be applied to alpha (from -1 to INT_MAX) (default -1)
	 * 
	 * @param ap
	 * @return
	 */
	public Boxblur ap(int ap) {
		return alphaPower(ap);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
