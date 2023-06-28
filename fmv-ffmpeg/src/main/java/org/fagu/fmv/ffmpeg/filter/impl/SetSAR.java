package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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
public class SetSAR extends SetAR<SetSAR> {

	protected SetSAR() {
		super("setsar");
	}

	protected SetSAR(String ratio) {
		this();
		ratio(ratio);
	}

	public static SetSAR build() {
		return new SetSAR();
	}

	public static SetSAR toRatio(String ratio) {
		return new SetSAR(ratio);
	}

}
