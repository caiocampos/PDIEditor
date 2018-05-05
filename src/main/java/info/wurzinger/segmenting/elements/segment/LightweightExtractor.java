package info.wurzinger.segmenting.elements.segment;

import java.util.ArrayList;

import javax.media.jai.PlanarImage;

public class LightweightExtractor {
	
	private ArrayList<double[]> segmentColors;
	private ArrayList<Integer> segmentAreas;
	private SegmentMatrix segmentMatrix;
	private int startSegmentLabel = SegmentMap.SEGMENT_LABEL_INFIMUM + 1;
	private int width = 0;
	private int height = 0;
	
	// TODO: remove constructor
	public LightweightExtractor(SegmentMatrix segmentMatrix) {
		this(segmentMatrix, null, null);
	}
	
	/**
	 * 
	 * @param segmentMatrix
	 * @param segmentColors contains the average colors per segment indexed by the segment label
	 * @param segmentAreas contains the number of pixel per segment indexed by the segment label
	 */
	public LightweightExtractor(SegmentMatrix segmentMatrix, ArrayList<double[]> segmentColors, ArrayList<Integer> segmentAreas) {
		this.segmentMatrix = segmentMatrix;
		
		this.segmentColors = segmentColors;
		this.segmentAreas = segmentAreas;
		
		if (segmentMatrix!=null) {
			this.startSegmentLabel = segmentMatrix.smallestSegmentLabel;
			this.width = segmentMatrix.getWidth();
			this.height = segmentMatrix.getHeight();
		}
	}
	
	/**
	 * Transforms the segment label matrix into a lightweight segment map and
	 * assigns the segments their average color and their areaSize in pixels.
	 * 
	 * @param sourceColorImage the multi-band source image
	 * @return all the segments and their relationships as a <code>SegmentMapTile</code> object.
	 */
	public SegmentMapTile createSegmentMap(PlanarImage sourceColorImage) {
		int label;
		Segment segment;
		
		SegmentMapTile map = new SegmentMapTile(segmentMatrix, sourceColorImage);
		
		for (int y = 0; y<height; y++) { // iterate over all pixel of the image
	    	for (int x = 0; x<width; x++) {
	    		segment = segmentMatrix.getSegment(x, y);
	    		label = segment.getLabel();
	    		
	    		if (map.get(label) == null) {
	    			
	    			if (segmentAreas!=null) {
	    				segment.setAreaSize(segmentAreas.get(label-startSegmentLabel));
	    			}
	    			if (segmentColors!=null) {
	    				segment.setColor(segmentColors.get(label-startSegmentLabel));
	    			}
	    			
	    			map.put(label, segment);
	    		}
	    	}
		}
		
		// set references between neighboring segments
		map.setSegmentNeighborhoodRelationships();
		
		return map;
	}
}
