package org.fagu.fmv.im.soft;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

/*-
 * #%L
 * fmv-soft-auto
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
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.im.exception.IMExceptionKnownAnalyzer;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.VersionDate;
import org.fagu.fmv.soft.find.SearchBehavior;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper.SearchMatching;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public abstract class IMSoftProvider extends SoftProvider {

	private static final String SOFT_MAGICK_NAME = "magick";

	private static final String DEFAULT_PATTERN_VERSION = "Version\\: ImageMagick ([0-9\\.\\-]+) (.*)";

	private static final String DEFAULT_PATTERN_DATE_1 = "(?:.*)([0-9]{4}-[0-9]{2}-[0-9]{2}) .*";

	private static final String DEFAULT_PATTERN_DATE_2 = "(?:.*)([0-9]{8}) .*";

	private static final Version V7 = new Version(7);

	protected IMSoftProvider(String name) {
		this(name, null);
	}

	protected IMSoftProvider(String name, SoftPolicy softPolicy) {
		super(name, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(7, 0, 8, 50))));
	}

	@Override
	public Optional<String> getGroupTitle() {
		return Optional.of("ImageMagick");
	}

	@Override
	public SearchBehavior getSearchBehavior() {
		return SearchBehavior.versionAndDate();
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		SearchMatching searchMatching = new SearchPropertiesHelper(searchProperties, this)
				.forMatchingVersion(DEFAULT_PATTERN_VERSION);
		return prepareSoftFoundFactory()
				.withParameters("-version")
				.parseVersionDate(line -> searchMatching.ifMatches(line, matcher -> {
					Version version = VersionParserManager.parse(matcher.group(1));
					Date date = null;
					if(matcher.groupCount() > 1) {
						date = parseDate(searchProperties, matcher.group(2));
					}
					return Optional.of(new VersionDate(version, date));
				})
						.orElse(null))
				.build();
	}

	@Override
	public FileFilter getFileFilter() {
		return f -> {
			String name = f.getName();
			String baseName = FilenameUtils.getBaseName(name);
			return getName().equals(baseName) || SOFT_MAGICK_NAME.equals(baseName);
		};
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		softLocator.enableCacheInSameFolderOfGroup(getGroupName());
		if(SystemUtils.IS_OS_WINDOWS) {
			softLocator.addDefaultLocator();
			ProgramFilesLocatorSupplier.with(softLocator)
					.findFolder(folder -> folder.getName().toLowerCase().startsWith("imagemagick"))
					.supplyIn();
		}
		return softLocator;
	}

	@Override
	public String getDownloadURL() {
		return "http://www.imagemagick.org/script/download.php";
	}

	@Override
	public SoftExecutor createSoftExecutor(Soft soft, File execFile, List<String> parameters) {
		if(SOFT_MAGICK_NAME.equalsIgnoreCase(FilenameUtils.getBaseName(soft.getFile().getName()))) {
			SoftInfo softInfo = soft.getFirstInfo();
			if(softInfo instanceof VersionSoftInfo) {
				VersionSoftInfo versionSoftInfo = (VersionSoftInfo)softInfo;
				Optional<Version> version = versionSoftInfo.getVersion();
				if(version.isPresent() && version.get().isUpperOrEqualsThan(V7)) {
					List<String> newParams;
					if(ConvertSoftProvider.NAME.equals(getName())) {
						newParams = parameters;
					} else {
						newParams = new ArrayList<>(parameters);
						newParams.add(0, getName());
					}
					return new SoftExecutor(this, execFile, newParams);
				}
			}
		}
		return super.createSoftExecutor(soft, execFile, parameters);
	}

	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return IMExceptionKnownAnalyzer.class;
	}

	// ******************************************************

	private Date parseDate(Properties searchProperties, String remainLine) {
		SearchPropertiesHelper searchPropertiesHelper = new SearchPropertiesHelper(searchProperties, this);
		Pattern pattern = searchPropertiesHelper.toPatternDate(DEFAULT_PATTERN_DATE_1);
		Matcher matcher = pattern.matcher(remainLine);
		if(matcher.matches()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return dateFormat.parse(matcher.group(1));
			} catch(Exception e) {
				// ignore
			}
			return null;
		}

		pattern = Pattern.compile(DEFAULT_PATTERN_DATE_2);
		matcher = pattern.matcher(remainLine);
		if(matcher.matches()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			try {
				return dateFormat.parse(matcher.group(1));
			} catch(Exception e) {
				// ignore
			}
		}
		return null;
	}

}
