package info.wurzinger.segmenting.neighborhood;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import info.wurzinger.segmenting.elements.Pixel;


@SuppressWarnings("serial")
public class NeighborhoodFour extends PixelNeighborhood {
	
	private static final int NR_OF_NEIGHBORS = 4;
	public enum Direction {
		NORTH(0), EAST(1), SOUTH(2), WEST(3);
		
		private int direction;
		
		private Direction(int direction) {
			this.direction = direction;
		}
		
		public int getDirection() {
			return direction;
		}
	}
	
	public NeighborhoodFour(Pixel p, Rectangle rectTile) {
		super(p, rectTile);
	}
	
	@Override
	public void addNeighboringPixels() {
		Pixel p;
		
		// top pixel
		p = isAtTop() ? null : new Pixel(centerPixel.getX(), centerPixel.getY()-1);
		addAtom(p);
		
		// right pixel
		p = isAtRight() ? null : new Pixel(centerPixel.getX()+1, centerPixel.getY());
		addAtom(p);
		
		// bottom pixel
		p = isAtBottom() ? null : new Pixel(centerPixel.getX(), centerPixel.getY()+1);
		addAtom(p);
		
		// left pixel
		p = isAtLeft() ? null : new Pixel(centerPixel.getX()-1, centerPixel.getY());
		addAtom(p);
	}
	
	public int getDirectionCount() {
		return NR_OF_NEIGHBORS;
	}
	
//	/**
//	 * {@inheritDoc}
//	 */
//	public List<Pixel> adaptWindingRuleEvenOdd(Pixel p, int directionOld, int directionNew, boolean doClockwise) {
//		return adaptWindingRuleEvenOdd(p, directionOld, directionNew, NR_OF_NEIGHBORS, doClockwise);
//	}
	
	/** {@inheritDoc} */
	public List<Pixel> adaptExternalBorderRule(Pixel p, int directionOld, int directionNew, boolean doClockwise) {
		return adaptExternalBorderRule(p, directionOld, directionNew, NR_OF_NEIGHBORS, doClockwise);
	}
	
	/**
	 * Adapts the internal border {@link Pixel} <code>p</code> and returns
	 * a list of {@link Pixel}s which represents the external border. In order
	 * to be able to calculate the external pixels, a few other information
	 * like the <em>old direction</em>, the <em>new direction</em>, the
	 * <em>number of possible directions in this neighborhood</em> and if
	 * the new direction was found clockwise or counter-clockwise going out
	 * of the old direction.  
	 * 
	 * @param p the internal border {@link Pixel} which should be adapted 
	 * @param directionOld the direction in which {@link Pixel} <code>p</code>
	 * was found 
	 * @param directionNew the direction in which the next {@link Pixel} will
	 * be found at the border ring
	 * @param nrOfNeighbors is 4 in a four-neighborhood or 8 in a eight-neighborhood
	 * @param doClockwise is <code>true</code> if outgoing from the {@link Pixel}
	 * <code>p</code> the next pixel at the border ring is found in clockwise direction.
	 * 
	 * @return a {@link List} of pixels representing the external border
	 */
	public static List<Pixel> adaptExternalBorderRule(Pixel p, int directionOld, int directionNew, int nrOfNeighbors, boolean doClockwise) {
		int step = 1;
		
		directionOld %= nrOfNeighbors;
		directionNew %= nrOfNeighbors;
		
		if (!doClockwise) step = -1; 
		
		LinkedList<Pixel> adaptedPixel = new LinkedList<Pixel>();
		
		if (directionOld!=directionNew) {
			int x, y;
			int directionTmp;
			
			while (directionOld!=directionNew) {
				directionTmp = getNextStep(directionOld, step, nrOfNeighbors);
				x = p.getX();
				y = p.getY();
				
				if (directionOld==Direction.NORTH.getDirection() || directionTmp==Direction.NORTH.getDirection()) {
					x--;
				}
				if (directionOld==Direction.EAST.getDirection() || directionTmp==Direction.EAST.getDirection()) {
					y--;
				}
				if (directionOld==Direction.SOUTH.getDirection() || directionTmp==Direction.SOUTH.getDirection()) {
					x++;
				}
				if (directionOld==Direction.WEST.getDirection() || directionTmp==Direction.WEST.getDirection()) {
					y++;
				}
				adaptedPixel.add(new Pixel(x, y));
				
				directionOld = directionTmp;
				getNextStep(directionTmp, step, nrOfNeighbors);
			}
		}
		
		return adaptedPixel;
	}
	
//	/**
//	 * {@see delaminator.algorithm.elements.PixelNeighborhood#adaptWindingRuleEvenOdd}
//	 */
//	public static List<Pixel> adaptWindingRuleEvenOdd(Pixel p, int directionOld, int directionNew, int nrOfNeighbors, boolean doClockwise) { // TODO: DELETE
////		int directionTmp;
//		int step = 1;
//		
//		directionOld %= nrOfNeighbors;
//		directionNew %= nrOfNeighbors;
//		
//		if (!doClockwise) step = -1; 
//		
//		LinkedList<Pixel> adaptedPixel = new LinkedList<Pixel>();
//		
//		if (directionOld!=directionNew) {
//			int x, y;
//			int directionTmp;
//			
//			while (directionOld!=directionNew) {
//				directionTmp = getNextStep(directionOld, step, nrOfNeighbors);
//				x = p.getX();
//				y = p.getY();
//				
//				if (directionOld==2 || directionTmp==2) {
//					x++;
//				}
//				if (directionOld==3 || directionTmp==3) {
//					y++;
//				}
//				adaptedPixel.add(new Pixel(x, y));
//				
//				directionOld = directionTmp;
//				getNextStep(directionTmp, step, nrOfNeighbors);
//			}
//		}
//		
//		return adaptedPixel;
//	}
	
	public static int getNextStep(int oldDirection, int step, int nrOfNeighbors) {
		return (oldDirection+step+nrOfNeighbors) % nrOfNeighbors;
	}
}
