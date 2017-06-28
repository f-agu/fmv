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
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.SoftExecutor.Executed;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.PlateformFileFilter;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.utils.order.OrderComparator;


/**
 * @author f.agu
 */
public class Soft {

	private static final Map<String, Soft> SOFT_NAME_CACHE = new HashMap<>();

	private final Founds founds;

	private final SoftProvider softProvider;

	public Soft(Founds founds, SoftProvider softProvider) {
		this.founds = Objects.requireNonNull(founds);
		this.softProvider = Objects.requireNonNull(softProvider);
	}

	// =============

	/**
	 * @param execFile
	 * @return
	 * @throws IOException
	 */
	public static Soft withExecFile(String execFile) throws IOException {
		File file = new File(execFile);
		if( ! execFile.contains("/") && ! execFile.contains("\\") && ! file.exists()) {
			// search in ENV PATH
			Locators locators = new Locators(PlateformFileFilter.getFileFilter(execFile));
			Locator locator = locators.byEnvPath();
			List<File> locatedFiles = locator.locate(null);
			if(locatedFiles.isEmpty()) {
				throw new FileNotFoundException(execFile);
			}
			Collections.sort(locatedFiles);
			file = locatedFiles.get(0);
		}
		return withExecFile(file);
	}

	/**
	 * @param softName
	 * @return
	 */
	public static Soft withExecFile(File file) throws IOException {
		if( ! file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		if( ! file.isFile()) {
			throw new IOException("It's not a file: " + file.getAbsolutePath());
		}
		Path path = file.toPath();
		if( ! Files.isExecutable(path)) {
			throw new IOException("Cannot execute: " + file.getAbsolutePath());
		}
		SoftProvider softProvider = new SoftProvider(file.getName()) {

			@Override
			public SoftPolicy getSoftPolicy() {
				return null;
			}

			@Override
			public SoftFoundFactory createSoftFoundFactory() {
				throw new RuntimeException("Not available !");
			}
		};
		TreeSet<SoftFound> founds = new TreeSet<>(Collections.singleton(SoftFound.found(file)));
		return new Soft(new Founds(softProvider.getName(), founds, null), softProvider);
	}

	/**
	 * @param name
	 * @return
	 */
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

	/**
	 * @param name
	 * @return
	 */
	public static Soft search(SoftProvider softProvider) {
		Soft soft = SOFT_NAME_CACHE.get(softProvider.getName());
		if(soft != null) {
			return soft;
		}
		soft = softProvider.search();
		SOFT_NAME_CACHE.put(softProvider.getName(), soft);
		return soft;
	}

	/**
	 * @return
	 */
	public static Stream<Soft> searchAll() {
		return SoftProvider.getSoftProviders().map(SoftProvider::search);
	}

	/**
	 * @param softSearchConsumer
	 * @return
	 */
	public static Stream<Soft> searchAll(Consumer<SoftSearch> softSearchConsumer) {
		return SoftProvider.getSoftProviders().map(sp -> sp.searchConfigurable(softSearchConsumer));
	}

	/**
	 * @param softProvider
	 * @return
	 */
	public static SoftSearch with(SoftProvider softProvider) {
		return new SoftSearch(softProvider);
	}

	/**
	 * @param softProvider
	 * @param softLocator
	 * @return
	 */
	public static SoftSearch with(SoftProvider softProvider, SoftLocator softLocator) {
		return new SoftSearch(softProvider, softLocator);
	}

	// =============

	/**
	 * @return
	 */
	public String getName() {
		return founds.getSoftName();
	}

	/**
	 * @return
	 */
	public boolean isFound() {
		return founds.isFound();
	}

	/**
	 * @return
	 */
	public Founds getFounds() {
		return founds;
	}

	/**
	 * @return
	 */
	public SoftFound getFirstFound() {
		if( ! founds.isFound()) {
			throw new IllegalStateException("Soft " + getName() + " not found");
		}
		return founds.getFirstFound();
	}

	/**
	 * @return
	 */
	public SoftInfo getFirstInfo() {
		return getFirstFound().getSoftInfo();
	}

	/**
	 * @return
	 */
	public File getFile() {
		return getFirstFound().getFile();
	}

	/**
	 * @return
	 */
	public SoftExecutor withoutParameter() {
		return withParameters(Collections.emptyList());
	}

	/**
	 * @param param1
	 * @param otherPparameters
	 * @return
	 */
	public SoftExecutor withParameters(String param1, String... otherPparameters) {
		List<String> params = new ArrayList<>(1 + otherPparameters.length);
		params.add(param1);
		params.addAll(Arrays.asList(otherPparameters));
		return withParameters(params);
	}

	/**
	 * @param parameters
	 * @return
	 */
	public SoftExecutor withParameters(List<String> parameters) {
		return softProvider.createSoftExecutor(this, getFile(), parameters);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Executed execute() throws IOException {
		return withoutParameter().execute();
	}

	/**
	 * @return
	 */
	public SoftProvider getSoftProvider() {
		return softProvider;
	}

	/**
	 * @return
	 */
	public SoftPolicy getSoftPolicy() {
		SoftPolicy softPolicy = founds.getSoftPolicy();
		return softPolicy != null ? softPolicy : softProvider.getSoftPolicy();
	}

	/**
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getName());
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
			buf.append(" <not found>");
		}
		return buf.toString();
	}

}
