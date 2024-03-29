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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


/**
 * @author f.agu
 */
public class Folder {

	private final static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private final String key;

	protected Folder(String key) {
		this.key = Objects.requireNonNull(key);
	}

	public static Folder byDay(long time) {
		return byDay(new Date(time));
	}

	public static Folder byDay(Date date) {
		return new Folder(DAY_FORMAT.format(date));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		Folder other = (Folder)obj;
		if(key == null) {
			if(other.key != null) {
				return false;
			}
		} else if( ! key.equals(other.key)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return key;
	}

}
