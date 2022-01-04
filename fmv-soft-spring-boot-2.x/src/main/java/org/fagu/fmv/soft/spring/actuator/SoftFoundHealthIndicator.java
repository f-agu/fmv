package org.fagu.fmv.soft.spring.actuator;

import java.io.File;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health;
import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health.CheckPolicy;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;


/**
 * @author f.agu
 */
public class SoftFoundHealthIndicator extends AbstractHealthIndicator {

	private final Map<Soft, FileInfo> fileInfos;

	private final boolean checkOnFileChange;

	public SoftFoundHealthIndicator(Health healthProps) {
		super("soft check failed");
		this.checkOnFileChange = healthProps.getCheckPolicy() == CheckPolicy.ON_FILE_CHANGE;
		this.fileInfos = checkOnFileChange ? new HashMap<>() : Collections.emptyMap();
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		Collection<Soft> softs = Softs.getHealthIndicators();
		if(softs.isEmpty()) {
			builder.unknown();
			return;
		}

		builder.up();
		Set<String> downSofts = new TreeSet<>();
		for(Soft soft : softs) {
			String msg = soft.toString();
			if( ! checkOnFileChange || needToRecheck(soft)) {
				SoftFound softFound = soft.getFounds().getFirstFound();
				if(soft.isFound()) {
					// recheck soft
					softFound = soft.reFind();
				}
				if(softFound == null || ! softFound.isFound()) {
					builder.down();
					downSofts.add(soft.getName());
					if(checkOnFileChange) {
						fileInfos.remove(soft);
					}
				} else if(checkOnFileChange) {
					fileInfos.put(soft, new FileInfo(softFound.getFile()));
				}
			}
			builder.withDetail(soft.getName(), msg);
		}
		if( ! downSofts.isEmpty()) {
			builder.withDetail("Down soft list", downSofts.stream().collect(Collectors.joining(", ")));
		}
	}

	// *********************************************

	private boolean needToRecheck(Soft soft) {
		FileInfo previousInfo = fileInfos.get(soft);
		if(previousInfo == null) {
			return true;
		}
		FileInfo newInfo = new FileInfo(soft.getFile());
		return ! newInfo.equals(previousInfo);
	}

	// ---------------------------------------------

	private static class FileInfo {

		private final long size;

		private final long modifiedTime;

		private FileInfo(File file) {
			this.size = file.length();
			this.modifiedTime = file.lastModified();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int)(modifiedTime ^ (modifiedTime >>> 32));
			result = prime * result + (int)(size ^ (size >>> 32));
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
			FileInfo other = (FileInfo)obj;
			if(modifiedTime != other.modifiedTime) {
				return false;
			}
			return size == other.size;
		}

	}

}
