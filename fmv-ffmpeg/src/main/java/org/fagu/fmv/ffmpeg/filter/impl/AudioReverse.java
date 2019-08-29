package org.fagu.fmv.ffmpeg.filter.impl;

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class AudioReverse extends AbstractFilter {

	protected AudioReverse() {
		super("areverse");
	}

	public static AudioReverse build() {
		return new AudioReverse();
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}
}
