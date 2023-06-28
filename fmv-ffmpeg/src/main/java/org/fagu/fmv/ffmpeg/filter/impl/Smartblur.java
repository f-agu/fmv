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
public class Smartblur extends AbstractFilter {

	protected Smartblur() {
		super("smartblur");
	}

	public static Smartblur build() {
		return new Smartblur();
	}

	/**
	 * Set luma radius (from 0.1 to 5) (default 1)
	 * 
	 * @param lumaRadius
	 * @return
	 */
	public Smartblur lumaRadius(float lumaRadius) {
		if(0.0 > lumaRadius || lumaRadius > 5.0) {
			throw new IllegalArgumentException("lumaRadius must be between 0.0 and 5.0: " + lumaRadius);
		}
		parameter("luma_radius", Float.toString(lumaRadius));
		return this;
	}

	/**
	 * Set luma radius (from 0.1 to 5) (default 1)
	 * 
	 * @param lr
	 * @return
	 */
	public Smartblur lr(float lr) {
		if(0.0 > lr || lr > 5.0) {
			throw new IllegalArgumentException("lr must be between 0.0 and 5.0: " + lr);
		}
		parameter("lr", Float.toString(lr));
		return this;
	}

	/**
	 * Set luma strength (from -1 to 1) (default 1)
	 * 
	 * @param lumaStrength
	 * @return
	 */
	public Smartblur lumaStrength(float lumaStrength) {
		if( - 1.0 > lumaStrength || lumaStrength > 1.0) {
			throw new IllegalArgumentException("lumaStrength must be between -1.0 and 1.0: " + lumaStrength);
		}
		parameter("luma_strength", Float.toString(lumaStrength));
		return this;
	}

	/**
	 * Set luma strength (from -1 to 1) (default 1)
	 * 
	 * @param ls
	 * @return
	 */
	public Smartblur ls(float ls) {
		if( - 1.0 > ls || ls > 1.0) {
			throw new IllegalArgumentException("ls must be between -1.0 and 1.0: " + ls);
		}
		parameter("ls", Float.toString(ls));
		return this;
	}

	/**
	 * Set luma threshold (from -30 to 30) (default 0)
	 * 
	 * @param lumaThreshold
	 * @return
	 */
	public Smartblur lumaThreshold(int lumaThreshold) {
		if( - 30 > lumaThreshold || lumaThreshold > 30) {
			throw new IllegalArgumentException("lumaThreshold must be between -30 and 30: " + lumaThreshold);
		}
		parameter("luma_threshold", Integer.toString(lumaThreshold));
		return this;
	}

	/**
	 * Set luma threshold (from -30 to 30) (default 0)
	 * 
	 * @param lt
	 * @return
	 */
	public Smartblur lt(int lt) {
		if( - 30 > lt || lt > 30) {
			throw new IllegalArgumentException("lt must be between -30 and 30: " + lt);
		}
		parameter("lt", Integer.toString(lt));
		return this;
	}

	/**
	 * Set chroma radius (from -0.9 to 5) (default -0.9)
	 * 
	 * @param chromaRadius
	 * @return
	 */
	public Smartblur chromaRadius(float chromaRadius) {
		if(0.0 > chromaRadius || chromaRadius > 5.0) {
			throw new IllegalArgumentException("chromaRadius must be between 0.0 and 5.0: " + chromaRadius);
		}
		parameter("chroma_radius", Float.toString(chromaRadius));
		return this;
	}

	/**
	 * Set chroma radius (from -0.9 to 5) (default -0.9)
	 * 
	 * @param cr
	 * @return
	 */
	public Smartblur cr(float cr) {
		if(0.0 > cr || cr > 5.0) {
			throw new IllegalArgumentException("cr must be between 0.0 and 5.0: " + cr);
		}
		parameter("cr", Float.toString(cr));
		return this;
	}

	/**
	 * Set chroma strength (from -2 to 1) (default -2)
	 * 
	 * @param chromaStrength
	 * @return
	 */
	public Smartblur chromaStrength(float chromaStrength) {
		if( - 2.0 > chromaStrength || chromaStrength > 1.0) {
			throw new IllegalArgumentException("chromaStrength must be between -2.0 and 1.0: " + chromaStrength);
		}
		parameter("chroma_strength", Float.toString(chromaStrength));
		return this;
	}

	/**
	 * Set chroma strength (from -2 to 1) (default -2)
	 * 
	 * @param cs
	 * @return
	 */
	public Smartblur cs(float cs) {
		if( - 2.0 > cs || cs > 1.0) {
			throw new IllegalArgumentException("cs must be between -2.0 and 1.0: " + cs);
		}
		parameter("cs", Float.toString(cs));
		return this;
	}

	/**
	 * Set chroma threshold (from -31 to 30) (default -31)
	 * 
	 * @param chromaThreshold
	 * @return
	 */
	public Smartblur chromaThreshold(int chromaThreshold) {
		if( - 31 > chromaThreshold || chromaThreshold > 30) {
			throw new IllegalArgumentException("chromaThreshold must be between -31 and 30: " + chromaThreshold);
		}
		parameter("chroma_threshold", Integer.toString(chromaThreshold));
		return this;
	}

	/**
	 * Set chroma threshold (from -31 to 30) (default -31)
	 * 
	 * @param ct
	 * @return
	 */
	public Smartblur ct(int ct) {
		if( - 31 > ct || ct > 30) {
			throw new IllegalArgumentException("ct must be between -31 and 30: " + ct);
		}
		parameter("ct", Integer.toString(ct));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
