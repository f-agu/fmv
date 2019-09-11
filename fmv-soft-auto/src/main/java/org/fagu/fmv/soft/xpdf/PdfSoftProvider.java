package org.fagu.fmv.soft.xpdf;

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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.fmv.soft.xpdf.exception.XpdfExceptionKnownAnalyzer;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public abstract class PdfSoftProvider extends SoftProvider {

	public PdfSoftProvider(String name) {
		this(name, null);
	}

	public PdfSoftProvider(String name, SoftPolicy softPolicy) {
		super(name, ObjectUtils.firstNonNull(softPolicy, getDefaultSoftPolicy()));
	}

	@Override
	public String getGroupName() {
		return "pdf";
	}

	@Override
	public Optional<String> getGroupTitle() {
		return Optional.of("Xpdf");
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		return prepareSoftFoundFactory()
				.withParameters("-v")
				.parseFactory((file, softPolicy) -> createParser(file))
				.exitValues(exitValues())
				.timeOut(10_000)
				.build();
	}

	@Override
	public String getDownloadURL() {
		final String DEFAULT_URL = "http://www.foolabs.com/xpdf/download.html";
		if(SystemUtils.IS_OS_WINDOWS) {
			return DEFAULT_URL;
		}
		return DEFAULT_URL + " (or http://poppler.freedesktop.org)";
	}

	@Override
	public SoftExecutor createSoftExecutor(Soft soft, File execFile, List<String> parameters) {
		XPdfVersionSoftInfo softInfo = (XPdfVersionSoftInfo)soft.getFirstInfo();

		// On Windows & Eclipse, file.encoding=UTF-8 -> fix encoding
		// On Windows & DOS, file.encoding=cp1252 -> do nothing
		if(softInfo != null && Provider.XPDF.equals(softInfo.getProvider()) && SystemUtils.IS_OS_WINDOWS && "UTF-8".equals(System
				.getProperty("file.encoding"))) {
			List<String> newParams = new ArrayList<>(parameters);
			if( ! parameters.contains("-enc")) {
				newParams.add(0, "-enc");
				newParams.add(1, "UTF-8");
			}
			SoftExecutor softExecutor = new SoftExecutor(this, execFile, newParams);
			softExecutor.charset(StandardCharsets.UTF_8);
			// depends on minimal version ? I don't know
			// Version minVer = new Version(3, 4);
			// softInfo.getVersion().ifPresent(v -> {
			// if(v.isUpperOrEqualsThan(minVer)) {
			// softExecutor.charset(StandardCharsets.UTF_8);
			// }
			// });
			return softExecutor;
		}

		return super.createSoftExecutor(soft, execFile, parameters);
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.find(programFile -> {
						File[] listFiles = programFile.listFiles(f -> f.getName().startsWith("xpdf"));
						if(listFiles == null || listFiles.length == 0) {
							return Collections.emptyList();
						}
						String arch = "bin" + System.getProperty("sun.arch.data.model");
						return Arrays.asList(listFiles).stream()
								.map(f -> new File(f, arch))
								.collect(Collectors.toList());
					})
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;
	}

	@Override
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return XpdfExceptionKnownAnalyzer.class;
	}

	// ***********************************************************************

	protected int[] exitValues() {
		return new int[] {0, 99};
	}

	// ---------------------------------

	protected enum Provider {
		XPDF, POPPLER
	}

	// ---------------------------------

	protected SoftPolicy getVersionPolicy(Provider provider) {
		Version minVer = null;
		if(Provider.POPPLER.equals(provider)) {
			minVer = new Version(0, 12);
		} else if(Provider.XPDF.equals(provider)) {
			minVer = Version.V3;
		} else {
			throw new RuntimeException("Undefined provider: " + provider);
		}
		return new VersionSoftPolicy().onAllPlatforms(minVersion(minVer));
	}

	// ***********************************************************************

	Parser createParser(File file) {
		return new Parser() {

			private Pattern pattern = Pattern.compile(getName() + " version ([0-9\\.]+)");

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
				return getVersionPolicy(provider).toSoftFound(new XPdfVersionSoftInfo(file, getName(), version, provider));
			}

		};
	}

	// ***********************************************************************

	private static SoftPolicy getDefaultSoftPolicy() {
		Version v012 = new Version(0, 12);
		BiPredicate<SoftInfo, Provider> isProvider = (s, p) -> s instanceof XPdfVersionSoftInfo && ((XPdfVersionSoftInfo)s).getProvider() == p;
		return new VersionSoftPolicy()
				.on("xpdf", s -> isProvider.test(s, Provider.XPDF), minVersion(Version.V3))
				.on("poppler", s -> isProvider.test(s, Provider.POPPLER), minVersion(v012))
				.onAllPlatforms(minVersion(v012));
	}
}
