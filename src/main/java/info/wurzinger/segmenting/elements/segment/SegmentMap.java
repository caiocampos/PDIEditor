package info.wurzinger.segmenting.elements.segment;

import static info.wurzinger.segmenting.elements.segment.state.SegmentMapState.LIGHTWEIGHT;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import javax.media.jai.PlanarImage;

import info.wurzinger.segmenting.elements.segment.state.SegmentMapState;
import info.wurzinger.segmenting.elements.segment.state.SegmentState;

public abstract class SegmentMap {
	
	public static final int SEGMENT_LABEL_INFIMUM = 0;
	public static final int SEGMENT_LABEL_SUPREMUM = Integer.MAX_VALUE;
	
	protected SegmentMapState state = LIGHTWEIGHT;
	
	protected TreeMap<Integer, Segment> segments;
	protected PlanarImage sourceColorImage;
	protected int imageWidth, imageHeight;
	
	protected SegmentMap(PlanarImage sourceColorImage) {
		this.sourceColorImage = sourceColorImage;
		
		if (sourceColorImage!=null) {
			imageWidth = sourceColorImage.getWidth();
			imageHeight = sourceColorImage.getHeight();
		} else {
			imageWidth = 0;
			imageHeight = 0;
		}
		
		// hold segments in TreeMap
		segments = new TreeMap<Integer, Segment>();
	}
	
	public SegmentMapState getState() {
		return state;
	}
	
	public void setState(SegmentMapState state) {
		this.state = state;
	}
	
	public void put(Segment segment) {
		segments.put(segment.getLabel(), segment);
	}
	
	public void put(int label, Segment segment) {
		segments.put(label, segment);
	}
	
	public Segment get(int label) {
		return segments.get(label);
	}
	
	public TreeMap<Integer, Segment> getSegmentsWithLabels() {
		return segments;
	}
	
	public Collection<Segment> getSegments() {
		return segments.values();
	}
	
	public int getSegmentCount() {
		return segments.size();
	}
	
	public int getHighestLabel() {
		return segments.lastKey();
	}
	
	public int getImageWidth() {
		return imageWidth;
	}
	
	public int getImageHeight() {
		return imageHeight;
	}
	
	public void remove(Segment segment) {
		if (segment==null) return;
		
		// remove reference to segment from neighbors
		for (Segment neighbor : segment.getNeighbors()) {
			neighbor.getNeighbors().remove(segment);
		}
		
		// remove segment from the segment list of map
		segments.remove(segment.getLabel());
	}
	
	/**
	 * This method eliminates several {@link Segment}s which holds less than
	 * <code>minSegmentSize<code> pixel.
	 * 
	 * @param minSegmentSize is the minimum number of pixel, which a segment
	 * in the map is allowed to have.
	 */
	public void joinSmallSegments(int minSegmentSize) {
		// generate List Array
		LinkedList<Segment> microSegments[] = new LinkedList[minSegmentSize];
		
		// create a List for every possible size
		for (int segmentSize = 0; segmentSize < microSegments.length; segmentSize++) {
			microSegments[segmentSize] = new LinkedList<Segment>();
		}
		
		// fill lists
		for (Segment segment : segments.values()) {
			// if the segment is too small, then put it into list
			if (segment.getAreaSize() < minSegmentSize && segment.getAreaSize() > 0) {
				microSegments[segment.getAreaSize()].add(segment);
			}
		}
		
		EdgeGradient minDistance, distance;
		Segment segment, mergedSegment, neighborMinDistance;
		Iterator<Segment> iter;
		
		// iterate over all lists
		for (int segmentSize = 0; segmentSize < microSegments.length; segmentSize++) {
			iter = microSegments[segmentSize].iterator();
			
			while (iter.hasNext()) {
				segment = iter.next();
				
				minDistance = null;
				neighborMinDistance = null;
				
				// find the neighbor with minimal distance
				for (Segment neighbor : segment.getNeighbors()) {
					distance = new EdgeGradient(segment, neighbor);
					
					if (minDistance==null || neighborMinDistance==null || distance.compareTo(minDistance) < 0) {
						minDistance = distance;
						neighborMinDistance = neighbor;
					}
				}
				
				// if a neighbor with minimal distance was found, then...
				if (minDistance!=null && neighborMinDistance!=null) {
					
					// remove segment from map
					segments.remove(segment.getLabel());
					iter.remove(); // remove the segment from the list via the iterator
					
					segments.remove(neighborMinDistance.getLabel());
					if (neighborMinDistance.getAreaSize() < minSegmentSize) {
						
						// remove neighborMinDistance from it's list
						microSegments[neighborMinDistance.getAreaSize()].remove(neighborMinDistance);
						
						// if the affected list is the list investigated currently, then the iterator must be updated
						if (neighborMinDistance.getAreaSize() == segment.getAreaSize()) {
							iter = microSegments[segmentSize].iterator(); // create new iterator without 'segment' and 'neighborMinDistance'
						}
					}
					
					// merge segments
					mergedSegment = Segment.add(segment, neighborMinDistance, getHighestLabel()+1);
					
					// set the two segments as FORWAREDer segments and point to the merged
					// segment as the parent segment
					segment.setState(SegmentState.FORWARD);
					segment.getForwardInfo().parent = mergedSegment;
					neighborMinDistance.setState(SegmentState.FORWARD);
					neighborMinDistance.getForwardInfo().parent = mergedSegment;
					
					// put new segment into map
					put(mergedSegment);
					
					// Because the size of the merged Segment must be bigger then the current one,
					// it can be possible that the mergedSegment is merged again during the algorithm.
					if (mergedSegment.getAreaSize() < minSegmentSize) {
						microSegments[mergedSegment.getAreaSize()].add(mergedSegment);
					}
				
				} // END if a min distance neighbor was found
				
			} // END for each segment in segmList
			
		} // END iteration over all lists
	}
}
