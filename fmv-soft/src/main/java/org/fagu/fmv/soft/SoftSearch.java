package org.fagu.fmv.soft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Function;

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

	SoftSearch(SoftProvider softProvider) {
		this(sp -> softProvider);
	}

	SoftSearch(Function<SoftPolicy, SoftProvider> softProviderSupplier) {
		this.softProviderSupplier = Objects.requireNonNull(softProviderSupplier);
		softFindListeners = new ArrayList<>();
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

	public SoftSearch withListener(SoftFindListener softFindListener) {
		if(softFindListener != null) {
			softFindListeners.add(softFindListener);
		}
		return this;
	}

	public SoftSearch withListeners(Collection<SoftFindListener> softFindListeners) {
		if(softFindListeners != null) {
			softFindListeners.stream()
					.filter(Objects::nonNull)
					.forEach(this.softFindListeners::add);
		}
		return this;
	}

	public SoftSearch withProperties(Properties searchProperties) {
		this.searchProperties = searchProperties;
		return this;
	}

	public Soft search() {
		SoftLocator locator = getLocator();
		Founds founds = locator.find(searchProperties);
		return createAndfireEventFound(founds, locator);
	}

	public Soft search(SoftTester softTester) {
		checkUsed();
		SoftLocator locator = getLocator();
		Founds founds = locator.find(searchProperties, softTester);
		return createAndfireEventFound(founds, locator);
	}

	public Soft search(SoftFoundFactory softFoundFactory) {
		checkUsed();
		SoftLocator locator = getLocator();
		Founds founds = locator.find(searchProperties, (file, loc, softPol) -> {
			try {
				SoftFound softFound = softFoundFactory.create(file, loc, softPol);
				if(softFound == null) {
					return SoftFound.foundBadSoft(file);
				}
				return softFound;
			} catch(IOException e) {
				return SoftFound.foundError(file, e.getMessage()).setLocalizedBy(locator.toString());
			}
		});
		return createAndfireEventFound(founds, locator);
	}

	// *****************

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

}
