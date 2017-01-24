package org.fagu.fmv.im.soft;

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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.im.exception.IMExceptionKnownAnalyzer;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.Soft.SoftExecutor;
import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public abstract class IMSoftProvider extends SoftProvider {

	private static final String SOFT_MAGICK_NAME = "magick";

	private static final Version V7 = new Version(7);

	/**
	 * @param name
	 */
	public IMSoftProvider(String name) {
		super(name);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		return ExecSoftFoundFactory.withParameters("-version") //
				.parseFactory(file -> createParser(getSoftName(), file)) //
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSearchFileFilter()
	 */
	@Override
	public FileFilter getSearchFileFilter() {
		return f -> {
			String name = f.getName();
			String baseName = FilenameUtils.getBaseName(name);
			return getName().equals(baseName) || SOFT_MAGICK_NAME.equals(baseName);
		};
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "http://www.imagemagick.org/script/download.php";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftPolicy()
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy() //
				.onAllPlatforms().minVersion(new Version(6, 6));
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftExecutor(org.fagu.fmv.soft.Soft, java.io.File, java.util.List)
	 */
	@Override
	public SoftExecutor createSoftExecutor(Soft soft, File execFile, List<String> parameters) {
		if(SOFT_MAGICK_NAME.equalsIgnoreCase(FilenameUtils.getBaseName(soft.getFile().getName()))) {
			SoftInfo softInfo = soft.getFirstInfo();
			if(softInfo instanceof VersionSoftInfo) {
				VersionSoftInfo versionSoftInfo = (VersionSoftInfo)softInfo;
				Optional<Version> version = versionSoftInfo.getVersion();
				if(version.isPresent() && version.get().isUpperOrEqualsThan(V7)) {
					List<String> newParams = new ArrayList<>(parameters);
					newParams.add(0, getName());
					return new SoftExecutor(this, execFile, newParams);
				}
			}
		}
		return super.createSoftExecutor(soft, execFile, parameters);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getExceptionKnownAnalyzerClass()
	 */
	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return IMExceptionKnownAnalyzer.class;
	}
	// ***********************************************************************

	/**
	 * @param softName
	 * @param file
	 * @return
	 */
	Parser createParser(SoftName softName, File file) {
		return new Parser() {

			private Pattern pattern = Pattern.compile("Version\\: ImageMagick ([0-9\\.\\-]+) (?:.*)([0-9]{4}-[0-9]{2}-[0-9]{2}) .*");

			private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			private Version version;

			private Date date;

			private boolean firstPass;

			@Override
			public void readLine(String line) {
				if(firstPass) {
					return;
				}
				firstPass = true;
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()) {
					version = VersionParserManager.parse(matcher.group(1));
					try {
						date = dateFormat.parse(matcher.group(2));
					} catch(Exception e) {
						// ignore
					}
				}
			}

			@Override
			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				return getSoftPolicy().toSoftFound(new VersionDateSoftInfo(file, softName, version, date));
			}

		};
	}
}
