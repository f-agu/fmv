package org.fagu.fmv.image;

import java.util.Optional;


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

	public Optional<Rectangle> intersection(Rectangle r) {
		int tx1 = this.x;
		int ty1 = this.y;
		int rx1 = r.x;
		int ry1 = r.y;
		long tx2 = tx1;
		tx2 += this.width;
		long ty2 = ty1;
		ty2 += this.height;
		long rx2 = rx1;
		rx2 += r.width;
		long ry2 = ry1;
		ry2 += r.height;
		if(tx1 < rx1)
			tx1 = rx1;
		if(ty1 < ry1)
			ty1 = ry1;
		if(tx2 > rx2)
			tx2 = rx2;
		if(ty2 > ry2)
			ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never overflow (they will never be
		// larger than the smallest of the two source w,h)
		// they might underflow, though...
		if(tx2 < Integer.MIN_VALUE)
			tx2 = Integer.MIN_VALUE;
		if(ty2 < Integer.MIN_VALUE)
			ty2 = Integer.MIN_VALUE;
		if(tx1 < 0 || ty1 < 0 || tx2 <= 0 || ty2 <= 0) {
			return Optional.empty();
		}
		return Optional.of(new Rectangle(tx1, ty1, (int)tx2, (int)ty2));
	}

	public Optional<Rectangle> union(Rectangle r) {
		long tx2 = this.width;
		long ty2 = this.height;
		long rx2 = r.width;
		long ry2 = r.height;
		int tx1 = this.x;
		int ty1 = this.y;
		tx2 += tx1;
		ty2 += ty1;
		int rx1 = r.x;
		int ry1 = r.y;
		rx2 += rx1;
		ry2 += ry1;
		if(tx1 > rx1)
			tx1 = rx1;
		if(ty1 > ry1)
			ty1 = ry1;
		if(tx2 < rx2)
			tx2 = rx2;
		if(ty2 < ry2)
			ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never underflow since both original rectangles
		// were already proven to be non-empty
		// they might overflow, though...
		if(tx2 > Integer.MAX_VALUE)
			tx2 = Integer.MAX_VALUE;
		if(ty2 > Integer.MAX_VALUE)
			ty2 = Integer.MAX_VALUE;
		if(tx1 < 0 || ty1 < 0 || tx2 <= 0 || ty2 <= 0) {
			return Optional.empty();
		}
		return Optional.of(new Rectangle(tx1, ty1, (int)tx2, (int)ty2));
	}

	public boolean isGlued(Rectangle other) {
		int ox = other.x;
		int omx = other.getMaxX();
		int mx = getMaxX();
		int oy = other.y;
		int my = getMaxY();
		int omy = other.getMaxY();
		for(int s = - 1; s <= 1; ++s) {
			if(((x + s == ox || x + s == omx
					|| mx + s == ox || mx + s == omx)
					&& ((y < oy && oy < my)
							|| (y < omy && omy < my)
							|| (y < oy && my > omy)
							|| (y > oy && my < omy)))
					|| ((y + s == oy || y + s == omy
							|| my + s == oy || my + s == omy)
							&& ((x < ox && ox < mx)
									|| (x < omx && omx < mx)
									|| (x < ox && mx > omx)
									|| (x > ox && mx < omx)))) {
				return true;
			}
		}
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
		return new StringBuilder(50)
				.append(getClass().getName()).append("[x=").append(x).append(",y=").append(y)
				.append(",width=").append(width).append(",height=").append(height).append(']')
				.toString();
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
