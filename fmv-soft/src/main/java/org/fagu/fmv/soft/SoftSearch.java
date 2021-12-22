package org.fagu.fmv.soft;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.SoftFindListener;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.SoftTester;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.utils.Proxifier;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class SoftSearch {

	private final Function<SoftPolicy, SoftProvider> softProviderSupplier;

	private SoftLocator softLocator;

	private SoftPolicy softPolicy;

	private List<SoftFindListener> softFindListeners;

	private SoftProvider softProvider;

	private Properties searchProperties;

	private Consumer<SoftLocator> softLocatorConsumer;

	private Consumer<ExecSoftFoundFactoryBuilder> builderConsumer;

	private final List<SearchListener> searchListeners;

	SoftSearch(SoftProvider softProvider) {
		this(sp -> softProvider);
	}

	SoftSearch(Function<SoftPolicy, SoftProvider> softProviderSupplier) {
		this.softProviderSupplier = Objects.requireNonNull(softProviderSupplier);
		softFindListeners = new ArrayList<>();
		searchListeners = new ArrayList<>();
	}

	public SoftSearch withPolicy(SoftPolicy softPolicy) {
		this.softPolicy = softPolicy;
		return this;
	}

	public SoftSearch withMinVersion(Version version) {
		return withPolicy(new VersionSoftPolicy().onAllPlatforms(VersionSoftPolicy.minVersion(version)));
	}

	public SoftSearch withMinVersion(int... values) {
		return withPolicy(new VersionSoftPolicy().onAllPlatforms(VersionSoftPolicy.minVersion(values)));
	}

	public SoftSearch withLocator(SoftLocator softLocator) {
		this.softLocator = softLocator;
		return this;
	}

	public SoftSearch withExecSoftFoundFactoryBuilder(Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		this.builderConsumer = builderConsumer;
		return this;
	}

	public SoftSearch withLocatorConsumer(Consumer<SoftLocator> conusmer) {
		this.softLocatorConsumer = conusmer;
		return this;
	}

	public SoftSearch addSoftFindListener(SoftFindListener softFindListener) {
		if(softFindListener != null) {
			softFindListeners.add(softFindListener);
		}
		return this;
	}

	public SoftSearch addSoftFindListeners(Collection<SoftFindListener> softFindListeners) {
		if(softFindListeners != null) {
			softFindListeners.stream()
					.filter(Objects::nonNull)
					.forEach(this.softFindListeners::add);
		}
		return this;
	}

	public SoftSearch addSearchListener(SearchListener searchListener) {
		if(searchListener != null) {
			searchListeners.add(searchListener);
		}
		return this;
	}

	public SoftSearch addSearchListeners(Collection<SearchListener> searchListeners) {
		if(searchListeners != null) {
			searchListeners.stream()
					.filter(Objects::nonNull)
					.forEach(this.searchListeners::add);
		}
		return this;
	}

	public SoftSearch withProperties(Properties searchProperties) {
		this.searchProperties = searchProperties;
		return this;
	}

	public Soft search() {
		return searchByFactory(() -> getProvider().createSoftFoundFactory(searchProperties, builderConsumer));
	}

	public Soft search(SoftFoundFactory softFoundFactory) {
		return searchByFactory(() -> softFoundFactory);
	}

	public Soft search(SoftTester softTester) {
		return searchByTester(() -> softTester);
	}

	// *****************

	private Soft searchByFactory(Supplier<SoftFoundFactory> softFoundFactorySupplier) {
		return searchByTester(() -> softTester(getLocator(), softFoundFactorySupplier.get()));
	}

	private Soft searchByTester(Supplier<SoftTester> softTesterSupplier) {
		checkUsed();
		SoftLocator locator = getLocator();
		Founds founds = locator.find(searchProperties, softTesterSupplier.get());
		return createAndfireEventFound(founds, locator);
	}

	private void checkUsed() {
		if(softProvider != null) {
			throw new IllegalStateException("Already used");
		}
	}

	private SoftProvider getProvider() {
		if(softProvider == null) {
			softProvider = softProviderSupplier.apply(softPolicy);
		}
		return softProvider;
	}

	private SoftLocator getLocator() {
		SoftLocator myLoc = softLocator;
		if(myLoc == null) {
			myLoc = getProvider().getSoftLocator();
		}
		if(softPolicy != null) {
			myLoc.setSoftPolicy(softPolicy);
		} else {
			myLoc.setSoftPolicy(getProvider().getSoftPolicy());
		}
		if(softLocatorConsumer != null) {
			softLocatorConsumer.accept(myLoc);
		}
		myLoc.addSearchListeners(searchListeners);
		return myLoc;
	}

	private Soft createAndfireEventFound(Founds founds, SoftLocator softLocator) {
		Soft soft = getProvider().createSoft(founds);

		Proxifier<SoftFindListener> proxifier = new Proxifier<>(SoftFindListener.class);
		proxifier.addAll(softFindListeners);
		ServiceLoader.load(SoftFindListener.class).forEach(proxifier::add);
		SoftFindListener softFindListener = proxifier.proxify();

		softFindListener.eventFound(softLocator, soft);
		return soft;
	}

	private SoftTester softTester(SoftLocator locator, SoftFoundFactory softFoundFactory) {
		return (file, loc, softPol) -> {
			try {
				SoftFound softFound = softFoundFactory.create(file, loc, softPol);
				if(softFound == null) {
					return SoftFound.foundBadSoft(file);
				}
				return softFound;
			} catch(IOException e) {
				return SoftFound.foundError(file, e.getMessage()).setLocalizedBy(locator.toString());
			}
		};
	}

}
