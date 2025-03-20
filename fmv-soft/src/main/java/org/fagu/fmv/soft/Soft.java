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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.SoftExecutor.Executed;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.FoundReason;
import org.fagu.fmv.soft.find.FoundReasons;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.PlateformFileFilter;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.fmv.utils.order.OrderComparator;


/**
 * @author f.agu
 */
public class Soft {

	private static final Map<String, Soft> SOFT_NAME_CACHE = new HashMap<>();

	private final Founds founds;

	private final SoftProvider softProvider;

	private final Properties searchProperties;

	public Soft(Founds founds, SoftProvider softProvider) {
		this.founds = Objects.requireNonNull(founds);
		this.searchProperties = ImmutableProperties.copyOf(founds.getSearchProperties());
		this.softProvider = Objects.requireNonNull(softProvider);
	}

	// --------------------------------------------------

	public static class SoftExecFileBuilder {

		private final File execFile;

		private List<Consumer<SoftLocator>> softLocatorConsumers = new ArrayList<>();

		private Properties searchProperties;

		private String url;

		private String logMessage;

		private Consumer<String> logger;

		private SoftExecFileBuilder(File execFile) throws IOException {
			this.execFile = Objects.requireNonNull(execFile);
			checkExecFile(execFile);
		}

		public SoftExecFileBuilder addSoftLocator(Consumer<SoftLocator> softLocatorConsumer) {
			if(softLocatorConsumer != null) {
				this.softLocatorConsumers.add(softLocatorConsumer);
			}
			return this;
		}

		public SoftExecFileBuilder withSearchProperties(Properties searchProperties) {
			this.searchProperties = searchProperties;
			return this;
		}

		public SoftExecFileBuilder withUrl(String url) {
			this.url = url;
			return this;
		}

		public SoftExecFileBuilder withLogMessage(String logMessage) {
			this.logMessage = logMessage;
			return this;
		}

		public SoftExecFileBuilder withLogger(Consumer<String> logger) {
			this.logger = logger;
			return this;
		}

		public SoftExecFile build() {
			return new SoftExecFile(this);
		}

		// ***********************************************************

		private static void checkExecFile(File execFile) throws IOException {
			if( ! execFile.isAbsolute()) {
				return;
			}
			if( ! execFile.exists()) {
				throw new FileNotFoundException(execFile.getAbsolutePath());
			}
			if( ! execFile.isFile()) {
				throw new IOException("It's not a file: " + execFile.getAbsolutePath());
			}
			Path path = execFile.toPath();
			if( ! Files.isExecutable(path)) {
				throw new IOException("Cannot execute: " + execFile.getAbsolutePath());
			}
		}

	}

	// --------------------------------------------------

	public static class SoftExecFile {

		private final File execFile;

		private final List<Consumer<SoftLocator>> softLocatorConsumers;

		private final Properties searchProperties;

		private final String url;

		private final String logMessage;

		private final Consumer<String> logger;

		private SoftExecFile(SoftExecFileBuilder builder) {
			this.execFile = builder.execFile;
			this.softLocatorConsumers = Collections.unmodifiableList(new ArrayList<>(builder.softLocatorConsumers));
			this.searchProperties = builder.searchProperties;
			this.url = builder.url;
			this.logMessage = builder.logMessage;
			this.logger = builder.logger;
		}

		public String getSoftName() {
			return FilenameUtils.getBaseName(execFile.getName());
		}

		public Founds getFounds() {
			String softName = getSoftName();
			SoftLocator softLocator = new SoftLocator(
					FilenameUtils.getBaseName(softName),
					null,
					PlateformFileFilter.plateformAndBasename(softName));
			softLocator.addDefaultLocator();
			softLocatorConsumers.forEach(slc -> slc.accept(softLocator));
			return softLocator.find(searchProperties);
		}

		public Soft getSoft() {
			Founds founds = getFounds();
			SoftFile softFile = new SoftFile("exec");
			if( ! founds.isFound()) {
				return new Soft(
						founds,
						softFile.softProvider(founds.getSoftName(), url, logMessage, null));
			}
			return softFile.with(
					founds.getSoftName(),
					url,
					logMessage,
					founds.getFirstFound().getFile(),
					logger);
		}

	}

	// --------------------------------------------------

	public static SoftExecFileBuilder withExecFileBuilder(String execFile) throws IOException {
		File file = new File(execFile);
		if( ! execFile.contains("/") && ! execFile.contains("\\") && ! file.exists()) {
			return new SoftExecFileBuilder(file);
		}
		throw new FileNotFoundException(file.getAbsolutePath());
	}

	public static SoftExecFileBuilder withExecFileBuilder(File execFile) throws IOException {
		return new SoftExecFileBuilder(execFile);
	}

	public static Soft withExecFile(String execFile) throws IOException {
		return withExecFileBuilder(execFile).build().getSoft();
	}

	public static Soft withExecFile(File file) throws IOException {
		return withExecFileBuilder(file).build().getSoft();
	}

	public static Soft search(String name) {
		Soft soft = SOFT_NAME_CACHE.get(name);
		if(soft != null) {
			return soft;
		}
		soft = SoftProvider.getSoftProviders()
				.filter(sp -> sp.getName().equalsIgnoreCase(name))
				.sorted(Collections.reverseOrder(OrderComparator.INSTANCE))
				.map(SoftProvider::search)
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Soft \"" + name + "\" undefined"));
		SOFT_NAME_CACHE.put(name, soft);
		return soft;
	}

	public static Soft search(SoftProvider softProvider) {
		Soft soft = SOFT_NAME_CACHE.get(softProvider.getName());
		if(soft != null) {
			return soft;
		}
		soft = softProvider.search();
		SOFT_NAME_CACHE.put(softProvider.getName(), soft);
		return soft;
	}

	public static Stream<Soft> searchAll() {
		return SoftProvider.getSoftProviders().map(SoftProvider::search);
	}

	public static Stream<Soft> searchAll(Consumer<SoftSearch> softSearchConsumer) {
		return SoftProvider.getSoftProviders().map(sp -> sp.searchConfigurable(softSearchConsumer));
	}

	public static SoftSearch with(SoftProvider softProvider) {
		return new SoftSearch(softProvider);
	}

	public static SoftSearch with(Function<SoftPolicy, SoftProvider> softProviderSupplier) {
		return new SoftSearch(softProviderSupplier);
	}

	// =============

	public String getName() {
		return founds.getSoftName();
	}

	public boolean isFound() {
		return founds.isFound();
	}

	public Founds getFounds() {
		return founds;
	}

	public SoftFound getFirstFound() {
		if( ! founds.isFound()) {
			// throw new IllegalStateException("Soft " + getName() + " not found");
			return null;
		}
		return founds.getFirstFound();
	}

	public SoftInfo getFirstInfo() {
		SoftFound firstFound = getFirstFound();
		return firstFound != null ? firstFound.getSoftInfo() : null;
	}

	public File getFile() {
		SoftFound firstFound = getFirstFound();
		return firstFound != null ? firstFound.getFile() : null;
	}

	public SoftFound reFind() throws IOException {
		return reFind(null);
	}

	public SoftFound reFind(Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) throws IOException {
		SoftFoundFactory softFoundFactory = getSoftProvider().createSoftFoundFactory(searchProperties, builderConsumer);
		return softFoundFactory.create(getFile(), null, getSoftPolicy());
	}

	public SoftExecutor withoutParameter() {
		return withParameters(Collections.emptyList());
	}

	public SoftExecutor withParameters(String param1, String... otherParameters) {
		List<String> params = new ArrayList<>(1 + otherParameters.length);
		params.add(param1);
		params.addAll(Arrays.asList(otherParameters));
		return withParameters(params);
	}

	public SoftExecutor withParameters(List<String> parameters) {
		return softProvider.createSoftExecutor(this, getFile(), parameters);
	}

	public Executed execute() throws IOException {
		return withoutParameter().execute();
	}

	public SoftProvider getSoftProvider() {
		return softProvider;
	}

	public SoftPolicy getSoftPolicy() {
		SoftPolicy softPolicy = founds.getSoftPolicy();
		return softPolicy != null ? softPolicy : softProvider.getSoftPolicy();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder()
				.append(getName());
		if(getFounds().isFound()) {
			SoftInfo softInfo = getFirstInfo();
			if(softInfo != null) {
				String info = softInfo.getInfo();
				if(StringUtils.isNotBlank(info)) {
					buf.append(' ').append(info);
				}
			}
			buf.append(" (").append(getFile().getAbsolutePath()).append(')');
		} else {
			NavigableSet<SoftFound> foundSet = getFounds().getFounds();
			if(foundSet.isEmpty()) {
				buf.append(" <unknown>");
			} else {
				buf.append(foundSet.stream()
						.map(Soft::getReasonText)
						.collect(Collectors.joining(" ; ", " <", ">")));
			}
		}
		return buf.toString();
	}

	// ***********************************************

	private static String getReasonText(SoftFound found) {
		FoundReason foundReason = found.getFoundReason();
		StringBuilder reasonBuilder = new StringBuilder(found.getFoundReason().name().replace('_', ' ').toLowerCase());
		String reason = found.getReason();
		if(foundReason == FoundReasons.BAD_VERSION) {
			if(StringUtils.isNotBlank(reason)) {
				reasonBuilder.append(" (").append(found.getFile().getAbsolutePath()).append(')')
						.append(": I need at least ").append(reason);
			}
		} else if(StringUtils.isNotBlank(reason)) {
			// FoundReasons.BAD_SOFT
			// FoundReasons.ERROR
			// FoundReasons.NOT_FOUND
			reasonBuilder.append(": ").append(reason);
		}
		return reasonBuilder.toString();
	}

}
