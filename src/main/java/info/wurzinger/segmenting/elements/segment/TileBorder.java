package info.wurzinger.segmenting.elements.segment;

import java.util.BitSet;

public class TileBorder {

	private static final long serialVersionUID = 1L;
	private static final int TOP = 0;
	private static final int RIGHT = 1;
	private static final int BOTTOM = 2;
	private static final int LEFT   = 3;
	
	private BitSet borderSet;
	
	/**
	 * Creates a new <code>TileBorder</code> which describes if a segment
	 * is laying at the border of a {@link SegmentMapTile}.
	 */
	public TileBorder() {
		borderSet = new BitSet(4); // tile borders in 4 directions (top, right, bottom, left)
	}
	
	/**
	 * Combines the border positioning with another TileBorder (AND combination).
	 */
	public void combine(TileBorder other) {
		borderSet.and(other.borderSet);
	}
	
	/** Sets the property <em>segment is at TOP border of tile</em> to <code>true</code> */
	public void setTop() {
		borderSet.set(TOP, true);
	}
	/** Sets the property <em>segment is at RIGHT border of tile</em> to <code>true</code> */
	public void setRight() {
		borderSet.set(RIGHT, true);
	}
	/** Sets the property <em>segment is at BOTTOM border of tile</em> to <code>true</code> */
	public void setBottom() {
		borderSet.set(BOTTOM, true);
	}
	/** Sets the property <em>segment is at LEFT border of tile</em> to <code>true</code> */
	public void setLeft() {
		borderSet.set(LEFT, true);
	}
	/** Sets the properties at which border a segment is laying */
	public void setBorders(boolean isAtTop, boolean isAtRight, boolean isAtBottom, boolean isAtLeft) {
		borderSet.set(TOP, isAtTop);
		borderSet.set(RIGHT, isAtRight);
		borderSet.set(BOTTOM, isAtBottom);
		borderSet.set(LEFT, isAtLeft);
	}
	
	
	/** Gets the property if <em>segment is at the TOP border of tile</em> */
	public boolean isTop() {
		return borderSet.get(TOP);
	}
	/** Gets the property if <em>segment is at the RIGHT border of tile</em> */
	public boolean isRight() {
		return borderSet.get(RIGHT);
	}
	/** Gets the property if <em>segment is at the BOTTOM border of tile</em> */
	public boolean isBottom() {
		return borderSet.get(BOTTOM);
	}
	/** Gets the property if <em>segment is at the LEFT border of tile</em> */
	public boolean isLeft() {
		return borderSet.get(LEFT);
	}
	
	/** Gets the property if <em>segment is at the tile border</em> */
	public boolean isAtBorder() {
		return !borderSet.isEmpty();
	}
	
	/** Gets the property if <em>segment is not at any tile border</em> */
	public boolean isInternal() {
		return borderSet.isEmpty();
	}
}
