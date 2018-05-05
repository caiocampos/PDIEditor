package info.wurzinger.segmenting.neighborhood;

import java.awt.Rectangle;
import java.util.List;

import info.wurzinger.segmenting.elements.Pixel;


public abstract class PixelNeighborhood extends Neighborhood {
	
	protected Pixel centerPixel;
	protected  Rectangle rectTile;
	
	public PixelNeighborhood(Pixel centerPixel, Rectangle rectTile) {
		super();
		
		this.centerPixel = centerPixel;
		this.rectTile = rectTile;
		
		addNeighboringPixels();
	}
	
	public abstract void addNeighboringPixels();
	public abstract int getDirectionCount();
	
	/**
	 * Transforms border pixel to the <code>WIND_EVEN_ODD</code> winding rule as the <code>
	 * Polygon</code> class use it. For further explanation for the winding rule see <code>
	 * PathIterator</code>.
	 * The parameterized directions consider the information when the border polygon path of
	 * a segment was followed.
	 * @param p The Pixel which should be adapted.
	 * @param directionOld The old direction which was evaluated when the last pixel was found.
	 * @param directionNew The new direction which was evaluated when the current pixel <emph>p</emph>
	 * was found.
	 * @return An <code>ArrayList</code> of Pixels representing the segments border.
	 */
	public abstract List<Pixel> adaptExternalBorderRule(Pixel p, int directionOld, int directionNew, boolean doClockwise);
	
	public void resetCenterPixel(Pixel centerPixel) {
		this.centerPixel = centerPixel;
		atoms.clear();
		addNeighboringPixels();
	}
	
	protected boolean isAtLeft() {
		return centerPixel.getX()<=rectTile.x;
	}

	protected boolean isAtTop() {
		return centerPixel.getY()<=rectTile.y;
	}
	
	protected boolean isAtRight() {
		return centerPixel.getX()>=rectTile.x+rectTile.width-1;
	}

	protected boolean isAtBottom() {
		return centerPixel.getY()>=rectTile.y+rectTile.height-1;
	}
	
	public static PixelNeighborhood createInstance(Pixel p, int width, int height) {
		return createInstance(p, new Rectangle(width, height));
	}
	
	public static PixelNeighborhood createInstance(Pixel p, Rectangle rectTile) {
		return new NeighborhoodFour(p, rectTile);
	}
	
	public static PixelNeighborhood createComplementInstance(Pixel p,  int width, int height) {
		return createComplementInstance(p, new Rectangle(width, height));
	}
	public static PixelNeighborhood createComplementInstance(Pixel p, Rectangle rectTile) {
		return new NeighborhoodEight(p, rectTile);
	}
	
	public int getNextStep(int oldDirection, int step) {
		return (oldDirection+step+getDirectionCount()) % getDirectionCount();
	}
}
