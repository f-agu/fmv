package org.fagu.fmv.soft.spring.actuator;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import java.text.SimpleDateFormat;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.FoundReason;
import org.fagu.fmv.soft.find.FoundReasons;
import org.fagu.fmv.soft.find.Founds;
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

	/**
	 * @see org.springframework.boot.actuate.info.InfoContributor#contribute(org.springframework.boot.actuate.info.Info.Builder)
	 */
	@Override
	public void contribute(Builder builder) {
		for(Soft soft : Softs.getInfoContributors()) {
			StringBuilder buf = new StringBuilder(100);
			if(soft.isFound()) {
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
			} else {
				Founds founds = soft.getFounds();
				SoftFound softFound = founds.getFirstFound();
				FoundReason foundReason = softFound != null ? softFound.getFoundReason() : FoundReasons.NOT_FOUND;
				buf.append(foundReason.name());
				String reason = softFound.getReason();
				if(reason != null) {
					buf.append(": ").append(reason);
				}
			}

			builder.withDetail(soft.getName(), buf.toString());
		}
	}

}
