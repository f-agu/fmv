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
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * {@link http://ffmpeg.org/ffmpeg-all.html#blend}
 * 
 * @author f.agu
 */
public class Blend extends AbstractFilter {

	/**
	 * @author f.agu
	 */
	public enum Mode {
		ADDITION,
		AND,
		AVERAGE,
		BURN,
		DARKEN,
		DIFFERENCE,
		DIVIDE,
		DODGE,
		EXCLUSION,
		HARDLIGHT,
		LIGHTEN,
		MULTIPLY,
		NEGATION,
		NORMAL,
		OR,
		OVERLAY,
		PHOENIX,
		PINLIGHT,
		REFLECT,
		SCREEN,
		SOFTLIGHT,
		SUBTRACT,
		VIVIDLIGHT,
		XOR
	}

	/**
	 * 
	 */
	protected Blend() {
		super("blend");
	}

	/**
	 * @return
	 */
	public static Blend build() {
		return new Blend();
	}

	/**
	 * @param startTime
	 * @param fadeDuration
	 * @return
	 */
	public Blend exprFade(Time startTime, Duration fadeDuration) {
		// 1.1 : startTime
		// 2.5 : endTime
		// 1.3 : fadeDuration
		Time endTime = startTime.add(fadeDuration);
		StringBuilder subExpr = new StringBuilder(100);
		// (if(gte(T,2.5),1,if(lte(T,1.1),0,min(max((T-1.1)/1.3,0),1))))
		subExpr.append("(if(gte(T,").append(endTime.toSeconds()).append("),1,if(lte(T,").append(startTime.toSeconds()).append("),0,min(max((T-");
		subExpr.append(startTime.toSeconds()).append(")/").append(fadeDuration.toSeconds()).append(",0),1))))");

		StringBuilder expr = new StringBuilder(200);
		// A*(1-(if(gte(T,2.5),1,if(lte(T,1.1),0,min(max((T-1.1)/1.3,0),1)))))+B*(if(gte(T,2.5),1,if(lte(T,1.1),0,min(max((T-1.1)/1.3,0),1))))
		expr.append("A*(1-").append(subExpr).append(")+B*").append(subExpr);
		return expr(expr.toString());
	}

	/**
	 * @param mode
	 * @return
	 */
	public Blend mode(Mode mode) {
		parameter("all_mode", mode.name().toLowerCase());
		return this;
	}

	/**
	 * @param repeat
	 * @return
	 */
	public Blend repeatLast(boolean repeat) {
		parameter("repeatlast", repeat ? "1" : "0");
		return this;
	}

	/**
	 * @param d
	 * @return
	 */
	public Blend opacity(double d) {
		parameter("all_opacity", Double.toString(d));
		return this;
	}

	/**
	 * @param expr
	 * @return
	 */
	public Blend expr(String expr) {
		parameter("all_expr", "'" + expr + "'");
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
