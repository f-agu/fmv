package org.fagu.fmv.ffmpeg.filter.impl;

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class Reverse extends AbstractFilter {

	protected Reverse() {
		super("reverse");
	}

	public static Reverse build() {
		return new Reverse();
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
