package org.fagu.fmv.mymedia.audiohash;

/**
 * Public class for return info from the AudioHasher::ber method.
 * https://github.com/starkdg/JPhashAudio/tree/master/src/main/java/org/phash/phashaudio
 */
public class AudioHashDistance {

	private int pos; // position into hash the first hash was matched

	private double cs; // confidence score, 0-1

	public AudioHashDistance() {
		pos = - 1;
		cs = 0.0;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public void setCs(double cs) {
		this.cs = cs;
	}

	public double getCs() {
		return cs;
	}
}
