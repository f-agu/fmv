package org.fagu.fmv.ffmpeg.logo;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.fagu.fmv.image.Rectangle;


/**
 * @author Utilisateur
 * @created 9 avr. 2018 22:48:22
 */
public class Detected {

	private final int movieWidth;

	private final int movieHeight;

	private final Collection<Rectangle> rectangles;

	private Detected(int movieWidth, int movieHeight, Collection<Rectangle> rectangles) {
		this.movieWidth = movieWidth;
		this.movieHeight = movieHeight;
		this.rectangles = rectangles;
	}

	public static Detected found(int movieWidth, int movieHeight, Collection<Rectangle> rectangles) {
		return new Detected(movieWidth, movieHeight, Objects.requireNonNull(rectangles));
	}

	public static Detected notFound(int movieWidth, int movieHeight) {
		return new Detected(movieWidth, movieHeight, null);
	}

	public int getMovieWidth() {
		return movieWidth;
	}

	public int getMovieHeight() {
		return movieHeight;
	}

	public Collection<Rectangle> getRectangles() {
		return rectangles == null ? Collections.emptyList() : rectangles;
	}

}
