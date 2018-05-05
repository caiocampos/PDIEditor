package info.wurzinger.segmenting.neighborhood;

import java.awt.Rectangle;
import java.util.List;

import info.wurzinger.segmenting.elements.Pixel;

@SuppressWarnings("serial")
public class NeighborhoodEight extends PixelNeighborhood {
	
	private final int NR_OF_NEIGHBORS = 8;
	
	public NeighborhoodEight(Pixel p, Rectangle rectTile) {
		super(p, rectTile);
	}

	@Override
	public void addNeighboringPixels() {
		Pixel p;
		
		// top pixel
		p = isAtTop() ? null : new Pixel(centerPixel.getX(), centerPixel.getY()-1);
		addAtom(p);
		
		// right-top pixel
		p = (isAtRight() || isAtTop()) ? null : new Pixel(centerPixel.getX()+1, centerPixel.getY()-1);
		addAtom(p);
		
		// right pixel
		p = isAtRight() ? null : new Pixel(centerPixel.getX()+1, centerPixel.getY());
		addAtom(p);
		
		// right-bottom pixel
		p = (isAtRight() || isAtBottom()) ? null : new Pixel(centerPixel.getX()+1, centerPixel.getY()+1);
		addAtom(p);
		
		// bottom pixel
		p = isAtBottom() ? null : new Pixel(centerPixel.getX(), centerPixel.getY()+1);
		addAtom(p);
		
		// left-bottom pixel
		p = (isAtLeft() || isAtBottom()) ? null : new Pixel(centerPixel.getX()-1, centerPixel.getY()+1);
		addAtom(p);
		
		// left pixel
		p = isAtLeft() ? null : new Pixel(centerPixel.getX()-1, centerPixel.getY());
		addAtom(p);

		// left-top pixel
		p = (isAtLeft() || isAtTop()) ? null : new Pixel(centerPixel.getX()-1, centerPixel.getY()-1);
		addAtom(p);
	}

	public int getDirectionCount() {
		return NR_OF_NEIGHBORS;
	}
	
//	/**
//	 * {@inheritDoc}
//	 */
//	public List<Pixel> adaptWindingRuleEvenOdd(Pixel p, int directionOld, int directionNew, boolean doClockwise) { // TODO: DELETE
//		return NeighborhoodFour.adaptWindingRuleEvenOdd(p, directionOld/2, directionNew/2, NR_OF_NEIGHBORS, doClockwise);
//	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Pixel> adaptExternalBorderRule(Pixel p, int directionOld, int directionNew, boolean doClockwise) {
		return NeighborhoodFour.adaptExternalBorderRule(p, directionOld/2, directionNew/2, NR_OF_NEIGHBORS, doClockwise);
	}
	
	public static boolean areNeighbors(Pixel p1, Pixel p2) {
		return Math.abs(p1.getX()-p2.getX()) <= 1 && Math.abs(p1.getY()-p2.getY()) <= 1; 
	}
}
