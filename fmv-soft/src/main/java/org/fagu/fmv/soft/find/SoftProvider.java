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
import java.util.List;
import java.util.Objects;
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

	/**
	 * @param name
	 * @param softPolicy
	 */
	public SoftProvider(String name, SoftPolicy softPolicy) {
		this.name = Objects.requireNonNull(name);
		this.softPolicy = softPolicy;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getGroupName() {
		Class<? extends SoftProvider> cls = getClass();
		Class<?> superclass = cls.getSuperclass();
		String grpName = superclass != SoftProvider.class ? superclass.getSimpleName() : cls.getSimpleName();
		grpName = StringUtils.substringBefore(grpName, "SoftProvider");
		grpName = StringUtils.substringBefore(grpName, "Provider");
		return grpName.toLowerCase();
	}

	// --------------------------------------

	/**
	 * @param searchProperties
	 * @return
	 */
	public abstract SoftFoundFactory createSoftFoundFactory(Properties searchProperties);

	// --------------------------------------

	/**
	 * @return
	 */
	public SoftPolicy getSoftPolicy() {
		return softPolicy;
	}

	/**
	 * @param founds
	 * @return
	 */
	public Soft createSoft(Founds founds) {
		return new Soft(founds, this);
	}

	/**
	 * @param searchProperties
	 * @return
	 */
	public Soft search() {
		return searchConfigurable(ss -> ss.withPolicy(getSoftPolicy()));
	}

	/**
	 * @param minVersion
	 * @param searchProperties
	 * @return
	 */
	public Soft searchMinVersion(Version minVersion) {
		return searchConfigurable(ss -> ss.withPolicy(new VersionSoftPolicy().onAllPlatforms(VersionSoftPolicy.minVersion(minVersion))));
	}

	/**
	 * @param softSearchConsumer
	 * @return
	 */
	public Soft searchConfigurable(Consumer<SoftSearch> softSearchConsumer) {
		SoftSearch softSearch = Soft.with(this);
		if(softSearchConsumer != null) {
			softSearchConsumer.accept(softSearch);
		}
		return softSearch.search(createSoftFoundFactory(ImmutableProperties.of()));
	}

	/**
	 * @return
	 */
	public String getDownloadURL() {
		return null;
	}

	/**
	 * @return
	 */
	public String getMinVersion() {
		SoftPolicy sPolicy = getSoftPolicy();
		return sPolicy != null ? sPolicy.toString() : "";
	}

	/**
	 * @return
	 */
	public SoftLocator getSoftLocator() {
		SoftPolicy sPolicy = getSoftPolicy();
		Sorter sorter = sPolicy != null ? sPolicy.getSorter() : null;
		SoftLocator softLocator = new SoftLocator(getName(), sorter, getFileFilter());
		softLocator.setSoftPolicy(sPolicy);
		softLocator.setEnvName(getName().toUpperCase() + "_HOME");
		return softLocator;
	}

	/**
	 * @return
	 */
	public FileFilter getFileFilter() {
		return null;
	}

	/**
	 * @param soft
	 * @param execFile
	 * @param parameters
	 * @return
	 */
	public SoftExecutor createSoftExecutor(Soft soft, File execFile, List<String> parameters) {
		return new SoftExecutor(this, execFile, parameters);
	}

	/**
	 * @return
	 */
	public Class<? extends ExceptionKnownAnalyzer> getExceptionKnownAnalyzerClass() {
		return null;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SoftProvider[" + name + "]";
	}

	// ************************************************

	/**
	 * @return
	 */
	public static Stream<SoftProvider> getSoftProviders() {
		return StreamSupport.stream(ServiceLoader.load(SoftProvider.class).spliterator(), false);
	}

	// ************************************************

	/**
	 * @return
	 */
	protected ExecSoftFoundFactoryPrepare prepareSoftFoundFactory() {
		return ExecSoftFoundFactory.forProvider(this);
	}

}
