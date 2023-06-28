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

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;


/**
 * @author f.agu
 */
public class AudioModifier extends AbstractFilter {

	protected AudioModifier() {
		super("aeval");
	}

	public static AudioModifier build() {
		return new AudioModifier();
	}

	public AudioModifier halfVolume() {
		return expr("val(ch)/2");
	}

	public AudioModifier invertPhase() {
		return expr("-val(ch)");
	}

	public AudioModifier expr(String... exprs) {
		parameter("exprs", "'" + StringUtils.join(exprs, '|') + "'");
		return this;
	}

	public AudioModifier channelLayout(ChannelLayout channelLayout) {
		parameter("c", channelLayout.toString());
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

}
