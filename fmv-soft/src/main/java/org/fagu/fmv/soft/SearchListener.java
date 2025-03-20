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

import java.util.Collection;
import java.util.Properties;

import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftTester;


/**
 * @author f.agu
 * @created 3 avr. 2020 15:05:10
 */
public interface SearchListener {

	default void eventPreSearch(String softName, Collection<Locator> locators, SoftTester tester, Properties searchProperties) {}

	default void eventStartLocator(String softName, Locator locator, SoftTester tester, Properties searchProperties) {}

	default void eventAddSoftFound(String softName, Locator locator, SoftFound softFound) {}
}
