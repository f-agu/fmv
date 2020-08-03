package org.fagu.fmv.soft.find;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Oodrive
 * @author f.agu
 * @created 3 août 2020 14:45:18
 */
public class MultiSoftFoundFactory implements SoftFoundFactory {

	private final List<SoftFoundFactory> factories;

	public MultiSoftFoundFactory(List<SoftFoundFactory> factories) {
		this.factories = Collections.unmodifiableList(new ArrayList<>(factories));
	}

	@Override
	public SoftFound create(File file, Locator locator, SoftPolicy softPolicy) throws IOException {
		SoftFound softFound;
		for(SoftFoundFactory factory : factories) {
			softFound = factory.create(file, locator, softPolicy);
			if(softFound.isFound()) {
				return softFound;
			}
		}
		return SoftFound.notFound();
	}

}
