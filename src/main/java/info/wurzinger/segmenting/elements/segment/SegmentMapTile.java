package info.wurzinger.segmenting.elements.segment;

import static info.wurzinger.segmenting.elements.segment.state.SegmentMapState.*;

import javax.media.jai.PlanarImage;

import info.wurzinger.segmenting.elements.segment.state.SegmentMapStateException;
import info.wurzinger.segmenting.elements.segment.state.SegmentStateException;

@SuppressWarnings("serial")
public class SegmentMapTile extends SegmentMap {
	
	protected SegmentMatrix segmentMatrix;
	protected int tileWidth, tileHeight;
	
	// this arrays holds the references of segments, which are next the tile border;
	// this is necessary to find segment neighborhoods when merging multiple SegmentMapTiles
	protected Segment[] rightNeighborIndex, topNeighborIndex, bottomNeighborIndex, leftNeighborIndex;
	
	public SegmentMapTile(SegmentMatrix segmentMatrix, PlanarImage sourceColorImage) {
		super(sourceColorImage);
		
		this.segmentMatrix = segmentMatrix;
		this.state = LIGHTWEIGHT;
		
		if (segmentMatrix!=null) {
			this.tileWidth = segmentMatrix.getWidth();
			this.tileHeight = segmentMatrix.getHeight();
		}
		
		this.topNeighborIndex = new Segment[tileWidth];
		this.rightNeighborIndex = new Segment[tileHeight];
		this.bottomNeighborIndex = new Segment[tileWidth];
		this.leftNeighborIndex = new Segment[tileHeight];
	}
	
	public Segment get(int labelMatrixX, int labelMatrixY) {
		return segmentMatrix.getSegment(labelMatrixX, labelMatrixY);
	}
	
	public SegmentMatrix getSegmentMatrix() {
		return segmentMatrix;
	}
	
	public int getTileWidth() {
		return tileWidth;
	}
	
	public int getTileHeight() {
		return tileHeight;
	}
	
	/**
	 * find references between neighboring segments
	 * @throws SegmentStateException 
	 */
	public void setSegmentNeighborhoodRelationships() {
		if (state!=LIGHTWEIGHT) throw new SegmentMapStateException(this, LIGHTWEIGHT);
		
		Segment s1, s2;
		
		for (int y = 0; y<tileHeight; y++) { // iterate over all pixel of the image
	    	for (int x = 0; x<tileWidth; x++) {
	    		
	    		if (x > 0 && segmentMatrix.getSegment(x, y) != segmentMatrix.getSegment(x-1, y)) {
	    			s1 = get(x, y);
	    			s2 = get(x-1, y);
	    			s1.addNeighbor(s2);
	    			s2.addNeighbor(s1); // and vice versa
	    		}
	    		
	    		if (y > 0 && segmentMatrix.getSegment(x, y) != segmentMatrix.getSegment(x, y-1)) {
	    			s1 = get(x, y);
	    			s2 = get(x, y-1);
	    			s1.addNeighbor(s2);
	    			s2.addNeighbor(s1); // and vice versa
	    		}
	    	}
		}
		
		// store segments laying at tile border
		
		int lastX = tileWidth-1;
		for (int y = 0; y<tileHeight; y++) {
			// left tile border
			leftNeighborIndex[y] = get(0, y);
			
			// right tile border
			rightNeighborIndex[y] = get(lastX, y);
		}
		
		int lastY = tileHeight-1;
		for (int x = 0; x<tileWidth; x++) {
			// top tile border
			topNeighborIndex[x] = get(x, 0);
			
			// bottom tile border
			bottomNeighborIndex[x] = get(x, lastY);
		}
	}
}
