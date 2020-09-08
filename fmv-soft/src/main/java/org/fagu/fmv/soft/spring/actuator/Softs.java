package org.fagu.fmv.soft.spring.actuator;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.fagu.fmv.soft.Soft;


/**
 * @author f.agu
 */
public class Softs {

	private static final Map<String, Soft> infoContributors = new HashMap<>();

	private static final Map<String, Soft> healthIndicators = new HashMap<>();

	private Softs() {}

	public static void contributeInfo(Soft soft) {
		if(soft != null) {
			infoContributors.put(soft.getName(), soft);
		}
	}

	public static void indicateHealth(Soft soft) {
		if(soft != null) {
			healthIndicators.put(soft.getName(), soft);
		}
	}

	public static Collection<Soft> getInfoContributors() {
		return Collections.unmodifiableCollection(infoContributors.values());
	}

	public static Collection<Soft> getHealthIndicators() {
		return Collections.unmodifiableCollection(healthIndicators.values());
	}

}
