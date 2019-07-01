package org.fagu.fmv.soft;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.maxVersion;

import java.util.Collections;

import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.gs.GSSoftProvider;
import org.fagu.version.Version;
import org.junit.Ignore;
import org.junit.Test;
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


/**
 * @author f.agu
 */
public class SoftTestCase {

	@Test
	@Ignore
	public void testFindAll() throws Exception {
		// System.getProperties().forEach((k, v) -> System.out.println(k + " : " + v));
		Soft.searchAll().forEach(s -> {
			String prefix = "[" + s.getSoftProvider().getGroupName() + "] " + s.getName() + ' ';
			if(s.isFound()) {
				System.out.println(prefix + s.getFirstInfo());
			} else {
				System.out.println(prefix + " ==== NOT FOUND ====. " + s.getSoftProvider().getDownloadURL());
			}
		});

	}

	@Test
	@Ignore
	public void testGS() throws Exception {
		GSSoftProvider gsSoftProvider = new GSSoftProvider();
		Soft soft = gsSoftProvider.searchConfigurable(ss -> {
			ss.withPolicy(new VersionSoftPolicy().onAllPlatforms(maxVersion(new Version(8))));
		});
		soft.getFounds().forEach(sf -> {
			System.out.println(sf.getFoundReason() + "  " + sf.getFile() + "  " + sf.getReason());
		});

		SoftLogger softChecker = new SoftLogger(Collections.singletonList(soft));
		softChecker.log(System.out::println);
		// System.out.println(soft);
	}

}
