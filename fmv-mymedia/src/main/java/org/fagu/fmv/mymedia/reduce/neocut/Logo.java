package org.fagu.fmv.mymedia.reduce.neocut;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import org.fagu.fmv.ffmpeg.filter.impl.Delogo;
import org.fagu.fmv.image.Rectangle;


/**
 * @author Oodrive
 * @author f.agu
 * @created 4 avr. 2018 15:43:58
 */
public class Logo extends Rectangle {

	private final boolean autoDetect;

	private Logo(boolean autoDetect, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.autoDetect = autoDetect;
	}

	public static Logo autoDetect() {
		return new Logo(true, 0, 0, 1, 1);
	}

	public static Logo defined(int x, int y, int w, int h) {
		return new Logo(false, x, y, w, h);
	}

	public boolean isAutoDetect() {
		return autoDetect;
	}

	public Delogo generateFilter() {
		return Delogo.build()
				.x(getX())
				.y(getY())
				.w(getWidth())
				.h(getHeight());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (autoDetect ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if( ! super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		Logo other = (Logo)obj;
		if(autoDetect != other.autoDetect)
			return false;
		return super.equals(obj);
	}

}
