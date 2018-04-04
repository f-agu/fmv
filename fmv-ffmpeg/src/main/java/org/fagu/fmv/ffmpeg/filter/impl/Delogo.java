package org.fagu.fmv.ffmpeg.filter.impl;

import java.util.Collections;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class Delogo extends AbstractFilter {

	/**
	 * 
	 */
	protected Delogo() {
		super("delogo");
	}

	/**
	 * @return
	 */
	public static Delogo build() {
		return new Delogo();
	}

	/**
	 * Set logo x position (from -1 to INT_MAX) (default -1)
	 * 
	 * @param x
	 * @return
	 */
	public Delogo x(int x) {
		if(x < - 1) {
			throw new IllegalArgumentException("x must be at least -1: " + x);
		}
		parameter("x", Integer.toString(x));
		return this;
	}

	/**
	 * Set logo y position (from -1 to INT_MAX) (default -1)
	 * 
	 * @param y
	 * @return
	 */
	public Delogo y(int y) {
		if(y < - 1) {
			throw new IllegalArgumentException("y must be at least -1: " + y);
		}
		parameter("y", Integer.toString(y));
		return this;
	}

	/**
	 * Set logo width (from -1 to INT_MAX) (default -1)
	 * 
	 * @param w
	 * @return
	 */
	public Delogo w(int w) {
		if(w < - 1) {
			throw new IllegalArgumentException("w must be at least -1: " + w);
		}
		parameter("w", Integer.toString(w));
		return this;
	}

	/**
	 * Set logo height (from -1 to INT_MAX) (default -1)
	 * 
	 * @param h
	 * @return
	 */
	public Delogo h(int h) {
		if(h < - 1) {
			throw new IllegalArgumentException("h must be at least -1: " + h);
		}
		parameter("h", Integer.toString(h));
		return this;
	}

	/**
	 * Set delogo area band size (from 0 to INT_MAX) (default 0)
	 * 
	 * @param band
	 * @return
	 */
	public Delogo band(int band) {
		if(band < 0) {
			throw new IllegalArgumentException("band must be at least 0: " + band);
		}
		parameter("band", Integer.toString(band));
		return this;
	}

	/**
	 * Set delogo area band size (from 0 to INT_MAX) (default 0)
	 * 
	 * @param t
	 * @return
	 */
	public Delogo t(int t) {
		if(t < 0) {
			throw new IllegalArgumentException("t must be at least 0: " + t);
		}
		parameter("t", Integer.toString(t));
		return this;
	}

	/**
	 * Show delogo area (default false)
	 * 
	 * @param show
	 * @return
	 */
	public Delogo show(boolean show) {
		parameter("show", show ? "1" : "0");
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
