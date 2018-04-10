package org.fagu.fmv.image;

/**
 * @author Oodrive
 * @author f.agu
 * @created 4 avr. 2018 15:43:58
 */
public class Rectangle {

	private final int x;

	private final int y;

	private final int width;

	private final int height;

	public Rectangle(int x, int y, int w, int h) {
		this.x = requirePositive("x", x);
		this.y = requirePositive("y", y);
		this.width = requirePositiveAndNotZero("width", w);
		this.height = requirePositiveAndNotZero("height", h);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMaxX() {
		return x + width - 1;
	}

	public int getMaxY() {
		return y + height - 1;
	}

	public int countPixels() {
		return width * height;
	}

	public boolean contains(int inX, int inY) {
		if(inX < x || inY < y) {
			return false;
		}
		int w = width + x;
		int h = height + y;
		// overflow || intersect
		return ((w < x || w > inX) &&
				(h < y || h > inY));
	}

	public boolean intersects(Rectangle r) {
		int rx = r.x;
		int ry = r.y;
		int rw = width + rx;
		int rh = height + ry;
		int tw = width + x;
		int th = height + y;
		// overflow || intersect
		return ((rw < rx || rw > x) &&
				(rh < ry || rh > y) &&
				(tw < x || tw > rx) &&
				(th < y || th > ry));
	}

	public boolean canJoinWith(Rectangle other) {

		// TODO
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Rectangle other = (Rectangle)obj;
		if(height != other.height)
			return false;
		if(width != other.width)
			return false;
		if(x != other.x)
			return false;
		if(y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(getClass().getName()).append("[x=").append(x).append(",y=").append(y)
				.append(",width=").append(width).append(",height=").append(height).append(']');
		return sb.toString();
	}

	// **************************************************

	private static int requirePositive(String title, int i) {
		if(i < 0) {
			throw new IllegalArgumentException(title + " must be positive: " + i);
		}
		return i;
	}

	private static int requirePositiveAndNotZero(String title, int i) {
		if(i <= 0) {
			throw new IllegalArgumentException(title + " must be over 0: " + i);
		}
		return i;
	}

}
