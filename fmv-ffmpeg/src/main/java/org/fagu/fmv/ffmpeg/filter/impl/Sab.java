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
public class Sab extends AbstractFilter {

	/**
	 * 
	 */
	protected Sab() {
		super("sab");
	}

	/**
	 * @return
	 */
	public static Sab build() {
		return new Sab();
	}

	/**
	 * Set luma radius (from 0.1 to 4) (default 1)
	 * 
	 * @param lumaRadius
	 * @return
	 */
	public Sab lumaRadius(float lumaRadius) {
		if(0.0 > lumaRadius || lumaRadius > 4.0) {
			throw new IllegalArgumentException("lumaRadius must be between 0.0 and 4.0: " + lumaRadius);
		}
		parameter("luma_radius", Float.toString(lumaRadius));
		return this;
	}

	/**
	 * Set luma radius (from 0.1 to 4) (default 1)
	 * 
	 * @param lr
	 * @return
	 */
	public Sab lr(float lr) {
		if(0.0 > lr || lr > 4.0) {
			throw new IllegalArgumentException("lr must be between 0.0 and 4.0: " + lr);
		}
		parameter("lr", Float.toString(lr));
		return this;
	}

	/**
	 * Set luma pre-filter radius (from 0.1 to 2) (default 1)
	 * 
	 * @param lumaPreFilterRadius
	 * @return
	 */
	public Sab lumaPreFilterRadius(float lumaPreFilterRadius) {
		if(0.0 > lumaPreFilterRadius || lumaPreFilterRadius > 2.0) {
			throw new IllegalArgumentException("lumaPreFilterRadius must be between 0.0 and 2.0: " + lumaPreFilterRadius);
		}
		parameter("luma_pre_filter_radius", Float.toString(lumaPreFilterRadius));
		return this;
	}

	/**
	 * Set luma pre-filter radius (from 0.1 to 2) (default 1)
	 * 
	 * @param lpfr
	 * @return
	 */
	public Sab lpfr(float lpfr) {
		if(0.0 > lpfr || lpfr > 2.0) {
			throw new IllegalArgumentException("lpfr must be between 0.0 and 2.0: " + lpfr);
		}
		parameter("lpfr", Float.toString(lpfr));
		return this;
	}

	/**
	 * Set luma strength (from 0.1 to 100) (default 1)
	 * 
	 * @param lumaStrength
	 * @return
	 */
	public Sab lumaStrength(float lumaStrength) {
		if(0.0 > lumaStrength || lumaStrength > 100.0) {
			throw new IllegalArgumentException("lumaStrength must be between 0.0 and 100.0: " + lumaStrength);
		}
		parameter("luma_strength", Float.toString(lumaStrength));
		return this;
	}

	/**
	 * Set luma strength (from 0.1 to 100) (default 1)
	 * 
	 * @param ls
	 * @return
	 */
	public Sab ls(float ls) {
		if(0.0 > ls || ls > 100.0) {
			throw new IllegalArgumentException("ls must be between 0.0 and 100.0: " + ls);
		}
		parameter("ls", Float.toString(ls));
		return this;
	}

	/**
	 * Set chroma radius (from -0.9 to 4) (default -0.9)
	 * 
	 * @param chromaRadius
	 * @return
	 */
	public Sab chromaRadius(float chromaRadius) {
		if(0.0 > chromaRadius || chromaRadius > 4.0) {
			throw new IllegalArgumentException("chromaRadius must be between 0.0 and 4.0: " + chromaRadius);
		}
		parameter("chroma_radius", Float.toString(chromaRadius));
		return this;
	}

	/**
	 * Set chroma radius (from -0.9 to 4) (default -0.9)
	 * 
	 * @param cr
	 * @return
	 */
	public Sab cr(float cr) {
		if(0.0 > cr || cr > 4.0) {
			throw new IllegalArgumentException("cr must be between 0.0 and 4.0: " + cr);
		}
		parameter("cr", Float.toString(cr));
		return this;
	}

	/**
	 * Set chroma pre-filter radius (from -0.9 to 2) (default -0.9)
	 * 
	 * @param chromaPreFilterRadius
	 * @return
	 */
	public Sab chromaPreFilterRadius(float chromaPreFilterRadius) {
		if(0.0 > chromaPreFilterRadius || chromaPreFilterRadius > 2.0) {
			throw new IllegalArgumentException("chromaPreFilterRadius must be between 0.0 and 2.0: " + chromaPreFilterRadius);
		}
		parameter("chroma_pre_filter_radius", Float.toString(chromaPreFilterRadius));
		return this;
	}

	/**
	 * Set chroma pre-filter radius (from -0.9 to 2) (default -0.9)
	 * 
	 * @param cpfr
	 * @return
	 */
	public Sab cpfr(float cpfr) {
		if(0.0 > cpfr || cpfr > 2.0) {
			throw new IllegalArgumentException("cpfr must be between 0.0 and 2.0: " + cpfr);
		}
		parameter("cpfr", Float.toString(cpfr));
		return this;
	}

	/**
	 * Set chroma strength (from -0.9 to 100) (default -0.9)
	 * 
	 * @param chromaStrength
	 * @return
	 */
	public Sab chromaStrength(float chromaStrength) {
		if(0.0 > chromaStrength || chromaStrength > 100.0) {
			throw new IllegalArgumentException("chromaStrength must be between 0.0 and 100.0: " + chromaStrength);
		}
		parameter("chroma_strength", Float.toString(chromaStrength));
		return this;
	}

	/**
	 * Set chroma strength (from -0.9 to 100) (default -0.9)
	 * 
	 * @param cs
	 * @return
	 */
	public Sab cs(float cs) {
		if(0.0 > cs || cs > 100.0) {
			throw new IllegalArgumentException("cs must be between 0.0 and 100.0: " + cs);
		}
		parameter("cs", Float.toString(cs));
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
