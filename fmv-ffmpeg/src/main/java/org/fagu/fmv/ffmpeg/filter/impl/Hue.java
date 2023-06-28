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
public class Hue extends AbstractFilter {

	protected Hue() {
		super("hue");
	}

	public static Hue build() {
		return new Hue();
	}

	/**
	 * Set the hue angle degrees expression
	 * 
	 * @param h
	 * @return
	 */
	public Hue h(String h) {
		parameter("h", h);
		return this;
	}

	/**
	 * Set the saturation expression (default "1")
	 * 
	 * @param s
	 * @return
	 */
	public Hue s(String s) {
		parameter("s", s);
		return this;
	}

	/**
	 * @return
	 */
	public Hue blackAndWhite() {
		return s("0");
	}

	/**
	 * Set the hue angle radians expression
	 * 
	 * @param H
	 * @return
	 */
	public Hue H(String h) {
		parameter("H", h);
		return this;
	}

	/**
	 * Set the brightness expression (default "0")
	 * 
	 * @param b
	 * @return
	 */
	public Hue b(String b) {
		parameter("b", b);
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
