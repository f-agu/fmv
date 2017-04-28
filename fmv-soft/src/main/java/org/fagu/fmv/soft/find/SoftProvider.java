package org.fagu.fmv.soft.find;

import java.io.File;

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

import java.io.FileFilter;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.Soft.SoftExecutor;
import org.fagu.fmv.soft.Soft.SoftSearch;
import org.fagu.fmv.soft.SoftName;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;


/**
 * @author f.agu
 */
public abstract class SoftProvider {

	private final String name;

	/**
	 * @param name
	 */
	public SoftProvider(String name) {
		this.name = Objects.requireNonNull(name);
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
		return StringUtils.substringBefore(grpName, "SoftProvider").toLowerCase();
	}

	// --------------------------------------

	/**
	 * @return
	 */
	public abstract SoftFoundFactory createSoftFoundFactory();

	/**
	 * @return
	 */
	public abstract SoftPolicy<?, ?, ?> getSoftPolicy();

	// --------------------------------------

	/**
	 * @param founds
	 * @return
	 */
	public Soft createSoft(Founds founds) {
		return new Soft(founds, this);
	}

	/**
	 * @return
	 */
	public Soft search() {
		return search(null);
	}

	/**
	 * @param softSearchConsumer
	 * @return
	 */
	public Soft search(Consumer<SoftSearch> softSearchConsumer) {
		SoftSearch softSearch = Soft.with(this).withLocator(getSoftLocator());
		if(softSearchConsumer != null) {
			softSearchConsumer.accept(softSearch);
		}
		return softSearch.search(createSoftFoundFactory());
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
		SoftPolicy<?, ?, ?> softPolicy = getSoftPolicy();
		return softPolicy != null ? softPolicy.toString() : "";
	}

	/**
	 * @return
	 */
	public SoftName getSoftName() {
		return new SoftName() {

			/**
			 * @see org.fagu.fmv.soft.SoftName#getName()
			 */
			@Override
			public String getName() {
				return SoftProvider.this.getName();
			}

			/**
			 * @see org.fagu.fmv.soft.SoftName#create(org.fagu.fmv.soft.find.Founds)
			 */
			@Override
			public Soft create(Founds founds) {
				return SoftProvider.this.createSoft(founds);
			}
		};
	}

	/**
	 * @return
	 */
	public SoftLocator getSoftLocator() {
		SoftPolicy<?, ?, ?> softPolicy = getSoftPolicy();
		Sorter sorter = softPolicy != null ? softPolicy.getSorter() : null;
		return new SoftLocator(getName().toUpperCase() + "_HOME", sorter);
	}

	/**
	 * @return
	 */
	public FileFilter getFileFilter() {
		return PlateformFileFilter.getFileFilter(getName());
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

}
