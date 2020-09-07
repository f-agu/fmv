package org.fagu.fmv.soft.find;

/*
 * #%L
 * fmv-utils
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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author f.agu
 */
public class SoftFound implements Comparable<SoftFound> {

	private static final SoftFound SF_NOT_FOUND = new SoftFound();

	private final FoundReason foundReason;

	private final String reason;

	private final File file;

	private final SoftInfo softInfo;

	private String localizedBy;

	private SoftFound() {
		file = null;
		softInfo = null;
		foundReason = FoundReasons.NOT_FOUND;
		reason = null;
	}

	private SoftFound(FoundReason foundReason, File file, SoftInfo softInfo, String reason) {
		this.foundReason = Objects.requireNonNull(foundReason);
		this.file = Objects.requireNonNull(file);
		this.softInfo = softInfo; // nullable
		this.reason = reason; // nullable
	}

	public static SoftFound found(File file) {
		return foundCheck(file, FoundReasons.FOUND, null, null);
	}

	public static SoftFound found(SoftInfo softInfo) {
		return foundCheck(softInfo.getFile(), FoundReasons.FOUND, softInfo, null);
	}

	public static SoftFound foundBadVersion(SoftInfo softInfo, String expectedVersion) {
		return foundCheck(softInfo.getFile(), FoundReasons.BAD_VERSION, softInfo, expectedVersion);
	}

	public static SoftFound foundBadSoft(File file) {
		return foundCheck(file, FoundReasons.BAD_SOFT, null, null);
	}

	public static SoftFound foundBadSoft(SoftInfo softInfo) {
		return foundBadSoft(softInfo, null);
	}

	public static SoftFound foundBadSoft(SoftInfo softInfo, String reason) {
		return foundCheck(softInfo.getFile(), FoundReasons.BAD_SOFT, softInfo, reason);
	}

	public static SoftFound foundError(File file, String reason) {
		return foundCheck(file, FoundReasons.ERROR, null, reason);
	}

	public static SoftFound multiple(final SoftFound... softFounds) {
		return multiple(Arrays.asList(softFounds));
	}

	public static SoftFound of(FoundReason foundReason, File file, SoftInfo softInfo, String reason) {
		if(foundReason == FoundReasons.MULTIPLE) {
			throw new IllegalArgumentException(foundReason.name());
		}
		return foundCheck(file, foundReason, softInfo, reason);
	}

	public static SoftFound multiple(final Collection<SoftFound> softFounds) {
		if(softFounds == null) {
			return SoftFound.notFound();
		}
		Collection<SoftFound> filteredSoftFounds = softFounds.stream().filter(Objects::nonNull).collect(Collectors.toList());
		if(filteredSoftFounds.isEmpty()) {
			return SoftFound.notFound();
		}
		SoftFound first = filteredSoftFounds.iterator().next();
		if(filteredSoftFounds.size() == 1) {
			return first;
		}
		return new SoftFound(first.getFoundReason(), first.getFile(), first.getSoftInfo(), first.getReason()) {

			@Override
			public FoundReason getFoundReason() {
				FoundReason current = null;
				for(SoftFound softFound : softFounds) {
					if(current != softFound.getFoundReason()) {
						return FoundReasons.MULTIPLE;
					}
					current = softFound.getFoundReason();
				}
				return current;
			}

			@Override
			public boolean isFound() {
				return filteredSoftFounds.stream().anyMatch(SoftFound::isFound);
			}

			@Override
			public String toString() {
				return filteredSoftFounds.stream().map(SoftFound::toString).collect(Collectors.joining(","));
			}
		};
	}

	public static SoftFound notFound() {
		return SF_NOT_FOUND;
	}

	// ===============

	public FoundReason getFoundReason() {
		return foundReason;
	}

	public String getReason() {
		return reason;
	}

	public String getLocalizedBy() {
		return localizedBy;
	}

	public SoftFound setLocalizedBy(String localizedBy) {
		this.localizedBy = localizedBy;
		return this;
	}

	public boolean isFound() {
		return foundReason == FoundReasons.FOUND;
	}

	public File getFile() {
		return file;
	}

	public SoftInfo getSoftInfo() {
		return softInfo;
	}

	public String getInfo() {
		return softInfo != null ? softInfo.getInfo() : "<version undefined>";
	}

	@Override
	public int compareTo(SoftFound o) {
		if(isFound() && ! o.isFound()) {
			return 1;
		}
		if( ! isFound() && o.isFound()) {
			return - 1;
		}
		return compareByInfo(o);
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(foundReason.name());
		if(file != null) {
			buf.append(':').append(file);
		}
		if(softInfo != null) {
			buf.append(' ').append('(').append(softInfo).append(')');
		}
		return buf.toString();
	}

	// **********************************************

	private int compareByInfo(SoftFound o) {
		if(softInfo != null) {
			SoftInfo otherInfo = o.getSoftInfo();
			if(otherInfo != null) {
				return softInfo.compareTo(otherInfo);
			}
		}
		File myFile = getFile();
		File otherFile = o.getFile();
		if(myFile != null && otherFile != null) {
			return myFile.compareTo(otherFile);
		}
		return Integer.compare(hashCode(), o.hashCode());
	}

	private static SoftFound foundCheck(File file, FoundReason foundReason, SoftInfo softInfo, String reason) {
		if(file == null || ! file.exists()) {
			return notFound();
		}
		return new SoftFound(foundReason, file, softInfo, reason);
	}

}
