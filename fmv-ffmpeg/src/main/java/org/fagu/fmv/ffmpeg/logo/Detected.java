package org.fagu.fmv.ffmpeg.logo;

import java.awt.Rectangle;
import java.util.Optional;


/**
 * @author Utilisateur
 * @created 9 avr. 2018 22:48:22
 */
public class Detected {

	private final int movieWidth;

	private final int movieHeight;

	private final Rectangle rectangle;

	/**
	 * @param movieWidth
	 * @param movieHeight
	 * @param rectangle
	 */
	public Detected(int movieWidth, int movieHeight, Rectangle rectangle) {
		this.movieWidth = movieWidth;
		this.movieHeight = movieHeight;
		this.rectangle = rectangle;
	}

	public int getMovieWidth() {
		return movieWidth;
	}

	public int getMovieHeight() {
		return movieHeight;
	}

	public Optional<Rectangle> getRectangle() {
		return Optional.ofNullable(rectangle);
	}

}
