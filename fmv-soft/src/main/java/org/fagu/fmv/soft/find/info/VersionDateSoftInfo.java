package org.fagu.fmv.soft.find.info;

/*
 * #%L
 * fmv-utils
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

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class VersionDateSoftInfo extends VersionSoftInfo {

	private final LocalDateTime date;

	public VersionDateSoftInfo(File file, String softName, Version version, Date date) {
		super(file, softName, version);
		this.date = date != null ? LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()) : null;
	}

	public VersionDateSoftInfo(File file, String softName, Version version, LocalDateTime date) {
		super(file, softName, version);
		this.date = date;
	}

	public Optional<Date> getDate() {
		return Optional.ofNullable(date)
				.map(d -> Date.from(d.atZone(ZoneId.systemDefault()).toInstant()));
	}

	public Optional<LocalDateTime> getLocalDateTime() {
		return Optional.ofNullable(date);
	}

	public Optional<LocalDate> getLocalDate() {
		return Optional.ofNullable(date)
				.map(LocalDateTime::toLocalDate);
	}

}
