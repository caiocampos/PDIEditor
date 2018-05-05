package info.wurzinger.segmenting.elements.segment;

/**
 * This class represents the color difference of two neighboring segments
 * and is used for example in the hierarchical watershed transformation.
 * 
 * @author Martin Wurzinger
 */
public class EdgeGradient implements Comparable<EdgeGradient> {
	private double edgeGradient;
	private int smallerLabel;
	private int biggerLabel;
	
	/**
	 * Creates a new instance by analyzing the colors of <code>segmentA</code>
	 * and <code>segmentB</code>. 
	 * In case of RGB colors the gradient is calculated by
	 * SqareRoot(Square(Red_a - Red_b) + Square(Green_a - Green_b) + Square(Blue_a - Blue_b))
	 */
	public EdgeGradient(Segment segmentA, Segment segmentB) {
		if (segmentA.getLabel()<segmentB.getLabel()) {
			smallerLabel = segmentA.getLabel();
			biggerLabel = segmentB.getLabel();
		} else {
			smallerLabel = segmentB.getLabel();
			biggerLabel = segmentA.getLabel();
		}
		
		// calculate the edge gradient by formula edgeGradient := Sqrt(sq(r1-r2)+sq(g1-g2)+sq(b1-b2))
		double squaredSum = 0d;
		for (int band=0; band<segmentA.getColor().length; band++) {
			squaredSum += Math.pow(segmentA.getColor()[band] - segmentB.getColor()[band], 2d);
		}
		edgeGradient = Math.sqrt(squaredSum);
	}
	
	/**
	 * Implements the {@link Comparable} interface.
	 * 
	 * @return a <em>negative</em> value if the gradient of <code>this</code>
	 * is smaller. If the gradient of <code>this</code> is bigger a
	 * <code>positive</code> value is returned. In case of an equal gradient
	 * 0 is returned.
	 * 
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(EdgeGradient comparingSegmentEdgeGradient) {
		if (edgeGradient < comparingSegmentEdgeGradient.edgeGradient) {
			return -1;
		} else if (edgeGradient > comparingSegmentEdgeGradient.edgeGradient) {
			return 1;
		} else {
			int compareSegmentSmallLabels = smallerLabel - comparingSegmentEdgeGradient.smallerLabel;
			
			if (compareSegmentSmallLabels!=0) {
				return compareSegmentSmallLabels;
			} else {
				return biggerLabel - comparingSegmentEdgeGradient.biggerLabel;
			}
		}
	}
	
	/**
	 * @return the smaller label of the two neighboring segments
	 */
	public int getSmallerLabel() {
		return smallerLabel;
	}
	
	/**
	 * @return the bigger label of the two neighboring segments
	 */
	public int getBiggerLabel() {
		return biggerLabel;
	}
	
	/**
	 * @return the calculated gradient between the two neighboring
	 * segments
	 */
	public double getEdgeGradient() {
		return edgeGradient;
	}
	
	/**
	 * @return an appropriate {@link String} representation
	 * @see Object#toString()
	 */
	public String toString() {
		return smallerLabel + " <-> " + biggerLabel + ": " + edgeGradient;
	}
}
