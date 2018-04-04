package org.fagu.fmv.mymedia.reduce.neocut;

import org.fagu.fmv.ffmpeg.filter.impl.Delogo;


/**
 * @author Oodrive
 * @author f.agu
 * @created 4 avr. 2018 15:43:58
 */
public class Logo {

	private final int x;

	private final int y;

	private final int w;

	private final int h;

	public Logo(int x, int y, int w, int h) {
		this.x = requirePositive("x", x);
		this.y = requirePositive("y", y);
		this.w = requirePositive("w", w);
		this.h = requirePositive("h", h);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public Delogo generateFilter() {
		return Delogo.build()
				.x(x)
				.y(y)
				.w(w)
				.h(h);
	}

	private static int requirePositive(String title, int i) {
		if(i < 0) {
			throw new IllegalArgumentException(title + " must be positive: " + i);
		}
		return i;
	}

}
