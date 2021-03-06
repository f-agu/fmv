package org.fagu.fmv.soft.xpdf;

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

import java.io.File;

import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.xpdf.PdfSoftProvider.Provider;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class XPdfVersionSoftInfo extends VersionSoftInfo {

	private final Provider provider;

	public XPdfVersionSoftInfo(File file, String softName, Version version, Provider provider) {
		super(file, softName, version);
		this.provider = provider;
	}

	public Provider getProvider() {
		return provider;
	}

	@Override
	public int compareTo(SoftInfo o) {
		if(o instanceof XPdfVersionSoftInfo) {
			XPdfVersionSoftInfo other = (XPdfVersionSoftInfo)o;
			Provider otherProvider = other.getProvider();
			if(provider != otherProvider) {
				return provider == Provider.POPPLER ? 1 : - 1;
			}
		}
		return super.compareTo(o);
	}

}
