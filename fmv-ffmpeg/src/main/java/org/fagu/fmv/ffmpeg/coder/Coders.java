package org.fagu.fmv.ffmpeg.coder;

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

import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Help;
import org.fagu.fmv.ffmpeg.utils.HelpCache;


/**
 * @author f.agu
 */
public abstract class Coders {

	private final String name;

	Coders(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		if(cache().contains('A')) {
			return Type.AUDIO;
		}
		if(cache().contains('V')) {
			return Type.VIDEO;
		}
		if(cache().contains('S')) {
			return Type.SUBTITLE;
		}
		throw new RuntimeException("Unknown type: " + StringUtils.join(cache().getChars(), ','));
	}

	public boolean isFrameLevelMultithreading() {
		return cache().contains('F');
	}

	public boolean isSliceLevelMultithreading() {
		return cache().contains('S');
	}

	public boolean isCodecExperimental() {
		return cache().contains('X');
	}

	public boolean isSupports_draw_horiz_band() {
		return cache().contains('B');
	}

	public boolean isSupportsDirectRenderingMethod_1() {
		return cache().contains('D');
	}

	public String getDescription() {
		return helpCache().cache(name).stream()
				.map(CoderHelp::getText)
				.collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		return name;
	}

	// ********************************************

	protected abstract <H extends CoderHelp> H cache();

	protected abstract <H extends CoderHelp, R extends Coders> HelpCache<R, H> helpCache();

	// ---------------------------------------------

	/**
	 * @author f.agu
	 */
	public abstract static class CoderHelp extends Help {

		protected CoderHelp(String name) {
			super(name);
		}
	}

}
