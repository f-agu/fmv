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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.Parser;
import org.fagu.fmv.soft.find.SearchBehavior;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.fmv.soft.xpdf.exception.XpdfExceptionKnownAnalyzer;
import org.fagu.fmv.utils.PlaceHolder;
import org.fagu.fmv.utils.Replacers;
import org.fagu.version.Version;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public abstract class PdfSoftProvider extends SoftProvider {

	private static final String DEFAULT_PATTERN_VERSION = "${soft.name} version ([0-9\\\\.]+) ?(?:.*)";

	protected PdfSoftProvider(String name) {
		this(name, null);
	}

	protected PdfSoftProvider(String name, SoftPolicy softPolicy) {
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
	public SearchBehavior getSearchBehavior() {
		return SearchBehavior.onlyVersion();
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		final Pattern pattern = new SearchPropertiesHelper(searchProperties, this)
				.toPatternVersion(DEFAULT_PATTERN_VERSION);
		return prepareBuilder(
				prepareSoftFoundFactory().withParameters("-v"),
				builderConsumer)
						.parseFactory((file, softPolicy) -> createParser(file, pattern))
						.exitValues(exitValues())
						.timeOut(10_000)
						.build();
	}

	@Override
	public String getDownloadURL() {
		return "https://www.xpdfreader.com/download.html, https://blog.alivate.com.au/tag/pdftotext/, https://poppler.freedesktop.org";
	}

	@Override
	public SoftExecutor createSoftExecutor(Soft soft, File execFile, List<String> parameters) {
		XPdfVersionSoftInfo softInfo = (XPdfVersionSoftInfo)soft.getFirstInfo();

		// On Windows & Eclipse, file.encoding=UTF-8 -> fix encoding
		// On Windows & DOS, file.encoding=cp1252 -> do nothing
		if(hasEncParameter()
				&& softInfo != null
				&& Provider.XPDF.equals(softInfo.getProvider())
				&& SystemUtils.IS_OS_WINDOWS
				&& "UTF-8".equals(System.getProperty("file.encoding"))) {
			List<String> newParams = new ArrayList<>(parameters);
			if( ! parameters.contains("-enc")) {
				newParams.add(0, "-enc");
				newParams.add(1, "UTF-8");
			}
			return new SoftExecutor(this, execFile, newParams)
					.charset(StandardCharsets.UTF_8);
		}

		return super.createSoftExecutor(soft, execFile, parameters);
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			final String arch = "bin" + System.getProperty("sun.arch.data.model");
			ProgramFilesLocatorSupplier.with(softLocator)
					.find(programFile -> {
						List<File> files = new ArrayList<>();

						// poppler
						streamInFolderStartsWith(programFile, "poppler")
								.map(f -> new File(f, "bin"))
								.forEach(files::add);

						// xpdf
						streamInFolderStartsWith(programFile, "xpdf")
								.map(f -> new File(f, arch))
								.forEach(files::add);

						return files;
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

	protected boolean hasEncParameter() {
		return false;
	}

	// ---------------------------------

	public enum Provider {

		POPPLER() {

			@Override
			public List<String> getDefaultOptionParameters() {
				if(SystemUtils.IS_OS_WINDOWS) {
					return Arrays.asList("-enc", "UTF-8");
				}
				return Collections.emptyList();
			}
		},
		XPDF() {

			@Override
			public List<String> getDefaultOptionParameters() {
				if(SystemUtils.IS_OS_WINDOWS) {
					return Arrays.asList("-enc", "UTF-8");
				}
				return Collections.emptyList();
			}
		};

		public abstract List<String> getDefaultOptionParameters();
	}

	// ---------------------------------

	protected SoftPolicy getVersionPolicy(Provider provider) {
		Version minVer = null;
		if(Provider.POPPLER.equals(provider)) {
			minVer = new Version(0, 12);
		} else if(Provider.XPDF.equals(provider)) {
			minVer = Version.V4;
		} else {
			throw new RuntimeException("Undefined provider: " + provider);
		}
		return new VersionSoftPolicy().onAllPlatforms(minVersion(minVer));
	}

	// ***********************************************************************

	Parser createParser(File file) {
		String pattern = PlaceHolder.format(
				DEFAULT_PATTERN_VERSION,
				Replacers.chain()
						.keyValue("soft.name", getName()));
		return createParser(file, Pattern.compile(pattern));
	}

	Parser createParser(File file, Pattern pattern) {
		return new Parser() {

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
				.on("xpdf", s -> isProvider.test(s, Provider.XPDF), minVersion(Version.V4))
				.on("poppler", s -> isProvider.test(s, Provider.POPPLER), minVersion(v012))
				.onAllPlatforms(minVersion(v012));
	}
}
