package info.wurzinger.jaiOp.hwst;

import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

import info.wurzinger.segmenting.elements.segment.EdgeGradient;
import info.wurzinger.segmenting.elements.segment.Segment;
import info.wurzinger.segmenting.elements.segment.SegmentMapTile;
import info.wurzinger.segmenting.elements.segment.SegmentMatrix;
import info.wurzinger.segmenting.elements.segment.state.SegmentState;

/**
 * Executes the hierarchical watershed transformation on a
 * {@link SegmentMapTile} object and merges the segments
 * until a defined <em>gradient</em> value has been reached.
 * All neighboring segments between them the color gradient is
 * smaller than the <code>minGradient</code> parameter have been
 * joined.
 * 
 * @author Martin Wurzinger
 */
public class HierarchicalWatershedTransformer {

	private SegmentMapTile segmentMapTile = null;
	private TreeSet<EdgeGradient> sortedSegmentEdges;
	
	/**
	 * Creates an new <code>HierarchicalWatershedTransformer</code> object
	 * from the given {@link SegmentMapTile}.
	 * 
	 * @param segmentMapTile is the {@link SegmentMapTile} on which
	 * the hierarchical watershed transformation is executed.
	 */
	public HierarchicalWatershedTransformer(SegmentMapTile segmentMapTile) {
		this.segmentMapTile = segmentMapTile;
		sortedSegmentEdges = new TreeSet<EdgeGradient>(); // uses compareTo
	}
	
	/**
	 * For reasons of memory concerns neighboring segments with similar
	 * color can be merged.
	 * Executes the hierarchical watershed transformation on the given
	 * <code>SegmentMapTile</code>. All neighboring segments between them
	 * the color gradient is smaller than the <code>minGradient</code> parameter
	 * have been joined.
	 * 
	 * @param minGradient All neighboring segments between them
	 * the color gradient is smaller than the <code>minGradient</code> parameter
	 * have been joined.
	 * @return the manipulated and more compact <code>SegmentMapTile</code> object 
	 * as it was given as constructor parameter.
	 */
	public SegmentMapTile execute(double minGradient) {
		
		extractEdges();
		
		int lastLabel = segmentMapTile.getHighestLabel();
		Segment smallerLabeldSegment, biggerLabeldSegment, mergedSegment;
		EdgeGradient smallestEdgeGradient = getFirstElement(sortedSegmentEdges);
		TreeMap<Integer, Segment> segments = segmentMapTile.getSegmentsWithLabels();
		
		while (smallestEdgeGradient!=null && smallestEdgeGradient.getEdgeGradient() < minGradient) {
			
			/* TODO: put if-block into an own private method to can observations by
			 * performance aspects (for example: length of sortedSegmentEdges) */ 
			if (segments.containsKey(smallestEdgeGradient.getSmallerLabel())
					&& segments.containsKey(smallestEdgeGradient.getBiggerLabel())) {
				
				// remove smaller labeled segment from TreeMap and references from neighbors
				smallerLabeldSegment = segments.remove(smallestEdgeGradient.getSmallerLabel());
				
				// remove bigger labeled segment from TreeMap and references from neighbors
				biggerLabeldSegment = segments.remove(smallestEdgeGradient.getBiggerLabel());
				
				mergedSegment = Segment.add(smallerLabeldSegment, biggerLabeldSegment, ++lastLabel);
				
				// set the two segments as FORWAREDer segments and point to the merged
				// segment as the parent segment
				smallerLabeldSegment.setState(SegmentState.FORWARD);
				smallerLabeldSegment.getForwardInfo().parent = mergedSegment;
				biggerLabeldSegment.setState(SegmentState.FORWARD);
				biggerLabeldSegment.getForwardInfo().parent = mergedSegment;
				
				segmentMapTile.put(mergedSegment);
				for (Segment neighbor : mergedSegment.getNeighbors()) {
					sortedSegmentEdges.add(new EdgeGradient(mergedSegment, neighbor));
				}
			}
			
			sortedSegmentEdges.remove(smallestEdgeGradient);
			smallestEdgeGradient = getFirstElement(sortedSegmentEdges);
		}
		
		replaceForwardingSegments();
		
		return segmentMapTile;
	}
	
	private void replaceForwardingSegments() {
		SegmentMatrix segmentMatrix = segmentMapTile.getSegmentMatrix();
		int supremumX = segmentMatrix.getWidth();
		int supremumY = segmentMatrix.getHeight();
		
		for (int x=0; x<supremumX; x++) {
			for (int y=0; y<supremumY; y++) {
				segmentMatrix.setSegment(x, y, segmentMatrix.getSegment(x, y).getParent());
			}
		}
	}
	
	/**	Adds all neighborhood relations to the sorted tree set.
	 * Because the neighborhood relationship is symmetric, every segment edge
	 * gradient is added twice. But due to the specific comparable implementation
	 * of EdgeGradient */
	private void extractEdges() {
		for (Segment segm : segmentMapTile.getSegments()) {
			for (Segment neighbor : segm.getNeighbors()) {
				sortedSegmentEdges.add(new EdgeGradient(segm, neighbor));
			}
		}
	}
	
	private static EdgeGradient getFirstElement(TreeSet<EdgeGradient> gradientSet) {
		try {
			return gradientSet.first();
		} catch(NoSuchElementException e) {
			return null;
		}
	}
}
