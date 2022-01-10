package org.fagu.fmv.soft.spring.actuator;

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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.FoundReason;
import org.fagu.fmv.soft.find.FoundReasons;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;


/**
 * @author f.agu
 */
public class SoftInfoContributor implements InfoContributor {

	private final boolean cacheEnabled;

	private final Duration cacheTimeOut;

	private LocalDateTime lastCheck;

	private Map<String, String> cache;

	public SoftInfoContributor(boolean cacheEnabled, Duration cacheTimeOut) {
		this.cacheEnabled = cacheEnabled;
		this.cacheTimeOut = cacheTimeOut;
	}

	@Override
	public void contribute(Builder builder) {
		Map<String, String> content = null;
		if(cacheEnabled && (cache == null || isTimeOut())) {
			content = content();
			lastCheck = LocalDateTime.now();
		} else {
			content = cache;
		}
		builder.withDetail("softs", content);
	}

	// *********************************************

	private boolean isTimeOut() {
		if(cacheTimeOut == null) {
			return false;
		}
		return lastCheck.plus(cacheTimeOut).isBefore(LocalDateTime.now());
	}

	private Map<String, String> content() {
		Map<String, String> map = new HashMap<>();
		for(Soft soft : Softs.getInfoContributors()) {
			String msg = null;
			if(soft.isFound()) {
				StringBuilder buf = new StringBuilder(100);
				SoftInfo softInfo = soft.getFirstInfo();
				if(softInfo instanceof VersionSoftInfo) {
					((VersionSoftInfo)softInfo).getVersion().ifPresent(v -> buf.append(v).append(", "));
				}
				if(softInfo instanceof VersionDateSoftInfo) {
					((VersionDateSoftInfo)softInfo).getDate().ifPresent(d -> {
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						buf.append(dateFormat.format(d)).append(", ");
					});
				}
				buf.append(soft.getFile().getAbsolutePath());
				msg = buf.toString();
			} else {
				StringJoiner joiner = new StringJoiner(" ; ");
				for(SoftFound softFound : soft.getFounds()) {
					StringBuilder buf = new StringBuilder();
					FoundReason foundReason = softFound != null ? softFound.getFoundReason() : FoundReasons.NOT_FOUND;
					buf.append(foundReason.name());
					String reason = softFound != null ? softFound.getReason() : null;
					if(reason != null) {
						buf.append(": ").append(reason);
					}
					joiner.add(buf.toString());
				}
				msg = joiner.toString();
			}

			map.put(soft.getName(), msg);
		}
		return map;
	}

}
