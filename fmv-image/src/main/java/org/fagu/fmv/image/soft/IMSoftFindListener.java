package org.fagu.fmv.image.soft;

/*-
 * #%L
 * fmv-image
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

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.SoftFindListener;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.im.IMSoftProvider;
import org.im4java.process.ProcessStarter;


/**
 * @author f.agu
 */
public class IMSoftFindListener implements SoftFindListener {

	/**
	 * 
	 */
	public IMSoftFindListener() {}

	/**
	 * @see org.fagu.fmv.soft.find.SoftFindListener#eventFound(org.fagu.fmv.soft.Soft)
	 */
	@Override
	public void eventFound(SoftLocator softLocator, Soft soft) {
		if(soft.isFound() && soft.getSoftProvider() instanceof IMSoftProvider) {
			String path = soft.getFile().getParent();
			softLocator.setPath(soft.getSoftName(), path);
			ProcessStarter.setGlobalSearchPath(path);
		}
	}
}
