package info.wurzinger.segmenting.neighborhood.inspectors;

import info.wurzinger.segmenting.elements.segment.Segment;
import info.wurzinger.segmenting.elements.segment.SegmentMatrix;
import info.wurzinger.segmenting.elements.Pixel;

/**
 * The <code>ClockwiseOutestPixelFinder</code> finds the next border-pixel in
 * clockwise direction of a segment in a 2D Integer matrix
 * (<code>int[][] segmentMatrix</code>).
 * containing the segment labels. This <code>NeighborhoodInspector</code> can be
 * used to transform a matrix containing segment labels into a space where the
 * segment borders are represented as polygons. The border representation can
 * safe memory for larger segments. 
 * 
 * @author Martin Wurzinger
 */
public class ClockwiseOutestPixelFinder extends NeighborhoodInspector<Pixel> {
	
	private Pixel outermostPixel, bufferedOutestPixel;
	private int newDirection, bufferedNewDirection, tmpDirection;
	
	// the segment which this object wants to transform from the pixel
	// definition into a border definition containing only the border pixels.
	private Segment segment;
	// the matrix containing the segment references
	private SegmentMatrix segmentMatrix;
	// the number of possible direction (for example: is 4 in a 4-neighbourhood)
	private int directionCount;
	// the current direction to look for the next border pixel
	private int direction;
	
	// the corrected direction to start 90� counter-clockwise looking for the next border pixel
	private int lookLeftFirstDirection;
	// the correction for the direction variable to start looking 90� counter-clockwise first
	private int lookLeftCorrection;
	
	// has direction changed in the last step
	private boolean hasDirectionChanged;
	// did the direction changed in clockwise direction
	private boolean hasDirectionChangedClockwise;
	
	private int initDirection;
	private Pixel initPixel;
	private boolean startReached;
	
	/**
	 * @param segment is the segment to look for
	 * @param segmentMatrix is the matrix containing the labels at
	 * 	each pixel of the segment the pixel belongs to 
	 * @param directionCount is the number of directions which are analyzed
	 * 	(for example: 4 in an four-neighborhood)
	 */
	public ClockwiseOutestPixelFinder(Segment segment, SegmentMatrix segmentLabelMatrix, int directionCount) {
		this.segment = segment;
		this.segmentMatrix = segmentLabelMatrix;
		this.directionCount = directionCount;
		lookLeftCorrection = directionCount / 4;
		direction = lookLeftCorrection;
		
		initDirection = direction;
		initPixel = null;
		startReached = false;
		
		reset();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void inspect(Pixel pixel) {
		if (outermostPixel==null) {
			if (pixel!=null && segmentMatrix.getSegment(pixel)==segment) {
				if (tmpDirection >= lookLeftFirstDirection) {
					outermostPixel = pixel;
					newDirection = tmpDirection;
					
					int orientedDirChangedValue = ((direction-tmpDirection) + directionCount) % directionCount;
					if (orientedDirChangedValue>=1 && orientedDirChangedValue<=lookLeftCorrection){
						hasDirectionChangedClockwise = false;
					}
				} else if (bufferedOutestPixel==null) {
					bufferedOutestPixel = pixel;
					bufferedNewDirection = tmpDirection;
				}
			}
		}
		
		tmpDirection++;
		
		if (tmpDirection >= directionCount) { // this was last run
			if (outermostPixel==null) {
				outermostPixel = bufferedOutestPixel;
				newDirection = bufferedNewDirection;
			}
			
			hasDirectionChanged = (direction != newDirection);
			direction = newDirection;
			
			if (initPixel==null) {
				initPixel = outermostPixel;
				initDirection = direction;
			} else {
				startReached = (initPixel.compareTo(outermostPixel)==0 && initDirection==direction);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void reset() {
		outermostPixel = null;
		bufferedOutestPixel = null;
		newDirection = 0;
		bufferedNewDirection = 0;
		lookLeftFirstDirection = (direction + (3*(directionCount/4))) % directionCount;
		
		tmpDirection = 0;
		hasDirectionChanged = false;
		hasDirectionChangedClockwise = true;
	}

	/**
	 * @return the next border pixel
	 */
	public Pixel getOutermostPixel() {
		return outermostPixel;
	}
	
	/**
	 * @return true if the path of the last found pixels changed in the last step
	 */
	public boolean hasDirectionChanged() {
		return hasDirectionChanged;
	}
	
	/**
	 * @return the current direction of the search process
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * If the method <code>hasDirectionChanged()</code> evaluates to <code>true</code>
	 * then it is interesting if the last outermost pixel was found in clockwise or
	 * counter-clockwise direction.
	 * 
	 * @return <code>true</code> if the last outermost pixel was found in clockwise
	 * direction and <code>false</code> if it was found in counter-clockwise direction. 
	 */
	public boolean hasDirectionChangedClockwise() {
		return hasDirectionChangedClockwise;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean inspectNullPixel() {
		return true;
	}
	
	public boolean wasStartReached() {
		return startReached;
	}
}
