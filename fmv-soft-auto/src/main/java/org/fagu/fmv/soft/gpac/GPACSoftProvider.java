package org.fagu.fmv.soft.gpac;

/*-
 * #%L
 * fmv-soft-auto
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

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.SearchBehavior;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public abstract class GPACSoftProvider extends SoftProvider {

	private static final String DEFAULT_PATTERN_VERSION = ".*GPAC version ([\\d+\\.]+).*";

	private final String foundParameter;

	protected GPACSoftProvider(String name, SoftPolicy softPolicy, String foundParameter) {
		super(name, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(0, 7))));
		this.foundParameter = Objects.requireNonNull(foundParameter);
	}

	@Override
	public Optional<String> getGroupTitle() {
		return Optional.of("GPAC");
	}

	@Override
	public SearchBehavior getSearchBehavior() {
		return SearchBehavior.onlyVersion();
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		// GPAC version 0.7.2-DEV-rev1143-g1c540fec-master
		final Pattern pattern = new SearchPropertiesHelper(searchProperties, this)
				.toPatternVersion(DEFAULT_PATTERN_VERSION);
		return prepareBuilder(
				prepareSoftFoundFactory().withParameters(foundParameter),
				builderConsumer)
						.parseVersion(line -> {
							Matcher matcher = pattern.matcher(line);
							if(matcher.matches()) {
								return VersionParserManager.parse(matcher.group(1));
							}
							return null;
						})
						.build();
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.find(programFile -> {
						File gpacFolder = new File(programFile, "GPAC");
						return gpacFolder.exists() ? Collections.singleton(gpacFolder) : Collections.emptyList();
					})
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;

	}

	@Override
	public String getDownloadURL() {
		return "https://gpac.wp.imt.fr/downloads/gpac-nightly-builds/";
	}

}
