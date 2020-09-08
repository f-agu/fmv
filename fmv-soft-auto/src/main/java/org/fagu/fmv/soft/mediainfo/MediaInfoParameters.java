package org.fagu.fmv.soft.mediainfo;

/*-
 * #%L
 * fmv-soft-auto
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.mediainfo.raw.RawDetails0ReadLine;


/**
 * @author f.agu
 * @created 3 juil. 2019 10:42:58
 */
public class MediaInfoParameters {

	private final Soft mediaInfoSoft;

	public MediaInfoParameters() {
		this(MediaInfo.search());
	}

	public MediaInfoParameters(Soft mediaInfoSoft) {
		this.mediaInfoSoft = Objects.requireNonNull(mediaInfoSoft);
	}

	public Info getAllParameters() throws IOException {
		final List<InfoBase> infoBases = new ArrayList<>();
		try (RawDetails0ReadLine readLineDetails0 = new RawDetails0ReadLine(infoBases::add)) {
			mediaInfoSoft.withParameters("--Info-Parameters")
					.addOutReadLine(readLineDetails0)
					.execute();
		}
		return new Info(infoBases);
	}

}
