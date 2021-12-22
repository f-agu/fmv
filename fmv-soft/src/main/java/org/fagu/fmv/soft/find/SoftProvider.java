package org.fagu.fmv.soft.find;

/*-
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
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.SoftSearch;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryPrepare;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.utils.ImmutableProperties;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public abstract class SoftProvider {

	private final String name;

	private final SoftPolicy softPolicy;

	protected SoftProvider(String name, SoftPolicy softPolicy) {
		this.name = Objects.requireNonNull(name);
		this.softPolicy = SoftPolicyProvider.find(name).orElse(softPolicy);
	}

	public String getName() {
		return name;
	}

	public String getGroupName() {
		Class<? extends SoftProvider> cls = getClass();
		Class<?> superclass = cls.getSuperclass();
		String grpName = superclass != SoftProvider.class ? superclass.getSimpleName() : cls.getSimpleName();
		grpName = StringUtils.substringBefore(grpName, "SoftProvider");
		grpName = StringUtils.substringBefore(grpName, "Provider");
		return grpName.toLowerCase();
	}

	public Optional<String> getGroupTitle() {
		return Optional.empty();
	}

	// --------------------------------------

	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		return createSoftFoundFactory(searchProperties, null);
	}

	public abstract SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer);

	// --------------------------------------

	public SearchBehavior getSearchBehavior() {
		return SearchBehavior.empty();
	}

	public SoftPolicy getSoftPolicy() {
		return softPolicy;
	}

	public Soft createSoft(Founds founds) {
		return new Soft(founds, this);
	}

	public Soft search() {
		return searchConfigurable(ss -> ss.withPolicy(getSoftPolicy()));
	}

	public Soft searchMinVersion(Version minVersion) {
		return searchConfigurable(ss -> ss.withPolicy(new VersionSoftPolicy().onAllPlatforms(VersionSoftPolicy.minVersion(minVersion))));
	}

	public Soft searchConfigurable(Consumer<SoftSearch> softSearchConsumer) {
		SoftSearch softSearch = Soft.with(this);
		if(softSearchConsumer != null) {
			softSearchConsumer.accept(softSearch);
		}
		return softSearch.search(createSoftFoundFactory(ImmutableProperties.of(), null));
	}

	public String getDownloadURL() {
		return null;
	}

	public String getLogMessageIfNotFound() {
		return "Add the path in your system environment PATH";
	}

	public String getMinVersion() {
		SoftPolicy sPolicy = getSoftPolicy();
		return sPolicy != null ? sPolicy.toString() : "";
	}

	public SoftLocator getSoftLocator() {
		SoftPolicy sPolicy = getSoftPolicy();
		Sorter sorter = sPolicy != null ? sPolicy.getSorter() : null;
		SoftLocator softLocator = new SoftLocator(getName(), sorter, getFileFilter());
		softLocator.setSoftPolicy(sPolicy);
		String upperCase = getName().toUpperCase()
				.replace('-', '_')
				.replace(' ', '_');
		softLocator.setEnvName(upperCase + "_HOME");
		return softLocator;
	}

	public FileFilter getFileFilter() {
		return null;
	}

	public SoftExecutor createSoftExecutor(Soft soft, File execFile, List<String> parameters) {
		return new SoftExecutor(this, execFile, parameters);
	}

	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return null;
	}

	@Override
	public String toString() {
		return "SoftProvider[" + name + "]";
	}

	// ************************************************

	public static Stream<SoftProvider> getSoftProviders() {
		return StreamSupport.stream(ServiceLoader.load(SoftProvider.class).spliterator(), false);
	}

	protected static ExecSoftFoundFactoryBuilder prepareBuilder(ExecSoftFoundFactoryBuilder builder,
			Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		if(builderConsumer != null) {
			builderConsumer.accept(builder);
		}
		return builder;
	}

	protected static Stream<File> streamInFolder(File parentFolder, String expectedName) {
		return streamInFolder(parentFolder, f -> f.getName().equalsIgnoreCase(expectedName));
	}

	protected static Stream<File> streamInFolderStartsWith(File parentFolder, String startsWith) {
		final String lower = startsWith.toLowerCase();
		return streamInFolder(parentFolder, f -> f.getName().toLowerCase().startsWith(lower));
	}

	protected static Stream<File> streamInFolder(File parentFolder, FileFilter folderFilter) {
		File[] files = parentFolder.listFiles(folderFilter);
		if(files == null || files.length == 0) {
			return Stream.empty();
		}
		return Arrays.stream(files);
	}

	// ************************************************

	protected ExecSoftFoundFactoryPrepare prepareSoftFoundFactory() {
		return ExecSoftFoundFactory.forProvider(this);
	}

}
