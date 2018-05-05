package info.wurzinger.segmenting.elements;

import info.wurzinger.segmenting.elements.Atom;


/**
 * One Pixel Object needs 24 Bytes
 * @author Martin Wurzinger
 *
 */
public class Pixel extends Atom implements Comparable<Pixel> {

	protected int x, y;

	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public int compareTo(Pixel p) {
		if (this.y < p.y || (this.y == p.y && this.x < p.x)) {
			return -1;
		} else if (this.x == p.x && this.y == p.y) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof Pixel) {
			Pixel p = (Pixel) o;
			return x == p.x && y == p.y;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "("+x+","+y+")";
	}
}
