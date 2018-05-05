package info.wurzinger.segmenting.neighborhood.inspectors;

import info.wurzinger.segmenting.elements.segment.Segment;
import info.wurzinger.segmenting.elements.segment.SegmentMatrix;
import info.wurzinger.segmenting.elements.Pixel;

import java.util.List;
import java.util.TreeSet;


public class NeighboringLabelFinder extends NeighborhoodInspector<Pixel> {
	
	private TreeSet<Segment> neighboringSegments;
	private SegmentMatrix segmentMatrix;
	private List<Segment> segmentsToIgnore;
	
	public NeighboringLabelFinder(SegmentMatrix segmentMatrix, List<Segment> segmentsToIgnore) {
		reset();
		this.segmentMatrix = segmentMatrix;
		this.segmentsToIgnore = segmentsToIgnore;
	}
	
	public void reset() {
		neighboringSegments = new TreeSet<Segment>();
	}
	
	public void inspect(Pixel p) {
		Segment segment = segmentMatrix.getRelativeSegment(p);
		
		if (segment!=null && !segmentsToIgnore.contains(segment)) {
			
			if (!neighboringSegments.contains(segment)) {
				neighboringSegments.add(segment);
			}
		}
	}
	
	public int getNeighboringSegmentCount() {
		return neighboringSegments.size();
	}
	
	public Segment getFirstNeigboringSegment() {
		return neighboringSegments.first();
	}
}
