package org.fagu.fmv.mymedia.reduce.neocut;

import org.fagu.fmv.ffmpeg.filter.impl.Delogo;
import org.fagu.fmv.image.Rectangle;


/**
 * @author Oodrive
 * @author f.agu
 * @created 4 avr. 2018 15:43:58
 */
public class Logo extends Rectangle {

	private final boolean autoDetect;

	private Logo(boolean autoDetect, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.autoDetect = autoDetect;
	}

	public static Logo autoDetect() {
		return new Logo(true, 0, 0, 0, 0);
	}

	public static Logo defined(int x, int y, int w, int h) {
		return new Logo(false, x, y, w, h);
	}

	public boolean isAutoDetect() {
		return autoDetect;
	}

	public Delogo generateFilter() {
		return Delogo.build()
				.x(getX())
				.y(getY())
				.w(getWidth())
				.h(getHeight());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (autoDetect ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if( ! super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		Logo other = (Logo)obj;
		if(autoDetect != other.autoDetect)
			return false;
		return super.equals(obj);
	}

}
