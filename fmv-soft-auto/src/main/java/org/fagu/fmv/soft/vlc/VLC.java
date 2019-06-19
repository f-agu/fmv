package org.fagu.fmv.soft.vlc;

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

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftSearch;


/**
 * @author f.agu
 */
public class VLC {

	private VLC() {
		throw new AssertionError("No instances for you!");
	}

	public static Soft search() {
		return Soft.search(new VLCSoftProvider());
	}

	public static SoftSearch searchWith() {
		return Soft.with(VLCSoftProvider::new);
	}

	// public static void main(String[] args) throws Exception {
	// search().withParameters("--extraintf", "rc")
	// .execute();
	// }
	// "c:\Program Files (x86)\VideoLAN\VLC\vlc.exe" dvdsimple:///e:\ --audio-language=fr --sub-language=en --sout
	// "#transcode{scodec=dvbs}:file{dst=d:\tmp\dvdout.ts,no-overwrite}" --sout-keep vlc://quit

}
