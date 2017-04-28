package org.fagu.fmv.soft;

import java.util.Collections;

import org.fagu.fmv.soft.find.SoftFindListener;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.fmv.soft.gs.GSSoftProvider;
import org.fagu.version.Version;
import org.junit.Ignore;
/*-
 * #%L
 * fmv-soft-auto
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
import org.junit.Test;


/**
 * @author f.agu
 */
public class SoftTestCase {

	/**
	 * 
	 */
	public SoftTestCase() {}

	@Test
	@Ignore
	public void testFindAll() throws Exception {
		Soft.searchAll(ss -> ss.withListener(new SoftFindListener() {

			@Override
			public void eventFound(SoftLocator softLocator, Soft soft) {
				// soft.getFounds().forEach(System.out::println);
			}
		})).forEach(s -> {
			// String url = s.isFound() ? "" : " ; " + s.getSoftProvider().getDownloadURL();
			System.out.println("[" + s.getSoftProvider().getGroupName() + "] " + s.getName() + ": " + s.getFirstInfo());
		});
	}

	@Test
	@Ignore
	public void testGS() throws Exception {
		GSSoftProvider gsSoftProvider = new GSSoftProvider();
		Soft soft = gsSoftProvider.searchConfigurable(ss -> {
			ss.withPolicy(new VersionPolicy().onAllPlatforms().maxVersion(new Version(8)));
		});
		soft.getFounds().forEach(sf -> {
			System.out.println(sf.getFoundReason() + "  " + sf.getFile() + "  " + sf.getReason());
		});
		SoftLogger softLogger = new SoftLogger(Collections.singletonList(soft));
		softLogger.log(System.out::println);
		// System.out.println(soft);
	}

}
