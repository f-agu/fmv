package org.fagu.fmv.soft.mplayer;

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

import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @created 5 juin 2017 09:57:00
 */
public abstract class MSoftProvider extends SoftProvider {

	private static final String DEFAULT_SUFFIX_PATTERN_VERSION = "${soft.name} sherpya-r(\\d+)+.*-[\\d\\.]+ \\(C\\).*";

	protected MSoftProvider(String name) {
		this(name, new VersionSoftPolicy()
				.onWindows(minVersion(37905))
				.onLinux(minVersion(1, 3)));
	}

	protected MSoftProvider(String name, SoftPolicy softPolicy) {
		super(name, softPolicy);
	}

	@Override
	public Optional<String> getGroupTitle() {
		return Optional.of("MPlayer");
	}

	@Override
	public SearchBehavior getSearchBehavior() {
		return SearchBehavior.onlyVersion();
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		final Pattern pattern = new SearchPropertiesHelper(searchProperties, this)
				.toPatternVersion(DEFAULT_SUFFIX_PATTERN_VERSION);
		return prepareBuilder(
				prepareSoftFoundFactory().withoutParameter(),
				builderConsumer)
						.parseVersion(line -> {
							Matcher matcher = pattern.matcher(line);
							return matcher.matches() ? VersionParserManager.parse(matcher.group(1)) : null;
						})
						.exitValue(MEncoderSoftProvider.NAME.equals(getName()) ? 1 : 0)
						.build();
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.findFolder(f -> f.getName().toLowerCase().startsWith("mplayer"))
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;

	}

	@Override
	public String getDownloadURL() {
		if(SystemUtils.IS_OS_WINDOWS) {
			return "http://oss.netfarm.it/mplayer/";
		}
		return "http://www.mplayerhq.hu/design7/dload.html";
	}

}
