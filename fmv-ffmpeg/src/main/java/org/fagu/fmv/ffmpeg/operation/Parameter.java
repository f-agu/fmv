package org.fagu.fmv.ffmpeg.operation;

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

import java.util.Collection;


/**
 * @author f.agu
 */
public class Parameter {

	// ----------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Way {
		BEFORE, AFTER
	}

	// ----------------------------------------

	private IOEntity ioEntity;

	private String name;

	private String value;

	private Way way;

	protected Parameter(String name, String value) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		this.name = name;
		this.value = value;
	}

	protected Parameter(Way way, IOEntity ioEntity, String name, String value) {
		if(way == null) {
			throw new NullPointerException("way");
		}
		if(ioEntity == null) {
			throw new NullPointerException("IOEntity");
		}
		if(name == null) {
			throw new NullPointerException("name");
		}
		this.way = way;
		this.ioEntity = ioEntity;
		this.name = name;
		this.value = value;
	}

	public static Parameter before(IOEntity ioEntity, String name) {
		return new Parameter(Way.BEFORE, ioEntity, name, null);
	}

	public static Parameter before(IOEntity ioEntity, String name, Object value) {
		return new Parameter(Way.BEFORE, ioEntity, name, value.toString());
	}

	public static Parameter after(IOEntity ioEntity, String name) {
		return new Parameter(Way.AFTER, ioEntity, name, null);
	}

	public static Parameter after(IOEntity ioEntity, String name, Object value) {
		return new Parameter(Way.AFTER, ioEntity, name, value.toString());
	}

	public static Parameter create(Way way, IOEntity ioEntity, String name) {
		return new Parameter(way, ioEntity, name, null);
	}

	public static Parameter create(Way way, IOEntity ioEntity, String name, Object value) {
		return new Parameter(way, ioEntity, name, value.toString());
	}

	public static Parameter createGlobal(String name) {
		return new Parameter(name, null);
	}

	public static Parameter createGlobal(String name, Object value) {
		return new Parameter(name, value.toString());
	}

	public boolean isGlobal() {
		return way == null && ioEntity == null;
	}

	public Way getWay() {
		return way;
	}

	public IOEntity getIOEntity() {
		return ioEntity;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public boolean hasValue() {
		return value != null;
	}

	public void addTo(Collection<String> commands) {
		commands.add(name);
		if(hasValue()) {
			commands.add(value);
		}
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder()
				.append("Param[");
		if(isGlobal()) {
			buf.append("global,");
		} else {
			buf.append(way).append(',').append(ioEntity).append(',');
		}
		buf.append(name);
		if(hasValue()) {
			buf.append('=').append(value);
		}
		return buf.append(']')
				.toString();
	}
}
