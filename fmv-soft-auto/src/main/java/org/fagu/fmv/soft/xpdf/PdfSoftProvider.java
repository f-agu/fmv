package org.fagu.fmv.soft.xpdf;

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
import java.io.IOException;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.fmv.soft.xpdf.exception.XpdfExceptionKnownAnalyzer;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public abstract class PdfSoftProvider extends SoftProvider {

	/**
	 * @param name
	 */
	public PdfSoftProvider(String name) {
		super(name);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getGroupName()
	 */
	@Override
	public String getGroupName() {
		return "pdf";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		return ExecSoftFoundFactory.withParameters("-v")
				.parseFactory((file, softPolicy) -> createParser(getSoftName(), file, softPolicy))
				.customizeExecutor(ex -> {
					ex.setExitValues(exitValues());
					ex.setTimeOut(10_000);
				})
				.build();
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		final String DEFAULT_URL = "http://www.foolabs.com/xpdf/download.html";
		if(SystemUtils.IS_OS_WINDOWS) {
			return DEFAULT_URL;
		}
		return DEFAULT_URL + " (or http://poppler.freedesktop.org)";
	}

	/**
	 * @see org.fagu.fmv.soft.xpdf.PdfSoftProvider#getSoftPolicy()
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		Version v012 = new Version(0, 12);
		BiPredicate<VersionSoftInfo, Provider> isProvider = (s, p) -> s instanceof XPdfVersionSoftInfo && ((XPdfVersionSoftInfo)s).getProvider() == p;
		return new VersionPolicy()
				.on("xpdf", s -> isProvider.test(s, Provider.XPDF))
				.minVersion(Version.V3)
				.on("poppler", s -> isProvider.test(s, Provider.POPPLER))
				.minVersion(v012)
				.onAllPlatforms()
				.minVersion(v012);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getExceptionKnownAnalyzerClass()
	 */
	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return XpdfExceptionKnownAnalyzer.class;
	}

	// ***********************************************************************

	/**
	 * @return
	 */
	protected int[] exitValues() {
		return new int[] {0, 99};
	}

	// ---------------------------------
	/**
	 * @author f.agu
	 */
	protected enum Provider {
		XPDF, POPPLER
	}

	// ---------------------------------

	/**
	 * @param provider
	 * @return
	 */
	protected VersionPolicy getVersionPolicy(Provider provider) {
		Version minVer = null;
		if(Provider.POPPLER.equals(provider)) {
			minVer = new Version(0, 12);
		} else if(Provider.XPDF.equals(provider)) {
			minVer = Version.V3;
		} else {
			throw new RuntimeException("Undefined provider: " + provider);
		}
		return new VersionPolicy().onAllPlatforms().minVersion(minVer);
	}

	// ***********************************************************************

	/**
	 * @param softName
	 * @param file
	 * @param softPolicy
	 * @return
	 */
	Parser createParser(SoftName softName, File file, SoftPolicy<?, ?, ?> softPolicy) {
		return new Parser() {

			private Pattern pattern = Pattern.compile(softName.getName() + " version ([0-9\\.]+)");

			private Version version;

			private Provider provider = Provider.XPDF;

			@Override
			public void readLine(String line) {
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()) {
					version = VersionParserManager.parse(matcher.group(1));
				} else if(line.contains("The Poppler Developers")) {
					provider = Provider.POPPLER;
				}
			}

			@Override
			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				return getVersionPolicy(provider).toSoftFound(new XPdfVersionSoftInfo(file, softName, version, provider));
			}

		};
	}

}
