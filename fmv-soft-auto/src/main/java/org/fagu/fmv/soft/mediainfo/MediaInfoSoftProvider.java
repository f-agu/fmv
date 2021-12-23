package org.fagu.fmv.soft.mediainfo;

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
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class MediaInfoSoftProvider extends SoftProvider {

	private static final String DEFAULT_PATTERN_VERSION = "MediaInfoLib \\- v([0-9\\.\\-]+)";

	public static final String NAME = "mediainfo";

	public MediaInfoSoftProvider() {
		this(null);
	}

	public MediaInfoSoftProvider(SoftPolicy softPolicy) {
		super(NAME,
				ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy().onAllPlatforms(minVersion(18))));
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		final Pattern pattern = new SearchPropertiesHelper(searchProperties, this)
				.toPatternVersion(DEFAULT_PATTERN_VERSION);
		return prepareBuilder(
				prepareSoftFoundFactory().withParameters("--Version"),
				builderConsumer)
						.parseVersion(line -> {
							Matcher matcher = pattern.matcher(line);
							if(matcher.matches()) {
								return new Version(VersionParserManager.parse(matcher.group(1)));
							}
							return null;
						})
						.build();
	}

	@Override
	public Optional<String> getGroupTitle() {
		return Optional.of("MediaArea");
	}

	@Override
	public SearchBehavior getSearchBehavior() {
		return SearchBehavior.onlyVersion();
	}

	@Override
	public String getDownloadURL() {
		return "https://mediaarea.net/fr/MediaInfo/Download  and select CLI";
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.find(programFile -> streamInFolderStartsWith(programFile, "mediainfo")
							.filter(folder -> ! new File(folder, "MediaInfo.dll").exists())
							.collect(Collectors.toList()))
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;
	}

}
