package org.fagu.fmv.soft;

import java.util.Collection;
import java.util.Properties;

import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftTester;


/**
 * @author Oodrive
 * @author f.agu
 * @created 3 avr. 2020 15:05:10
 */
public interface SearchListener {

	default void eventPreSearch(Collection<Locator> locators, SoftTester tester, Properties searchProperties) {}

	default void eventAddSoftFound(Locator locator, SoftFound softFound) {}
}
