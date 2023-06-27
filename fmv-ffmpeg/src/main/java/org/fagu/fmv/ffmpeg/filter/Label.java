package org.fagu.fmv.ffmpeg.filter;

import java.util.Objects;

import org.fagu.fmv.ffmpeg.operation.Type;

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


/**
 * @author f.agu
 */
public class Label {

	/**
	 * @author f.agu
	 */
	public enum State {
		INPUT, INTERMEDIATE
	}

	// ---------------------------------------

	private final State state;

	private final String name;

	private Label(String name, State state) {
		this.name = Objects.requireNonNull(name);
		this.state = Objects.requireNonNull(state);
	}

	public String getName() {
		return name;
	}

	public State getState() {
		return state;
	}

	@Override
	public int hashCode() {
		if(state == State.INPUT) {
			return name.hashCode();
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if( ! (obj instanceof Label)) {
			return false;
		}
		Label other = (Label)obj;
		return state == State.INPUT && state == other.state && name.equals(other.name);
	}

	@Override
	public String toString() {
		return new StringBuilder(50)
				.append("Label(").append(Integer.toHexString(hashCode())).append(',').append(name).append(',').append(state).append(')')
				.toString();
	}

	public static Label of(String name, State state) {
		return new Label(name, state);
	}

	public static Label input(String name) {
		return new Label(name, State.INPUT);
	}

	public static Label input(String name, Type type) {
		return type == null ? input(name) : input(name + ':' + type.code());
	}

	public static Label input(int index) {
		return input(index, null);
	}

	public static Label input(int index, Type type) {
		return input(Integer.toString(index), type);
	}

	public static Label intermediate(String prefix) {
		return new Label(prefix, State.INTERMEDIATE);
	}

}
