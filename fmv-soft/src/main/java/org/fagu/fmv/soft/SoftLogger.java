package org.fagu.fmv.soft;

/*
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.find.FoundReason;
import org.fagu.fmv.soft.find.FoundReasons;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.spring.actuator.Softs;


/**
 * @author f.agu
 */
public class SoftLogger {

	private final List<Soft> softs;

	public SoftLogger(List<Soft> softs) {
		this.softs = new ArrayList<>(softs);
		Collections.sort(this.softs, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
	}

	public boolean log(Consumer<String> logger) {
		logDetails(logger);
		return logConclusion(logger);
	}

	public SoftLogger logDetails(Consumer<String> logger) {
		int maxLength = getNameMaxLength() + 3;
		for(Soft soft : softs) {
			Founds founds = soft.getFounds();
			String name = soft.getName();

			SoftFound firstFound = founds.getFirstFound();
			SoftProvider softProvider = soft.getSoftProvider();
			if(founds.isFound()) {
				toLine(startLine(maxLength, name, firstFound), softProvider, firstFound, logger);
			} else {
				if(founds.isEmpty()) {
					toLine(startLine(maxLength, name, firstFound), softProvider, SoftFound.notFound(), logger);
				} else {
					int index = 0;
					for(SoftFound softFound : founds) {
						toLine(startLine(maxLength, 0 == index++ ? name : null, softFound), softProvider, softFound, logger);
					}
				}

				String downloadURL = softProvider.getDownloadURL();
				if(StringUtils.isNotBlank(downloadURL)) {
					logger.accept("    Download at: " + downloadURL);
				}
				String logMessageIfNotFound = softProvider.getLogMessageIfNotFound();
				if(StringUtils.isNotBlank(logMessageIfNotFound)) {
					logger.accept("    " + logMessageIfNotFound);
				}
			}
		}
		return this;
	}

	public SoftLogger supplyInfoContributor() {
		softs.forEach(Softs::contributeInfo);
		return this;
	}

	public SoftLogger supplyHealthIndicator() {
		softs.forEach(Softs::indicateHealth);
		return this;
	}

	public boolean logConclusion(Consumer<String> logger) {
		int countNotFound = (int)softs.stream().map(Soft::isFound).filter(b -> ! b).count();
		if(countNotFound <= 0) {
			return true;
		}

		logger.accept("=====================================================================");
		if(countNotFound == 1) {
			logger.accept("A software is missing");
		} else {
			logger.accept(countNotFound + " softwares (on " + softs.size() + ") are missing");
		}
		logger.accept("Don't forget the PATH env");
		String pathEnv = System.getenv("PATH");
		logger.accept("PATH: " + pathEnv);
		if(pathEnv.contains("eclipse")) {
			logger.accept("AND STOP & START YOUR IDE");
		}
		logger.accept("=====================================================================");
		return false;
	}

	// *************************************************

	private int getNameMaxLength() {
		return softs.stream().mapToInt(soft -> soft.getName().length()).max().orElse(0);
	}

	private String getReasonName(SoftFound softFound) {
		FoundReason foundReason = softFound != null ? softFound.getFoundReason() : FoundReasons.NOT_FOUND;
		return StringUtils.center(foundReason.name(), 11);
	}

	private StringBuilder startLine(int maxLength, String name, SoftFound softFound) {
		return new StringBuilder()
				.append(StringUtils.rightPad(name != null ? name : "", maxLength))
				.append('[').append(getReasonName(softFound)).append(']');
	}

	private void toLine(StringBuilder line, SoftProvider softProvider, SoftFound softFound, Consumer<String> formatConsumer) {
		if(softFound != null) {
			String info = softFound.getInfo();
			if(softFound.isFound()) {
				appendFile(line, softFound);
				if(StringUtils.isNotBlank(info)) {
					line.append(' ').append('(').append(info).append(')');
				}
				formatConsumer.accept(line.toString());
				return;
			}
			appendFile(line, softFound);

			FoundReason foundReason = softFound.getFoundReason();
			if(foundReason == FoundReasons.BAD_VERSION) {
				line.append(" (find version ").append(info != null ? info : "?").append(" but I need another version: ");
				line.append(softFound.getReason()).append(')');
				formatConsumer.accept(line.toString());
				return;
			}
			String reason = softFound.getReason();
			if(StringUtils.isNotBlank(reason)) {
				line.append(": ").append(reason);
			}

			formatConsumer.accept(line.toString());

			String minVersion = softProvider.getMinVersion();
			if(StringUtils.isNotBlank(minVersion)) {
				formatConsumer.accept("   Minimum version: " + minVersion);
			}
		}

	}

	private void appendFile(StringBuilder line, SoftFound softFound) {
		File file = softFound.getFile();
		if(file != null) {
			line.append(' ').append(' ').append(file.getAbsolutePath());
		}
	}

}
