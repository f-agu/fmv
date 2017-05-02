package org.fagu.fmv.soft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;

import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.SoftFindListener;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.SoftTester;
import org.fagu.fmv.soft.utils.Proxifier;

/**
 * @author f.agu
 */
public class SoftSearch {

	private final SoftProvider softProvider;

	private final SoftLocator softLocator;

	private SoftPolicy<?, ?, ?> softPolicy;

	private List<SoftFindListener> softFindListeners;

	/**
	 * @param softProvider
	 */
	SoftSearch(SoftProvider softProvider) {
		this(softProvider, null);
	}

	/**
	 * @param softProvider
	 * @param softLocator
	 */
	SoftSearch(SoftProvider softProvider, SoftLocator softLocator) {
		this.softProvider = Objects.requireNonNull(softProvider);
		this.softLocator = softLocator;
		softFindListeners = new ArrayList<>();
	}

	/**
	 * @param softPolicy
	 * @return
	 */
	public SoftSearch withPolicy(SoftPolicy<?, ?, ?> softPolicy) {
		this.softPolicy = softPolicy;
		return this;
	}

	/**
	 * @param softFindListener
	 * @return
	 */
	public SoftSearch withListener(SoftFindListener softFindListener) {
		if(softFindListener != null) {
			softFindListeners.add(softFindListener);
		}
		return this;
	}

	/**
	 * @param softFindListeners
	 * @return
	 */
	public SoftSearch withListeners(Collection<SoftFindListener> softFindListeners) {
		if(softFindListeners != null) {
			softFindListeners.stream()
					.filter(Objects::nonNull)
					.forEach(this.softFindListeners::add);
		}
		return this;
	}

	/**
	 * @return
	 */
	public Soft search() {
		SoftLocator locator = getLocator();
		Founds founds = locator.find();
		return createAndfireEventFound(founds, locator);
	}

	/**
	 * @param softTester
	 * @return
	 */
	public Soft search(SoftTester softTester) {
		SoftLocator locator = getLocator();
		Founds founds = locator.find(softTester);
		return createAndfireEventFound(founds, locator);
	}

	/**
	 * @param softFoundFactory
	 * @return
	 */
	public Soft search(SoftFoundFactory softFoundFactory) {
		SoftLocator locator = getLocator();
		Founds founds = locator.find((file, loc, softPolicy) -> {
			try {
				SoftFound softFound = softFoundFactory.create(file, loc, softPolicy);
				if(softFound == null) {
					return SoftFound.foundBadSoft(file);
				}
				return softFound;
			} catch(ExecutionException e) {
				return SoftFound.foundError(file, e.getMessage()).setLocalizedBy(locator.toString());
			} catch(IOException e) {
				return SoftFound.foundError(file, e.getMessage()).setLocalizedBy(locator.toString());
			}
		});
		return createAndfireEventFound(founds, locator);
	}

	// *****************

	/**
	 * 
	 */
	private SoftLocator getLocator() {
		SoftLocator myLoc = softLocator;
		if(myLoc == null) {
			myLoc = softProvider.getSoftLocator();
		}
		if(softPolicy != null) {
			myLoc.setSoftPolicy(softPolicy);
		}
		return myLoc;
	}

	/**
	 * @param founds
	 * @param softLocator
	 * @return
	 */
	private Soft createAndfireEventFound(Founds founds, SoftLocator softLocator) {
		Soft soft = softProvider.createSoft(founds);

		Proxifier<SoftFindListener> proxifier = new Proxifier<>(SoftFindListener.class);
		proxifier.addAll(softFindListeners);
		ServiceLoader.load(SoftFindListener.class).forEach(proxifier::add);
		SoftFindListener softFindListener = proxifier.proxify();

		softFindListener.eventFound(softLocator, soft);
		return soft;
	}

}